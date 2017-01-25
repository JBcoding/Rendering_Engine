import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by madsbjoern on 21/01/2017.
 */
public class Group extends Shape {
    private List<Shape> shapes;
    private Vector3D boundingCubeStartPoint; // smallest point in cube
    private Vector3D boundingCubeEndPoint; // largest point in cube
    private Shape lastShape;
    private Group parent;

    public Group() {
        super();
        shapes = new ArrayList<>();

        boundingCubeEndPoint = new Vector3D(-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE);
        boundingCubeStartPoint = new Vector3D(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);

        parent = null;
    }

    public void addShape(Shape s) {
        shapes.add(s);

        boundingCubeStartPoint = boundingCubeStartPoint.getMinXYZ(s.getSmallestPointInBoundingBox());
        boundingCubeEndPoint = boundingCubeEndPoint.getMaxXYZ(s.getLargestPointInBoundingBox());

        if (s.getClass() == Group.class) {
            ((Group)s).setParent(this);
        }

        if (parent != null) {
            parent.updateBoundingBox(this);
        }
    }

    private void updateBoundingBox(Group g) {
        boundingCubeStartPoint = boundingCubeStartPoint.getMinXYZ(g.getSmallestPointInBoundingBox());
        boundingCubeEndPoint = boundingCubeEndPoint.getMaxXYZ(g.getLargestPointInBoundingBox());

        if (parent != null) {
            parent.updateBoundingBox(this);
        }
    }

    @Override
    protected Vector3D getNormalVector(Vector3D hitPoint, Vector3D startPoint) {
        return lastShape.getNormalVector(hitPoint, startPoint);
    }

    @Override
    protected Vector3D getReflectiveDirection(Vector3D startPoint, Vector3D direction, Vector3D hitPoint) {
        return lastShape.getReflectiveDirection(startPoint, direction, hitPoint);
    }

    @Override
    protected Vector3D getIntersectionPoint(Vector3D startPoint, Vector3D direction, Shape shapeToIgnore) {
        if (isHittingBoundingCube(startPoint, direction)) {
            double smallestDistance = Double.MAX_VALUE;
            Vector3D closestPoint = null;
            for (Shape s : shapes) {
                if (s == shapeToIgnore) {
                    continue;
                }
                Vector3D intersectionPoint = s.getIntersectionPoint(startPoint, direction, shapeToIgnore);
                if (intersectionPoint != null) {
                    double distance = intersectionPoint.sub(startPoint).getMagnitude();
                    if (distance < smallestDistance) {
                        smallestDistance = distance;
                        closestPoint = intersectionPoint;
                        lastShape = s;
                    }
                }
            }
            return closestPoint;
        }
        return null;
    }

    private boolean isHittingBoundingCube(Vector3D startPoint, Vector3D direction) {
        Vector3D directionFraction = new Vector3D(1.f / direction.getX(), 1.f / direction.getY(), 1.f / direction.getZ());
        if (directionFraction.getX() != directionFraction.getX()) { // it's nan
            directionFraction = new Vector3D(Double.MAX_VALUE, directionFraction.getY(), directionFraction.getZ());
        }
        if (directionFraction.getY() != directionFraction.getY()) { // it's nan
            directionFraction = new Vector3D(directionFraction.getX(), Double.MAX_VALUE, directionFraction.getZ());
        }
        if (directionFraction.getZ() != directionFraction.getZ()) { // it's nan
            directionFraction = new Vector3D(directionFraction.getX(), directionFraction.getY(), Double.MAX_VALUE);
        }

        double t1 = (boundingCubeStartPoint.getX() - startPoint.getX()) * directionFraction.getX();
        double t2 = (boundingCubeEndPoint.getX() - startPoint.getX()) * directionFraction.getX();
        double t3 = (boundingCubeStartPoint.getY() - startPoint.getY()) * directionFraction.getY();
        double t4 = (boundingCubeEndPoint.getY() - startPoint.getY()) * directionFraction.getY();
        double t5 = (boundingCubeStartPoint.getZ() - startPoint.getZ()) * directionFraction.getZ();
        double t6 = (boundingCubeEndPoint.getZ() - startPoint.getZ()) * directionFraction.getZ();

        double tmin = Math.max(Math.max(Math.min(t1, t2), Math.min(t3, t4)), Math.min(t5, t6));
        double tmax = Math.min(Math.min(Math.max(t1, t2), Math.max(t3, t4)), Math.max(t5, t6));

        if (tmax < 0) { // we are hitting, bus it's behind os
            return false;
        } else if (tmin > tmax) { // we don't hit
            return false;
        }
        return true;
    }

    @Override
    protected Vector3D getSmallestPointInBoundingBox() {
        return boundingCubeStartPoint;
    }

    @Override
    protected Vector3D getLargestPointInBoundingBox() {
        return boundingCubeEndPoint;
    }

    @Override
    protected Shape getShape() {
        return lastShape.getShape();
    }

    @Override
    public Color getColor() {
        return (lastShape != null) ? lastShape.getColor() : super.getColor();
    }

    @Override
    public double getTransparency() {
        return (lastShape != null) ? lastShape.getTransparency() : super.getTransparency();
    }

    @Override
    public double getReflectiveness() {
        return (lastShape != null) ? lastShape.getReflectiveness() : super.getReflectiveness();
    }

    @Override
    public void setColor(Color color) {
        for (Shape s : shapes) {
            s.setColor(color);
        }
    }

    @Override
    public void setTransparency(double transparency) {
        for (Shape s : shapes) {
            s.setTransparency(transparency);
        }
    }

    @Override
    public void setReflectiveness(double reflectiveness) {
        for (Shape s : shapes) {
            s.setReflectiveness(reflectiveness);
        }
    }

    @Override
    public Shape getByName(String name) {
        if (this.name.equals(name)) {
            return this;
        }
        for (Shape s : shapes) {
            Shape shape = s.getByName(name);
            if (shape != null) {
                return shape;
            }
        }
        return null;
    }

    @Override
    public void removeByName(String name) {
        Shape s = getByName(name);
        if (s != null) {
            shapes.remove(s);
            return;
        }
        for (Shape shape : shapes) {
            shape.removeByName(name);
        }
    }

    @Override
    public List<Shape> getShapes() {
        return shapes;
    }

    public void setParent(Group parent) {
        this.parent = parent;
    }

    public Group getParent() {
        return parent;
    }

    public void groupUp() {
        if (shapes.size() > 0) {
            shapes = Utils.groupUpShapes(shapes).getShapes();
        }
    }
}
