
public class DataMap{
  int width, height;
  boolean[][] isPixel;
  RenderData[][] map;

  public DataMap(){
    this(512,512);
  }
  public DataMap(int x, int y){
    width=x;
    height=y;
    isPixel = new boolean[width][height];
    map = new RenderData[width][height];
    for(int i=0; i< width;i++) for(int j=0; j< height; j++) isPixel[i][j]=false;
  }

  public void clear(){
    for(int i=0; i< width;i++) for(int j=0; j< height; j++) isPixel[i][j]=false;
  }

  void setPixelData( double[] v, int id){
    /**
     * ______POSITION_____
     * 0  X : Position           (Absolute)
     * 1  Y : Position           (Absolute)
     * 2  Z : Depth              (Uncertain)
     * ______UV MAPPING___
     * 3  U : Texture Coordinate (Normalized)
     * 4  V : Texture Coordinate (Normalized)
     * ______NORMALS______
     * 5 nX : Reflection Coord X (Normalized)
     * 6 nY : Reflection Coord Y (Normalized)
     * 7 nZ : Fresnel Value      (Normalized)
    **/

    int x = (int)v[0];
    int y = (int)v[1];
    if( x>= width || y>= height || x<0 || y<0) return;
    if( isPixel[x][y]){
      if(map[x][y].depth > v[2])  map[x][y].putData( v, id );
    }else{
      isPixel[x][y] = true;
      map[x][y] = new RenderData( v, id);
    }
  }
}
