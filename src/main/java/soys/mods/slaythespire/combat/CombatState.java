package soys.mods.slaythespire.combat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 中文：玩家侧战斗状态 Capability 数据。这里保存能量、格挡、力量、能力牌持续效果和本场战斗临时标记。
 * English: Player-side combat-state Capability data. It stores energy, block, strength, active power effects, and per-combat temporary flags.
 */
public final class CombatState {
    // 中文：基础战斗字段会同步到客户端 HUD；其余字段主要服务端用于卡牌结算。
    // English: Basic combat fields are synced to the client HUD; the remaining fields mainly drive server-side card settlement.
    private boolean inCombat;
    private int energy;
    private int maxEnergy = CombatRules.MAX_ENERGY;
    private int block;
    private int strength;
    private int targetEntityId = -1;
    private long lastCombatGameTime;
    private int temporaryStrengthLoss;
    private int rageBlockPerAttack;
    private int metallicizeBlock;
    private boolean barricade;
    private int demonFormStrength;
    private int combustDamage;
    private int flameBarrierDamage;
    private int juggernautDamage;
    private int berserkEnergy;
    private boolean brutality;
    private int feelNoPainBlock;
    private int darkEmbraceDraw;
    private int evolveDraw;
    private int fireBreathingDamage;
    private boolean corruption;
    private int doubleTapCharges;
    private boolean drawLocked;
    private int ruptureStrength;
    private int hpLossCount;
    // 中文：Rampage 类叠加值按卡牌 id 保存，避免不同实例之间丢失成长。
    // English: Rampage-style stacking values are stored by card id so growth survives across different item instances.
    private final Map<String, Integer> rampageBonus = new HashMap<>();
    // 中文：记录本场被消耗的卡牌，供 Exhume 类效果从消耗堆中取回。
    // English: Cards exhausted this combat are recorded so Exhume-style effects can return one from the exhaust pile.
    private final List<String> exhaustedCards = new ArrayList<>();
    // 中文：记录被本玩家影响过的实体，战斗结束时清理它们身上的易伤/虚弱等状态。
    // English: Entities affected by this player are tracked so vulnerable, weak, and similar statuses can be cleared when combat ends.
    private final Set<Integer> affectedCombatantIds = new HashSet<>();

    // 中文：返回玩家当前是否处于卡牌战斗中。
    // English: Returns whether the player is currently in card combat.
    public boolean isInCombat() {
        return inCombat;
    }

    // 中文：返回当前可用能量。
    // English: Returns the currently available energy.
    public int getEnergy() {
        return energy;
    }

    // 中文：返回战斗开始时恢复到的最大能量。
    // English: Returns the maximum energy restored at combat start.
    public int getMaxEnergy() {
        return maxEnergy;
    }

    // 中文：返回当前格挡值。
    // English: Returns the current block value.
    public int getBlock() {
        return block;
    }

    // 中文：返回当前力量值。
    // English: Returns the current strength value.
    public int getStrength() {
        return strength;
    }

    // 中文：返回最近一次战斗目标实体 id。
    // English: Returns the most recent combat target entity id.
    public int getTargetEntityId() {
        return targetEntityId;
    }

    // 中文：返回最近一次卡牌战斗交互的游戏时间。
    // English: Returns the game time of the most recent card-combat interaction.
    public long getLastCombatGameTime() {
        return lastCombatGameTime;
    }

    // 中文：返回每次攻击触发的愤怒格挡量。
    // English: Returns the Rage block amount triggered per attack.
    public int getRageBlockPerAttack() {
        return rageBlockPerAttack;
    }

    // 中文：返回金属化每回合提供的格挡量。
    // English: Returns the block amount granted by Metallicize each turn.
    public int getMetallicizeBlock() {
        return metallicizeBlock;
    }

    // 中文：返回壁垒效果是否已生效。
    // English: Returns whether Barricade is active.
    public boolean isBarricade() {
        return barricade;
    }

    // 中文：返回恶魔形态每回合提供的力量。
    // English: Returns the strength granted each turn by Demon Form.
    public int getDemonFormStrength() {
        return demonFormStrength;
    }

    // 中文：返回燃烧每回合造成的群体伤害。
    // English: Returns the area damage dealt each turn by Combust.
    public int getCombustDamage() {
        return combustDamage;
    }

