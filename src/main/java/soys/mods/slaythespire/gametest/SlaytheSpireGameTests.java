package soys.mods.slaythespire.gametest;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.registries.RegistryObject;
import soys.mods.slaythespire.Slaythespire;
import soys.mods.slaythespire.card.CardDefinitions;
import soys.mods.slaythespire.card.CardItem;
import soys.mods.slaythespire.collectible.CollectibleDefinition;
import soys.mods.slaythespire.collectible.CollectibleDefinitions;
import soys.mods.slaythespire.collectible.CollectibleItem;
import soys.mods.slaythespire.combat.CombatRules;
import soys.mods.slaythespire.combat.CombatState;
import soys.mods.slaythespire.equipment.IroncladArmorItem;
import soys.mods.slaythespire.equipment.IroncladArmorMaterial;
import soys.mods.slaythespire.equipment.IroncladSet;
import soys.mods.slaythespire.registry.ModItems;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * 中文：核心规则 GameTest。这里覆盖战斗状态、注册表、卡牌物品适配、收藏品隔离与外观套装零数值约束。
 * English: Core rule GameTests. These cover combat state, registries, card adapters, collectible isolation, and zero-stat appearance equipment.
 */
@GameTestHolder(Slaythespire.MODID)
@PrefixGameTestTemplate(false)
public final class SlaytheSpireGameTests {
    // 中文：禁止实例化 GameTest 集合类。
    // English: Prevents instantiation of this GameTest container.
    private SlaytheSpireGameTests() {
    }

    @GameTest(template = "combat_baseline")
    // 中文：验证进入战斗会创建唯一权威的 CombatState。
    // English: Verifies that entering combat creates the single authoritative CombatState.
    public static void enterCombatCreatesUniqueTruthSource(GameTestHelper helper) {
        CombatState state = new CombatState();
        state.beginCombat(20L, -1);

        helper.succeedIf(() -> {
            assertTrue(state.isInCombat(), "Combat state should be active after beginCombat");
            assertTrue(state.getEnergy() == CombatRules.MAX_ENERGY, "Combat should begin with max energy");
            assertTrue(state.getBlock() == 0, "Combat should begin with zero block");
        });
    }

