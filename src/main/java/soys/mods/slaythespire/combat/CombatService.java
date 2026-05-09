package soys.mods.slaythespire.combat;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.Nullable;
import soys.mods.slaythespire.card.CardDefinition;
import soys.mods.slaythespire.card.CardDefinitions;
import soys.mods.slaythespire.card.CardItem;
import soys.mods.slaythespire.card.CardStacks;
import soys.mods.slaythespire.card.CardTarget;
import soys.mods.slaythespire.card.CardType;
import soys.mods.slaythespire.card.CardUseContext;
import soys.mods.slaythespire.network.ModNetworking;
import soys.mods.slaythespire.registry.ModItems;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

/**
 * 中文：服务端战斗结算服务。卡牌使用、费用、目标校验、伤害、格挡、生成卡和状态同步都从这里统一执行。
 * English: Server-side combat settlement service. Card use, costs, target validation, damage, block, generated cards, and state sync are centralized here.
 */
public final class CombatService {
    // 中文：禁止实例化服务端战斗结算工具类。
    // English: Prevents instantiation of this server-side combat settlement utility.
    private CombatService() {
    }

    // 中文：尝试打出一张卡牌并执行费用、目标、效果、消耗和同步结算。
    // English: Attempts to play one card and settle cost, targeting, effect, exhaust, and synchronization.
    public static boolean tryUseCard(ServerPlayer player, CardDefinition definition, @Nullable LivingEntity target, ItemStack stack) {
        // 中文：这是所有卡牌打出的主入口；客户端交互最终都必须落到服务端这个方法，避免双端各自结算。
        // English: This is the main card-play entrypoint; every client interaction must reach this server method to avoid split-side settlement.
        CombatState state = CombatStateAccess.get(player);
        boolean creative = player.isCreative();
        long gameTime = player.serverLevel().getGameTime();

        if (!definition.isPlayable()) {
            player.displayClientMessage(Component.translatable("message.slaythespire.card_unplayable"), true);
            ModNetworking.sync(player);
            return false;
        }

        if (!state.isInCombat()) {
            // 中文：第一次打牌时自动开启轻量战斗，不需要额外回合按钮或外部 JSON 流程。
            // English: The first card play automatically starts lightweight combat without an extra end-turn button or external JSON flow.
            beginCombat(player, state, gameTime, target);
        }

        if (!creative && CardStacks.isUsedThisCombat(stack)) {
            player.displayClientMessage(Component.translatable("message.slaythespire.card_already_used"), true);
            ModNetworking.sync(player);
            return false;
        }

        if (!validateTarget(player, definition, target)) {
            ModNetworking.sync(player);
            return false;
        }

        CardUseContext context = new CardUseContext(player, state, definition, stack, target);
        if (!definition.canUse().test(context)) {
            player.displayClientMessage(Component.translatable("message.slaythespire.card_cannot_use"), true);
            ModNetworking.sync(player);
            return false;
        }

        int actualCost = currentCost(player, definition, stack);
        int xCost = definition.isXCost() ? (creative ? Math.max(1, state.getMaxEnergy()) : state.getEnergy()) : -1;
        // 中文：普通费用先扣能量；X 费记录当前能量作为展示/效果输入，然后消耗全部能量。
        // English: Normal costs spend energy first; X-cost records current energy for display/effect input and then spends all energy.
        if (!creative && !definition.isXCost() && !state.tryConsumeEnergy(actualCost)) {
            player.displayClientMessage(Component.translatable("message.slaythespire.not_enough_energy"), true);
            ModNetworking.sync(player);
            return false;
        }
        if (definition.isXCost()) {
            CardStacks.setCostOverride(stack, xCost);
            if (!creative) {
                state.spendAllEnergy();
            }
        }

        int targetId = target == null ? -1 : target.getId();
        state.markCardUse(gameTime, targetId);
        if (!creative && definition.type() == CardType.POWER) {
            CardStacks.setUsedThisCombat(stack, true);
        }

        if (definition.isAttack() && state.getRageBlockPerAttack() > 0) {
            gainBlock(player, state.getRageBlockPerAttack());
        }

        boolean repeats = definition.isAttack() && state.getDoubleTapCharges() > 0;
        if (repeats) {
            // 中文：Double Tap 只消耗一次充能，随后重复执行同一个 CardEffect。
            // English: Double Tap consumes one charge and then runs the same CardEffect twice.
            state.consumeDoubleTapCharge();
        }

        definition.effect().apply(context);
        if (repeats) {
            definition.effect().apply(context);
        }

        if (definition.exhausts() || (state.isCorruption() && definition.isSkill())) {
            // 中文：消耗逻辑先记录进消耗堆，再由后续 shrink 或生成卡清理处理物品数量。
            // English: Exhaust logic records the card in the exhaust pile before shrink or generated-card cleanup changes stack counts.
            exhaustStack(player, stack);
        }

        if (!creative) {
            stack.shrink(1);
        }

        if (creative && definition.isXCost()) {
            CardStacks.setCostOverride(stack, null);
        }

        ModNetworking.sync(player);
        return true;
    }