    // 中文：返回火焰屏障反击伤害值。
    // English: Returns the retaliatory damage from Flame Barrier.
    public int getFlameBarrierDamage() {
        return flameBarrierDamage;
    }

    // 中文：返回获得格挡时触发的硬撑伤害值。
    // English: Returns the Juggernaut damage triggered when gaining block.
    public int getJuggernautDamage() {
        return juggernautDamage;
    }

    // 中文：返回狂暴提供的额外能量值。
    // English: Returns the extra energy granted by Berserk.
    public int getBerserkEnergy() {
        return berserkEnergy;
    }

    // 中文：返回残暴效果是否已生效。
    // English: Returns whether Brutality is active.
    public boolean isBrutality() {
        return brutality;
    }

    // 中文：返回无惧疼痛在消耗卡牌时提供的格挡量。
    // English: Returns the block granted by Feel No Pain when exhausting cards.
    public int getFeelNoPainBlock() {
        return feelNoPainBlock;
    }

    // 中文：返回黑暗之拥在消耗卡牌时提供的抽牌量。
    // English: Returns the draw amount granted by Dark Embrace when exhausting cards.
    public int getDarkEmbraceDraw() {
        return darkEmbraceDraw;
    }

    // 中文：返回进化在状态牌进入手牌时提供的抽牌量。
    // English: Returns the draw amount granted by Evolve when status cards enter hand.
    public int getEvolveDraw() {
        return evolveDraw;
    }

    // 中文：返回火焰吐息触发时造成的群体伤害。
    // English: Returns the area damage dealt when Fire Breathing triggers.
    public int getFireBreathingDamage() {
        return fireBreathingDamage;
    }

    // 中文：返回腐化效果是否已生效。
    // English: Returns whether Corruption is active.
    public boolean isCorruption() {
        return corruption;
    }

    // 中文：返回双发剩余触发次数。
    // English: Returns the remaining Double Tap charges.
    public int getDoubleTapCharges() {
        return doubleTapCharges;
    }

    // 中文：返回抽牌是否被锁定。
    // English: Returns whether drawing cards is locked.
    public boolean isDrawLocked() {
        return drawLocked;
    }

    // 中文：返回破裂在失去生命时提供的力量值。
    // English: Returns the strength granted by Rupture when HP is lost.
    public int getRuptureStrength() {
        return ruptureStrength;
    }

    // 中文：返回本场战斗记录的生命损失次数。
    // English: Returns the number of HP-loss events recorded this combat.
    public int getHpLossCount() {
        return hpLossCount;
    }

    // 中文：返回本场战斗被消耗卡牌列表。
    // English: Returns the list of cards exhausted this combat.
    public List<String> exhaustedCards() {
        return exhaustedCards;
    }

    // 中文：返回本玩家影响过的战斗实体 id 集合。
    // English: Returns the set of combatant entity ids affected by this player.
    public Set<Integer> affectedCombatantIds() {
        return affectedCombatantIds;
    }

    // 中文：进入新战斗并初始化所有单场战斗状态。
    // English: Enters a new combat and initializes all per-encounter state.
    public void beginCombat(long gameTime, int targetEntityId) {
        // 中文：进入新战斗时重置所有单场状态，但 maxEnergy 保留，便于未来遗物或效果扩展最大能量。
        // English: Starting combat resets all per-combat state but keeps maxEnergy so future relics or effects can extend maximum energy.
        inCombat = true;
        energy = maxEnergy;
        block = 0;
        strength = 0;
        this.targetEntityId = targetEntityId;
        lastCombatGameTime = gameTime;
        temporaryStrengthLoss = 0;
        rageBlockPerAttack = 0;
        metallicizeBlock = 0;
        barricade = false;
        demonFormStrength = 0;
        combustDamage = 0;
        flameBarrierDamage = 0;
        juggernautDamage = 0;
        berserkEnergy = 0;
        brutality = false;
        feelNoPainBlock = 0;
        darkEmbraceDraw = 0;
        evolveDraw = 0;
        fireBreathingDamage = 0;
        corruption = false;
        doubleTapCharges = 0;
        drawLocked = false;
        ruptureStrength = 0;
        hpLossCount = 0;
        rampageBonus.clear();
        exhaustedCards.clear();
    }

