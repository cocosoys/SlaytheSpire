package soys.mods.slaythespire.combat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class CombatState {
    private boolean inCombat;
    private int energy;
    private int maxEnergy = CombatRules.MAX_ENERGY;
    private int block;
    private int strength;
    private int turn;
    private int targetEntityId = -1;
    private long nextTurnGameTime;
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
    private final Map<String, Integer> rampageBonus = new HashMap<>();
    private final List<String> exhaustedCards = new ArrayList<>();
    private final Set<Integer> affectedCombatantIds = new HashSet<>();

    public boolean isInCombat() {
        return inCombat;
    }

    public int getEnergy() {
        return energy;
    }

    public int getMaxEnergy() {
        return maxEnergy;
    }

    public int getBlock() {
        return block;
    }

    public int getStrength() {
        return strength;
    }

    public int getTurn() {
        return turn;
    }

    public int getTargetEntityId() {
        return targetEntityId;
    }

    public long getNextTurnGameTime() {
        return nextTurnGameTime;
    }

    public long getLastCombatGameTime() {
        return lastCombatGameTime;
    }

    public int getRageBlockPerAttack() {
        return rageBlockPerAttack;
    }

    public int getMetallicizeBlock() {
        return metallicizeBlock;
    }

    public boolean isBarricade() {
        return barricade;
    }

    public int getDemonFormStrength() {
        return demonFormStrength;
    }

    public int getCombustDamage() {
        return combustDamage;
    }

    public int getFlameBarrierDamage() {
        return flameBarrierDamage;
    }

    public int getJuggernautDamage() {
        return juggernautDamage;
    }

    public int getBerserkEnergy() {
        return berserkEnergy;
    }

    public boolean isBrutality() {
        return brutality;
    }

    public int getFeelNoPainBlock() {
        return feelNoPainBlock;
    }

    public int getDarkEmbraceDraw() {
        return darkEmbraceDraw;
    }

    public int getEvolveDraw() {
        return evolveDraw;
    }

    public int getFireBreathingDamage() {
        return fireBreathingDamage;
    }

    public boolean isCorruption() {
        return corruption;
    }

    public int getDoubleTapCharges() {
        return doubleTapCharges;
    }

    public boolean isDrawLocked() {
        return drawLocked;
    }

    public int getRuptureStrength() {
        return ruptureStrength;
    }

    public int getHpLossCount() {
        return hpLossCount;
    }

    public List<String> exhaustedCards() {
        return exhaustedCards;
    }

    public Set<Integer> affectedCombatantIds() {
        return affectedCombatantIds;
    }

    public void beginCombat(long gameTime, int targetEntityId) {
        inCombat = true;
        energy = maxEnergy;
        block = 0;
        strength = 0;
        turn = 1;
        this.targetEntityId = targetEntityId;
        lastCombatGameTime = gameTime;
        nextTurnGameTime = gameTime + CombatRules.TURN_LENGTH_TICKS;
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

    public void markCardUse(long gameTime, int targetEntityId) {
        lastCombatGameTime = gameTime;
        if (targetEntityId >= 0) {
            this.targetEntityId = targetEntityId;
        }
    }

    public boolean tryConsumeEnergy(int cost) {
        if (cost < 0) {
            return true;
        }
        if (energy < cost) {
            return false;
        }

        energy -= cost;
        return true;
    }

    public void gainEnergy(int amount) {
        energy = Math.max(0, energy + amount);
    }

    public void spendAllEnergy() {
        energy = 0;
    }

    public void startNextTurn(long gameTime) {
        if (!inCombat) {
            return;
        }

        turn++;
        energy = maxEnergy + berserkEnergy;
        if (!barricade) {
            block = 0;
        }
        if (metallicizeBlock > 0) {
            block += metallicizeBlock;
        }
        if (temporaryStrengthLoss > 0) {
            strength -= temporaryStrengthLoss;
            temporaryStrengthLoss = 0;
        }
        if (demonFormStrength > 0) {
            strength += demonFormStrength;
        }
        rageBlockPerAttack = 0;
        flameBarrierDamage = 0;
        drawLocked = false;
        nextTurnGameTime = gameTime + CombatRules.TURN_LENGTH_TICKS;
        lastCombatGameTime = gameTime;
    }

    public void addBlock(int amount) {
        block += Math.max(0, amount);
    }

    public int absorbDamage(float incomingDamage) {
        int absorbed = Math.min(block, (int) Math.ceil(incomingDamage));
        block -= absorbed;
        return absorbed;
    }

    public void addStrength(int amount) {
        strength += amount;
    }

    public void addTemporaryStrength(int amount) {
        addStrength(amount);
        temporaryStrengthLoss += Math.max(0, amount);
    }

    public void addRageBlockPerAttack(int amount) {
        rageBlockPerAttack += Math.max(0, amount);
    }

    public void addMetallicize(int amount) {
        metallicizeBlock += Math.max(0, amount);
    }

    public void setBarricade(boolean value) {
        barricade = value;
    }

    public void addDemonFormStrength(int amount) {
        demonFormStrength += Math.max(0, amount);
    }

    public void addCombustDamage(int amount) {
        combustDamage += Math.max(0, amount);
    }

    public void addFlameBarrierDamage(int amount) {
        flameBarrierDamage += Math.max(0, amount);
    }

    public void addJuggernautDamage(int amount) {
        juggernautDamage += Math.max(0, amount);
    }

    public void addBerserkEnergy(int amount) {
        berserkEnergy += Math.max(0, amount);
    }

    public void setBrutality(boolean value) {
        brutality = value;
    }

    public void addFeelNoPainBlock(int amount) {
        feelNoPainBlock += Math.max(0, amount);
    }

    public void addDarkEmbraceDraw(int amount) {
        darkEmbraceDraw += Math.max(0, amount);
    }

    public void addEvolveDraw(int amount) {
        evolveDraw += Math.max(0, amount);
    }

    public void addFireBreathingDamage(int amount) {
        fireBreathingDamage += Math.max(0, amount);
    }

    public void setCorruption(boolean value) {
        corruption = value;
    }

    public void addDoubleTapCharges(int amount) {
        doubleTapCharges += Math.max(0, amount);
    }

    public void consumeDoubleTapCharge() {
        if (doubleTapCharges > 0) {
            doubleTapCharges--;
        }
    }

    public void setDrawLocked(boolean value) {
        drawLocked = value;
    }

    public void addRuptureStrength(int amount) {
        ruptureStrength += Math.max(0, amount);
    }

    public void recordHpLoss() {
        hpLossCount++;
    }

    public int getRampageBonus(String cardId) {
        return rampageBonus.getOrDefault(cardId, 0);
    }

    public void addRampageBonus(String cardId, int amount) {
        rampageBonus.merge(cardId, amount, Integer::sum);
    }

    public void addExhaustedCard(String cardId) {
        exhaustedCards.add(cardId);
    }

    public void markAffectedCombatant(int entityId) {
        if (entityId >= 0) {
            affectedCombatantIds.add(entityId);
        }
    }

    public String popExhaustedCard() {
        return exhaustedCards.isEmpty() ? null : exhaustedCards.remove(exhaustedCards.size() - 1);
    }

    public void clearEncounterState() {
        inCombat = false;
        energy = 0;
        block = 0;
        strength = 0;
        turn = 0;
        targetEntityId = -1;
        nextTurnGameTime = 0L;
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
