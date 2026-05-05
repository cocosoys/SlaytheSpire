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

public final class CombatService {
    private CombatService() {
    }

    public static boolean tryUseCard(ServerPlayer player, CardDefinition definition, @Nullable LivingEntity target, ItemStack stack) {
        CombatState state = CombatStateAccess.get(player);
        boolean creative = player.isCreative();
        long gameTime = player.serverLevel().getGameTime();

        if (!definition.isPlayable()) {
            player.displayClientMessage(Component.translatable("message.slaythespire.card_unplayable"), true);
            ModNetworking.sync(player);
            return false;
        }

        if (!state.isInCombat()) {
            beginCombat(player, state, gameTime, target);
        }

        if (!creative && (CardStacks.isUsedThisCombat(stack) || CardStacks.isUsedThisTurn(stack))) {
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
        if (!creative) {
            CardStacks.setUsedThisTurn(stack, true);
        }

        if (!creative && definition.type() == CardType.POWER) {
            CardStacks.setUsedThisCombat(stack, true);
        }

        if (definition.isAttack() && state.getRageBlockPerAttack() > 0) {
            gainBlock(player, state.getRageBlockPerAttack());
        }

        boolean repeats = definition.isAttack() && state.getDoubleTapCharges() > 0;
        if (repeats) {
            state.consumeDoubleTapCharge();
        }

        definition.effect().apply(context);
        if (repeats) {
            definition.effect().apply(context);
        }

        if (definition.exhausts() || (state.isCorruption() && definition.isSkill())) {
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

    public static void beginCombat(ServerPlayer player, CombatState state, long gameTime, @Nullable LivingEntity target) {
        state.beginCombat(gameTime, target == null ? -1 : target.getId());
        CombatantStatusAccess.get(player).clear();
        player.displayClientMessage(Component.translatable("message.slaythespire.combat_start"), true);
        ModNetworking.sync(player);
    }

    public static void safeExitCombat(ServerPlayer player, @Nullable Component message) {
        CombatState state = CombatStateAccess.get(player);
        List<Integer> affectedIds = new ArrayList<>(state.affectedCombatantIds());
        state.clearEncounterState();
        clearCombatFlags(player.getInventory());
        removeGeneratedCards(player.getInventory(), false);
        CombatantStatusAccess.get(player).clear();
        clearNearbyEnemyStatuses(player, affectedIds);
        if (message != null) {
            player.displayClientMessage(message, true);
        }
        ModNetworking.sync(player);
    }

    public static void endTurnNow(ServerPlayer player) {
        CombatState state = CombatStateAccess.get(player);
        if (!state.isInCombat()) {
            return;
        }

        if (shouldEndCombatWithoutTarget(state.getTargetEntityId(), findEnemies(player).size())) {
            safeExitCombat(player, Component.translatable("message.slaythespire.combat_end_no_enemies"));
            return;
        }

        long gameTime = player.serverLevel().getGameTime();
        processEndTurn(player);
        state.startNextTurn(gameTime);
        onTurnAdvanceStatuses(player);
        clearTurnFlags(player.getInventory());
        removeGeneratedCards(player.getInventory(), true);
        applyStartTurnPowers(player);
        player.displayClientMessage(Component.translatable("message.slaythespire.turn_refresh", state.getTurn()), true);
        ModNetworking.sync(player);
    }

    public static void tick(ServerPlayer player) {
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

        // Automatic turn advancement disabled: turns must be ended explicitly by the player (via HUD button or key).

        if (gameTime - state.getLastCombatGameTime() >= CombatRules.COMBAT_TIMEOUT_TICKS) {
            safeExitCombat(player, Component.translatable("message.slaythespire.combat_timeout"));
        }
    }

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

    public static boolean isValidEnemyTarget(ServerPlayer player, @Nullable LivingEntity target) {
        return target != null && target.isAlive() && target != player;
    }

    public static int currentCost(ServerPlayer player, CardDefinition definition, ItemStack stack) {
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

    public static float dealAttackDamage(ServerPlayer player, @Nullable LivingEntity target, float baseDamage) {
        return dealAttackDamage(player, target, baseDamage, 1, true);
    }

    public static float dealAttackDamage(ServerPlayer player, @Nullable LivingEntity target, float baseDamage, int strengthMultiplier, boolean useStrength) {
        if (target == null) {
            return 0.0F;
        }

        CombatState state = CombatStateAccess.get(player);
        CombatantStatus playerStatus = CombatantStatusAccess.get(player);
        CombatantStatus targetStatus = CombatantStatusAccess.get(target);
        float damage = computeAttackDamage(baseDamage, state.getStrength(), playerStatus.weak(), targetStatus.vulnerable(), strengthMultiplier, useStrength);
        float before = target.getHealth();
        target.hurt(player.damageSources().playerAttack(player), damage);
        return Math.max(0.0F, before - target.getHealth());
    }

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

    public static boolean shouldEndCombatWithoutTarget(int targetEntityId, int enemyCount) {
        return targetEntityId < 0 && enemyCount <= 0;
    }

    public static float dealAllEnemies(ServerPlayer player, float damage) {
        float total = 0.0F;
        for (LivingEntity enemy : findEnemies(player)) {
            total += dealAttackDamage(player, enemy, damage);
        }
        return total;
    }

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

    public static void applyVulnerable(LivingEntity entity, int turns) {
        CombatantStatusAccess.get(entity).addVulnerable(turns);
    }

    public static void applyVulnerableFromPlayer(ServerPlayer player, LivingEntity entity, int turns) {
        CombatStateAccess.get(player).markAffectedCombatant(entity.getId());
        applyVulnerable(entity, turns);
    }

    public static void applyWeak(LivingEntity entity, int turns) {
        CombatantStatusAccess.get(entity).addWeak(turns);
    }

    public static void applyWeakFromPlayer(ServerPlayer player, LivingEntity entity, int turns) {
        CombatStateAccess.get(player).markAffectedCombatant(entity.getId());
        applyWeak(entity, turns);
    }

    public static void applyWeakToAllEnemies(ServerPlayer player, int turns) {
        for (LivingEntity enemy : findEnemies(player)) {
            CombatStateAccess.get(player).markAffectedCombatant(enemy.getId());
            applyWeak(enemy, turns);
        }
    }

    public static void applyVulnerableToAllEnemies(ServerPlayer player, int turns) {
        for (LivingEntity enemy : findEnemies(player)) {
            CombatStateAccess.get(player).markAffectedCombatant(enemy.getId());
            applyVulnerable(enemy, turns);
        }
    }

    public static void modifyTargetStrength(LivingEntity entity, int amount) {
        CombatantStatusAccess.get(entity).addStrength(amount);
    }

    public static void modifyTargetStrengthFromPlayer(ServerPlayer player, LivingEntity entity, int amount) {
        CombatStateAccess.get(player).markAffectedCombatant(entity.getId());
        modifyTargetStrength(entity, amount);
    }

    public static boolean enemyHasVulnerable(LivingEntity entity) {
        return CombatantStatusAccess.get(entity).vulnerable() > 0;
    }

    public static void gainEnergy(ServerPlayer player, int amount) {
        CombatStateAccess.get(player).gainEnergy(amount);
    }

    public static void gainBlock(ServerPlayer player, int amount) {
        gainBlock(player, amount, true);
    }

    public static void gainBlock(ServerPlayer player, int amount, boolean triggerJuggernaut) {
        CombatState state = CombatStateAccess.get(player);
        state.addBlock(amount);
        if (triggerJuggernaut && state.getJuggernautDamage() > 0) {
            LivingEntity target = randomEnemy(player, player.getRandom());
            if (target != null) {
                dealAttackDamage(player, target, state.getJuggernautDamage(), 1, false);
            }
        }
    }

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

    public static void drawTemporaryCards(ServerPlayer player, int count) {
        CombatState state = CombatStateAccess.get(player);
        if (count <= 0 || state.isDrawLocked()) {
            return;
        }

        for (int i = 0; i < count; i++) {
            CardDefinition definition = randomOwnedDefinition(player, card -> !card.isStatus());
            if (definition == null) {
                definition = CardDefinitions.STRIKE;
            }
            ItemStack stack = createGeneratedCard(definition.id(), true, null);
            addStack(player, stack);
        }
    }

    public static void createPersistentGeneratedCopy(ServerPlayer player, ResourceLocation id) {
        addStack(player, createGeneratedCard(id, false, null));
    }

    public static void createTemporaryGeneratedCopy(ServerPlayer player, ResourceLocation id, @Nullable Integer costOverride) {
        addStack(player, createGeneratedCard(id, true, costOverride));
    }

    public static CardDefinition randomOwnedAttack(ServerPlayer player) {
        return randomOwnedDefinition(player, CardDefinition::isAttack);
    }

    public static void reduceRandomCardCostThisTurn(ServerPlayer player, int amount) {
        List<ItemStack> candidates = availableCardStacks(player, stack -> definitionFromStack(stack).isPlayable());
        if (candidates.isEmpty()) {
            return;
        }

        ItemStack chosen = candidates.get(player.getRandom().nextInt(candidates.size()));
        CardDefinition definition = definitionFromStack(chosen);
        int nextCost = Math.max(0, currentCost(player, definition, chosen) - amount);
        CardStacks.setCostOverride(chosen, nextCost);
    }

    public static void addStatusCard(ServerPlayer player, ResourceLocation id, boolean expiresEndTurn) {
        addStack(player, createGeneratedCard(id, expiresEndTurn, null));
        CombatState state = CombatStateAccess.get(player);
        if (state.getFireBreathingDamage() > 0) {
            dealAllEnemies(player, state.getFireBreathingDamage());
        }
        if (state.getEvolveDraw() > 0 && !state.isDrawLocked()) {
            drawTemporaryCards(player, state.getEvolveDraw());
        }
    }

    public static void exhaustStack(ServerPlayer player, ItemStack stack) {
        CardStacks.setUsedThisCombat(stack, true);
        CardStacks.setUsedThisTurn(stack, true);
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

    public static int exhaustAllMatching(ServerPlayer player, Predicate<CardDefinition> filter, @Nullable ItemStack exclude) {
        List<ItemStack> candidates = availableCardStacks(player, stack -> stack != exclude && filter.test(definitionFromStack(stack)));
        for (ItemStack stack : candidates) {
            exhaustStack(player, stack);
        }
        return candidates.size();
    }

    public static void exhumeRandomCard(ServerPlayer player) {
        CombatState state = CombatStateAccess.get(player);
        String id = state.popExhaustedCard();
        if (id == null) {
            return;
        }

        ItemStack stack = createGeneratedCard(ResourceLocation.tryParse(id), true, null);
        CardStacks.setGenerated(stack, false);
        CardStacks.setUsedThisCombat(stack, false);
        CardStacks.setUsedThisTurn(stack, false);
        addStack(player, stack);
    }

    public static void copyRandomFromInventory(ServerPlayer player, Predicate<CardDefinition> filter, int amount) {
        List<ItemStack> candidates = availableCardStacks(player, stack -> filter.test(definitionFromStack(stack)));
        for (int i = 0; i < amount && !candidates.isEmpty(); i++) {
            ItemStack source = candidates.get(player.getRandom().nextInt(candidates.size()));
            createPersistentGeneratedCopy(player, definitionFromStack(source).id());
        }
    }

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

    public static void resetUsedThisTurnOnRandomCard(ServerPlayer player) {
        List<ItemStack> candidates = inventoryCardStacks(player, stack -> CardStacks.isUsedThisTurn(stack) && !CardStacks.isUsedThisCombat(stack));
        if (candidates.isEmpty()) {
            return;
        }

        CardStacks.setUsedThisTurn(candidates.get(player.getRandom().nextInt(candidates.size())), false);
    }

    public static boolean allAvailableCardsAreAttacks(ServerPlayer player, ItemStack current) {
        for (ItemStack stack : availableCardStacks(player, stack -> stack != current)) {
            CardDefinition definition = definitionFromStack(stack);
            if (!definition.isAttack()) {
                return false;
            }
        }
        return true;
    }

    public static int countStrikeCards(ServerPlayer player) {
        int total = 0;
        for (ItemStack stack : inventoryCardStacks(player, stack -> definitionFromStack(stack).effectKey().contains("strike"))) {
            total += stack.getCount();
        }
        return total;
    }

    public static int nearbyEnemyCount(ServerPlayer player) {
        return findEnemies(player).size();
    }

    public static boolean enemyIntendsToAttack(ServerPlayer player, @Nullable LivingEntity target) {
        if (!(target instanceof Monster monster)) {
            return false;
        }
        return monster.getTarget() == player;
    }

    public static void increaseMaxHealth(ServerPlayer player, double amount) {
        var attribute = player.getAttribute(Attributes.MAX_HEALTH);
        if (attribute == null) {
            return;
        }
        attribute.setBaseValue(attribute.getBaseValue() + amount);
        player.heal((float) amount);
    }

    public static ItemStack createGeneratedCard(ResourceLocation id, boolean expiresEndTurn, @Nullable Integer costOverride) {
        ItemStack stack = ModItems.createCardStack(id);
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        CardStacks.setGenerated(stack, true);
        CardStacks.setExpiresEndTurn(stack, expiresEndTurn);
        CardStacks.setCostOverride(stack, costOverride);
        return stack;
    }

    public static CardDefinition definitionFromStack(ItemStack stack) {
        if (stack.getItem() instanceof CardItem cardItem) {
            CardDefinition definition = cardItem.definition(stack, null);
            if (definition != null) {
                return definition;
            }
        }
        throw new IllegalArgumentException("Item stack is not a registered Slay the Spire card: " + stack);
    }

    private static void processEndTurn(ServerPlayer player) {
        Inventory inventory = player.getInventory();
        for (ItemStack stack : inventory.items) {
            if (!(stack.getItem() instanceof CardItem cardItem)) {
                continue;
            }

            CardDefinition definition = cardItem.definition(stack, player.level());
            if (definition == null) {
                continue;
            }
            if (definition.ethereal() && !CardStacks.isUsedThisTurn(stack) && !CardStacks.isUsedThisCombat(stack)) {
                exhaustStack(player, stack);
            }
        }

        CombatState state = CombatStateAccess.get(player);
        if (state.getCombustDamage() > 0) {
            player.hurt(player.damageSources().generic(), 1.0F);
            dealAllEnemies(player, state.getCombustDamage());
        }
    }

    private static void applyStartTurnPowers(ServerPlayer player) {
        CombatState state = CombatStateAccess.get(player);
        if (state.isBrutality()) {
            player.hurt(player.damageSources().generic(), 1.0F);
            drawTemporaryCards(player, 1);
        }
    }

    private static void onTurnAdvanceStatuses(ServerPlayer player) {
        CombatantStatusAccess.get(player).onTurnAdvance();
        for (LivingEntity enemy : findEnemies(player)) {
            CombatantStatusAccess.get(enemy).onTurnAdvance();
        }
    }

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

    private static void clearTurnFlags(Inventory inventory) {
        for (ItemStack stack : inventory.items) {
            CardStacks.clearTurnFlags(stack);
        }
    }

    private static void clearCombatFlags(Inventory inventory) {
        for (ItemStack stack : inventory.items) {
            CardStacks.clearCombatFlags(stack);
        }
    }

    private static void removeGeneratedCards(Inventory inventory, boolean endTurnOnly) {
        for (ItemStack stack : inventory.items) {
            if (!(stack.getItem() instanceof CardItem)) {
                continue;
            }
            if (!CardStacks.isGenerated(stack)) {
                continue;
            }
            if (endTurnOnly && !CardStacks.expiresEndTurn(stack)) {
                continue;
            }
            stack.shrink(stack.getCount());
        }
    }

    private static void addStack(ServerPlayer player, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
        player.getInventory().setChanged();
    }

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

    private static List<ItemStack> availableCardStacks(ServerPlayer player, Predicate<ItemStack> filter) {
        return inventoryCardStacks(player, stack -> !CardStacks.isUsedThisCombat(stack) && !CardStacks.isUsedThisTurn(stack) && filter.test(stack));
    }

    private static List<LivingEntity> findEnemies(ServerPlayer player) {
        return player.serverLevel()
                .getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(CombatRules.ENEMY_SEARCH_RADIUS),
                        entity -> entity != player && entity.isAlive() && !entity.isAlliedTo(player))
                .stream()
                .sorted(Comparator.comparingDouble(entity -> entity.distanceToSqr(player)))
                .toList();
    }

    private static LivingEntity randomEnemy(ServerPlayer player, RandomSource random) {
        List<LivingEntity> enemies = findEnemies(player);
        return enemies.isEmpty() ? null : enemies.get(random.nextInt(enemies.size()));
    }

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

    private static LivingEntity asLiving(@Nullable net.minecraft.world.entity.Entity entity) {
        return entity instanceof LivingEntity living ? living : null;
    }

    public static void refreshCardPreviews(ServerPlayer player) {
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
