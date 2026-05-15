import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.List;

public final class SpriteSheetSlicer {
    private record Sprite(String output, int x, int y, int w, int h, boolean trim) {
    }

    private static final List<Sprite> SPRITES = List.of(
            new Sprite("textures/tiles/tile_grass.png", 17, 45, 103, 116, false),
            new Sprite("textures/tiles/tile_dirt.png", 143, 45, 101, 116, false),
            new Sprite("textures/ui/overlay_select_yellow.png", 267, 42, 98, 118, false),
            new Sprite("textures/ui/overlay_invalid_red.png", 385, 42, 100, 118, false),
            new Sprite("textures/ui/overlay_valid_blue.png", 505, 42, 100, 118, false),
            new Sprite("textures/features/feature_forest.png", 617, 24, 179, 145, true),
            new Sprite("textures/features/feature_tree_cluster.png", 805, 43, 94, 127, true),
            new Sprite("textures/features/feature_tree.png", 926, 49, 67, 124, true),

            new Sprite("textures/buildings/building_dwelling.png", 9, 207, 115, 137, true),
            new Sprite("textures/buildings/building_rice_farm.png", 139, 208, 119, 136, true),
            new Sprite("textures/buildings/building_rice_paddy.png", 283, 210, 105, 133, true),
            new Sprite("textures/buildings/building_woodcutters_hut.png", 145, 400, 123, 135, true),
            new Sprite("textures/buildings/building_mine.png", 523, 209, 105, 132, true),
            new Sprite("textures/buildings/building_smithy.png", 641, 204, 103, 137, true),
            new Sprite("textures/buildings/building_workshop.png", 766, 207, 98, 134, true),
            new Sprite("textures/buildings/building_market.png", 879, 205, 112, 137, true),
            new Sprite("textures/buildings/building_guard_post.png", 678, 394, 117, 147, true),
            new Sprite("textures/buildings/building_temple.png", 812, 393, 151, 149, true),

            new Sprite("textures/icons/icon_resource_rice.png", 21, 586, 88, 83, true),
            new Sprite("textures/icons/icon_resource_timber.png", 143, 589, 98, 76, true),
            new Sprite("textures/icons/icon_resource_tools.png", 278, 587, 82, 80, true),
            new Sprite("textures/icons/icon_resource_luxury_goods.png", 409, 590, 100, 76, true),
            new Sprite("textures/icons/icon_parameter_happiness.png", 20, 857, 49, 49, true),
            new Sprite("textures/icons/icon_parameter_protection.png", 80, 857, 49, 49, true),
            new Sprite("textures/icons/icon_parameter_food.png", 140, 857, 49, 49, true),
            new Sprite("textures/icons/icon_parameter_faith.png", 202, 857, 49, 49, true),
            new Sprite("textures/icons/icon_parameter_housing.png", 263, 857, 49, 49, true),
            new Sprite("textures/icons/icon_parameter_craftsmanship.png", 323, 857, 49, 49, true),
            new Sprite("textures/icons/icon_population.png", 22, 720, 78, 84, true),
            new Sprite("textures/icons/icon_event_alert.png", 906, 1104, 52, 63, true),
            new Sprite("textures/icons/icon_policy_agricultural_expansion.png", 557, 586, 91, 82, true),
            new Sprite("textures/icons/icon_policy_military_protection.png", 698, 586, 83, 80, true),
            new Sprite("textures/icons/icon_policy_craftsmen_production.png", 823, 588, 83, 78, true),

            new Sprite("textures/ui/panel_parchment.png", 16, 957, 73, 93, true),
            new Sprite("textures/ui/panel_wood.png", 113, 956, 73, 94, true),
            new Sprite("textures/ui/panel_large.png", 881, 948, 114, 109, true),
            new Sprite("textures/ui/button_wood.png", 16, 1101, 56, 69, true),
            new Sprite("textures/ui/button_play.png", 17, 1101, 56, 69, true),
            new Sprite("textures/ui/button_pause.png", 80, 1101, 56, 69, true),
            new Sprite("textures/ui/button_fast.png", 143, 1101, 56, 69, true),
            new Sprite("textures/ui/button_faster.png", 206, 1101, 56, 69, true),
            new Sprite("textures/ui/button_close.png", 755, 1102, 56, 68, true),
            new Sprite("textures/ui/icon_scroll.png", 692, 1103, 56, 67, true),
            new Sprite("textures/placeholders/missing_asset.png", 16, 1360, 76, 103, false)
    );

