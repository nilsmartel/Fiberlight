public class Pixel{
  int r,g,b;
  public Pixel(){
    this.r = 0;
    this.g = 0;
    this.b = 0;
  }
  public Pixel(int grey){
    this.r = grey;
    this.g = grey;
    this.b = grey;
  }
  public Pixel(int r, int g, int b){
    this.r = r;
    this.g = g;
    this.b = b;
  }
  public Pixel(double r, double g, double b){
    this.r = (int)r;
    this.g = (int)g;
    this.b = (int)b;
  }

  public Pixel blend( Pixel p, double fac ){
    r = (int)(r*(1-fac) + p.r*fac );
    g = (int)(g*(1-fac) + p.g*fac );
    b = (int)(b*(1-fac) + p.b*fac );
    return this;
  }
}