    // 中文：开始玩家轻量战斗并同步初始状态。
    // English: Starts lightweight player combat and syncs the initial state.
    public static void beginCombat(ServerPlayer player, CombatState state, long gameTime, @Nullable LivingEntity target) {
        state.beginCombat(gameTime, target == null ? -1 : target.getId());
        CombatantStatusAccess.get(player).clear();
        player.displayClientMessage(Component.translatable("message.slaythespire.combat_start"), true);
        ModNetworking.sync(player);
    }

    // 中文：安全退出战斗并清理所有本场战斗遗留数据。
    // English: Safely exits combat and clears all per-encounter leftover data.
    public static void safeExitCombat(ServerPlayer player, @Nullable Component message) {
        // 中文：安全退出会同时清玩家状态、卡牌 NBT、临时生成卡和敌人状态，作为所有结束路径的统一出口。
        // English: Safe exit clears player state, card NBT, generated cards, and enemy statuses, serving as the shared exit path for all endings.
        CombatState state = CombatStateAccess.get(player);
        List<Integer> affectedIds = new ArrayList<>(state.affectedCombatantIds());
        state.clearEncounterState();
        clearCombatFlags(player.getInventory());
        removeGeneratedCards(player.getInventory());
        CombatantStatusAccess.get(player).clear();
        clearNearbyEnemyStatuses(player, affectedIds);
        if (message != null) {
            player.displayClientMessage(message, true);
        }
        ModNetworking.sync(player);
    }

    // 中文：每 tick 检查战斗目标和超时结束条件。
    // English: Checks combat target and timeout end conditions each tick.
    public static void tick(ServerPlayer player) {
        // 中文：每个服务端玩家 tick 检查战斗是否应自动结束，不在这里推进传统回合。
        // English: Each server player tick checks whether combat should auto-end; this does not advance a traditional turn system.
        CombatState state = CombatStateAccess.get(player);
        if (!state.isInCombat()) {
            return;
        }

        long gameTime = player.serverLevel().getGameTime();
        LivingEntity target = state.getTargetEntityId() >= 0 ? asLiving(player.serverLevel().getEntity(state.getTargetEntityId())) : null;

        if (target != null && !target.isAlive()) {
            safeExitCombat(player, Component.translatable("message.slaythespire.combat_end_target_down"));
            return;
        }
        if (shouldEndCombatWithoutTarget(state.getTargetEntityId(), findEnemies(player).size())) {
            safeExitCombat(player, Component.translatable("message.slaythespire.combat_end_no_enemies"));
            return;
        }

        if (gameTime - state.getLastCombatGameTime() >= CombatRules.COMBAT_TIMEOUT_TICKS) {
            safeExitCombat(player, Component.translatable("message.slaythespire.combat_timeout"));
        }
    }

    // 中文：校验卡牌目标是否满足定义要求。
    // English: Validates whether the card target satisfies the definition requirements.
    public static boolean validateTarget(ServerPlayer player, CardDefinition definition, @Nullable LivingEntity target) {
        if (definition.target() != CardTarget.ENEMY) {
            return true;
        }
        if (!isValidEnemyTarget(player, target)) {
            player.displayClientMessage(Component.translatable("message.slaythespire.need_enemy_target"), true);
            return false;
        }
        return true;
    }

