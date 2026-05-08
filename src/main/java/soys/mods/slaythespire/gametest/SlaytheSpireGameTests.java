package soys.mods.slaythespire.gametest;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import soys.mods.slaythespire.Slaythespire;
import soys.mods.slaythespire.card.CardDefinitions;
import soys.mods.slaythespire.combat.CombatRules;
import soys.mods.slaythespire.combat.CombatState;
import soys.mods.slaythespire.registry.ModItems;

@GameTestHolder(Slaythespire.MODID)
@PrefixGameTestTemplate(false)
public final class SlaytheSpireGameTests {
    private SlaytheSpireGameTests() {
    }

    @GameTest(template = "combat_baseline")
    public static void enterCombatCreatesUniqueTruthSource(GameTestHelper helper) {
        CombatState state = new CombatState();
        state.beginCombat(20L, -1);

        helper.succeedIf(() -> {
            assertTrue(state.isInCombat(), "Combat state should be active after beginCombat");
            assertTrue(state.getTurn() == 1, "Combat should begin on turn 1");
            assertTrue(state.getEnergy() == CombatRules.MAX_ENERGY, "Combat should begin with max energy");
            assertTrue(state.getBlock() == 0, "Combat should begin with zero block");
        });
    }

    @GameTest(template = "combat_baseline")
    public static void exitCombatClearsEncounterLocalState(GameTestHelper helper) {
        CombatState state = new CombatState();
        state.beginCombat(40L, 23);
        state.addBlock(9);
        state.addStrength(2);
        state.tryConsumeEnergy(2);
        state.clearEncounterState();

        helper.succeedIf(() -> {
            assertTrue(!state.isInCombat(), "Combat should be inactive after clearEncounterState");
            assertTrue(state.getEnergy() == 0, "Energy should reset when encounter ends");
            assertTrue(state.getBlock() == 0, "Block should reset when encounter ends");
            assertTrue(state.getStrength() == 0, "Strength should reset when encounter ends");
            assertTrue(state.getTurn() == 0, "Turn should reset when encounter ends");
            assertTrue(state.getTargetEntityId() == -1, "Target should reset when encounter ends");
        });
    }

    @GameTest(template = "combat_baseline")
    public static void insufficientEnergyFailsCleanly(GameTestHelper helper) {
        CombatState state = new CombatState();
        state.beginCombat(60L, -1);

        boolean firstUse = state.tryConsumeEnergy(CombatRules.MAX_ENERGY);
        boolean secondUse = state.tryConsumeEnergy(1);

        helper.succeedIf(() -> {
            assertTrue(firstUse, "Exact energy spend should succeed");
            assertTrue(!secondUse, "Overspending energy should fail");
            assertTrue(state.getEnergy() == 0, "Failed energy spend should not make energy negative");
        });
    }

    @GameTest(template = "combat_baseline")
    public static void itemAdapterStartsWithoutCombatTruth(GameTestHelper helper) {
        ItemStack stack = ModItems.createCardStack(CardDefinitions.STRIKE.id());

        helper.succeedIf(() -> {
            assertTrue(!stack.hasTag(), "Fresh card item should not store combat truth in NBT");
            assertTrue(stack.getItem() instanceof soys.mods.slaythespire.card.CardItem, "Card item should resolve to the registered adapter item");
        });
    }

    @GameTest(template = "combat_baseline")
    public static void cardRegistryContainsRedPortraitCards(GameTestHelper helper) {
        helper.succeedIf(() -> {
            assertTrue(CardDefinitions.all().size() == 16, "All available red 1024 portrait card definitions should be registered");
            assertTrue(CardDefinitions.all().contains(CardDefinitions.STRIKE), "Strike definition should be registered");
            assertTrue(CardDefinitions.all().contains(CardDefinitions.DEFEND), "Defend definition should be registered");
            assertTrue(CardDefinitions.all().contains(CardDefinitions.BARRICADE), "Barricade definition should be registered");
            assertTrue(CardDefinitions.all().contains(CardDefinitions.RUPTURE), "Rupture definition should be registered");
        });
    }

    @GameTest(template = "combat_baseline")
    public static void cardStacksResolveForRedPortraitDefinitions(GameTestHelper helper) {
        ResourceLocation removedId = ResourceLocation.fromNamespaceAndPath(Slaythespire.MODID, "bash_card");
        ItemStack strike = ModItems.createCardStack(CardDefinitions.STRIKE.id());
        ItemStack defend = ModItems.createCardStack(CardDefinitions.DEFEND.id());
        ItemStack juggernaut = ModItems.createCardStack(CardDefinitions.JUGGERNAUT.id());
        ItemStack removed = ModItems.createCardStack(removedId);

        helper.succeedIf(() -> {
            assertTrue(!strike.isEmpty(), "Strike stack should be registered");
            assertTrue(!defend.isEmpty(), "Defend stack should be registered");
            assertTrue(!juggernaut.isEmpty(), "Power stacks should be registered");
            assertTrue(removed.isEmpty(), "Cards without current 1024 portrait assets should not create stacks");
        });
    }

    @GameTest(template = "combat_baseline")
    public static void nextTurnRefreshesEnergyAndClearsBlock(GameTestHelper helper) {
        CombatState state = new CombatState();
        state.beginCombat(80L, -1);
        state.tryConsumeEnergy(2);
        state.addBlock(7);
        state.startNextTurn(140L);

        helper.succeedIf(() -> {
            assertTrue(state.getTurn() == 2, "Next turn should increment the turn counter");
            assertTrue(state.getEnergy() == CombatRules.MAX_ENERGY, "Next turn should refresh energy");
            assertTrue(state.getBlock() == 0, "Next turn should clear block for the MVP ruleset");
        });
    }

    @GameTest(template = "combat_baseline")
    public static void vulnerableDamageIsAppliedExactlyOnce(GameTestHelper helper) {
        float damage = soys.mods.slaythespire.combat.CombatService.computeAttackDamage(10.0F, 2, 0, 1, 1, true);

        helper.succeedIf(() -> assertTrue(damage == 18.0F, "Vulnerable should apply a single 1.5x multiplier to modified attack damage"));
    }

    @GameTest(template = "combat_baseline")
    public static void combatWithoutTargetEndsWhenNoEnemiesRemain(GameTestHelper helper) {
        helper.succeedIf(() -> assertTrue(
                soys.mods.slaythespire.combat.CombatService.shouldEndCombatWithoutTarget(-1, 0),
                "Combat without a locked target should end immediately when no enemies remain"
        ));
    }

    @GameTest(template = "combat_baseline")
    public static void clearingEncounterStateDropsTrackedCombatants(GameTestHelper helper) {
        CombatState state = new CombatState();
        state.beginCombat(100L, 9);
        state.markAffectedCombatant(12);
        state.markAffectedCombatant(18);
        state.clearEncounterState();

        helper.succeedIf(() -> assertTrue(
                state.affectedCombatantIds().isEmpty(),
                "Encounter cleanup should clear tracked combatant ids so enemy debuffs cannot leak across fights"
        ));
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new GameTestAssertException(message);
        }
    }
}