    // 中文：记录一次卡牌使用的时间和可选目标。
    // English: Records the time and optional target of one card use.
    public void markCardUse(long gameTime, int targetEntityId) {
        lastCombatGameTime = gameTime;
        if (targetEntityId >= 0) {
            this.targetEntityId = targetEntityId;
        }
    }

    // 中文：尝试扣除卡牌费用，能量不足时返回 false。
    // English: Attempts to consume card cost and returns false when energy is insufficient.
    public boolean tryConsumeEnergy(int cost) {
        // 中文：负费用代表无需扣费的内部调用，X 费会在 CombatService 中单独处理。
        // English: Negative cost means an internal free use; X-cost cards are handled separately in CombatService.
        if (cost < 0) {
            return true;
        }
        if (energy < cost) {
            return false;
        }

        energy -= cost;
        return true;
    }

    // 中文：增加当前能量，最低保持为 0。
    // English: Adds current energy while keeping it at least zero.
    public void gainEnergy(int amount) {
        energy = Math.max(0, energy + amount);
    }

    // 中文：消耗当前全部能量。
    // English: Spends all current energy.
    public void spendAllEnergy() {
        energy = 0;
    }

    // 中文：增加当前格挡值。
    // English: Adds to the current block value.
    public void addBlock(int amount) {
        block += Math.max(0, amount);
    }

    // 中文：用格挡吸收即将受到的伤害，并返回吸收量。
    // English: Absorbs incoming damage with block and returns the absorbed amount.
    public int absorbDamage(float incomingDamage) {
        // 中文：格挡按向上取整吸收 Minecraft 浮点伤害，避免 0.5 伤害绕过 1 点格挡。
        // English: Block absorbs Minecraft float damage using ceiling so 0.5 damage cannot bypass 1 block.
        int absorbed = Math.min(block, (int) Math.ceil(incomingDamage));
        block -= absorbed;
        return absorbed;
    }

    // 中文：修改当前力量值。
    // English: Modifies the current strength value.
    public void addStrength(int amount) {
        strength += amount;
    }

    // 中文：增加临时力量并记录之后需要回收的数量。
    // English: Adds temporary strength and records the amount to reclaim later.
    public void addTemporaryStrength(int amount) {
        addStrength(amount);
        temporaryStrengthLoss += Math.max(0, amount);
    }

    // 中文：增加每次攻击触发的愤怒格挡量。
    // English: Adds the Rage block amount triggered per attack.
    public void addRageBlockPerAttack(int amount) {
        rageBlockPerAttack += Math.max(0, amount);
    }

    // 中文：增加金属化每回合格挡量。
    // English: Adds the per-turn block amount from Metallicize.
    public void addMetallicize(int amount) {
        metallicizeBlock += Math.max(0, amount);
    }

    // 中文：设置壁垒效果是否生效。
    // English: Sets whether Barricade is active.
    public void setBarricade(boolean value) {
        barricade = value;
    }

    // 中文：增加恶魔形态每回合力量值。
    // English: Adds the per-turn strength value from Demon Form.
    public void addDemonFormStrength(int amount) {
        demonFormStrength += Math.max(0, amount);
    }

    // 中文：增加燃烧每回合群体伤害值。
    // English: Adds the per-turn area damage from Combust.
    public void addCombustDamage(int amount) {
        combustDamage += Math.max(0, amount);
    }

    // 中文：增加火焰屏障反击伤害值。
    // English: Adds retaliatory damage from Flame Barrier.
    public void addFlameBarrierDamage(int amount) {
        flameBarrierDamage += Math.max(0, amount);
    }

    // 中文：增加硬撑触发伤害值。
    // English: Adds Juggernaut trigger damage.
    public void addJuggernautDamage(int amount) {
        juggernautDamage += Math.max(0, amount);
    }

    // 中文：增加狂暴提供的额外能量。
    // English: Adds the extra energy granted by Berserk.
    public void addBerserkEnergy(int amount) {
        berserkEnergy += Math.max(0, amount);
    }

    // 中文：设置残暴效果是否生效。
    // English: Sets whether Brutality is active.
    public void setBrutality(boolean value) {
        brutality = value;
    }

    // 中文：增加无惧疼痛消耗触发格挡量。
    // English: Adds the Feel No Pain block amount triggered by exhausting cards.
    public void addFeelNoPainBlock(int amount) {
        feelNoPainBlock += Math.max(0, amount);
    }

