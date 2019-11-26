package nl.jqno.equalsverifier.testhelpers.types;

public class Point3D extends Point {
    public int z;

    public Point3D(int x, int y, int z) {
        super(x, y);
        this.z = z;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Point3D)) {
            return false;
        }
        return super.equals(obj) && ((Point3D) obj).z == z;
    }

    @Override
    public int hashCode() {
        return super.hashCode() + (31 * z);
    }

    @Override
    public String toString() {
        return super.toString() + "," + z;
    }
}