    // 中文：判断实体是否可作为敌方目标。
    // English: Checks whether an entity is a valid enemy target.
    public static boolean isValidEnemyTarget(ServerPlayer player, @Nullable LivingEntity target) {
        return target != null && target.isAlive() && target != player;
    }

    // 中文：计算当前上下文下单张卡牌的实际费用。
    // English: Computes the effective cost of one card in the current context.
    public static int currentCost(ServerPlayer player, CardDefinition definition, ItemStack stack) {
        // 中文：费用优先级：单张卡覆盖值 > Corruption 技能免费 > 特殊卡动态费用 > 定义费用。
        // English: Cost priority is per-stack override, Corruption free skills, special dynamic cost, then definition cost.
        Integer override = CardStacks.getCostOverride(stack);
        if (override != null) {
            return override;
        }

        CombatState state = CombatStateAccess.get(player);
        if (state.isCorruption() && definition.isSkill()) {
            return 0;
        }
        if ("blood_for_blood_card".equals(definition.effectKey())) {
            return Math.max(0, definition.cost() - state.getHpLossCount());
        }
        return definition.cost();
    }

    // 中文：按默认力量倍率对单个目标造成攻击伤害。
    // English: Deals attack damage to one target using the default strength multiplier.
    public static float dealAttackDamage(ServerPlayer player, @Nullable LivingEntity target, float baseDamage) {
        return dealAttackDamage(player, target, baseDamage, 1, true);
    }

    // 中文：按指定力量倍率和是否吃力量修正对单个目标造成攻击伤害。
    // English: Deals attack damage to one target with configurable strength multiplier and strength usage.
    public static float dealAttackDamage(ServerPlayer player, @Nullable LivingEntity target, float baseDamage, int strengthMultiplier, boolean useStrength) {
        if (target == null) {
            return 0.0F;
        }

        // 中文：攻击伤害在这里统一合并玩家力量、攻击者虚弱和目标易伤，避免每张卡重复写倍率。
        // English: Attack damage combines player strength, attacker weak, and target vulnerable here so individual cards do not duplicate multipliers.
        CombatState state = CombatStateAccess.get(player);
        CombatantStatus playerStatus = CombatantStatusAccess.get(player);
        CombatantStatus targetStatus = CombatantStatusAccess.get(target);
        float damage = computeAttackDamage(baseDamage, state.getStrength(), playerStatus.weak(), targetStatus.vulnerable(), strengthMultiplier, useStrength);
        float before = target.getHealth();
        target.hurt(player.damageSources().playerAttack(player), damage);
        return Math.max(0.0F, before - target.getHealth());
    }

    // 中文：纯计算攻击伤害数值，供结算和 GameTest 共用。
    // English: Purely computes attack damage for both settlement code and GameTests.
    public static float computeAttackDamage(float baseDamage, int playerStrength, int attackerWeakTurns, int targetVulnerableTurns, int strengthMultiplier, boolean useStrength) {
        float damage = baseDamage;
        if (useStrength) {
            damage += playerStrength * strengthMultiplier;
        }
        if (attackerWeakTurns > 0) {
            damage *= 0.75F;
        }
        if (targetVulnerableTurns > 0) {
            damage *= 1.5F;
        }
        return Math.max(0.0F, damage);
    }

    // 中文：判断无明确目标时是否应因附近无敌人而结束战斗。
    // English: Checks whether combat should end when there is no explicit target and no nearby enemies.
    public static boolean shouldEndCombatWithoutTarget(int targetEntityId, int enemyCount) {
        return targetEntityId < 0 && enemyCount <= 0;
    }

    // 中文：对附近所有敌人造成攻击伤害并返回总实际伤害。
    // English: Deals attack damage to all nearby enemies and returns total actual damage.
    public static float dealAllEnemies(ServerPlayer player, float damage) {
        float total = 0.0F;
        for (LivingEntity enemy : findEnemies(player)) {
            total += dealAttackDamage(player, enemy, damage);
        }
        return total;
    }

