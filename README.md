# SlaytheSpire

**语言：中文 | [English](README_EN.md)**

SlaytheSpire 是一个面向 Minecraft Forge 1.20.1 的卡牌战斗实验性 Mod。它尝试把《杀戮尖塔》式的卡牌使用、费用、格挡和战斗 HUD 带入 Minecraft，并重点提供一套可复用的“纯渲染卡牌”框架，让卡牌不再依赖普通物品材质，而是在 Minecraft 内按底片、费用球、卡图、标题、类型和描述组合渲染。

当前版本是框架验证阶段，围绕当前可用的红色 1024 卡图资源注册卡牌：

- **打击 / Strike**：费用 1，攻击牌，对目标造成 6 点伤害。
- **防御 / Defend**：费用 1，技能牌，获得 5 点格挡。
- **红色能力牌**：注册壁垒、狂暴、残暴、自燃、腐化、黑暗之拥、恶魔形态、进化、无惧疼痛、火焰吐息、激怒、势不可挡、金属化和破裂。

## 主要特性

- Forge 1.20.1 / Java 17 Mod。
- 基于 Java API 的卡牌定义，不再使用外部 JSON 卡牌注册。
- 通用卡牌物品渲染器，按 1024 像素 cardui 与卡图资产组合绘制卡面。
- 支持卡牌名称、类型、费用、描述和物品提示文本的国际化。
- 战斗状态能力系统，记录能量、格挡、力量和目标状态。
- 客户端战斗 HUD，显示 HP、能量、格挡和力量。
- 服务端侧处理卡牌效果与战斗状态同步，客户端代码隔离在 client 包中。

## 玩家使用

1. 在创造模式物品栏中打开 **杀戮尖塔卡牌** 分类。
2. 取出 `打击`、`防御` 或红色能力牌。
3. 对敌对目标使用 `打击`，或直接使用 `防御` 与能力牌。
4. 第一次成功使用卡牌会进入战斗状态。
5. 战斗中会显示 HUD，可查看生命、能量、格挡和力量。

## 开发者扩展方式

本项目当前采用统一的 Java API 卡牌接入方式。新增卡牌时主要修改两个位置：

- `src/main/java/soys/mods/slaythespire/card/CardDefinitions.java`
  - 注册卡牌 ID、稀有度、类型、目标、费用和效果。
- `src/main/java/soys/mods/slaythespire/client/card/SlayCardRenderSpecs.java`
  - 为卡牌绑定底片、上方卡图和费用球材质。

语言文本放在：

- `src/main/resources/assets/slaythespire/lang/zh_cn.json`
- `src/main/resources/assets/slaythespire/lang/en_us.json`

卡牌渲染资产放在：

- `src/main/resources/assets/slaythespire/textures/cardui/`
- `src/main/resources/assets/slaythespire/textures/cards/1024portraits/`

当前设计有意移除了旧的外部 JSON 卡牌、旧动态贴图管理器和旧资源模板，以保证后续卡牌都通过统一的 Java API 与纯渲染框架接入。

## 构建与验证

项目使用 Gradle Wrapper：

```powershell
.\gradlew.bat compileJava
.\gradlew.bat processResources
.\gradlew.bat runGameTestServer
```

开发客户端运行：

```powershell
.\gradlew.bat runClient
```

专用服务端运行：

```powershell
.\gradlew.bat runServer
```

## 项目状态

这是一个早期验证版，目标不是完整复刻《杀戮尖塔》的全部卡池，而是先搭好在 Minecraft 中可扩展、可国际化、可服务端运行的卡牌渲染与战斗框架。当前卡池跟随工作区中已存在的红色 1024 卡图资源，后续新增卡牌应优先复用现有 `CardDefinition` 与 `SlayCardRenderSpec` 结构。

## 许可

本项目使用 `gradle.properties` 中声明的 MIT 许可证。
