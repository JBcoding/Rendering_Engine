/**
 * Created by madsbjoern on 02/12/2016.
 */
public class Sphere extends Shape {
    private Vector3D center;
    private double radius;

    private double center_getMagnitudeSquare;

    public Sphere(Vector3D center, double radius) {
        super();
        this.center = center;
        this.radius = radius;

        center_getMagnitudeSquare = center.getMagnitudeSquare();
    }

    private Vector3D transformPoint(Vector3D v, Matrix3x3 m) {
        Vector3D vCenterDiff = v.sub(center);
        vCenterDiff = m.multiplyWithVector3D(vCenterDiff);
        return vCenterDiff.add(center);
    }

    @Override
    protected Vector3D getNormalVector(Vector3D hitPoint, Vector3D startPoint) {
        hitPoint = transformPoint(hitPoint, inverseTransformationMatrix);
        Vector3D normalVector = hitPoint.sub(center);
        return  transformationMatrix.multiplyWithVector3D(normalVector);
    }

    @Override
    public Vector3D getReflectiveDirection(Vector3D startPoint, Vector3D direction, Vector3D hitPoint) {
        hitPoint = transformPoint(hitPoint, inverseTransformationMatrix);
        direction = inverseTransformationMatrix.multiplyWithVector3D(direction);
        Vector3D diff = center.sub(hitPoint);
        Vector3D newDirection = direction.rotateAroundAxis(diff, new Vector3D(), Math.PI).scale(-1);
        return transformationMatrix.multiplyWithVector3D(newDirection);
    }

    @Override
    protected Vector3D getIntersectionPoint(Vector3D startPoint, Vector3D direction, Shape shapeToIgnore) {
        // We inverse transform the direction instead of transforming the object
        direction = inverseTransformationMatrix.multiplyWithVector3D(direction);
        startPoint = transformPoint(startPoint, inverseTransformationMatrix);
        /*
        Sphere equation (x - c.x)^2 + (y - c.y)^2 + (z - c.z)^2 = r^2
        Line equation (x, y, z) = (o_x + d_x t, o_y + d_y t, o_z + d_z t), o = startPoint, d = direction
        Substitute line equation into sphere equation and solve for t
        d__x^2*t^2+d__y^2*t^2+d__z^2*t^2-2*c__x*d__x*t-2*c__y*d__y*t-2*c__z*d__z*t+2*d__x*o__x*t+2*d__y*o__y*t+2*d__z*o__z*t+c__x^2-2*c__x*o__x+c__y^2-2*c__y*o__y+c__z^2-2*c__z*o__z+o__x^2+o__y^2+o__z^2-r^2
        a * t^2 + b * t + c = 0
        a = d__x^2+d__y^2+d__z^2
        a = d.length^2
        b = -2*c__x*d__x-2*c__y*d__y-2*c__z*d__z+2*d__x*o__x+2*d__y*o__y+2*d__z*o__z
        b = 2 * d.o - 2 * d.c,     . is dot product
        c = +c__x^2-2*c__x*o__x+c__y^2-2*c__y*o__y+c__z^2-2*c__z*o__z+o__x^2+o__y^2+o__z^2-r^2
        c = c.length^2 + o.length^2 - 2 * c.o - r^2,       . is dot product

        First we calculate the discriminant to see if we have any real solutions
        the discriminant is b^2 - 4ac
        */
        double a = direction.getMagnitudeSquare();
        double b = 2 * direction.dot(startPoint) - 2 * direction.dot(center);
        double c = center_getMagnitudeSquare + startPoint.getMagnitudeSquare() - 2 * center.dot(startPoint) - radius * radius;
        double discriminant = b * b - 4 * a * c;
        if (discriminant < 0) { // no real solutions for t
            return null;
        }
        // if t has 1 positive solution, then we just hit the edge of the sphere af a tangent
        // if t has 2 positive solutions, then we hit though the sphere, so we just take the smallest t
        // if t has 1 negative solution, then we just hit the edge of the sphere, but it's behind os
        // if t has 2 negative solutions, then we hit though the sphere, but it's behind os
        // if t has 1 positive and 1 negative solution, then we are inside the sphere **
        // **: this we will not render
        //
        // t = (-b +- sqrt(d)) / (2 a)
        if (discriminant == 0) { // only one solution
            double t = -b / (2 * a);
            if (t < 0) {
                return null;
            }
            Vector3D interceptionPoint = startPoint.add(direction.scale(t));
            // We have to remember to transform the object interception point back, after we transformed the direction
            return transformPoint(interceptionPoint, transformationMatrix);
        }
        // t has 2 solutions
        double t1 = (-b + Math.sqrt(discriminant)) / (2 * a);
        double t2 = (-b - Math.sqrt(discriminant)) / (2 * a);
        if (t1 < 0 || t2 < 0) {
            return null;
        }
        double t = (t1 < t2) ? t1 : t2;
        Vector3D interceptionPoint = startPoint.add(direction.scale(t));
        // We have to remember to transform the object interception point back, after we transformed the direction
        return transformPoint(interceptionPoint, transformationMatrix);
    }

    @Override
    protected Vector3D getSmallestPointInBoundingBox() {
        double maxStretch = Math.max(Math.max(this.stretchX, this.stretchY), this.stretchZ);
        return center.add(transformationMatrix.multiplyWithVector3D(new Vector3D(-radius * maxStretch, -radius * maxStretch, -radius * maxStretch)));
    }

    @Override
    protected Vector3D getLargestPointInBoundingBox() {
        double maxStretch = Math.max(Math.max(this.stretchX, this.stretchY), this.stretchZ);
        return center.add(transformationMatrix.multiplyWithVector3D(new Vector3D(radius * maxStretch, radius * maxStretch, radius * maxStretch)));
    }

    @Override
    protected Shape getShape() {
        return this;
    }
}
