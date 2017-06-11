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

    public static Pixel blendPixels( Pixel a, Pixel b, double fac ){
        fac=Math.max(Math.min(fac, 1), 0);
        return new Pixel(
                (int)( a.r*(1-fac) + b.r*fac ),
                (int)( a.g*(1-fac) + b.g*fac ),
                (int)( a.b*(1-fac) + b.b*fac )
        );

    }


    void setOff(){
        this.r=0;
        this.g=0;
        this.b=0;
    }

    void add( Pixel p ){
        this.r+=p.r;
        this.g+=p.g;
        this.b+=p.b;
    }

    void add( Pixel p, double fac){
        this.r+=p.r*fac;
        this.g+=p.g*fac;
        this.b+=p.b*fac;
    }

    void divide( int i ){
        this.r/=i;
        this.g/=i;
        this.b/=i;
    }
}
