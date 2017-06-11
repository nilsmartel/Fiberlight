
public class Face{
    int[][] id;
    String material;
    public Face( String a, String b, String c, String material ){
        this.material = material;
        String[] inp = new String[3];
        inp[0] = a;
        inp[1] = b;
        inp[2] = c;
        this.id = new int[3][3];
        for(int i=0; i<3; i++){
            for(int j=0;j<3;j++) this.id[i][j]= -1;

            String[] fields = inp[i].split("/");
            for(int j=0; j< fields.length; j++ )this.id[i][j] += Integer.parseInt(fields[j]);
        }
    }

    public Face( String[] inp, String material){
        this.material = material;
        this.id = new int[3][3];
        for(int i=0; i<3; i++){
            for(int j=0;j<3;j++) this.id[i][j]= -1;

            String[] fields = inp[i].split("/");
            for(int j=0; j< fields.length; j++ )this.id[i][j] += Integer.parseInt(fields[j]);
        }
    }

    public Face(String a, String b, String c){
        this( a, b, c, "");
    }
    public Face(String[] inp){
        this(inp, "");
    }
}