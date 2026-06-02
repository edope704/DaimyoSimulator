# DaimyoSimulator

A rule-based simulation game set in an ancient Japanese village. You build huts,
farms and other structures, manage villagers and resources, and watch the village
grow tick by tick.

This guide assumes **no prior knowledge of Java, Maven, or programming**. If you can
copy-paste commands into a terminal and follow steps in order, you can run both the
automated tests and the game itself.

---

## 1. What you need to install (one time only)

The project needs exactly two free tools. If you already have them, skip to
[section 2](#2-get-the-project-on-your-computer).

### Tool A — Java 17 (JDK)

Java is the language the game is written in. You need version **17** of the **JDK**
(Java Development Kit — *not* just the "JRE"/runtime).

**Easiest option (all systems): Adoptium Temurin 17**

1. Go to <https://adoptium.net/temurin/releases/?version=17>.
2. Pick your operating system (Windows / macOS / Linux) and download the **JDK 17**
   installer (`.msi` on Windows, `.pkg` on macOS, package for Linux).
3. Run the installer and accept the defaults.
   - **On Windows**, when the installer shows a "Custom Setup" / feature screen,
     make sure **"Set JAVA_HOME variable"** and **"Add to PATH"** are enabled
     (click the drive icon next to them and choose "Will be installed on local hard
     drive"). This saves you from configuring anything by hand.

**Verify it worked.** Open a fresh terminal (see the box below) and run:

```bash
java -version
```

You should see a line containing `17` (for example `openjdk version "17.0.11"`).
If you instead see "command not found" or a version like `1.8` or `11`, Java 17 is
not installed/active yet — re-run the installer and make sure PATH is set, then open
a brand-new terminal.

> **How to open a terminal**
> - **Windows:** press the Start key, type `powershell`, press Enter. (This project
>   was developed on Windows with PowerShell.)
> - **macOS:** press `Cmd+Space`, type `Terminal`, press Enter.
> - **Linux:** open your "Terminal" application.
>
> Always open a **new** terminal window after installing something, otherwise it
> won't see the newly installed tools.

### Tool B — Maven

Maven is the tool that downloads the game's dependencies, compiles the code, runs
the tests, and launches the game. You do **not** need to learn Maven — you only run
a couple of commands.

- **Windows:**
  1. Download the "Binary zip archive" from <https://maven.apache.org/download.cgi>
     (the file named like `apache-maven-3.9.x-bin.zip`).
  2. Unzip it to a simple location, e.g. `C:\maven`.
  3. Add the `bin` folder to your PATH:
     - Start key → type `environment variables` → open **"Edit the system
       environment variables"** → button **"Environment Variables…"**.
     - Under **"User variables"**, select `Path` → **Edit** → **New** → paste
       `C:\maven\apache-maven-3.9.x\bin` (match the folder name you actually
       unzipped) → OK on every window.
  4. Open a **new** PowerShell window.

  *Alternatively*, if you have [Chocolatey](https://chocolatey.org/) or
  [winget](https://learn.microsoft.com/windows/package-manager/): run
  `winget install Apache.Maven` (then open a new terminal).

- **macOS:** install [Homebrew](https://brew.sh/) if you don't have it, then run
  `brew install maven`.

- **Linux (Debian/Ubuntu):** run `sudo apt update && sudo apt install maven`.
  On Fedora: `sudo dnf install maven`.

**Verify it worked.** In a new terminal run:

```bash
mvn -version
```

You should see Maven's version **and** a line mentioning Java `17`. If it mentions a
different Java version, Maven is using the wrong Java — make sure Java 17 is
installed and is the default `java` on your PATH (re-check `java -version`).

---

## 2. Get the project on your computer

If you received this project as a **folder/zip**, just unzip it somewhere simple
(avoid paths with strange characters) and remember where it is.

If you have a link to the Git repository and have [Git](https://git-scm.com/)
installed, you can instead run:

```bash
git clone <repository-url>
```

Either way, open a terminal **inside the project folder** — the folder that contains
this `README.md` and a file called `pom.xml`. To move into a folder use `cd`:

```bash
cd path/to/DaimyoSimulator
```

> On Windows you can also open the folder in File Explorer, then type `powershell`
> in the address bar and press Enter — it opens a terminal already in that folder.

To confirm you are in the right place, list the files; you should see `pom.xml`:

```bash
# Windows PowerShell
dir
# macOS / Linux
ls
```

The very first Maven command you run will **download all dependencies from the
internet**, so make sure you are online. This download happens only once and may take
a few minutes; later runs are fast.

---

## 3. Run the tests

Tests check that the game's rules behave correctly. From the project folder run:

```bash
mvn clean test
```

What to expect:
- The first run prints lots of `Downloading...` lines — that's normal.
- At the end you should see **`BUILD SUCCESS`** and a summary like
  `Tests run: N, Failures: 0, Errors: 0`.
- If you see **`BUILD FAILURE`**, scroll up to the first red/`ERROR` line — that's
  the real cause. The most common reason is the wrong Java version (see
  [Troubleshooting](#5-troubleshooting)).

---

## 4. Run the game

From the project folder, run these **two** commands in order:

```bash
mvn clean package
mvn -pl :desktop exec:java
```

- The first command compiles everything and runs the tests; wait for `BUILD SUCCESS`.
- The second command opens the game window.

> ### macOS users — use this instead
> On macOS the graphics backend must start on the program's first thread, so the
> normal `exec:java` will not work. Use the dedicated command:
>
> ```bash
> mvn clean package
> mvn -pl :desktop exec:exec@run-mac
> ```

To stop the game, simply close its window (or press `Ctrl+C` in the terminal).

> **Why two commands, and why no `-am` on the run command?**
> `mvn clean package` builds the game and installs its internal pieces (`core` and
> `libgdx`) into your local Maven cache, so the `desktop` launcher can run on its
> own. Do **not** add `-am` to the `exec:java` command: it would try to "run" every
> part of the project, including the top-level one that has no program to start, and
> fail with an error about a missing `mainClass`.

### How to play (quick start)

- A brand-new village starts with free **starter buildings**
  (1 Woodcutter's Hut + 2 Dwellings).
- **Left-click** a building button → you enter "build mode"; then **left-click** a
  cell on the grid to place the building.
- **Right-click** or press **`Escape`** → cancel build or demolish mode.
- **Demolish** button (left panel) → click any building to remove it (no refund).
- Click a **Market** cell → "Open Market" → exchange resources (one shared market;
  it scales with how many Markets you own; 10-tick cooldown).
- Move the map with **WASD** or the **arrow keys**; **zoom** with the mouse wheel.
- **F3** toggles a debug grid overlay.
- The **?** button opens the in-game tutorial; the **gear / sound** buttons open
  settings and audio volume.
- Resource numbers turn **yellow** when a stock is ≤ 30 and **red** when ≤ 10.
- Building timber cost **goes up** the more copies of a building you already own
  (it stops increasing from the 5th copy).
- You can place at most **2 buildings per tick**; the limit resets each time the
  village advances a tick.

For the full controls list, balance numbers, and building requirements, see
[docs/COMMANDS.md](docs/COMMANDS.md).

---

## 5. Troubleshooting

| Symptom | What it means / how to fix |
| --- | --- |
| `java` or `mvn` is "not recognized" / "command not found" | The tool isn't on your PATH, or you didn't open a **new** terminal after installing. Re-check [section 1](#1-what-you-need-to-install-one-time-only) and open a fresh terminal. |
| `mvn -version` shows Java 8 / 11 / 21 instead of 17 | Java 17 isn't the active version. Install JDK 17 (Temurin) and make sure it's first on your PATH; on Windows the Temurin installer's "Set JAVA_HOME" + "Add to PATH" options handle this. |
| `BUILD FAILURE` mentioning `release version 17 not supported` | You're compiling with an older JDK. Same fix as above — switch to JDK 17. |
| First build hangs or fails downloading | Check your internet connection / proxy and run the command again; Maven resumes the download. |
| Game command fails with `The parameters 'mainClass' ... are missing or invalid` | You probably added `-am` to the run command, or ran it on the whole project. Use exactly `mvn -pl :desktop exec:java` (or the macOS variant). |
| Game window doesn't appear on macOS | Use `mvn -pl :desktop exec:exec@run-mac`, not `exec:java`. |

---

## Save files

The game has **5 named save slots**. Save files live here:

```text
%USERPROFILE%\.daimyosimulator\savegame_<slot>.json   (Windows)
~/.daimyosimulator/savegame_<slot>.json               (Linux / macOS)
```

`<slot>` is `1`–`5`; slot `1` is the default. The Save/Load dialogs let you pick a
slot and show which slots already contain a save. Each save stores the full village
state: grid, buildings, natural features, villagers, roles, housing, resources,
parameters, tick number, policy state, cooldowns, birth/starvation progress, market
cooldown, builds-this-tick counter, and event history.

---

## Project structure (for the curious / developers)

You don't need this to run the game — it's here for anyone who wants to read or
modify the code. The project is split into three Maven modules:

- `src/core` (packages under `core.*`): pure Java domain logic — simulation
  services, placement rules, policies, persistence (Jackson JSON), DTOs, snapshots,
  and JUnit 5 tests. It must not import `com.badlogic.gdx.*`.
- `src/libgdx` (packages under `gdx.*`): rendering, input, the Scene2D HUD, audio,
  PNG assets, and adapters from snapshots to render/UI models.
- `src/desktop` (package `desktop`): the LWJGL3 desktop launcher only. The program's
  entry point is `desktop.DesktopLauncher`.

Dependency direction:

```text
desktop -> libgdx -> core
```

Module artifact IDs are `core`, `libgdx`, and `desktop` under the
`it.unipd:daimyosimulator` parent (Java 17, libGDX 1.12.1, Jackson 2.17.1,
JUnit 5.10.2).

### Useful developer commands

```bash
mvn clean test            # test every module
mvn clean package         # compile + test + package every module
mvn -pl :core test        # test only the core module
mvn -pl :libgdx -am test  # test libgdx (and core, which it depends on)
```

Use `:core`, `:libgdx`, and `:desktop` as Maven selectors. The old selectors
`daimyosimulator-core` / `-libgdx` / `-desktop` no longer exist.

### Notes

Runtime art is loaded from individual PNG sprites under
`src/libgdx/main/resources/assets/textures/sprites`; `missing_asset.png` is shown
(with a logged warning) for any missing sprite key. `docs/Textures.png` is kept only
as a reference sprite sheet. AI-assisted code must be reviewed by the team before
submission.
