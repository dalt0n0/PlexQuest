# PlexQuest Roadmap

## v0.1 — Foundation (current)
- [x] Kotlin + Jetpack Compose project scaffold
- [x] plex.tv sign-in (email + password)
- [x] Server discovery via plex.tv resources API
- [x] Library browser (movies, shows, music)
- [x] Recently Added & On Deck rows
- [x] Search
- [x] ExoPlayer video playback with custom controls
- [x] Progress tracking (resume bar on poster)
- [x] Quest flat-panel compatible (2D window mode)
- [ ] Wire up PlayerViewModel metadata → stream URL (placeholder in v0.1)
- [ ] Token-based login dialog

## v0.2 — Polish
- [ ] TV show drill-down (show → seasons → episodes)
- [ ] Background art on detail screen
- [ ] Subtitle track selection (SRT + embedded)
- [ ] Audio track selection
- [ ] Playback quality selector (transcoder support)
- [ ] Watch state sync (mark watched/unwatched)
- [ ] Collections & playlists

## v0.3 — Quest UX
- [ ] Controller button mappings (A=play, B=back, trigger=scrub)
- [ ] Quest spatial audio passthrough option
- [ ] Picture-in-picture (watch while in other apps)
- [ ] Pinned floating window (Quest multitasking panel)
- [ ] Remote server auto-reconnect

## v1.0 — Release candidate
- [ ] Music library (albums, artists, now-playing widget)
- [ ] Photo library
- [ ] Multi-user / PIN lock
- [ ] Direct play vs transcode decision logic
- [ ] Offline download support
- [ ] Submission to Meta Quest App Lab

## Help wanted
- UI polish / animations
- Quest gamepad input handling
- HDR/Dolby Vision detection
- Performance profiling on Quest 3
