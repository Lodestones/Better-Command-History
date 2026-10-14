<div align="center">
<br />
<img alt="Better Command History" width="200" src="src/main/resources/assets/bettercommandhistory/icon.png" />
<h1>Better Command History</h1>
<b>
  <i>A QoL mod for chat history. Type the start of a command and press Up to jump straight back to it, skipping everything you ran since.</i>
</b>
<h4>Supports 1.21.11, 26.1, 26.2 and 26.3</h4>

<a href="https://lode.gg/mod/better-command-history"><img alt="Download" src="https://img.shields.io/badge/download-lode.gg-ffffff?style=for-the-badge" /></a>
<a href="https://discord.com/invite/X8KP2XMdqu"><img alt="Discord" src="https://img.shields.io/badge/discord-join-5865F2?style=for-the-badge&logo=discord&logoColor=white" /></a>
<a href="LICENSE"><img alt="MIT License" src="https://img.shields.io/badge/license-MIT-blue?style=for-the-badge" /></a>
</div>

<br />

## The problem

Vanilla's Up arrow walks back through everything you sent, one entry at a time. Run `/tp Apollo30 KatelynYT`, then `/heal` and `/feed`, and getting back to that teleport means pressing Up three times. The longer your session, the worse it gets.

## The fix

Type the start of what you want, then press Up.

- `/tp` then Up cycles through only your `/tp` commands, newest first.
- `/tp Ap` then Up jumps straight to `/tp Apollo30 KatelynYT`. The more you type, the narrower it gets.
- Down steps back toward newer matches, then restores what you typed.
- A command you ran many times shows up once, at its most recent position.
- With an empty chat box, Up walks your whole history, minus the repeats.

Matching ignores case, so `/TP ap` finds the same commands.

## Saved history

Commands are remembered across restarts. Each one is forgotten after 7 days without use, and the history keeps your 50 most recent commands. It builds on the history Minecraft already saves, so nothing you had before is lost, and removing the mod keeps it.

With [Mod Menu](https://modrinth.com/mod/modmenu) installed, the config button lets you:

- **Save history**: turn saving off to keep commands for the current session only. Turning it off deletes the saved history.
- **Forget after**: 1 day up to 90 days, or never.
- **Commands kept**: 10 to 100.
- **Clear history**: forget everything right now.

Settings live in `config/bettercommandhistory.json`.

## Details

- **Client-side only.** Works on any server.
- **No dependencies.** Does not require Fabric API. Mod Menu is optional.
- **Tab still works.** When your text matches history, Up and Down browse history even while command suggestions are showing; Tab still cycles the suggestions.

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 1.21.11, 26.1, 26.2 or 26.3.
2. Download the jar for your Minecraft version from [lode.gg](https://lode.gg/mod/better-command-history).
3. Put it in your `mods` folder.

## Building

Requires Java 25.

```sh
./gradlew build
```

Jars for every supported version are written to `build/libs/`.

## License

Open source under the [MIT License](LICENSE). Issues and pull requests are welcome.
