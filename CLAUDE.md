# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

MoM IME (Master of Magic - Implode's Multiplayer Edition) is a Java multiplayer reimplementation of the classic strategy game Master of Magic. It uses a client-server architecture with TCP/IP networking and JAXB-based XML messaging. Originally ported from Delphi. Licensed under GPL.

## Build System

Maven 3.x multi-module project targeting **Java 25**. Source encoding is **CP1252** (not UTF-8).

### Build Order (dependencies must be installed first)

The workspace contains multiple independent Maven projects that must be built in dependency order. Each is a separate git submodule/directory under the workspace root:

```bash
# 1. Parent POM (all others depend on this)
cd parent && mvn install && cd ..

# 2. Foundation libraries (no inter-dependencies, can build in any order)
cd utils && mvn install && cd ..
cd map && mvn install && cd ..
cd audio && mvn install && cd ..

# 3. Multiplayer stack (multi-module, builds in correct order internally)
cd multiplayer && mvn install && cd ..

# 4. XML editor framework
cd xmleditor && mvn install && cd ..

# 5. Main game (multi-module: Common, Server, Client, Mods, Utils, Editors, Assembly)
cd momime && mvn install && cd ..
```

To skip tests during build: `mvn install -DskipTests`

### Running a Single Test

```bash
cd momime/Server   # or Client, Common, etc.
mvn test -Dtest=ClassName
mvn test -Dtest=ClassName#methodName
```

### Running the Game

After building, use `run.ps1` from the workspace root. It starts the server, waits 15 seconds, then starts the client. Both run with `-Xmx2048m`. The assembled distribution is in `momime/Assembly/target/momime-<version>/`.

Entry points:
- Server: `momime.server.MomServerKickOff` (arg: `server`)
- Client: `momime.client.MomClientKickOff` (arg: `client`)

## Architecture

### Module Map

| Directory | Artifact | Purpose |
|-----------|----------|---------|
| `parent/` | parent (0.8.0) | Common parent POM for all projects |
| `utils/` | ndgutils (0.8.0) | Swing utilities, layout managers, image caching |
| `map/` | ndgmap (0.8.0) | Map coordinate systems and traversal |
| `audio/` | ndgaudio (0.8.0) | MP3 audio playback (JLayer) |
| `multiplayer/` | ndgmultiplayer (0.8.0) | TCP/IP networking, sessions, user registry |
| `xmleditor/` | xmleditor (0.8.0) | XSD-driven generic XML editor framework |
| `momime/Common` | momime-common | Shared DB schemas, JAXB-generated classes, game calculations |
| `momime/Server` | momime-server | Game server: AI, map generation, fog of war, turn processing |
| `momime/Client` | momime-client | Game client: Swing UI, graphics, audio, localization |
| `momime/Utils` | momime-utils | LBX and .ndgarc file format handlers |
| `momime/Editors` | momime-editors | XML data editors for game databases |
| `momime/Mods` | momime-mods | Example game modifications |
| `momime/Assembly` | momime-assembly | Packages everything into runnable distribution |
| `momime/ClientResourceConversion` | momime-client-resourceConversion | Legacy asset conversion tools (LBX, DFM, upscaling) |

### Key Technical Decisions

- **Spring XML DI** (not annotations): All wiring in `momime-*-beans.xml` files (~2500 lines for client). Beans are mostly singletons instantiated eagerly at startup.
- **JAXB code generation**: XSD schemas generate Java classes for all databases and network messages. Schemas live alongside resources; generated sources appear in `target/`.
- **Single-threaded session model**: Server runs one thread per game session; all messages queue through that thread. Client is also single-threaded (message processing interleaved with screen redraws). No thread-safety concerns in game logic.
- **Swing UI with fixed-pixel layouts**: Uses custom `XmlLayoutManager` for pixel-positioned components on bitmap backgrounds, plus `GridBagLayout` for flexible areas.
- **Image pipeline**: Supports PNG, LBX (original MoM format), and custom NDGBMP. Images are cached lazily via `ImageCache`. Optional 2x upscaled variants use `-UP` suffix.

### Graphics System

- `ZOrderGraphics` provides deferred rendering with z-order sorting
- `OverlandMapBitmapGeneratorImpl` composites terrain tiles (20x18 px each, 40x36 upscaled) into full map bitmaps
- `ModifiedImageCacheImpl` handles image transformations (doubling, recoloring) with caching
- Graphics database defined in XML (`Graphics.momime.xml`), validated against XSD

### Network Protocol

JAXB-serialized XML messages over TCP/IP. Message types defined in XSD schemas under Common. ~60 client-to-server + ~90 server-to-client message types. `ServiceLocatorFactoryBean` provides message factories.

## Testing

- **JUnit 5** (Jupiter 6.0.2) with **Mockito** (5.21.0)
- Tests in `src/test/java` and `src/test/resources` per module
- Run all tests: `mvn test` from any module directory