    // 中文：增加黑暗之拥消耗触发抽牌量。
    // English: Adds the Dark Embrace draw amount triggered by exhausting cards.
    public void addDarkEmbraceDraw(int amount) {
        darkEmbraceDraw += Math.max(0, amount);
    }

    // 中文：增加进化触发抽牌量。
    // English: Adds the draw amount triggered by Evolve.
    public void addEvolveDraw(int amount) {
        evolveDraw += Math.max(0, amount);
    }

    // 中文：增加火焰吐息触发伤害值。
    // English: Adds Fire Breathing trigger damage.
    public void addFireBreathingDamage(int amount) {
        fireBreathingDamage += Math.max(0, amount);
    }

    // 中文：设置腐化效果是否生效。
    // English: Sets whether Corruption is active.
    public void setCorruption(boolean value) {
        corruption = value;
    }

    // 中文：增加双发可触发次数。
    // English: Adds Double Tap charges.
    public void addDoubleTapCharges(int amount) {
        doubleTapCharges += Math.max(0, amount);
    }

    // 中文：消耗一次双发触发次数。
    // English: Consumes one Double Tap charge.
    public void consumeDoubleTapCharge() {
        if (doubleTapCharges > 0) {
            doubleTapCharges--;
        }
    }

    // 中文：设置抽牌锁定状态。
    // English: Sets the draw-lock state.
    public void setDrawLocked(boolean value) {
        drawLocked = value;
    }

    // 中文：增加破裂触发力量值。
    // English: Adds Rupture trigger strength.
    public void addRuptureStrength(int amount) {
        ruptureStrength += Math.max(0, amount);
    }

    // 中文：记录一次由卡牌或效果造成的生命损失。
    // English: Records one HP-loss event caused by a card or effect.
    public void recordHpLoss() {
        hpLossCount++;
    }

    // 中文：读取指定卡牌的狂宴式成长加成。
    // English: Reads the Rampage-style growth bonus for a specific card.
    public int getRampageBonus(String cardId) {
        return rampageBonus.getOrDefault(cardId, 0);
    }

    // 中文：累加指定卡牌的狂宴式成长加成。
    // English: Adds to the Rampage-style growth bonus for a specific card.
    public void addRampageBonus(String cardId, int amount) {
        rampageBonus.merge(cardId, amount, Integer::sum);
    }

    // 中文：记录一张本场战斗被消耗的卡牌。
    // English: Records one card exhausted during this combat.
    public void addExhaustedCard(String cardId) {
        exhaustedCards.add(cardId);
    }

    // 中文：记录一个被本玩家战斗效果影响过的实体。
    // English: Records an entity affected by this player's combat effects.
    public void markAffectedCombatant(int entityId) {
        if (entityId >= 0) {
            affectedCombatantIds.add(entityId);
        }
    }

    // 中文：从消耗堆取出最近记录的一张卡牌 id。
    // English: Pops the most recently recorded card id from the exhaust pile.
    public String popExhaustedCard() {
        return exhaustedCards.isEmpty() ? null : exhaustedCards.remove(exhaustedCards.size() - 1);
    }

    // 中文：清空当前战斗的全部临时状态。
    // English: Clears all temporary state for the current encounter.
    public void clearEncounterState() {
        // 中文：退出战斗时彻底清空单场状态，防止能力牌效果、临时强度、目标 id 泄漏到下一场战斗。
        // English: Exiting combat clears all per-encounter state so power effects, temporary strength, and target id do not leak into the next fight.
        inCombat = false;
        energy = 0;
        block = 0;
        strength = 0;
        targetEntityId = -1;
        lastCombatGameTime = 0L;
        temporaryStrengthLoss = 0;
        rageBlockPerAttack = 0;
        metallicizeBlock = 0;
        barricade = false;
        demonFormStrength = 0;
        combustDamage = 0;
        flameBarrierDamage = 0;
        juggernautDamage = 0;
        berserkEnergy = 0;
        brutality = false;
        feelNoPainBlock = 0;
        darkEmbraceDraw = 0;
        evolveDraw = 0;
        fireBreathingDamage = 0;
        corruption = false;
        doubleTapCharges = 0;
        drawLocked = false;
        ruptureStrength = 0;
        hpLossCount = 0;
        rampageBonus.clear();
        exhaustedCards.clear();
        affectedCombatantIds.clear();
    }
}
