import java.awt.*;

/**
 * Created by madsbjoern on 30/10/2016.
 */
public class Triangle extends Shape {
    private Vector3D p1, p2, p3; // the 3 corners

    private Vector3D normalVector;
    private double normalVector_dot_p1;
    private double areaOfTriangleTimes2;
    private Vector3D boundingCubeStartPoint; // smallest point in cube
    private Vector3D boundingCubeEndPoint; // largest point in cube

    public Triangle(Vector3D p1, Vector3D p2, Vector3D p3) {
        super();
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;

        Vector3D p1p2 = p1.sub(p2);
        Vector3D p1p3 = p1.sub(p3);

        normalVector = p1p2.cross(p1p3);
        areaOfTriangleTimes2 = normalVector.getMagnitude();
        normalVector_dot_p1 = normalVector.dot(p1);

        double smallestX = Math.min(p1.getX(), Math.min(p2.getX(), p3.getX())) - .1;
        double smallestY = Math.min(p1.getY(), Math.min(p2.getY(), p3.getY())) - .1;
        double smallestZ = Math.min(p1.getZ(), Math.min(p2.getZ(), p3.getZ())) - .1;

        double largestX = Math.max(p1.getX(), Math.max(p2.getX(), p3.getX())) + .1;
        double largestY = Math.max(p1.getY(), Math.max(p2.getY(), p3.getY())) + .1;
        double largestZ = Math.max(p1.getZ(), Math.max(p2.getZ(), p3.getZ())) + .1;

        boundingCubeStartPoint = new Vector3D(smallestX, smallestY, smallestZ);
        boundingCubeEndPoint = new Vector3D(largestX, largestY, largestZ);

    }

    @Override
    protected Vector3D getNormalVector(Vector3D hitPoint, Vector3D startPoint) {
        Vector3D directionToStartPoint = startPoint.sub(hitPoint);
        if (Math.cos(normalVector.getAngleToVector(directionToStartPoint)) < 0) { // point out of the plane in the wrong direction
            return normalVector.scale(-1);
        }
        return normalVector;
    }

    @Override
    public Vector3D getReflectiveDirection(Vector3D startPoint, Vector3D direction, Vector3D hitPoint) {
        Vector3D newDirection = direction.rotateAroundAxis(normalVector, new Vector3D(), Math.PI).scale(-1);
        return newDirection;
    }

    @Override
    protected Vector3D getIntersectionPoint(Vector3D startPoint, Vector3D direction) {
        /*
        plane Equation N_x (x - p1_x) + N_y (y - p1_y) + N_z (z - p1_z) = 0
        Line equation (x, y, z) = (o_x + d_x t, o_y + d_y t, o_z + d_z t), o = startPoint, d = direction
        Substitute line equation into plane equation and solve for t
        t = -(N__x*o__x-N__x*p1__x+N__y*o__y-N__y*p1__y+N__z*o__z-N__z*p1__z)/(N__x*d__x+N__y*d__y+N__z*d__z)
        t = -(N.o - N.p1) / N.d,     . is dot product
        t = -A / B
        If B equals 0, the the line is parallel to the plane
        */
        double B = normalVector.dot(direction);
        if (B == 0) { // Line is parallel to the plane
            return null;
        }
        double A = normalVector.dot(startPoint) - normalVector_dot_p1;
        double t = -A / B;
        if (t <= 0) { // The plane is behind the line
            return null;
        }
        Vector3D planeIntersectionPoint = startPoint.add(direction.scale(t));
        /*
        Check to see if we are outside the bounding cube (faster than calculating barycentric coordinates)
        */
        if (planeIntersectionPoint.getX() < boundingCubeStartPoint.getX()) {return null;}
        if (planeIntersectionPoint.getY() < boundingCubeStartPoint.getY()) {return null;}
        if (planeIntersectionPoint.getZ() < boundingCubeStartPoint.getZ()) {return null;}
        if (planeIntersectionPoint.getX() > boundingCubeEndPoint.getX()) {return null;}
        if (planeIntersectionPoint.getY() > boundingCubeEndPoint.getY()) {return null;}
        if (planeIntersectionPoint.getZ() > boundingCubeEndPoint.getZ()) {return null;}
        /*
        Transform to barycentric coordinates to check if point is inside triangle
        https://en.wikipedia.org/wiki/Barycentric_coordinate_system
        */
        Vector3D Pp1 = planeIntersectionPoint.sub(p1); // P is the plane intersection point
        Vector3D Pp2 = planeIntersectionPoint.sub(p2);
        Vector3D Pp3 = planeIntersectionPoint.sub(p3);
        double alpha = Pp2.cross(Pp3).getMagnitude();
        double beta = Pp3.cross(Pp1).getMagnitude();
        double gamma = Pp1.cross(Pp2).getMagnitude();
        if (Math.abs(alpha + beta + gamma - areaOfTriangleTimes2) < 0.0000001) { // the point is inside the triangle
            return planeIntersectionPoint;
        } else { // it's not inside
            return null;
        }
    }

    @Override
    protected Vector3D getSmallestPointInBoundingBox() {
        return boundingCubeStartPoint;
    }

    @Override
    protected Vector3D getLargestPointInBoundingBox() {
        return boundingCubeEndPoint;
    }
}
