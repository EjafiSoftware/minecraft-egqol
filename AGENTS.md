# AGENTS.md

This file guides coding agents working in this repository.

## Project

**Ejafi Games Quality of Life (EGQoL)** is a NeoForge mod for Minecraft 1.21.1 with the mod ID `egqol`. It adds small conveniences to vanilla play, and registers content, so it is required on both sides. The Minecraft and NeoForge versions are in `gradle.properties`, and the NeoForge API reference for this version is [docs.neoforged.net/docs/1.21.1](https://docs.neoforged.net/docs/1.21.1/).

| Feature | Package |
|---|---|
| String from Wool: wool and shears craft 4 String, and the shears return with 1 more damage | `toolcrafting` |

## Commands

| Command | What it does |
|---|---|
| `./gradlew build` | Spotless check with palantir-java-format, compile under Error Prone with NullAway at error severity, PMD and CPD over every source set, unit tests, and the jar in `build/libs/` |
| `./gradlew runGameTestServer` | Runs every GameTest in the `egqol` namespace and exits |
| `./gradlew runData` | Regenerates `src/generated/resources/` |
| `./gradlew spotlessApply` | Formats every Java source set |
| `./gradlew runClient`, `./gradlew runServer` | Dev runs. Both load the `gameTest` source set and enable the `egqol` GameTest namespace, so `/test` works in them. |

**The gate** is `./gradlew runData` with `git status --porcelain` printing nothing, then `./gradlew build`, then `./gradlew runGameTestServer`, and all of them must pass before work is done. CI (`.github/workflows/build.yml`) runs the same steps through the shared `mod-gate.yml` workflow. CI also runs CodeQL through `mod-codeql.yml` on pushes to `main`, and skips changes that touch only markdown. Dependabot (`.github/dependabot.yml`) opens monthly grouped updates for Gradle and GitHub Actions.

### Build configuration

`build.gradle` applies only the `com.ejafi.neoforge-mod` convention plugin from [EjafiSoftware/minecraft-build](https://github.com/EjafiSoftware/minecraft-build), which owns the source sets, runs, tool and library versions, and the `neoforge.mods.toml` expansion. Its README describes what the plugin configures and the GitHub Packages token Gradle needs to download it. A build change that every mod should get belongs in that repo, and this repo picks it up by moving the plugin version in `build.gradle` and the workflow refs in `build.yml` to the new tag.

The dev runs need JDK 21. `gradle/gradle-daemon-jvm.properties` pins the Gradle daemon to JDK 21, and Gradle downloads one from the URLs in that file when none is installed. Each run uses `runs/<run name>` as its game directory.

### Releases

Pushing a tag that equals `mod_version`, such as `1.1.0`, on a commit in `main` runs the gate, then publishes a GitHub release titled with the version. The release carries `egqol-<version>.jar` and the `## [<version>]` section of `CHANGELOG.md`. A second job then uploads the same jar and notes to CurseForge project 1765812, which the `CURSE_PROJECT_ID` repository variable names, using the `CURSE_API_TOKEN` organization secret. The release job fails without publishing when the tag doesn't match `mod_version`, the commit isn't in `main`, or the changelog has no section for the version.

## Source layout

```
src/main/java/com/ejafi/egqol/
├── Egqol.java                             # @Mod entry point: calls each feature's register(IEventBus)
└── toolcrafting/
    ├── ToolCraftingRegisters.java         # Recipe serializer register: egqol:tool_shapeless
    └── ToolShapelessRecipe.java           # Shapeless crafting recipe that returns its tools damaged; its Serializer wraps the vanilla shapeless codecs

src/datagen/java/com/ejafi/egqol/datagen/
├── DataGatherer.java                      # GatherDataEvent subscriber that registers every provider
└── recipe/
    ├── EgqolRecipeProvider.java           # egqol:crafting/string_from_wool and its unlock advancement
    └── ToolShapelessRecipeOutput.java     # RecipeOutput wrapper that saves a vanilla shapeless recipe as a ToolShapelessRecipe

src/testFixtures/java/com/ejafi/egqol/
├── ItemStackContents.java                 # Item, count, and component patch of an ItemStack, for value assertions
└── toolcrafting/
    ├── IngredientStackFaker.java          # Random stack without durability, optionally one with a crafting remainder
    ├── ToolStackFaker.java                # Random item with durability, with its remaining durability and unbreakable state
    └── ToolShapelessRecipeFaker.java      # ToolShapelessRecipe with generated group, category, and result

src/test/java/com/ejafi/egqol/toolcrafting/
└── ToolShapelessRecipeTest.java

src/gameTest/java/com/ejafi/egqol/
├── GameTestTemplates.java                 # Template name constant EMPTY_7X7X7
└── toolcrafting/
    └── StringFromWoolGameTests.java
```

Datagen packages are named for the data kind each provider produces, and `DataGatherer` at the root is the only class that reaches across them.

## How it works

### Tool crafting

`ToolShapelessRecipe` extends the vanilla `ShapelessRecipe`, so it matches, assembles, and shows in the recipe book as any shapeless crafting recipe does. Its recipe type stays `minecraft:crafting`, and only its serializer, `egqol:tool_shapeless`, differs. The serializer reuses the vanilla shapeless map codec and stream codec, converting through `ToolShapelessRecipe.copyOf`, so the JSON format is the vanilla one.

`getRemainingItems` decides what each grid slot leaves behind.

| Ingredient | Remainder |
|---|---|
| No `minecraft:max_damage` component | Its crafting remainder, such as a Bucket from a Water Bucket, or nothing |
| `minecraft:unbreakable` component | The tool, unchanged |
| One durability left | Nothing, so the tool breaks |
| Otherwise | The tool with 1 more damage |

The recipe hook gets no level or player, so Unbreaking is not rolled and a breaking tool plays no sound. The vanilla Crafter block uses the same remainders.

### String from Wool

`EgqolRecipeProvider` builds the recipe with the vanilla `ShapelessRecipeBuilder` and saves it through `ToolShapelessRecipeOutput`, which converts it to a `ToolShapelessRecipe`. The recipe takes one `#minecraft:wool` and one `#c:tools/shear` and gives 4 String, which reverses the vanilla 4 String to 1 Wool recipe. It unlocks in the recipe book when the player holds any item in `#c:tools/shear`.

### Data generation

Every file a data provider can produce comes from `./gradlew runData`, which writes to `src/generated/resources/`. That folder is committed and is added to the main resources by the convention plugin. Its `.cache` is gitignored and left out of the jar.

The providers live in the `datagen` source set, which sees `main` and which only the data run loads, so the jar never contains them.

### Event buses

| Bus | Subscribers |
|---|---|
| Mod bus, through `@EventBusSubscriber` | `DataGatherer` (`GatherDataEvent`) |

On this NeoForge build, `@EventBusSubscriber` registers each handler on the bus its event belongs to, so `DataGatherer` needs no `bus` argument.

## Testing

**Unit tests** live in `src/test` and run on JUnit 5 through ModDevGradle's `unitTest` support with `net.neoforged:testframework`. Registries load, so tests use real items, but the unit-test runtime binds no data tags. A unit test therefore never checks tag membership, and tag contents are tested in GameTests.

`ItemStack` has no value equality, so tests compare `ItemStackContents.read(stack)` values.

**GameTests** live in `src/gameTest`. Each holder class carries `@GameTestHolder(Egqol.MOD_ID)` and `@PrefixGameTestTemplate(false)`, and each test names its template through `GameTestTemplates.EMPTY_7X7X7`, which resolves to `src/gameTest/resources/data/egqol/structure/empty_7x7x7.nbt`. `StringFromWoolGameTests` looks the recipe up through the server's `RecipeManager`, so it covers the generated JSON, the serializer, and both tags.

**Fixtures** live in the `testFixtures` source set from Gradle's `java-test-fixtures` plugin, shared by the unit tests and the GameTests. The convention plugin binds `main`, `testFixtures`, `gameTest`, and `datagen` to the `egqol` mod, so the dev runs and the unit-test runtime load them as one mod, while the jar contains only `main`.

## Coding standards

Code follows the frie-skills skills `brandon-standards` with its `references/java.md`, `neoforge-modding`, and `engineering-patterns`. Only the repo-specific facts below are recorded here.

- The mod has no `api` package.
- A new feature gets its own package under `com.ejafi.egqol` with a `register(IEventBus)` that `Egqol` calls, and a row in the feature table above.
- Recipe IDs follow `egqol:<recipe type>/<name>`, such as `egqol:crafting/string_from_wool`.

## Resource locations

| Files | Source |
|---|---|
| `data/egqol/recipe/crafting/string_from_wool.json` | `EgqolRecipeProvider` |
| `data/egqol/advancement/recipes/misc/crafting/string_from_wool.json` | `EgqolRecipeProvider` |

Every path in that table is under `src/generated/resources/`.

No provider covers the files below, so they are hand-written.

| File | Path |
|---|---|
| Mod logo, the Ejafi `profile-icons/basic/color.svg` brandmark exported at 1024 by 1024, matching the CurseForge project icon | `src/main/resources/logo.png` |
| Mod metadata template, expanded by `generateModMetadata` | `src/main/templates/META-INF/neoforge.mods.toml` |
| GameTest template | `src/gameTest/resources/data/egqol/structure/empty_7x7x7.nbt` |
