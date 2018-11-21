package equalsverifier.testhelpers.types;

public final class ColorPoint3D extends Point3D {
    final Color color;

    public ColorPoint3D(int x, int y, int z, Color color) {
        super(x, y, z);
        this.color = color;
    }
}
