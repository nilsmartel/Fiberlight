
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

class DoubleNode{
  double[] value;
  DoubleNode next;
  public DoubleNode( double[] data){
    this.value = new double[data.length];
    for(int i=0;i<data.length;i++) this.value[i] = data[i];
    this.next = null;
  }
}

class FaceNode{
  //(Vert) (DataType)
  //(vid)(DataType)
  int[][] idx;
  FaceNode next;
  public FaceNode(int[] a, int[] b, int[] c){
    this.idx = new int[3][3];
    for(int i=0;i<3;i++){
      this.idx[0][i] = a[i];
      this.idx[1][i] = b[i];
      this.idx[2][i] = c[i];
    }
    this.next = null;
  }
}

//Mesh obj = new Parser("obj/cube.obj").createMesh();

public class Parser{
  String name;
  DoubleNode vert = null;
  DoubleNode vt = null;
  DoubleNode vn = null;
  FaceNode face = null;

  public Parser(String fileURL){
    //System.out.print("loading file ["+ fileURL +"]\n");
    try{
      File file = new File(fileURL);
      FileReader fileReader = new FileReader(file);
      BufferedReader bufferedReader = new BufferedReader(fileReader);

      String line;
      while ((line = bufferedReader.readLine()) != null){
        this.parseLine(line);
      }
      fileReader.close();
      //System.out.print("Parsing File completed\n");
    }catch (IOException e){
      e.printStackTrace();
    }
  }

  void parseLine(String line){
    String[] str = line.split(" ");
    if(str[0].equals("#")){
      return;
    }
    if(str[0].equals("o")){
      this.name = "";
      for(int i=1;i<str.length-1;i++){
        this.name+=str[i] + " ";
      }
      this.name+=str[str.length-1];
      //System.out.print("Objekt Name = ["+this.name+"]\n");
      return;
    }
    if(str[0].equals("v")){
      double[] v = {0,0,0};
      for(int i=0;i<3;i++){
        v[i] = Double.parseDouble(str[i+1]);
      }
      DoubleNode node = new DoubleNode(v);
      this.appendNode( node, 0);
      return;
    }
    if(str[0].equals("vt") ){
      double[] v = {0,0};
      for(int i=0;i<2;i++){
        v[i] = Double.parseDouble(str[i+1]);
      }
      this.appendNode( new DoubleNode(v), 1);
      return;
    }
    if(str[0].equals("vn") ){
      double[] v = {0,0,0};
      for(int i=0;i<3;i++){
        v[i] = Double.parseDouble(str[i+1]);
      }
      this.appendNode( new DoubleNode(v), 2);
      return;
    }
    if(str[0].equals("f") ){
      int[][] idx = new int[4][3];
      for(int i=0;i<4;i++){
        for(int j=0;j<3;j++){
          idx[i][j] = 0;
        }
      }
      for(int i=0;i<str.length-1;i++){
        String s = str[i+1];
        int j=0;

        for(int x=0;x<s.length();x++){
          char ch = s.charAt(x);
          if(ch == '/'){
            j++;
            continue;
          }

          idx[i][j]*= 10;
          idx[i][j]+= ch-'0';
        }
      }
      this.appendNode( new FaceNode(idx[0], idx[1], idx[2]));
      if(str.length > 4){
        this.appendNode( new FaceNode(idx[2], idx[3], idx[0]));
      }
      return;
    }
  }

  public Mesh createMesh(){
    Mesh obj = new Mesh();
    obj.name = this.name;
    int len_vert = this.getNodeLength(this.vert);

    obj.vert = new double[len_vert][3];
    obj.mxVert = new double[len_vert][3];
    DoubleNode node = this.vert;
    for(int i=0; i<len_vert;i++){
      obj.vert[i] = node.value;
      node = node.next;
    }
    int len_vt = this.getNodeLength(this.vt);
    obj.vt = new double[len_vt][2];
    node = this.vt;
    for(int i=0; i<len_vt;i++){
      obj.vt[i] = node.value;
      node = node.next;
    }
    int len_vn = this.getNodeLength(this.vn);
    obj.vn = new double[len_vn][3];
    obj.mxVn = new double[len_vn][3];
    node = this.vn;
    for(int i=0; i<len_vn;i++){
      obj.vn[i] = node.value;
      node = node.next;
    }
    //obj.faceData = { 1, 1, 1};
    /*
    System.out.print("line 154\n");
    int[][] fidx = this.face.idx;
    System.out.print("int[][] fidx returned\n");
    for(int i=0;i<3;i++){
      for(int j=1;j<3;j++){
        if(fidx[i][j] == 0){
          obj.faceData[j]=0;
        }
      }
    }
    */
    int len_data=0;
    for( boolean i : obj.faceData) if(i) len_data++;
    int len_face = getNodeLength(this.face);
    obj.face = new int[len_face][len_data][3];
    FaceNode f = this.face;
    for(int fid=0; fid<len_face;fid++){
      for(int vid=0;vid<3;vid++){
        obj.face[fid][0][vid] = f.idx[vid][0]; //VERT
        int dx=1;
        if(obj.faceData[1]){
          obj.face[fid][dx][vid] = f.idx[vid][1]; //VT
          dx++;
        }
        if(obj.faceData[2]){
          obj.face[fid][dx][vid] = f.idx[vid][2]; //VN
        }
      }
      f = f.next;
    }
    //System.out.print("Mesh Data complete\n");
    return obj;
  }

  int getNodeLength(DoubleNode root){
    if(root == null) return 0;
    DoubleNode node = root;
    int length = 1;
    while(node.next != null){
      length++;
      node= node.next;
    }
    return length;
  }

  int getNodeLength(FaceNode root){
    if(root == null) return 0;
    FaceNode node = root;
    int length = 1;
    while(node.next != null){
      length++;
      node= node.next;
    }
    return length;
  }

  void appendNode( DoubleNode add, int id){
    switch(id){
      case 0:
      if(this.vert != null){
        DoubleNode node = this.vert;
        while(node.next != null){
          node = node.next;
        }
        node.next = add;
      }else{
        this.vert = add;
      }
      break;
      case 1:
      if(this.vt != null){
        DoubleNode node = this.vt;
        while(node.next != null){
          node = node.next;
        }
        node.next = add;
      }else{
        this.vt = add;
      }
      break;
      case 2:
      if(this.vn != null){
        DoubleNode node = this.vn;
        while(node.next != null){
          node = node.next;
        }
        node.next = add;
      }else{
        this.vn = add;
      }
      break;
    }
  }

  void appendNode( FaceNode add){
    if(this.face != null){
      FaceNode node = this.face;
      while(node.next != null){
        node = node.next;
      }
      node.next = add;
    }else{
      this.face = add;
    }
  }

}
