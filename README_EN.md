# SlaytheSpire

**Language: [中文](README.md) | English**

SlaytheSpire is an experimental Minecraft Forge 1.20.1 mod that brings Slay the Spire inspired card combat into Minecraft. The current focus is a reusable fully rendered card framework: cards are no longer represented by ordinary item icons, but are composed in Minecraft from a card base, cost orb, card art, title, type banner, and description text.

The current version is a framework validation slice built around the available red 1024 portrait assets:

- **Strike**: costs 1, attack card, deals 6 damage to the target.
- **Defend**: costs 1, skill card, grants 5 Block.
- **Red Powers**: registers Barricade, Berserk, Brutality, Combust, Corruption, Dark Embrace, Demon Form, Evolve, Feel No Pain, Fire Breathing, Inflame, Juggernaut, Metallicize, and Rupture.

## Features

- Forge 1.20.1 / Java 17 mod.
- Java API based card definitions. External JSON card registration has been removed.
- Shared card item renderer using 1024 px card UI and portrait assets.
- Internationalized card names, card types, costs, descriptions, and item tooltips.
- Combat capability state for energy, block, strength, turn, and target state.
- Client combat HUD showing HP, energy, block, strength, and turn.
- End-turn support through a key binding or HUD button.
- Server-side card effect handling and combat state synchronization, with client-only code isolated under the client package.

## Player Usage

1. Open the **Slay the Spire Cards** creative tab.
2. Take `Strike`, `Defend`, or one of the red Power cards.
3. Use `Strike` on a hostile target, or use `Defend` and Power cards directly.
4. The first successful card use starts combat.
5. During combat, the HUD shows HP, energy, block, strength, and turn.
6. Use the configured key binding or click the HUD end-turn button to advance the turn.

## Developer Extension Points

The project currently uses one unified Java API path for cards. To add a new card, the main extension points are:

- `src/main/java/soys/mods/slaythespire/card/CardDefinitions.java`
  - Register the card ID, rarity, type, target, cost, and effect.
- `src/main/java/soys/mods/slaythespire/client/card/SlayCardRenderSpecs.java`
  - Bind the card base texture, upper card art, and cost orb texture.

Language text lives in:

- `src/main/resources/assets/slaythespire/lang/zh_cn.json`
- `src/main/resources/assets/slaythespire/lang/en_us.json`

Card rendering assets live in:

- `src/main/resources/assets/slaythespire/textures/cardui/`
- `src/main/resources/assets/slaythespire/textures/cards/1024portraits/`

The older external JSON card system, dynamic texture manager, and resource templates were intentionally removed so that future cards use the same Java API and rendered-card framework.

## Build and Verification

Use the Gradle Wrapper:

```powershell
.\gradlew.bat compileJava
.\gradlew.bat processResources
.\gradlew.bat runGameTestServer
```

Run a development client:

```powershell
.\gradlew.bat runClient
```

Run a dedicated server:

```powershell
.\gradlew.bat runServer
```

## Project Status

This is an early validation build. The goal is not to ship a complete Slay the Spire card pool yet, but to establish a reusable, internationalized, server-compatible card rendering and combat framework inside Minecraft. The current card pool follows the red 1024 portrait assets that are present in the workspace; future cards should reuse the existing `CardDefinition` and `SlayCardRenderSpec` structure.

## License

The project uses the MIT license declared in `gradle.properties`.
