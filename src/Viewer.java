
import java.awt.*;
import java.awt.event.*;
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

  public static Model rawMesh;

  public static void main(String[] args) throws InterruptedException{
    GuiFrame app = new GuiFrame(512,512);
    String filepath = "suz.obj";
    if(args.length > 0){
      filepath = args[0];
    }

    rawMesh = Parser2nd.parseObjFile( filepath );
    app.setTitle( rawMesh.name );
    TextureStack.loadTextureMaps();

    int r1=1;
    double r2=128;
    while(r1++ < r2){
      //if(r1%4 == 0) app.clearIdx(192);
      double fac = Math.pow((r1/r2),2);
      app.view.matrix.setScale( 96*fac );
      app.view.matrix.setRot( -Math.PI*fac, 1.00002*(1-fac), 0.7*(1-fac));
      //app.view.matrix.rotateMx();
      app.view.setCamera();
      app.view.renderMesh();
      app.drawCanvas();
      Thread.sleep(20);
    }
  }
}

class Camera{
  DataMap renderPass;
  LinaObj matrix;
  int width, height;
  double scale = 32;
  Tri[] triData;

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
    triData = Viewer.rawMesh.getTriData(matrix);
  }
  void renderMesh(){
    this.renderPass.clear();
    for( Tri t : triData ){
      this.rasterTri( t.v );
    }
  }

  void rasterTri(double[][] v){
    int len = v[0].length;
    int up = 0;
    int down = 0;
    int dx = this.width/2;
    int dy = this.height/2;
    for(int i=0;i<3;i++){
      v[i][0]*= 1-v[i][2]/2;
      v[i][0]+=dx;
      v[i][1]*= 1-v[i][2]/2;
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

class GuiFrame{

  int width, height;
  private final int height_offset;
  JFrame frame;
  int labelHeight = 16;
  JLabel label;
  Camera view;
  Canvas can;
  Mesh obj;
  //double scale= 64;

  int cur_x=0;
  int cur_y=0;

  public GuiFrame(){
    this(512,512);
  }
  public GuiFrame( int width, int height ){
    this.width = width;
    this.height= height;
    this.height_offset = this.getHeightOffset();
    this.can = new Canvas(width, height);
    this.view = new Camera(width, height);
    this.frame = new JFrame();
    this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.frame.setLayout(null);
    this.frame.add(this.can);
    this.frame.setSize( this.can.getWidth(), this.can.getHeight() + this.height_offset + this.labelHeight);

    this.label = new JLabel("Hello World");
    this.frame.getContentPane().setLayout(new BorderLayout());
    this.frame.getContentPane().add("South", label);

    frame.addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseMoved(MouseEvent me) {
        cur_x = me.getX();
        cur_y = me.getY() - height_offset;
        label.setText("x: " + cur_x + "  |y: " + cur_y);
        //alsXYMouseLabel.repaint();
      }
    });

    this.frame.setVisible(true);
  }

  int getHeightOffset(){
    String os_name = System.getProperty("os.name");
    if(os_name.indexOf("Mac OS X") >= 0){
      return 22;
    }
    return 0;
  }

  void showText(String txt) throws InterruptedException{
    this.label.setText(txt);
    int i;
    for(i=0;i< this.labelHeight; i++ ){
      this.frame.setSize( this.can.getWidth(), this.can.getHeight() + this.height_offset + i);
      Thread.sleep(30);
    }
    Thread.sleep(5000);
    for(; i>0;i--){
      this.frame.setSize( this.can.getWidth(), this.can.getHeight() + this.height_offset + i);
      Thread.sleep(30);
    }
    this.frame.setSize( this.can.getWidth(), this.can.getHeight() + this.height_offset);
  }

  void setTitle( String title ){
    this.frame.setTitle( title);
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
    Pixel bgColor = new Pixel( 8, 8, 8);

    for(int x=0; x< this.can.getWidth(); x++){
      for(int y=0; y< this.can.getHeight(); y++){
        if(this.view.renderPass.isPixel[x][y]){
          this.can.idx.setPixel(x,y, PixelShader.depth( this.view.renderPass.map[x][y] ) );
        }else{
          this.can.idx.setPixel(x,y, bgColor );
        }
      }
    }
    this.can.redraw();
  }
}
