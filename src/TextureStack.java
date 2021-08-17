
public class TextureStack {
    public static Idx polar = new Idx();
    static boolean isLoaded = false;

    public static void loadTextureMaps() {
        if (!isLoaded) {
            isLoaded = true;
            TextureStack.polar = new Idx("map/beach.jpg");
        }
    }

    public static Pixel getReflection(double x, double y) {
        return TextureStack.polar.getChoordPixel(0.5 + x / 2, 0.5 + y / 2);
    }
}
