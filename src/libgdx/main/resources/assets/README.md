# DaimyoSimulator Assets

Runtime art is loaded from individual PNG files under `textures/sprites`.

The libGDX layer declares loaded sprite keys in `SpriteSheetRegionRegistry` and applies `TextureFilter.Nearest` to keep pixel art crisp. `missing_asset.png` is the fallback for missing sprite keys.

`docs/Textures.png` is kept only as a reference sprite sheet.
