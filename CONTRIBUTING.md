# Contributing to PlexQuest

Thanks for helping build a better Plex experience on Meta Quest.

## Getting started

1. Fork the repo and clone your fork
2. Open in Android Studio (Hedgehog or later)
3. Let Gradle sync — no extra setup needed
4. Run on a connected Quest via ADB or Android Studio's wireless debug

## Dev setup for Quest

```bash
# Enable developer mode on your Quest via the Meta Quest companion app
# Then connect via USB or Wi-Fi ADB:
adb connect <quest-ip>:5555
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Project structure

```
app/src/main/java/com/plexquest/app/
├── data/
│   ├── api/          — Retrofit interfaces (Plex API + plex.tv auth)
│   ├── models/       — Data classes mirroring the Plex API schema
│   ├── repository/   — Single source of truth, exposes Flows
│   └── store/        — DataStore preferences (token, servers)
├── di/               — Hilt modules
├── ui/
│   ├── components/   — Reusable Compose components
│   ├── screens/      — One file per screen
│   └── theme/        — Colors, typography, Material theme
└── viewmodel/        — One ViewModel per screen
```

## Contribution guidelines

- Keep PRs focused — one feature or fix per PR
- New screens need a ViewModel; avoid logic in Composables
- No hardcoded strings — use `strings.xml`
- Respect the Plex API rate limits; don't hammer on every recomposition
- Test on device, not just the emulator (Quest behaviour differs)

## Roadmap / good first issues

See [ROADMAP.md](ROADMAP.md) for what's planned. Items marked **help wanted** are good starting points.

## Code of conduct

Be respectful. This is a fan project; keep it fun.
