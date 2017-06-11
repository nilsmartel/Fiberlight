
// Matrix & Vector Operations
// equation for 3D Transformation Matrix

public class LinaObj{
    double[][] mx = {
            { 1, 0, 0},
            { 0, 1, 0},
            { 0, 0, 1}
    };
    double[] rot = { 0, 0, 0};
    double[] offset = { 0, 0, 0 };
    double scale = 1;

    void setScale( double s ){
        this.scale = s;
    }

    void setScale_relative( double s ){
        this.scale*= s;
    }

    void setRot( double x, double y, double z){
        this.rot[0] =x;
        this.rot[1] =y;
        this.rot[2] =z;
    }

    void setRot(double[] v){
        this.rot[0] =v[0];
        this.rot[1] =v[0];
        this.rot[2] =v[0];
    }
    void setRot_relative( double x, double y, double z){
        this.rot[0] +=x;
        this.rot[1] +=y;
        this.rot[2] +=z;
    }
    double[] copyRotation(){
        double[] p = new double[3];
        for(int i=0;i<3;i++) p[i] = this.rot[i];
        return p;
    }

    double[] getOffset(){
        return this.offset;
    }
    double[] copyOffset(){
        double[] p = new double[3];
        for(int i=0;i<3;i++) p[i] = this.offset[i];
        return p;
    }
    void setOffset( double x, double y, double z){
        this.offset[0] =x;
        this.offset[1] =y;
        this.offset[2] =z;
    }

    void rotateMx(){
        double[][] imx = {
                { 1, 0, 0},
                { 0, 1, 0},
                { 0, 0, 1}
        };
        double[] co = new double[3];
        double[] si = new double[3];
        for(int i=0;i<3;i++){
            co[i] = Math.cos( this.rot[i]);
            si[i] = Math.sin( this.rot[i]);
        }
        int r = 0;

        double[][][] rot = {
                {
                        { 1, 0, 0},
                        { 0, co[r],-si[r]},
                        { 0, si[r], co[r++]}
                },
                {
                        { co[r], 0, si[r]},
                        { 0, 1, 0},
                        {-si[r], 0, co[r++]}
                },
                {
                        { co[r],-si[r], 0},
                        { si[r], co[r], 0},
                        { 0, 0, 1}
                },
        };

        for(r=0;r<3;r++){
            imx[0] = this.align( imx[0], rot[r] );
            imx[1] = this.align( imx[1], rot[r] );
            imx[2] = this.align( imx[2], rot[r] );
        }
        this.mx = imx;
    }

    Vector alignVector( Vector in ){
        Vector v = new Vector( 0, 0, 0 );
        for(int a=0;a<3;a++){
            for(int b=0;b<3;b++){
                v.value[b]+= this.mx[a][b]*in.value[a];
            }
        }
        for(int b=0;b<3;b++){
            v.value[b]*= this.scale;
        }
        return v;
    }

    double[] align( double[] in, double[][] mx){
        double[] v = {0,0,0};
        for(int a=0;a<3;a++){
            for(int b=0;b<3;b++){
                v[b]+= mx[a][b]*in[a];
            }
        }
        return v;
    }

    double[] alignCoord( double[] in, double[][] mx){
        double[] v = this.copyOffset();
        double s = this.scale;
        for(int a=0;a<3;a++){
            for(int b=0;b<3;b++){
                v[b]+= (mx[a][b]*in[a])*s;
            }
        }
        return v;
    }

    double[] scale( double[] in, double s ){
        //double[] v = {0,0,0};
        for(int a=0;a<in.length;a++){
            in[a]*=s;
        }
        return in;
    }

    double[] add( double[][] in){
        double[] v = {0,0,0};
        for(int a=0;a<3;a++){
            for(int b=0;b<3;b++){
                v[b]+= in[a][b];
            }
        }
        return v;
    }
}