    // 中文：对随机敌人进行多段攻击并返回总实际伤害。
    // English: Performs multiple random-enemy hits and returns total actual damage.
    public static float dealRandomEnemyHits(ServerPlayer player, float damage, int hits) {
        float total = 0.0F;
        RandomSource random = player.getRandom();
        for (int i = 0; i < hits; i++) {
            LivingEntity target = randomEnemy(player, random);
            if (target == null) {
                break;
            }
            total += dealAttackDamage(player, target, damage);
        }
        return total;
    }

    // 中文：给指定实体施加易伤。
    // English: Applies vulnerable to the given entity.
    public static void applyVulnerable(LivingEntity entity, int turns) {
        CombatantStatusAccess.get(entity).addVulnerable(turns);
    }

    // 中文：由玩家施加易伤并记录受影响实体。
    // English: Applies vulnerable from a player and records the affected entity.
    public static void applyVulnerableFromPlayer(ServerPlayer player, LivingEntity entity, int turns) {
        CombatStateAccess.get(player).markAffectedCombatant(entity.getId());
        applyVulnerable(entity, turns);
    }

    // 中文：给指定实体施加虚弱。
    // English: Applies weak to the given entity.
    public static void applyWeak(LivingEntity entity, int turns) {
        CombatantStatusAccess.get(entity).addWeak(turns);
    }

    // 中文：由玩家施加虚弱并记录受影响实体。
    // English: Applies weak from a player and records the affected entity.
    public static void applyWeakFromPlayer(ServerPlayer player, LivingEntity entity, int turns) {
        CombatStateAccess.get(player).markAffectedCombatant(entity.getId());
        applyWeak(entity, turns);
    }

    // 中文：给附近所有敌人施加虚弱。
    // English: Applies weak to all nearby enemies.
    public static void applyWeakToAllEnemies(ServerPlayer player, int turns) {
        for (LivingEntity enemy : findEnemies(player)) {
            CombatStateAccess.get(player).markAffectedCombatant(enemy.getId());
            applyWeak(enemy, turns);
        }
    }

    // 中文：给附近所有敌人施加易伤。
    // English: Applies vulnerable to all nearby enemies.
    public static void applyVulnerableToAllEnemies(ServerPlayer player, int turns) {
        for (LivingEntity enemy : findEnemies(player)) {
            CombatStateAccess.get(player).markAffectedCombatant(enemy.getId());
            applyVulnerable(enemy, turns);
        }
    }

    // 中文：修改目标实体自身的力量修正。
    // English: Modifies the target entity's local strength modifier.
    public static void modifyTargetStrength(LivingEntity entity, int amount) {
        CombatantStatusAccess.get(entity).addStrength(amount);
    }

    // 中文：由玩家修改目标力量并记录受影响实体。
    // English: Modifies target strength from a player and records the affected entity.
    public static void modifyTargetStrengthFromPlayer(ServerPlayer player, LivingEntity entity, int amount) {
        CombatStateAccess.get(player).markAffectedCombatant(entity.getId());
        modifyTargetStrength(entity, amount);
    }

    // 中文：判断敌人是否带有易伤。
    // English: Checks whether an enemy has vulnerable.
    public static boolean enemyHasVulnerable(LivingEntity entity) {
        return CombatantStatusAccess.get(entity).vulnerable() > 0;
    }

    // 中文：让玩家获得能量。
    // English: Grants energy to the player.
    public static void gainEnergy(ServerPlayer player, int amount) {
        CombatStateAccess.get(player).gainEnergy(amount);
    }

    // 中文：让玩家获得格挡，并允许触发硬撑。
    // English: Grants block to the player and allows Juggernaut triggers.
    public static void gainBlock(ServerPlayer player, int amount) {
        gainBlock(player, amount, true);
    }

    // 中文：让玩家获得格挡，并可控制是否触发硬撑。
    // English: Grants block to the player and can control whether Juggernaut triggers.
    public static void gainBlock(ServerPlayer player, int amount, boolean triggerJuggernaut) {
        // 中文：获得格挡时可触发 Juggernaut；内部调用可通过 triggerJuggernaut=false 避免递归连锁。
        // English: Gaining block can trigger Juggernaut; internal calls can pass triggerJuggernaut=false to avoid recursive chains.
        CombatState state = CombatStateAccess.get(player);
        state.addBlock(amount);
        if (triggerJuggernaut && state.getJuggernautDamage() > 0) {
            LivingEntity target = randomEnemy(player, player.getRandom());
            if (target != null) {
                dealAttackDamage(player, target, state.getJuggernautDamage(), 1, false);
            }
        }
    }

