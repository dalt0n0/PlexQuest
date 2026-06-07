# PlexQuest

**Open-source Plex client for Meta Quest — as a flat 2D panel, not a full VR experience.**

Watch your Plex library inside your headset without leaving your game or app. PlexQuest runs as a standard Android panel in the Quest universal menu — you can overlay it on anything, resize it, or focus on it as a flat cinema window.

> Sideload-ready APKs are published on every release. Quest App Lab submission is planned for v1.0.

---

## Features

| Feature | Status |
|---|---|
| plex.tv sign-in + token login | ✅ v0.1 |
| Auto-discover Plex servers (local + remote) | ✅ v0.1 |
| Movies & TV library browser | ✅ v0.1 |
| Recently Added + Continue Watching rows | ✅ v0.1 |
| Search across all libraries | ✅ v0.1 |
| ExoPlayer direct play | ✅ v0.1 |
| Resume progress bar on posters | ✅ v0.1 |
| Quest flat-window / panel mode | ✅ v0.1 |
| TV shows → seasons → episodes | 🔜 v0.2 |
| Subtitle & audio track selection | 🔜 v0.2 |
| Quest controller button mappings | 🔜 v0.3 |
| Picture-in-picture | 🔜 v0.3 |
| Quest App Lab release | 🔜 v1.0 |

---

## Sideloading

### Requirements
- Meta Quest 2, 3, or Pro
- Developer mode enabled on your headset (via the Meta Quest companion app)
- ADB installed on your computer, **or** use [SideQuest](https://sidequestvr.com)

### Install via ADB

```bash
# Enable USB debugging, then:
adb install PlexQuest-v0.1-debug.apk
```

### Install via SideQuest
1. Download the latest APK from [Releases](../../releases)
2. Drag the APK onto SideQuest while your Quest is connected

The app appears in **Unknown Sources** in your Quest library.

---

## Building from source

**Requirements:** Android Studio Hedgehog (2023.1.1) or later, JDK 17

```bash
git clone https://github.com/YOUR_USERNAME/PlexQuestApp.git
cd PlexQuestApp

# Debug build
./gradlew assembleDebug

# Install directly to a connected Quest
./gradlew installDebug
```

The APK is at `app/build/outputs/apk/debug/app-debug.apk`.

---

## Architecture

```
PlexQuest (Android, Kotlin + Jetpack Compose)
│
├── UI layer         Compose screens + ViewModels (MVVM)
├── Data layer       Retrofit → Plex HTTP API → Repository (Flow-based)
└── Storage          DataStore — auth token, saved servers
```

- **No transcoding by default** — direct play only in v0.1; transcoder support coming in v0.2
- **No background process** — the app only runs when the Quest panel is open
- **Privacy-first** — credentials go directly to your Plex server or plex.tv; nothing is stored by this app beyond your token

### Key files

| Path | Purpose |
|---|---|
| [`NavHost.kt`](app/src/main/java/com/plexquest/app/ui/NavHost.kt) | Navigation graph |
| [`PlexApi.kt`](app/src/main/java/com/plexquest/app/data/api/PlexApi.kt) | Plex server API |
| [`PlexAuthApi.kt`](app/src/main/java/com/plexquest/app/data/api/PlexAuthApi.kt) | plex.tv auth + resource discovery |
| [`PlexRepository.kt`](app/src/main/java/com/plexquest/app/data/repository/PlexRepository.kt) | Data access, Flow wrappers |
| [`PlayerScreen.kt`](app/src/main/java/com/plexquest/app/ui/screens/PlayerScreen.kt) | Video player UI |
| [`PlayerViewModel.kt`](app/src/main/java/com/plexquest/app/viewmodel/PlayerViewModel.kt) | ExoPlayer lifecycle |

---

## Why a flat panel?

Most Quest media apps force full-VR or theatre mode. PlexQuest intentionally runs as a **2D Android panel** in the Quest universal menu, which means:

- You can watch while playing a game side-by-side
- No need to exit your current VR experience
- Works with Quest's built-in multitasking / window resizing
- Lower GPU overhead than a full VR scene

---

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for setup instructions and guidelines.
See [ROADMAP.md](ROADMAP.md) for what's planned and what needs help.

---

## Legal

PlexQuest is an independent, community-built project. It is not affiliated with, endorsed by, or sponsored by Plex Inc.

Plex® is a registered trademark of Plex Inc. This app uses the publicly documented Plex Media Server HTTP API.

Licensed under the [Apache 2.0 License](LICENSE).
