
public class MeshAlgorithm {

    public static Vector getSurfaceNormal(Vector[] tri) {
        Vector nrm = new Vector(0, 0, 0);
        Vector u = tri[0].subtract(tri[1]);
        Vector v = tri[2].subtract(tri[0]);
        nrm.x(u.y() * v.z() - u.z() * v.y());
        nrm.y(u.z() * v.x() - u.x() * v.z());
        nrm.z(u.x() * v.y() - u.y() * v.x());

        return nrm;
    }
}