    // 中文：处理卡牌效果造成的玩家生命损失。
    // English: Handles player HP loss caused by card effects.
    public static void loseHpFromCard(ServerPlayer player, int amount) {
        if (amount <= 0) {
            return;
        }

        CombatState state = CombatStateAccess.get(player);
        player.hurt(player.damageSources().generic(), amount);
        state.recordHpLoss();
        if (state.getRuptureStrength() > 0) {
            state.addStrength(state.getRuptureStrength());
        }
    }

    // 中文：生成指定数量的临时抽牌结果。
    // English: Generates the given number of temporary drawn-card results.
    public static void drawTemporaryCards(ServerPlayer player, int count) {
        // 中文：抽牌在 Minecraft 背包中表现为生成临时卡牌，战斗结束会统一删除 generated 标记的卡。
        // English: Drawing cards appears as generating temporary card items in the Minecraft inventory; generated cards are removed at combat end.
        CombatState state = CombatStateAccess.get(player);
        if (count <= 0 || state.isDrawLocked()) {
            return;
        }

        for (int i = 0; i < count; i++) {
            CardDefinition definition = randomOwnedDefinition(player, card -> !card.isStatus());
            if (definition == null) {
                definition = CardDefinitions.STRIKE;
            }
            ItemStack stack = createGeneratedCard(definition.id(), null);
            addStack(player, stack);
        }
    }

    // 中文：创建一张不会在战斗结束自动删除的生成卡副本。
    // English: Creates a generated card copy that is not automatically removed at combat end.
    public static void createPersistentGeneratedCopy(ServerPlayer player, ResourceLocation id) {
        addStack(player, createGeneratedCard(id, null));
    }

    // 中文：创建一张带可选费用覆盖的临时生成卡副本。
    // English: Creates a temporary generated card copy with an optional cost override.
    public static void createTemporaryGeneratedCopy(ServerPlayer player, ResourceLocation id, @Nullable Integer costOverride) {
        addStack(player, createGeneratedCard(id, costOverride));
    }

    // 中文：从玩家拥有卡牌中随机选择一张攻击牌定义。
    // English: Selects a random attack card definition from cards owned by the player.
    public static CardDefinition randomOwnedAttack(ServerPlayer player) {
        return randomOwnedDefinition(player, CardDefinition::isAttack);
    }

    // 中文：向玩家背包加入状态牌并触发相关能力牌效果。
    // English: Adds a status card to the player's inventory and triggers related power effects.
    public static void addStatusCard(ServerPlayer player, ResourceLocation id) {
        // 中文：状态牌加入背包时立即触发 Fire Breathing/Evolve 类能力，贴近杀戮尖塔的状态牌反馈。
        // English: Adding a status card immediately triggers Fire Breathing and Evolve-style powers, matching Slay the Spire's status-card feedback.
        addStack(player, createGeneratedCard(id, null));
        CombatState state = CombatStateAccess.get(player);
        if (state.getFireBreathingDamage() > 0) {
            dealAllEnemies(player, state.getFireBreathingDamage());
        }
        if (state.getEvolveDraw() > 0 && !state.isDrawLocked()) {
            drawTemporaryCards(player, state.getEvolveDraw());
        }
    }

    // 中文：执行卡牌消耗语义并触发消耗相关能力。
    // English: Applies card exhaust semantics and triggers exhaust-related powers.
    public static void exhaustStack(ServerPlayer player, ItemStack stack) {
        // 中文：消耗只处理卡牌语义，不直接删除物品；真正数量变化由调用方决定。
        // English: Exhaust handles card semantics and does not directly delete the item; callers decide the actual stack count change.
        CardStacks.setUsedThisCombat(stack, true);
        CardDefinition definition = definitionFromStack(stack);
        CombatState state = CombatStateAccess.get(player);
        state.addExhaustedCard(definition.id().toString());

        if (state.getFeelNoPainBlock() > 0) {
            gainBlock(player, state.getFeelNoPainBlock());
        }
        if (state.getDarkEmbraceDraw() > 0 && !state.isDrawLocked()) {
            drawTemporaryCards(player, state.getDarkEmbraceDraw());
        }
        if ("sentinel_card".equals(definition.effectKey())) {
            state.gainEnergy(2);
        }
    }

