/**
 * Created by madsbjoern on 10/12/2016.
 */
public class Matrix3x3 {
    private double  a, b, c,
                    d, e, f,
                    g, h, i;

    public Matrix3x3() {
        a = b = c = d = e = f = g = h = i = 0;
    }

    public Matrix3x3(Vector3D v) { // Stretching matrix
        this();
        a = v.getX();
        e = v.getY();
        i = v.getZ();
    }

    public Matrix3x3(double angle, Vector3D axis) { // Rotation matrix, with angle around axis
        makeAsRotationMatrix(angle, axis);
    }

    public Matrix3x3(double angleX, double angleY, double angleZ) {
        Matrix3x3 temp = new Matrix3x3(new Vector3D(1, 1, 1));
        makeAsRotationMatrix(angleZ, new Vector3D(0, 0, 1));
        temp = multiplyWithMatrix3x3(temp);
        makeAsRotationMatrix(angleY, new Vector3D(0, 1, 0));
        temp = multiplyWithMatrix3x3(temp);
        makeAsRotationMatrix(angleX, new Vector3D(1, 0, 0));
        temp = multiplyWithMatrix3x3(temp);
        a = temp.a;
        b = temp.b;
        c = temp.c;
        d = temp.d;
        e = temp.e;
        f = temp.f;
        g = temp.g;
        h = temp.h;
        i = temp.i;
    }

    public Matrix3x3(double a, double b, double c, double d, double e, double f, double g, double h, double i) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.e = e;
        this.f = f;
        this.g = g;
        this.h = h;
        this.i = i;
    }

    private void makeAsRotationMatrix(double angle, Vector3D axis) {
        double l = axis.getX(), m = axis.getY(), n = axis.getZ();
        double cosAngle = Math.cos(angle);
        double sinAngle = Math.sin(angle);
        a = l * l * (1 - cosAngle) + cosAngle; b = m * l * (1 - cosAngle) - n * sinAngle; c = n * l * (1 - cosAngle) + m * sinAngle;
        d = l * m * (1 - cosAngle) + n * sinAngle; e = m * m * (1 - cosAngle) + cosAngle; f = n * m * (1 - cosAngle) - l * sinAngle;
        g = l * n * (1 - cosAngle) - m * sinAngle; h = m * n * (1 - cosAngle) + l * sinAngle; i = n * n * (1 - cosAngle) + cosAngle;
    }

    public Vector3D getFirstRow() {
        return new Vector3D(a, b, c);
    }

    public Vector3D getSecoundRow() {
        return new Vector3D(d, e, f);
    }

    public Vector3D getThirdRow() {
        return new Vector3D(g, h, i);
    }

    public Vector3D getFirstColumn() {
        return new Vector3D(a, d, g);
    }

    public Vector3D getSecoundColumn() {
        return new Vector3D(b, e, h);
    }

    public Vector3D getThirdColumn() {
        return new Vector3D(c, f, i);
    }

    public Vector3D multiplyWithVector3D(Vector3D v) {
        return new Vector3D(
                a * v.getX() + b * v.getY() + c * v.getZ(),
                d * v.getX() + e * v.getY() + f * v.getZ(),
                g * v.getX() + h * v.getY() + i * v.getZ()
        );
    }

    public Matrix3x3 multiplyWithMatrix3x3(Matrix3x3 m) {
        return new Matrix3x3(
                getFirstRow().dot(m.getFirstColumn()),
                getFirstRow().dot(m.getSecoundColumn()),
                getFirstRow().dot(m.getThirdColumn()),
                getSecoundRow().dot(m.getFirstColumn()),
                getSecoundRow().dot(m.getSecoundColumn()),
                getSecoundRow().dot(m.getThirdColumn()),
                getThirdRow().dot(m.getFirstColumn()),
                getThirdRow().dot(m.getSecoundColumn()),
                getThirdRow().dot(m.getThirdColumn())
                );
    }

    public double getDeterminant() {
        return a * e * i + b * f * g + c * d * h + c * e * g + b * d * i + a * f * h;
    }

    public Matrix3x3 getInverseMatrix() {
        double A = (e * i - f * h), D = -(b * i - c * h), G = (b * f - c * e);
        double B = -(d * i - f * g), E = (a * i - c * g), H = -(a * f - c * d);
        double C = (d * h - e * g), F = -(a * h - b * g), I = (a * e - b * d);
        double factor = 1 / getDeterminant();
        return new Matrix3x3(
                A * factor, D * factor, G * factor,
                B * factor, E * factor, H * factor,
                C * factor, F * factor, I * factor);
    }

    @Override
    public String toString() {
        return "Matrix3x3 [\n\t" + a + ", " + b + ", " + c + "\n\t" + d + ", " + e + ", " + f + "\n\t" + g + ", " + h + ", " + i + "]";
    }
}
