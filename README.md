# Ejafi Games Quality of Life (EGQoL)

EGQoL is a NeoForge mod for Minecraft 1.21.1 that adds small conveniences to vanilla play. It must be installed on both the server and the client.

## Player Guide

### String from Wool

Put any wool and a pair of shears in a crafting grid to get 4 String. The shears stay in the grid and lose 1 durability, so a full stack of wool converts with one shift-click. Shears break when their last point of durability is used, and Unbreaking doesn't reduce the cost. Any shears in the `c:tools/shear` tag work, including modded ones.

A datapack can change the yield by overriding `data/egqol/recipe/crafting/string_from_wool.json`.

## Development

[AGENTS.md](AGENTS.md) covers the build, the tests, and the source layout. EGQoL uses Mojang's official mapping names with Parchment parameter names, under the [Mojang License](https://github.com/NeoForged/NeoForm/blob/main/Mojang.md).