    // 中文：随机消耗满足条件的可用卡牌并返回消耗数量。
    // English: Randomly exhausts available cards matching the filter and returns the exhausted count.
    public static int exhaustRandomCards(ServerPlayer player, Predicate<CardDefinition> filter, int limit, @Nullable ItemStack exclude) {
        List<ItemStack> candidates = availableCardStacks(player, stack -> stack != exclude && filter.test(definitionFromStack(stack)));
        int count = 0;
        while (count < limit && !candidates.isEmpty()) {
            ItemStack chosen = candidates.remove(player.getRandom().nextInt(candidates.size()));
            exhaustStack(player, chosen);
            count++;
        }
        return count;
    }

    // 中文：消耗所有满足条件的可用卡牌并返回数量。
    // English: Exhausts every available card matching the filter and returns the count.
    public static int exhaustAllMatching(ServerPlayer player, Predicate<CardDefinition> filter, @Nullable ItemStack exclude) {
        List<ItemStack> candidates = availableCardStacks(player, stack -> stack != exclude && filter.test(definitionFromStack(stack)));
        for (ItemStack stack : candidates) {
            exhaustStack(player, stack);
        }
        return candidates.size();
    }

    // 中文：从消耗堆取回一张卡牌并加入背包。
    // English: Returns one card from the exhaust pile to the inventory.
    public static void exhumeRandomCard(ServerPlayer player) {
        CombatState state = CombatStateAccess.get(player);
        String id = state.popExhaustedCard();
        if (id == null) {
            return;
        }

        ItemStack stack = createGeneratedCard(ResourceLocation.tryParse(id), null);
        CardStacks.setGenerated(stack, false);
        CardStacks.setUsedThisCombat(stack, false);
        addStack(player, stack);
    }

    // 中文：从玩家背包中随机复制满足条件的卡牌。
    // English: Randomly copies cards matching the filter from the player's inventory.
    public static void copyRandomFromInventory(ServerPlayer player, Predicate<CardDefinition> filter, int amount) {
        List<ItemStack> candidates = availableCardStacks(player, stack -> filter.test(definitionFromStack(stack)));
        for (int i = 0; i < amount && !candidates.isEmpty(); i++) {
            ItemStack source = candidates.get(player.getRandom().nextInt(candidates.size()));
            createPersistentGeneratedCopy(player, definitionFromStack(source).id());
        }
    }

    // 中文：随机播放一张其他可用卡牌的效果并将其消耗。
    // English: Plays the effect of another random available card and exhausts it.
    public static void playRandomCardAndExhaust(ServerPlayer player, ItemStack sourceStack) {
        List<ItemStack> candidates = availableCardStacks(player, stack -> stack != sourceStack && definitionFromStack(stack).isPlayable());
        if (candidates.isEmpty()) {
            return;
        }

        ItemStack randomStack = candidates.get(player.getRandom().nextInt(candidates.size()));
        CardDefinition definition = definitionFromStack(randomStack);
        LivingEntity target = definition.target() == CardTarget.ENEMY ? randomEnemy(player, player.getRandom()) : null;
        definition.effect().apply(new CardUseContext(player, CombatStateAccess.get(player), definition, randomStack, target));
        exhaustStack(player, randomStack);
    }

    // 中文：判断除当前卡外的可用卡牌是否全为攻击牌。
    // English: Checks whether all available cards except the current one are attacks.
    public static boolean allAvailableCardsAreAttacks(ServerPlayer player, ItemStack current) {
        for (ItemStack stack : availableCardStacks(player, stack -> stack != current)) {
            CardDefinition definition = definitionFromStack(stack);
            if (!definition.isAttack()) {
                return false;
            }
        }
        return true;
    }

