
import java.util.*;

public class Mesh {
    String name;
    Vector[] vert;
    Vector[] nrm;
    Vector[] uv;
    Face[] face;

    public Mesh(String title, List<Vector> v, List<Vector> vn, List<Vector> vt, List<Face> f) {
        name = title;
        vert = v.toArray(new Vector[v.size()]);
        nrm = vn.toArray(new Vector[vn.size()]);
        uv = vt.toArray(new Vector[vt.size()]);
        face = f.toArray(new Face[f.size()]);
    }

    public Mesh() {
        name = "Fiberlight";
        vert = new Vector[0];
        nrm = new Vector[0];
        uv = new Vector[0];
        face = new Face[0];
    }

    public boolean isEmpty() {
        return (this.face.length == 0);
    }

    public Vector[] getTriangle(int i) {
        if (i > this.face.length)
            i %= this.face.length;
        Vector[] tri = new Vector[3];
        tri[0] = this.vert[this.face[i].id[0][0]];
        tri[1] = this.vert[this.face[i].id[1][0]];
        tri[2] = this.vert[this.face[i].id[2][0]];
        return tri;
    }

    public TriNode[] getTriData(LinaObj lin) {
        if (isEmpty())
            return new TriNode[0];
        lin.rotateMx();

        TriNode[] tri = new TriNode[this.face.length];
        Vector[] tVert = new Vector[this.vert.length];
        Vector[] tNrm = new Vector[this.nrm.length];

        for (int i = 0; i < this.vert.length; i++) {
            tVert[i] = lin.alignVectorScaled(this.vert[i]);
        }

        for (int i = 0; i < this.nrm.length; i++) {
            tNrm[i] = lin.alignVector(this.nrm[i]);
        }

        for (int i = 0; i < this.face.length; i++) {
            tri[i] = new TriNode(i, tVert[this.face[i].id[0][0]], this.uv[this.face[i].id[0][1]],
                    tNrm[this.face[i].id[0][2]],

                    tVert[this.face[i].id[1][0]], this.uv[this.face[i].id[1][1]], tNrm[this.face[i].id[1][2]],

                    tVert[this.face[i].id[2][0]], this.uv[this.face[i].id[2][1]], tNrm[this.face[i].id[2][2]]);
        }

        return tri;
    }

    public TriNode[] getTriData_inBounds(LinaObj lin) {
        if (isEmpty())
            return new TriNode[0];

        TriNode[] tri = new TriNode[this.face.length];
        lin.rotateMx();
        Vector[] tVert = new Vector[this.vert.length];
        Vector[] tNrm = new Vector[this.nrm.length];

        // -> [ x, y, z]
        Range[] bounds = new Range[3];

        for (int i = 0; i < this.vert.length; i++) {
            // this.mxVert[i] = Lina.alignCoord(this.vert[i], Lina.mx);
            tVert[i] = lin.alignVector(this.vert[i]);
            for (int a = 0; a < 3; a++)
                bounds[a].setRange(tVert[i].value[a]);
        }
        // max-=min;
        bounds[0].value[1] -= bounds[0].value[0];
        bounds[1].value[1] -= bounds[1].value[0];
        bounds[2].value[1] -= bounds[2].value[0];
        for (int i = 0; i < this.vert.length; i++) {
            for (int a = 0; a < 3; a++) {
                tVert[i].value[a] -= bounds[a].value[0];
                tVert[i].value[a] /= bounds[a].value[1];
            }
        }

        for (int i = 0; i < this.nrm.length; i++) {
            tNrm[i] = lin.alignVector(this.nrm[i]);
        }

        for (int i = 0; i < this.face.length; i++) {
            tri[i] = new TriNode(i, tVert[this.face[i].id[0][0]], this.uv[this.face[i].id[0][1]],
                    tNrm[this.face[i].id[0][2]],

                    tVert[this.face[i].id[1][0]], this.uv[this.face[i].id[1][1]], tNrm[this.face[i].id[1][2]],

                    tVert[this.face[i].id[2][0]], this.uv[this.face[i].id[2][1]], tNrm[this.face[i].id[2][2]]);
        }

        return tri;
    }
}

class Range {
    double[] value = { Double.MAX_VALUE, Double.MIN_NORMAL };

    public Range() {
    }

    double getMin() {
        return value[0];
    }

    double getMax() {
        return value[1];
    }

    void setMin(double num) {
        value[0] = num;
    }

    void setMax(double num) {
        value[1] = num;
    }

    void setRange(double num) {
        if (num < value[0])
            value[0] = num;
        if (num > value[1])
            value[1] = num;
    }
}
