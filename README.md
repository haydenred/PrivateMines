# PrivateMines

A Spigot plugin that gives each player their own private mine with configurable blocks, upgradeable tiers, and schematic support.

## Features

- **Personal mines** — each player gets their own protected mine area
- **Upgradeable tiers** — define multiple mine types (e.g. starter → god → donor) with different block compositions
- **Schematic support** — load mine structures from WorldEdit schematics
- **Auto-reset** — mines automatically refill on a configurable timer
- **GUI menu** — interactive inventory menu with configurable items, requirements, and actions
- **PlaceholderAPI support** — use placeholders in messages and menu conditions
- **Multi-version** — supports both legacy and modern WorldEdit/WorldGuard versions

## Dependencies

- [Spigot](https://www.spigotmc.org/) 1.13+
- [WorldEdit](https://enginehub.org/worldedit)
- [WorldGuard](https://enginehub.org/worldguard)
- [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)

## Installation

1. Build with Maven: `mvn clean package`
2. Copy `target/PrivateMines-1.0.0-SNAPSHOT.jar` to your server's `plugins/` folder
3. Place your mine schematics in `plugins/PrivateMines/schematics/`
4. Restart the server

## Schematic Marker Blocks

When the plugin pastes a schematic, it scans for two special marker blocks to determine the mine region and spawn point. These are configurable in `config.yml`:

```yaml
settings:
  mines:
    blocks:
      mine-region: DIAMOND_BLOCK
      spawn-point: GOLD_BLOCK
```

- **`DIAMOND_BLOCK`** — Place **two** of these in your schematic to define opposite corners of the mine region. The plugin uses these to create a WorldGuard region that gets filled with the tier's block composition. Both markers are replaced with air after detection.
- **`GOLD_BLOCK`** — Place **one** of these in your schematic to set the player spawn point. This is where players teleport to when visiting their mine. The marker is replaced with air after detection.

When building a custom schematic, place the two diamond blocks at the corners of the area you want mined, and the gold block where players should spawn.

## Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/privatemines` | Opens the mine GUI | — |
| `/privatemines create` | Create your mine | `privatemines.create` |
| `/privatemines teleport` | Teleport to your mine | `privatemines.teleport` |
| `/privatemines reset` | Reset your mine blocks | `privatemines.reset` |
| `/privatemines upgrade` | Upgrade to the next tier | `privatemines.upgrade` |
| `/privatemines delete` | Delete your mine | `privatemines.delete` |

Admin commands require the `privatemines.admin` permission (op by default).

## Configuration

Mine tiers are defined in `config.yml` with a priority, block filling percentages, and a schematic:

```yaml
mine_types:
  test:
    priority: 1
    filling:
      - GOLD_BLOCK:10
      - EMERALD_BLOCK:40
      - DIAMOND_BLOCK:50
    schematic: "schematics/test.schem"
```

Other settings include mine spacing (`mine-distance`), reset delay, and WorldGuard region flags for both the mine and structure regions. See `config.yml` for the full list.

## Project Structure & Build

PrivateMines uses a **Maven multi-module** setup to support multiple Minecraft server versions from a single codebase. The parent POM defines four modules:

```
PrivateMines/
├── main/      # Core plugin logic (Java 8) — commands, GUI, mine management, storage
├── modern/    # WorldEdit 7.2.9 / WorldGuard 7.1.0 implementations (Java 17)
├── legacy/    # WorldEdit 6.1.4 / WorldGuard 6.2 + FAWE implementations (Java 8)
└── dist/      # Distribution module — shades all modules into a single JAR
```

The `main` module contains the plugin's core code and defines provider interfaces (`WorldEditProvider`, `WorldGuardProvider`, `WorldEditRegionProvider`) for WorldEdit and WorldGuard operations. It has no direct dependency on either library — instead, the `modern` and `legacy` modules each implement those interfaces against their respective API versions.

At runtime, the plugin detects which version of WorldEdit/WorldGuard is installed on the server and loads the appropriate implementation. This means the same JAR works on both older servers (1.8.8+) and modern ones (1.19+).

The `dist` module uses the Maven Shade plugin to combine `main`, `modern`, and `legacy` into a single distributable JAR (`target/PrivateMines-1.0.0-SNAPSHOT.jar`). The `main` module also shades and relocates [XSeries](https://github.com/CryptoMorin/XSeries) for cross-version material compatibility.

To build:

```bash
mvn clean package
```

## License

All rights reserved. 

## Notes
Most programming was not AI assisted as the project was made in 2022 and abandoned, project was recently used to test claude by implementing new UI actions, readme generated with help from AI.