    // 中文：统计玩家背包中名称包含 strike 的卡牌数量。
    // English: Counts card stacks in the player's inventory whose effect key contains strike.
    public static int countStrikeCards(ServerPlayer player) {
        int total = 0;
        for (ItemStack stack : inventoryCardStacks(player, stack -> definitionFromStack(stack).effectKey().contains("strike"))) {
            total += stack.getCount();
        }
        return total;
    }

    // 中文：返回玩家附近敌人数量。
    // English: Returns the number of enemies near the player.
    public static int nearbyEnemyCount(ServerPlayer player) {
        return findEnemies(player).size();
    }

    // 中文：判断目标怪物当前是否把玩家作为攻击目标。
    // English: Checks whether the target monster currently intends to attack the player.
    public static boolean enemyIntendsToAttack(ServerPlayer player, @Nullable LivingEntity target) {
        if (!(target instanceof Monster monster)) {
            return false;
        }
        return monster.getTarget() == player;
    }

    // 中文：提高玩家最大生命并立即治疗等量生命。
    // English: Increases player max health and immediately heals the same amount.
    public static void increaseMaxHealth(ServerPlayer player, double amount) {
        var attribute = player.getAttribute(Attributes.MAX_HEALTH);
        if (attribute == null) {
            return;
        }
        attribute.setBaseValue(attribute.getBaseValue() + amount);
        player.heal((float) amount);
    }

    // 中文：创建一张生成卡物品栈并写入生成标记和费用覆盖。
    // English: Creates a generated card stack and writes generated and cost-override flags.
    public static ItemStack createGeneratedCard(ResourceLocation id, @Nullable Integer costOverride) {
        ItemStack stack = ModItems.createCardStack(id);
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        CardStacks.setGenerated(stack, true);
        CardStacks.setCostOverride(stack, costOverride);
        return stack;
    }

    // 中文：从物品栈解析卡牌定义，非卡牌物品会抛出错误。
    // English: Resolves a card definition from an item stack, throwing for non-card items.
    public static CardDefinition definitionFromStack(ItemStack stack) {
        if (stack.getItem() instanceof CardItem cardItem) {
            CardDefinition definition = cardItem.definition(stack, null);
            if (definition != null) {
                return definition;
            }
        }
        throw new IllegalArgumentException("Item stack is not a registered Slay the Spire card: " + stack);
    }

    // 中文：清理附近敌人和记录受影响实体身上的临时战斗状态。
    // English: Clears temporary combat statuses from nearby enemies and recorded affected entities.
    private static void clearNearbyEnemyStatuses(ServerPlayer player, List<Integer> affectedIds) {
        for (Integer affectedId : affectedIds) {
            LivingEntity affected = asLiving(player.serverLevel().getEntity(affectedId));
            if (affected != null) {
                CombatantStatusAccess.get(affected).clear();
            }
        }
        for (LivingEntity enemy : findEnemies(player)) {
            CombatantStatusAccess.get(enemy).clear();
        }
    }

    // 中文：清理背包中所有卡牌的战斗临时 NBT 标记。
    // English: Clears combat-temporary NBT flags from all cards in the inventory.
    private static void clearCombatFlags(Inventory inventory) {
        for (ItemStack stack : inventory.items) {
            CardStacks.clearCombatFlags(stack);
        }
    }

    // 中文：删除背包中本场战斗临时生成的卡牌。
    // English: Removes cards temporarily generated during this combat from the inventory.
    private static void removeGeneratedCards(Inventory inventory) {
        for (ItemStack stack : inventory.items) {
            if (!(stack.getItem() instanceof CardItem)) {
                continue;
            }
            if (!CardStacks.isGenerated(stack)) {
                continue;
            }
            stack.shrink(stack.getCount());
        }
    }

    // 中文：把物品栈加入玩家背包，失败时掉落到世界中。
    // English: Adds a stack to the player's inventory, dropping it into the world if insertion fails.
    private static void addStack(ServerPlayer player, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
        player.getInventory().setChanged();
    }

    // 中文：收集玩家背包中满足条件的卡牌物品栈。
    // English: Collects card stacks in the player's inventory that match the filter.
    private static List<ItemStack> inventoryCardStacks(ServerPlayer player, Predicate<ItemStack> filter) {
        List<ItemStack> stacks = new ArrayList<>();
        for (ItemStack stack : player.getInventory().items) {
            if (stack.isEmpty() || !(stack.getItem() instanceof CardItem) || !filter.test(stack)) {
                continue;
            }
            stacks.add(stack);
        }
        return stacks;
    }

