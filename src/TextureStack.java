
public class TextureStack{
  public static Idx polar;
  public static void loadTextureMaps(){
    TextureStack.polar = new Idx("map/beach.jpg");
  }

  public static Pixel getReflection( double x, double y){
    return TextureStack.polar.getChoordPixel( 0.5+ x/2, 0.5+ y/2 );
  }
}
