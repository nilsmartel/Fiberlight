
public class Mesh{
  String name;
  double[] min, max;
  double[][] vert;
  double[][] vt;
  double[][] vn;

  double[][] mxVert;
  double[][] mxVn;
  // (Face) (DataType) (Data Index)
  // (fid) (DataType) (idx)
  int[][][] face;
  boolean[] faceData;

  public Mesh(){
    this.faceData = new boolean[3];
    this.faceData[0] = true;
    this.faceData[1] = true;
    this.faceData[2] = true;

    this.min = new double[3];
    this.max = new double[3];
  }

  public void printMesh(){
    for( double[] v : this.vert){
      System.out.print("[vert]  ");
      for( double w : v){
        System.out.print( w + "  ");
      }
      System.out.print("\n");
    }
    for( double[] v : this.vt){
      System.out.print("[uv]    ");
      for( double w : v){
        System.out.print( w + "  ");
      }
      System.out.print("\n");
    }
    for( double[] v : this.vn){
      System.out.print("[nrm]   ");
      for( double w : v){
        System.out.print( w + "  ");
      }
      System.out.print("\n");
    }
    for( double[] v : this.mxVert){
      System.out.print("[vert]' ");
      for( double w : v){
        System.out.print( w + "  ");
      }
      System.out.print("\n");
    }
    for( double[] v : this.mxVn){
      System.out.print("[nrm] ' ");
      for( double w : v){
        System.out.print( w + "  ");
      }
      System.out.print("\n");
    }
  }

  void alignToMatrix(){
    for(int i=0;i< this.vert.length;i++){
      //this.mxVert[i] = Lina.alignCoord(this.vert[i], Lina.mx);
      this.mxVert[i] = Lina.align(this.vert[i], Lina.mx);
    }

    for(int i=0;i< this.vn.length;i++){
      this.mxVn[i] = Lina.align(this.vn[i], Lina.mx);
    }

    this.getBound();
    this.normalizeDepth();
  }

  void getBound(){
    for(int i=0; i<3; i++){
      this.min[i] = this.mxVert[0][i];
      this.max[i] = this.mxVert[0][i];
    }
    for( double[] v : this.mxVert){
      for(int i=0; i<3; i++){
        this.min[i] = Math.min( this.min[i], v[i]);
        this.max[i] = Math.max( this.max[i], v[i]);
      }
    }
  }

  void normalizeDepth(){
    double z0 = this.min[2];
    double z1 = this.max[2] - this.min[2];
    //double fac = 0.5;
    for(int i=0; i<this.mxVert.length; i++){
      this.mxVert[i][2]-=z0;
      this.mxVert[i][2]/=z1;

      //this.mxVert[i][0]*= (1-fac)*this.mxVert[i][2] + fac;
      //this.mxVert[i][1]*= (1-fac)*this.mxVert[i][2] + fac;
    }
  }

  double[][] getTriData( int id){
    if(id>= this.face.length) return null;
    int len =3;
    if(this.faceData[1]) len+=2;
    if(this.faceData[2]) len+=3;

    double[][] data = new double[3][len];
    int idx=0;
    int cur=0;

    for( double[] vec : this.getTri(id)){
      int i = 0;
      for( double val : vec ){
        data[idx][cur + i++] = val;
      }
      idx++;
    }
    cur+=3;
    idx=0;

    if(this.faceData[1]){
      for( double[] vec : this.getTriVt(id)){
        int i = 0;
        for( double val : vec ){
          data[idx][cur + i++] = val;
        }
        idx++;
      }
      cur+=2;
      idx=0;
    }

    if(this.faceData[2]){
      for( double[] vec : this.getTriNrm(id)){
        int i = 0;
        for( double val : vec ){
          data[idx][cur + i++] = val;
        }
        idx++;
      }
    }

    return data;
  }

  double[][] getTri( int id ){
    if(id>= this.face.length) return null;
    //id-=1;
    double[][] tri = new double[3][3];
    for(int i=0;i<3;i++){
      int idx = this.face[id][0][i];
      tri[i] = this.mxVert[idx-1];
    }
    return tri;
  }
  double[][] getTriVt( int id ){
    if(id>= this.face.length) return null;
    //id-=1;
    double[][] tri = new double[3][2];
    for(int i=0;i<2;i++){
      int idx = this.face[id][1][i];
      tri[i] = this.vt[idx-1];
    }
    return tri;
  }

  double[][] getTriNrm( int id ){
    if(id>= this.face.length) return null;
    //id-=1;
    double[][] tri = new double[3][3];
    for(int i=0;i<3;i++){
      int idx = this.face[id][2][i];
      tri[i] = this.mxVn[idx-1];
    }
    return tri;
  }
}
