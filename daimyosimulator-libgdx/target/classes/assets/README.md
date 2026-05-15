# DaimyoSimulator Assets

Runtime art comes from `textures/source/Textures.png`, copied from `docs/Textures.png`.

`tools/SpriteSheetSlicer.java` slices the sheet into individual transparent PNGs under `textures/tiles`, `textures/buildings`, `textures/features`, `textures/icons`, `textures/ui`, and `textures/placeholders`.

The libGDX layer loads those sliced PNGs through registries and applies `TextureFilter.Nearest` to keep pixel art crisp. `missing_asset.png` is only a real fallback for missing sprite keys.