    // 中文：收集玩家背包中本场战斗尚未使用且满足条件的卡牌。
    // English: Collects cards in the player's inventory that are unused this combat and match the filter.
    private static List<ItemStack> availableCardStacks(ServerPlayer player, Predicate<ItemStack> filter) {
        return inventoryCardStacks(player, stack -> !CardStacks.isUsedThisCombat(stack) && filter.test(stack));
    }

    // 中文：查找玩家附近可作为敌人的存活实体。
    // English: Finds living nearby entities that can be treated as enemies.
    private static List<LivingEntity> findEnemies(ServerPlayer player) {
        // 中文：敌人搜索按距离排序，随机/全体效果都基于同一个附近敌人集合。
        // English: Enemy search is distance-sorted, and random or all-enemy effects share the same nearby enemy set.
        return player.serverLevel()
                .getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(CombatRules.ENEMY_SEARCH_RADIUS),
                        entity -> entity != player && entity.isAlive() && !entity.isAlliedTo(player))
                .stream()
                .sorted(Comparator.comparingDouble(entity -> entity.distanceToSqr(player)))
                .toList();
    }

    // 中文：从附近敌人中随机选择一个目标。
    // English: Selects one random target from nearby enemies.
    private static LivingEntity randomEnemy(ServerPlayer player, RandomSource random) {
        List<LivingEntity> enemies = findEnemies(player);
        return enemies.isEmpty() ? null : enemies.get(random.nextInt(enemies.size()));
    }

    // 中文：从玩家拥有卡牌中随机选择满足条件的定义，缺失时回退到已注册红牌。
    // English: Randomly selects a matching definition from owned cards, falling back to registered red cards.
    private static CardDefinition randomOwnedDefinition(ServerPlayer player, Predicate<CardDefinition> filter) {
        List<CardDefinition> candidates = new ArrayList<>();
        for (ItemStack stack : inventoryCardStacks(player, ignored -> true)) {
            CardDefinition definition = definitionFromStack(stack);
            if (filter.test(definition)) {
                candidates.add(definition);
            }
        }
        if (candidates.isEmpty()) {
            for (CardDefinition definition : CardDefinitions.playableRedCards()) {
                if (filter.test(definition)) {
                    candidates.add(definition);
                }
            }
        }
        return candidates.isEmpty() ? null : candidates.get(player.getRandom().nextInt(candidates.size()));
    }

    // 中文：把普通实体安全转换为 LivingEntity。
    // English: Safely casts a generic entity to LivingEntity.
    private static LivingEntity asLiving(@Nullable net.minecraft.world.entity.Entity entity) {
        return entity instanceof LivingEntity living ? living : null;
    }

    // 中文：刷新玩家背包中卡牌 tooltip 所需的动态预览 NBT。
    // English: Refreshes dynamic preview NBT needed by card tooltips in the player's inventory.
    public static void refreshCardPreviews(ServerPlayer player) {
        // 中文：同步 HUD 前刷新每张卡的预览 NBT，使 tooltip 能显示当前力量、格挡、能量等动态值。
        // English: Before syncing the HUD, refresh preview NBT on each card so tooltips show current strength, block, energy, and related dynamic values.
        CombatState state = CombatStateAccess.get(player);
        int strikeCount = countStrikeCards(player);
        for (ItemStack stack : player.getInventory().items) {
            if (!(stack.getItem() instanceof CardItem)) {
                continue;
            }
            CardStacks.setPreviewStrength(stack, state.getStrength());
            CardStacks.setPreviewBlock(stack, state.getBlock());
            CardStacks.setPreviewEnergy(stack, state.getEnergy());
            CardStacks.setPreviewStrikeCount(stack, strikeCount);
            CardStacks.setPreviewHpLoss(stack, state.getHpLossCount());
            CardStacks.setPreviewCorruption(stack, state.isCorruption());
        }
        player.getInventory().setChanged();
        player.containerMenu.broadcastChanges();
    }
}
