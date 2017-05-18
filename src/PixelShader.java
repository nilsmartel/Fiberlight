
public class PixelShader{
  static Pixel ambient( RenderData pass ){
    int grey = 64 + (int)(128*pass.fresnel);
    return new Pixel(grey,grey,grey);
  }

  static Pixel reflection( RenderData pass ){
    Pixel refl = TextureStack.getReflection(pass.nrm_x, pass.nrm_y);
    Pixel color= new Pixel( 48, 230, 64);
    return color.blend(refl, 0.02+ 1-(pass.fresnel*0.98) );
  }
}
