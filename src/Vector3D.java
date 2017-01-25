/**
 * Created by madsbjoern on 30/10/2016.
 */
public class Vector3D {
    private double x, y, z;

    public Vector3D() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3D(Vector3D direction, double length) {
        direction = direction.getUnitVector();
        this.x = direction.x * length;
        this.y = direction.y * length;
        this.z = direction.z * length;
    }

    public Vector3D scale(double s) {
        return new Vector3D(x * s, y * s, z * s);
    }

    public Vector3D add(Vector3D v) {
        return new Vector3D(x + v.x, y + v.y, z + v.z);
    }

    public Vector3D sub(Vector3D v) {
        return new Vector3D(x - v.x, y - v.y, z - v.z);
    }

    public Vector3D cross(Vector3D v) {
        return new Vector3D(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x);
    }

    public double dot(Vector3D v) {
        return x * v.x + y * v.y + z * v.z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public Vector3D getUnitVector() {
        double magnitude = getMagnitude();
        return new Vector3D(x / magnitude, y / magnitude, z / magnitude);
    }

    public double getMagnitude() {
        return Math.sqrt(x * x + y * y + z * z);
    }
    
    public double getMagnitudeSquare() {
        return x * x + y * y + z * z;
    }

    public double getAngleToVector(Vector3D v) {
        return Math.acos(dot(v) / (getMagnitude() * v.getMagnitude()));
    }

    public Vector3D deepCopy() {
        return new Vector3D(x, y, z);
    }

    public boolean isZero() {
        if (getMagnitude() < 5e-9) {
            x = 0;
            y = 0;
            z = 0;
            return true;
        } else {
            return false;
        }
    }

    public Vector3D projectionOn(Vector3D v) {
        return v.getUnitVector().scale(dot(v) / v.getMagnitude());
    }

    public Vector3D rotateAroundAxis(Vector3D axisDirection, Vector3D axisPoint, double angle) { // axisPoint is just a random point on the line
        /*
        result of rotating the point (x,y,z) about the line through (a,b,c) with direction vector ⟨u,v,w⟩ (where u2 + v2 + w2 = 1) by the angle θ
        newX = (a(v^2 + w^2) - u(bv + cw - ux - vy - wz ))(1 - cos θ) + xcos θ + (- cv + bw - wy + vz)sin θ
        newY = (b(u^2 + w^2) - v(au + cw - ux - vy - wz ))(1 - cosθ ) + y cosθ + (cu - aw + wx - uz )sin θ
        newZ = (c(u^2 + v^2) - w (au + bv - ux - vy - wz ))(1 - cos θ) + zcos θ + (- bu + av - vx + uy )sinθ
        */
        axisDirection = axisDirection.getUnitVector();
        double cosTheta = Math.cos(angle);
        double sinTheta = Math.sin(angle);
        double a = axisPoint.x, b = axisPoint.y, c = axisPoint.z;
        double u = axisDirection.x, v = axisDirection.y, w = axisDirection.z;

        double newX = (a * (v * v + w * w) - u * (b * v + c * w - u * x - v * y - w * z)) * (1 - cosTheta) + x * cosTheta + (- c * v + b * w - w * y + v * z) * sinTheta;
        double newY = (b * (u * u + w * w) - v * (a * u + c * w - u * x - v * y - w * z)) * (1 - cosTheta) + y * cosTheta + (c * u - a * w + w * x - u * z) * sinTheta;
        double newZ = (c * (u * u + v * v) - w * (a * u + b * v - u * x - v * y - w * z)) * (1 - cosTheta) + z * cosTheta + (- b * u + a * v - v * x + u * y) * sinTheta;
        return new Vector3D(newX, newY, newZ);
    }

    public Vector3D getMaxXYZ(Vector3D v) {
        return new Vector3D(Math.max(x, v.x), Math.max(y, v.y), Math.max(z, v.z));
    }

    public Vector3D getMinXYZ(Vector3D v) {
        return new Vector3D(Math.min(x, v.x), Math.min(y, v.y), Math.min(z, v.z));
    }

    public double getBoundingBoxVolumen(Vector3D v) {
        return Math.abs((x - v.x) * (y - v.y) * (z - v.z));
    }

    public String toString() {
        return "Vec3D(" + x + ", " + y + ", " + z + ")";
    }

    public String toStringEasy() {
        return x + " " + y + " " + z;
    }
}