    @GameTest(template = "combat_baseline")
    // 中文：验证退出战斗会清理单场战斗状态。
    // English: Verifies that exiting combat clears encounter-local state.
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
            assertTrue(state.getTargetEntityId() == -1, "Target should reset when encounter ends");
        });
    }

    @GameTest(template = "combat_baseline")
    // 中文：验证能量不足时扣费失败且不会变成负数。
    // English: Verifies that insufficient energy fails cleanly without going negative.
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
    // 中文：验证新卡牌物品不会在 NBT 中保存战斗真相。
    // English: Verifies that a fresh card item does not store combat truth in NBT.
    public static void itemAdapterStartsWithoutCombatTruth(GameTestHelper helper) {
        // 中文：新卡牌物品不应把战斗事实写进 NBT，战斗真相必须来自玩家 CombatState。
        // English: Fresh card items should not store combat truth in NBT; authoritative combat truth belongs to player CombatState.
        ItemStack stack = ModItems.createCardStack(CardDefinitions.STRIKE.id());

        helper.succeedIf(() -> {
            assertTrue(!stack.hasTag(), "Fresh card item should not store combat truth in NBT");
            assertTrue(stack.getItem() instanceof CardItem, "Card item should resolve to the registered adapter item");
        });
    }

    @GameTest(template = "combat_baseline")
    // 中文：验证当前红色 1024 卡牌定义全部注册。
    // English: Verifies that the current red 1024 card definitions are registered.
    public static void cardRegistryContainsRedPortraitCards(GameTestHelper helper) {
        // 中文：当前只保留已迁移且有 1024 portrait 资源的红色牌数量。
        // English: This count covers only migrated red cards with 1024 portrait assets.
        helper.succeedIf(() -> {
            assertTrue(CardDefinitions.all().size() == 16, "All available red 1024 portrait card definitions should be registered");
            assertTrue(CardDefinitions.all().contains(CardDefinitions.STRIKE), "Strike definition should be registered");
            assertTrue(CardDefinitions.all().contains(CardDefinitions.DEFEND), "Defend definition should be registered");
            assertTrue(CardDefinitions.all().contains(CardDefinitions.BARRICADE), "Barricade definition should be registered");
            assertTrue(CardDefinitions.all().contains(CardDefinitions.RUPTURE), "Rupture definition should be registered");
        });
    }

    @GameTest(template = "combat_baseline")
    // 中文：验证卡牌物品栈只会为已迁移 1024 资源的定义创建。
    // English: Verifies that card stacks are created only for definitions migrated to 1024 assets.
    public static void cardStacksResolveForRedPortraitDefinitions(GameTestHelper helper) {
        // 中文：没有迁移到当前 1024 资源集的旧卡牌必须返回空栈，避免创造模式出现缺图卡。
        // English: Old cards not migrated to the current 1024 asset set must return empty stacks, preventing missing-art cards in creative mode.
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
    // 中文：验证易伤伤害倍率只应用一次。
    // English: Verifies that the vulnerable damage multiplier is applied exactly once.
    public static void vulnerableDamageIsAppliedExactlyOnce(GameTestHelper helper) {
        float damage = soys.mods.slaythespire.combat.CombatService.computeAttackDamage(10.0F, 2, 0, 1, 1, true);

        helper.succeedIf(() -> assertTrue(damage == 18.0F, "Vulnerable should apply a single 1.5x multiplier to modified attack damage"));
    }

    @GameTest(template = "combat_baseline")
    // 中文：验证无锁定目标且无敌人时战斗会结束。
    // English: Verifies that combat ends when there is no locked target and no enemies remain.
    public static void combatWithoutTargetEndsWhenNoEnemiesRemain(GameTestHelper helper) {
        helper.succeedIf(() -> assertTrue(
                soys.mods.slaythespire.combat.CombatService.shouldEndCombatWithoutTarget(-1, 0),
                "Combat without a locked target should end immediately when no enemies remain"
        ));
    }

    @GameTest(template = "combat_baseline")
    // 中文：验证清理战斗会移除受影响实体追踪。
    // English: Verifies that combat cleanup removes tracked affected combatants.
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

    @GameTest(template = "combat_baseline")
    // 中文：验证遗物、药水和卡牌注册数量不会因为批量收藏品生成而漂移。
    // English: Verifies that relic, potion, and card counts do not drift after batch collectible generation.
    public static void collectionRegistryCountsMatchManifests(GameTestHelper helper) {
        helper.succeedIf(() -> {
            assertTrue(CollectibleDefinitions.RELICS.size() == 232, "Relic manifest should contain exactly 232 direct relic png entries");
            assertTrue(CollectibleDefinitions.POTIONS.size() == 88, "Potion manifest should contain exactly 88 recursive potion png entries");
            assertTrue(count(ModItems.relicCollectibles()) == 232, "Registered relic collectible count should match the relic manifest");
            assertTrue(count(ModItems.potionCollectibles()) == 88, "Registered potion collectible count should match the potion manifest");
            assertTrue(CardDefinitions.all().size() == 16, "Card definition count should remain at the migrated red-card baseline");
        });
    }

    @GameTest(template = "combat_baseline")
    // 中文：验证收藏品不复用 CardItem，也不会进入卡牌 BEWLR 渲染路径。
    // English: Verifies that collectibles do not reuse CardItem and do not enter the card BEWLR render path.
    public static void collectibleItemsAreNotCards(GameTestHelper helper) {
        helper.succeedIf(() -> {
            for (RegistryObject<Item> item : ModItems.collectibles()) {
                Item registered = item.get();
                assertTrue(registered instanceof CollectibleItem, "Collectible registry entries should use CollectibleItem");
                assertTrue(!(registered instanceof CardItem), "Collectibles must not be CardItem instances");
            }
        });
    }

    @GameTest(template = "combat_baseline")
    // 中文：验证每个收藏品都有模型、贴图与语言键，并且中文语言文件不使用 Unicode 转义。
    // English: Verifies that every collectible has model, texture, and language keys, and that Chinese language text is not Unicode-escaped.
    public static void collectibleResourcesAreComplete(GameTestHelper helper) {
        String zh = resourceText("assets/slaythespire/lang/zh_cn.json");
        String en = resourceText("assets/slaythespire/lang/en_us.json");

        helper.succeedIf(() -> {
            assertTrue(!zh.contains("\\u"), "Chinese language file should use direct Simplified Chinese instead of Unicode escapes");
            for (CollectibleDefinition definition : CollectibleDefinitions.all()) {
                String itemPath = definition.id().getPath();
                assertTrue(resourceExists("assets/slaythespire/models/item/" + itemPath + ".json"), "Missing model for " + itemPath);
                assertTrue(resourceExists("assets/slaythespire/textures/" + definition.texturePath().getPath() + ".png"), "Missing texture for " + itemPath);
                assertTrue(zh.contains("\"item.slaythespire." + itemPath + "\""), "Missing zh_cn key for " + itemPath);
                assertTrue(en.contains("\"item.slaythespire." + itemPath + "\""), "Missing en_us key for " + itemPath);
            }
        });
    }

    @GameTest(template = "combat_baseline")
    // 中文：验证铁甲战士装备材质与物品属性保持零战斗收益。
    // English: Verifies that the Ironclad equipment material and item attributes remain zero-benefit for combat.
    public static void ironcladEquipmentHasZeroCombatStats(GameTestHelper helper) {
        helper.succeedIf(() -> {
            for (ArmorItem.Type type : ArmorItem.Type.values()) {
                assertTrue(IroncladArmorMaterial.INSTANCE.getDefenseForType(type) == 0, "Ironclad material defense should be zero for " + type);
            }
            assertTrue(IroncladArmorMaterial.INSTANCE.getToughness() == 0.0F, "Ironclad material toughness should be zero");
            assertTrue(IroncladArmorMaterial.INSTANCE.getKnockbackResistance() == 0.0F, "Ironclad material knockback resistance should be zero");
            assertTrue(IroncladArmorMaterial.INSTANCE.getEnchantmentValue() == 0, "Ironclad material enchantment value should be zero");
            assertTrue(count(ModItems.ironcladEquipment()) == 4, "Ironclad set should register four equipment pieces");
            for (RegistryObject<Item> item : ModItems.ironcladEquipment()) {
                assertTrue(item.get() instanceof IroncladArmorItem, "Ironclad equipment should use IroncladArmorItem");
                ItemStack stack = new ItemStack(item.get());
                assertTrue(!item.get().isEnchantable(stack), "Ironclad equipment should not be enchantable");
                for (EquipmentSlot slot : EquipmentSlot.values()) {
                    assertTrue(item.get().getDefaultAttributeModifiers(slot).isEmpty(), "Ironclad equipment should not add attribute modifiers");
                }
            }
        });
    }

    @GameTest(template = "combat_baseline")
    // 中文：验证四件套检测必须匹配正确装备槽，避免单件或错槽触发完整外观。
    // English: Verifies that four-piece detection requires the correct equipment slots, preventing single or mismatched pieces from activating the full appearance.
    public static void ironcladPieceMatchingUsesCorrectSlots(GameTestHelper helper) {
        helper.succeedIf(() -> {
            assertTrue(IroncladSet.isIroncladPiece(new ItemStack(ModItems.IRONCLAD_HELMET.get()), ArmorItem.Type.HELMET), "Helmet should match helmet slot");
            assertTrue(!IroncladSet.isIroncladPiece(new ItemStack(ModItems.IRONCLAD_HELMET.get()), ArmorItem.Type.CHESTPLATE), "Helmet should not match chest slot");
            assertTrue(IroncladSet.isIroncladPiece(new ItemStack(ModItems.IRONCLAD_CHESTPLATE.get()), ArmorItem.Type.CHESTPLATE), "Chestplate should match chest slot");
            assertTrue(IroncladSet.isIroncladPiece(new ItemStack(ModItems.IRONCLAD_LEGGINGS.get()), ArmorItem.Type.LEGGINGS), "Leggings should match legs slot");
            assertTrue(IroncladSet.isIroncladPiece(new ItemStack(ModItems.IRONCLAD_BOOTS.get()), ArmorItem.Type.BOOTS), "Boots should match feet slot");
        });
    }

    // 中文：GameTest 断言辅助方法。
    // English: Helper assertion method for GameTests.
    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new GameTestAssertException(message);
        }
    }

    private static int count(Iterable<?> iterable) {
        int count = 0;
        for (Object ignored : iterable) {
            count++;
        }
        return count;
    }

    private static boolean resourceExists(String path) {
        return SlaytheSpireGameTests.class.getClassLoader().getResource(path) != null;
    }

    private static String resourceText(String path) {
        try (InputStream stream = SlaytheSpireGameTests.class.getClassLoader().getResourceAsStream(path)) {
            if (stream == null) {
                throw new GameTestAssertException("Missing resource " + path);
            }
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new GameTestAssertException("Failed reading resource " + path + ": " + exception.getMessage());
        }
    }
}
