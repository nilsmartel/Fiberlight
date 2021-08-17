
public class RenderData {
    double depth, coord_u, coord_v, nrm_x, nrm_y, fresnel;
    int id;

    public RenderData(double[] v, int id) {
        this.id = id;
        int i = 2;
        depth = v[i++];
        coord_u = v[i++];
        coord_v = v[i++];
        nrm_x = v[i++];
        nrm_y = v[i++];
        fresnel = v[i++];
    }

    public void putData(double[] v, int id) {
        this.id = id;
        int i = 2;
        depth = v[i++];
        coord_u = v[i++];
        coord_v = v[i++];
        nrm_x = v[i++];
        nrm_y = v[i++];
        fresnel = v[i++];
    }

    public Vector getUV() {
        return new Vector(coord_u, coord_v);
    }
}
