
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Scanner;

// clear && javac Viewer.java && java Viewer && rm ./*.class

// main function
class Viewer{
  //Remember to print Request before calling this.
  public static void getUserInput(){
    Scanner scanner = new Scanner(System.in);
    while (scanner.hasNext()) {
      String txt = scanner.next();
      if(txt.equals("rot")){
        double a = (scanner.nextInt()*Math.PI)/180;
        double b = (scanner.nextInt()*Math.PI)/180;
        double c = (scanner.nextInt()*Math.PI)/180;
        app.view.matrix.setRot(a,b,c);
        app.view.setCamera();
        app.drawCanvas();
      }
      if(txt.equals("rot+")){
        double a = (scanner.nextInt()*Math.PI)/180;
        double b = (scanner.nextInt()*Math.PI)/180;
        double c = (scanner.nextInt()*Math.PI)/180;
        app.view.matrix.setRot_relative(a,b,c);
        app.view.setCamera();
        app.drawCanvas();
      }
      if(txt.equals("scale")){
        app.view.matrix.setScale( scanner.nextDouble() );
        app.view.setCamera();
        app.drawCanvas();
      }
      if(txt.equals("scale+")){
        app.view.matrix.setScale_relative( scanner.nextDouble() );
        app.view.setCamera();
        app.drawCanvas();
      }
      if(txt.equals("getRotation")){
        double[] r = app.view.matrix.copyRotation();
        int x,y,z;
        x=(int)((r[0]*180)/Math.PI);
        y=(int)((r[1]*180)/Math.PI);
        z=(int)((r[2]*180)/Math.PI);
        System.out.println(" { "+x+", "+y+", "+z+"}");
      }
    }
    //int age = scanner.nextInt();
    //return textInput;
  }

  public static Model rawMesh;
  public static GuiFrame app;
  public static void main(String[] args) throws InterruptedException{
    app = new GuiFrame(512,512);
    String filepath = "suz.obj";
    if(args.length > 0){
      filepath = args[0];
    }

    rawMesh = Parser2nd.parseObjFile( filepath );
    app.setTitle( rawMesh.name );
    TextureStack.loadTextureMaps();

    int r1=1;
    double r2=64;
    while(r1++ < r2){
      //if(r1%4 == 0) app.clearIdx(192);
      double fac = Math.pow((r1/r2),2);
      app.view.matrix.setScale( 96*fac );
      app.view.matrix.setRot( -Math.PI*fac, 1.00002*(1-fac), 0.7*(1-fac));
      //app.view.matrix.rotateMx();
      app.view.setCamera();
      //app.view.renderMesh();
      app.drawCanvas();
      Thread.sleep(20);
    }
    getUserInput();
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
  int cur_dx=0;
  int cur_dy=0;

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
      /*
      public void mousePressed(MouseEvent me) {
        //             [             ]
        String info = "[mousePressed ]";
        cur_x = me.getX();
        cur_y = me.getY() - height_offset;
        info+="  x: " + cur_x + "  |y: " + cur_y;
        label.setText(info);
      }
      */
      public void mouseMoved(MouseEvent me) {
        String info = "[mouseMoved   ]";
        cur_x = me.getX();
        cur_y = me.getY() - height_offset;
        info+="  x: " + cur_x + "  |y: " + cur_y;
        label.setText(info);
      }
      public void mouseDragged(MouseEvent me) {
        String info = "[mouseDragged ]";
        cur_dx = me.getX() -cur_x;
        cur_dy = (me.getY() - height_offset) -cur_y;
        cur_x = me.getX();
        cur_y = me.getY() - height_offset;

        view.matrix.setRot_relative( -cur_dy*.01, cur_dx*.02, 0);
        view.setCamera();
        drawCanvas();
        info+="  x: " + cur_dx + "  |y: " + cur_dy;
        label.setText(info);
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
