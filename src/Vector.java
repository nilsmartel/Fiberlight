
public class Vector implements Comparable<Vector> {
    double[] value;

    public Vector(double a, double b, double c ){
        value = new double[3];
        value[0]=a;
        value[1]=b;
        value[2]=c;
    }

    public Vector(double a, double b ){
        value = new double[2];
        value[0]=a;
        value[1]=b;
    }

    public Vector(double[] a){
        value = new double[a.length];
        for(int i=0;i<a.length;i++)
            value[i] = a[i];
    }

    public Vector(Vector[] merge){
        int len=0;
        for( Vector v : merge )
            len += v.value.length();
        value = new double[len];
        int i=0;
        for( Vector v : merge )
            for( double d : v )
                value[i++] = d;
    }

    double x(){
        return this.value[0];
    }

    double y(){
        return this.value[1];
    }

    double z(){
        return (value.length < 3) ? 0 : this.value[2];
    }

    @Override
    public int compareTo( Vector v ){
        return Double.compare( this.value[1], v.value[1] );
    }
    @Override
    public String toString(){
        int i=0;
        String s = "{ " + value[i++];
        while(i<value.length)
            s+= ", "+value[i++];
        return  s + "}";
    }
}

class TriNode{
    double[][] v;
    int id;
    public TriNode( int id, Vector a0,Vector a1,Vector a2,Vector b0,Vector b1,Vector b2,Vector c0,Vector c1,Vector c2){
        this.id = id;
        v = new double[3][8];
        int i=0;
        v[0][i] = a0.value[i++];
        v[0][i] = a0.value[i++];
        v[0][i] = a0.value[i++];
        v[0][i] = a1.value[i++ -3];
        v[0][i] = a1.value[i++ -3];
        v[0][i] = a2.value[i++ -5];
        v[0][i] = a2.value[i++ -5];
        v[0][i] = a2.value[i   -5];
        i=0;
        v[1][i] = b0.value[i++];
        v[1][i] = b0.value[i++];
        v[1][i] = b0.value[i++];
        v[1][i] = b1.value[i++ -3];
        v[1][i] = b1.value[i++ -3];
        v[1][i] = b2.value[i++ -5];
        v[1][i] = b2.value[i++ -5];
        v[1][i] = b2.value[i   -5];
        i=0;
        v[2][i] = c0.value[i++];
        v[2][i] = c0.value[i++];
        v[2][i] = c0.value[i++];
        v[2][i] = c1.value[i++ -3];
        v[2][i] = c1.value[i++ -3];
        v[2][i] = c2.value[i++ -5];
        v[2][i] = c2.value[i++ -5];
        v[2][i] = c2.value[i   -5];
    }
}
