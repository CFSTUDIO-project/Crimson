# CRIMSON

<p align="center">
  <img src="src/main/resources/assets/crimson/logo.svg" width="112" alt="Crimson logo">
</p>

<p align="center"><strong>A crimson glass client utility suite for Minecraft Java 1.21.11.</strong><br>
Fabric client mod · Java 21 · MIT License</p>

## Features

- Animated click GUI with six categories and Right Shift open/close key.
- Crimson, ruby, violet and ice accent palette foundations; configurable dark panel themes.
- HUD modules: coordinates, FPS, facing direction, health, hunger, biome, attack cooldown, sprint state, world clock, local clock and keystrokes.
- Hold C to zoom; configurable Fullbright module.
- Click sounds and local save/load/reset configuration.
- Client-side only. No server plugin, packet manipulation or automated combat.

## Install

1. Install Minecraft Java 1.21.11.
2. Install Fabric Loader 0.18.x and Fabric API for 1.21.11.
3. Download the `crimson-1.0.0.jar` artifact from the repository's **Actions → Build Crimson** workflow.
4. Put the JAR in your Minecraft `mods` folder and launch the Fabric profile.

## Controls

| Key | Action |
|---|---|
| Right Shift | Open / close Crimson |
| C (hold) | Zoom, when enabled |
| Escape | Close the GUI |

## Build locally

Requires JDK 21 and Gradle 9.2.1:

```bash
gradle --no-daemon clean build
```

The remapped JAR is written to `build/libs/`.

## Configuration

Settings are stored in `.minecraft/config/crimson.properties`. This project keeps visual and HUD utilities client-side. Any future world-scanning feature must remain disabled on multiplayer unless the server owner explicitly permits it and the server's rules allow it.

## Project status

This is an initial implementation. The GitHub Actions build must pass and the mod must be tested in a real 1.21.11 instance before a release can be called verified.

## License

MIT. See [LICENSE](LICENSE).
