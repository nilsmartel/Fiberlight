
import java.awt.*;
import javax.swing.*;
import java.util.Scanner;

// clear && javac Viewer.java && java Viewer && rm ./*.class

// main function
class Viewer{
  //Remember to print Request before calling this.
  public static String getUserInput(){
    Scanner scanner = new Scanner(System.in);
    String textInput = scanner.next();
    //int age = scanner.nextInt();
    return textInput;
  }

  public static void main(String[] args) throws InterruptedException{
    GuiFrame app = new GuiFrame(512,512);
    String filepath = "data/ico/high.obj";
    if(args.length > 0){
      filepath = args[0];
    }
    Parser p = new Parser( filepath );
    app.obj = p.createMesh();
    app.setTitle( app.obj.name );

    int r1=1;
    double r2=48;
    app.clearIdx(192);
    while(r1++ < r2){
      //if(r1%4 == 0) app.clearIdx(192);
      double fac = Math.pow((r1/r2),2);
      app.scale = 96*fac;
      Lina.setRot( -Math.PI*fac, 1.00002*(1-fac), 0.7*(1-fac));
      Lina.rotateMx();
      app.obj.alignToMatrix();
      app.renderMesh();
      app.can.redraw(true);
      Thread.sleep(30);
    }

    double isi = 0;
    while(true){
      Thread.sleep(20);
      Lina.rot[1] = Math.sin(isi)*Math.PI;
      Lina.rot[0]+= 0.0001;
      Lina.rot[2]+= 0.001;

      isi+=0.01;
      Lina.rotateMx();
      app.obj.alignToMatrix();
      //app.clearIdx(192);
      app.renderMesh();
      app.can.redraw(true);
    }
  }
}



class GuiFrame{
  void clearIdx( int grey ){
    grey=grey%256;
    for(int m=0;m<this.can.idx.data.length; m++){
      this.can.idx.data[m] = grey;
    }
  }

  int width, height;
  String title = "";
  JFrame frame;
  int labelHeight = 0;
  JLabel label;
  Canvas can;
  Mesh obj;
  double scale= 64;
  private final int height_offset;

  DataMap renderPass;

  public GuiFrame( int width, int height ){
    this.width = width;
    this.height= height;
    this.height_offset = this.getHeightOffset();
    this.can = new Canvas(width, height);
    this.showGui();
    TextureStack.loadTextureMaps();
    this.renderPass = new DataMap(this.width, this.height);
  }

  private void showGui(){
    this.frame = new JFrame();
    this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.frame.setTitle(this.title);
    this.frame.setLayout(null);
    this.frame.add(this.can);
    this.frame.setSize( this.can.getWidth(), this.can.getHeight() + this.height_offset + this.labelHeight);

    this.label = new JLabel("Hello World");
    this.frame.getContentPane().setLayout(new BorderLayout());
    this.frame.getContentPane().add("South", label);

    this.frame.setVisible(true);
  }

  void showText(String txt) throws InterruptedException{
    this.label.setText(txt);
    for(int i=0;i<16; i+=4){
      this.frame.setSize( this.can.getWidth(), this.can.getHeight() + this.height_offset + i);
      Thread.sleep(30);
    }
    this.frame.setSize( this.can.getWidth(), this.can.getHeight() + this.height_offset + 16);
    Thread.sleep(5000);
    for(int i=12; i>0;i-=4){
      this.frame.setSize( this.can.getWidth(), this.can.getHeight() + this.height_offset + i);
      Thread.sleep(30);
    }
    this.frame.setSize( this.can.getWidth(), this.can.getHeight() + this.height_offset);

  }

  void setTitle( String frame_title ){
    this.title = frame_title;
    this.frame.setTitle(this.title);
  }

  int getHeightOffset(){
    String os_name = System.getProperty("os.name");
    if(os_name.indexOf("Mac OS X") >= 0){
      return 22;
    }
    return 0;
  }

  private boolean updateInter = false;
  void setInterval( int timer ) throws InterruptedException{
    if(!this.updateInter){
      this.updateInter=true;
    }
    while(this.updateInter){
      this.onUpdate();
      Thread.sleep(timer);
    }
  }

  void stopInterval(){
    this.updateInter=false;
  }

  int time=0;
  void onUpdate(){
    this.time++;
  }

  void drawCanvas(){
    Pixel bgColor = new Pixel( 64,64,64 );

    for(int x=0; x< this.width; x++){
      for(int y=0; y< this.height; y++){
        if(this.renderPass.isPixel[x][y]){
          this.can.idx.setPixel(x,y, PixelShader.reflection( this.renderPass.map[x][y] ) );
        }else{
          this.can.idx.setPixel(x,y, bgColor );
        }
      }
    }
  }

  void drawWireframe( Pixel col){
    Idx bit = this.can.idx;
    double[][] v;
    for(int i=0; i<this.obj.face.length;i++){
      v = this.obj.getTri(i);
      for(int j=0; j<3; j++ ){
        bit.drawLine( v[j], v[(j+1)%3], col);
      }
    }
  }

  void renderMesh(){
    this.renderPass.clear();
    for(int i=0; i<this.obj.face.length;i++){
      double[][] v = this.obj.getTriData( i );
      this.rasterTri( v );
    }

    this.drawCanvas();
  }

  void rasterTri(double[][] v){
    int len = v[0].length;
    int up = 0;
    int down = 0;
    int dx = this.width/2;
    int dy = this.height/2;
    for(int i=0;i<3;i++){
      v[i][0]*=this.scale +this.scale*v[i][2];
      v[i][0]+=dx;
      v[i][1]*=this.scale +this.scale*v[i][2];
      v[i][1]+=dy;
    }
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
      this.renderPass.setPixelData(xv[l]);
      for(int x = 1+(int)xv[l][0]; x < (int)xv[(l+1)%2][0]; x++ ){
        f = ( x - xv[l][0] ) / ( xv[(l+1)%2][0] - xv[l][0]);
        data[0] = x;
        for(int qu=2; qu< len; qu++){
          data[qu] = xv[l][qu]*(1-f) + xv[(l+1)%2][qu]*f;
        }
        this.renderPass.setPixelData(data);
      }
      y++;
    }
  }

}