    public static void main(String[] args) throws IOException {
        Path source = Path.of(args.length > 0 ? args[0] : "docs/Textures.png");
        Path assetsRoot = Path.of(args.length > 1 ? args[1] : "daimyosimulator-libgdx/src/main/resources/assets");
        Path runtimeSource = assetsRoot.resolve("textures/source/Textures.png");
        Files.createDirectories(runtimeSource.getParent());
        Files.copy(source, runtimeSource, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        BufferedImage sheet = ImageIO.read(source.toFile());
        for (Sprite sprite : SPRITES) {
            BufferedImage crop = new BufferedImage(sprite.w, sprite.h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = crop.createGraphics();
            graphics.drawImage(sheet, 0, 0, sprite.w, sprite.h,
                    sprite.x, sprite.y, sprite.x + sprite.w, sprite.y + sprite.h, null);
            graphics.dispose();
            clearConnectedCheckerboard(crop);
            BufferedImage output = sprite.trim ? trim(crop) : crop;
            Path destination = assetsRoot.resolve(sprite.output);
            Files.createDirectories(destination.getParent());
            ImageIO.write(output, "png", destination.toFile());
        }
    }

    private static void clearConnectedCheckerboard(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        boolean[][] visited = new boolean[height][width];
        ArrayDeque<int[]> queue = new ArrayDeque<>();
        for (int x = 0; x < width; x++) {
            addIfBackground(image, visited, queue, x, 0);
            addIfBackground(image, visited, queue, x, height - 1);
        }
        for (int y = 0; y < height; y++) {
            addIfBackground(image, visited, queue, 0, y);
            addIfBackground(image, visited, queue, width - 1, y);
        }
        while (!queue.isEmpty()) {
            int[] p = queue.removeFirst();
            int x = p[0];
            int y = p[1];
            image.setRGB(x, y, 0);
            addIfBackground(image, visited, queue, x + 1, y);
            addIfBackground(image, visited, queue, x - 1, y);
            addIfBackground(image, visited, queue, x, y + 1);
            addIfBackground(image, visited, queue, x, y - 1);
        }
    }

    private static void addIfBackground(BufferedImage image, boolean[][] visited, ArrayDeque<int[]> queue, int x, int y) {
        if (x < 0 || y < 0 || x >= image.getWidth() || y >= image.getHeight() || visited[y][x]) {
            return;
        }
        visited[y][x] = true;
        if (isCheckerboardBackground(image.getRGB(x, y))) {
            queue.add(new int[]{x, y});
        }
    }

    private static boolean isCheckerboardBackground(int argb) {
        int alpha = (argb >>> 24) & 0xff;
        int red = (argb >>> 16) & 0xff;
        int green = (argb >>> 8) & 0xff;
        int blue = argb & 0xff;
        if (alpha == 0) {
            return true;
        }
        int max = Math.max(red, Math.max(green, blue));
        int min = Math.min(red, Math.min(green, blue));
        return min >= 235 && max - min <= 12;
    }

    private static BufferedImage trim(BufferedImage image) {
        int minX = image.getWidth();
        int minY = image.getHeight();
        int maxX = -1;
        int maxY = -1;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if (((image.getRGB(x, y) >>> 24) & 0xff) != 0) {
                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                }
            }
        }
        if (maxX < minX || maxY < minY) {
            return image;
        }
        return image.getSubimage(minX, minY, maxX - minX + 1, maxY - minY + 1);
    }
}
