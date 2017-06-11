
public class PixelShader{
  static Pixel ambient( RenderData pass ){
    int grey = 64 + (int)(128*pass.fresnel);
    return new Pixel(grey,grey,grey);
  }

  static Pixel normal( RenderData pass ){
    //int grey = 64 + (int)(128*pass.fresnel);
    return new Pixel( 64+pass.nrm_x*128,  64+pass.nrm_y*128,  64+pass.fresnel*128 );
  }

  static Pixel depth( RenderData pass ){
    int grey = 64 + (int)(128*pass.depth);
    return new Pixel(grey);
  }

  static Pixel reflection( RenderData pass ){
    Pixel refl = TextureStack.getReflection(pass.nrm_x, pass.nrm_y);
    Pixel color= new Pixel( 48, 230, 64);
    return color.blend(refl, 0.02+ 1-(pass.fresnel*0.98) );
  }
}
