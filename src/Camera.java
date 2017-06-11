
public class Camera{
    DataMap renderPass;
    LinaObj matrix;
    int width, height;
    double scale = 32;
    TriNode[] triData;

    public Camera(){
        this(512,512);
    }

    public Camera(int sx, int sy){
        this.width = sx;
        this.height= sy;
        this.matrix = new LinaObj();
        this.renderPass = new DataMap(this.width, this.height);
    }

    void setCamera(){
        triData = Viewer.rawMesh.getTriData( this.matrix);
        renderMesh();
    }
    void renderMesh(){
        this.renderPass.clear();
        int dx = this.width/2;
        int dy = this.height/2;
        for( TriNode t : triData ){
            for(int i=0;i<3;i++){
                //t.v[i][0]*= 1-t.v[i][2]/2;
                t.v[i][0]+=dx;
                //t.v[i][1]*= 1-t.v[i][2]/2;
                t.v[i][1]+=dy;
            }
            this.rasterTri( t.v, t.id );
        }
    }

    void rasterTri(double[][] v, int vertId ){
        int len = v[0].length;
        int up = 0;
        int down = 0;

        for(int i=1; i<3;i++){
            if(v[i][1] < v[up][1]) up=i;
            if(v[i][1] > v[down][1]) down=i;
        }
        int[] id= new int[3];
        double f;
        double[] fac= new double[3];
        id[1]=up;
        id[2]=up;
        int y = (int)v[up][1]+1;
        int dir = 1;
        double[][] xv = new double[2][len];
        double[] data = new double[len];
        int a,b;
        int l = 0;
        while( y < v[down][1] ){
            data[1] = y;
            for(int i=1;i<3;i++){
                if( v[ (id[i]+i)%3][1] < y){
                    id[i] = (id[i]+i)%3;
                }
                fac[i-1] = (y - v[id[i]][1])/( v[(id[i]+i)%3][1] - v[id[i]][1] );

                xv[i-1][0] = v[id[i]][0]*(1-fac[i-1]) + v[(id[i]+i)%3][0]*fac[i-1];
                xv[i-1][1] = y;
                for(int qu=2; qu< len; qu++){
                    xv[i-1][qu] = v[id[i]][qu]*(1-fac[i-1]) + v[(id[i]+i)%3][qu]*fac[i-1];
                }
            }
            if(xv[l][0] > xv[(l+1)%2][0]){
                l = (l+1)%2;
            }
            this.renderPass.setPixelData(xv[l], vertId );
            for(int x = 1+(int)xv[l][0]; x < (int)xv[(l+1)%2][0]; x++ ){
                f = ( x - xv[l][0] ) / ( xv[(l+1)%2][0] - xv[l][0]);
                data[0] = x;
                for(int qu=2; qu< len; qu++){
                    data[qu] = xv[l][qu]*(1-f) + xv[(l+1)%2][qu]*f;
                }
                this.renderPass.setPixelData(data, vertId );
            }
            y++;
        }
    }
}

class Projector extends Camera{
    Pixel bgColor;
    Idx srcImage;
    public Projector(){
        this(512,512);
    }

    public Projector(int sx, int sy){
        this.width = sx;
        this.height= sy;
        this.matrix = new LinaObj();
        this.renderPass = new DataMap(this.width, this.height);
        this.srcImage=new Idx(1,1,
                new Pixel(255,255,255)
        );
        bgColor = new Pixel(0,0,0);
    }

    void setCamera(){
        triData = Viewer.rawMesh.getTriData_inBounds( matrix);
        renderPass.clear();

        for( TriNode t : triData )
            rasterTri(t.v, t.id, width, height);
    }

    private void rasterTri(double[][] v, int id, int width, int height) {

        for(int i=0;i<3;i++){
            v[i][0]*= width;
            v[i][1]*= height;
        }
        rasterTri(v, id);
    }

    public void setSrcImage( Idx i ){
        this.srcImage = i;
    }

    public void setBgColor(Pixel bgColor) {
        this.bgColor = bgColor;
    }

    public Idx renderProjection( int sx, int sy ){
        Idx src = srcImage.getResizedIdx(width,height);
        Idx des = new Idx(sx,sy, bgColor );

        for(int x=0; x< width; x++){
            for(int y=0; y< height; y++){
                if(this.renderPass.isPixel[x][y]){
                    des.setPixelAtCoord( this.renderPass.map[x][y].getUV(), src.getPixel(x,y));
                    //Next step is filling the spaces in between
                }
            }
        }
        return des;
    }
}