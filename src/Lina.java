
// Matrix & Vector Operations
// equation for 3D Transformation Matrix

public class Lina {
    static double[][] mx = { { 1, 0, 0 }, { 0, 1, 0 }, { 0, 0, 1 } };
    static double[] rot = { 0, 0, 0 };
    static double[] offset = { 0, 0, 0 };
    static double scale = 1;

    static void setScale(double s) {
        Lina.scale = s;
    }

    static void setRot(double x, double y, double z) {
        Lina.rot[0] = x;
        Lina.rot[1] = y;
        Lina.rot[2] = z;
    }

    static double[] getOffset() {
        return Lina.offset;
    }

    static double[] copyOffset() {
        double[] p = new double[3];
        for (int i = 0; i < 3; i++)
            p[i] = Lina.offset[i];
        return p;
    }

    static void setOffset(double x, double y, double z) {
        Lina.offset[0] = x;
        Lina.offset[1] = y;
        Lina.offset[2] = z;
    }

    static void rotateMx() {
        double[][] mx = { { 1, 0, 0 }, { 0, 1, 0 }, { 0, 0, 1 } };
        double[] co = new double[3];
        double[] si = new double[3];
        for (int i = 0; i < 3; i++) {
            co[i] = Math.cos(Lina.rot[i]);
            si[i] = Math.sin(Lina.rot[i]);
        }
        int r = 0;

        double[][][] rot = { { { 1, 0, 0 }, { 0, co[r], -si[r] }, { 0, si[r], co[r++] } },
            { { co[r], 0, si[r] }, { 0, 1, 0 }, { -si[r], 0, co[r++] } },
            { { co[r], -si[r], 0 }, { si[r], co[r], 0 }, { 0, 0, 1 } }, };

        for (r = 0; r < 3; r++) {
            mx[0] = Lina.align(mx[0], rot[r]);
            mx[1] = Lina.align(mx[1], rot[r]);
            mx[2] = Lina.align(mx[2], rot[r]);
        }
        Lina.mx = mx;
    }

    static double[] align(double[] in, double[][] mx) {
        double[] v = { 0, 0, 0 };
        for (int a = 0; a < 3; a++) {
            for (int b = 0; b < 3; b++) {
                v[b] += mx[a][b] * in[a];
            }
        }
        return v;
    }

    static double[] alignCoord(double[] in, double[][] mx) {
        double[] v = Lina.copyOffset();
        double s = Lina.scale;
        for (int a = 0; a < 3; a++) {
            for (int b = 0; b < 3; b++) {
                v[b] += (mx[a][b] * in[a]) * s;
            }
        }
        return v;
    }

    static double[] scale(double[] in, double s) {
        // double[] v = {0,0,0};
        for (int a = 0; a < in.length; a++) {
            in[a] *= s;
        }
        return in;
    }

    static double[] add(double[][] in) {
        double[] v = { 0, 0, 0 };
        for (int a = 0; a < 3; a++) {
            for (int b = 0; b < 3; b++) {
                v[b] += in[a][b];
            }
        }
        return v;
    }
}
