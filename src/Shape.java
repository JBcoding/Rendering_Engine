import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by madsbjoern on 30/10/2016.
 */
public abstract class Shape {
    protected Color color;
    protected double transparency;
    protected double reflectiveness;
    protected Matrix3x3 transformationMatrix, inverseTransformationMatrix;
    protected double stretchX, stretchY, stretchZ;
    protected double rotationX, rotationY, rotationZ;
    protected String name;
    /*
    reflectiveness overrides transparency
    so if you have:
    reflectiveness = .5 and transparency = .5
    then you will see 50% of the reflected color, 25% of the transparency color and 25% of the original color
    */

    public Shape() {
        color = Color.gray;
        transparency = 0;
        reflectiveness = 0;

        stretchX = stretchY = stretchZ = 1;
        rotationX = rotationY = rotationZ = 0;
        updateTransformationMatrix();

        name = "-1";
    }

    public Color getColor(Vector3D startPoint, Vector3D direction, RenderingEngine renderingEngine) {
        Vector3D hitPoint = getIntersectionPoint(startPoint, direction, null);
        Vector3D normalVector = getNormalVector(hitPoint, startPoint);
        Color c = Utils.mergeColors(getColor(), Color.BLACK, renderingEngine.getLightIntensity(hitPoint, normalVector, getShape()));
        if (getTransparency() == 0 && getReflectiveness() == 0) {
            return c;
        }
        if (getTransparency() != 0) {
            Color transparencyColor = renderingEngine.getColorByRaycast(hitPoint, direction, getTransparency(), getShape());
            c = Utils.mergeColors(transparencyColor, c, getTransparency());
        }
        if (getReflectiveness() != 0) {
            Vector3D reflectiveDirection = getReflectiveDirection(startPoint, direction, hitPoint);
            Color reflectivenessColor = renderingEngine.getColorByRaycast(hitPoint, reflectiveDirection, getReflectiveness(), getShape());
            c = Utils.mergeColors(reflectivenessColor, c, getReflectiveness());
        }
        return c;
    }

    protected abstract Vector3D getNormalVector(Vector3D hitPoint, Vector3D startPoint);

    public double getDistance(Vector3D startPoint, Vector3D direction, Shape shapeToIgnore) {
        Vector3D intersectionPoint = getIntersectionPoint(startPoint, direction, shapeToIgnore);
        if (intersectionPoint == null) {
            return Double.MAX_VALUE;
        }
        return intersectionPoint.sub(startPoint).getMagnitude();
    }

    protected abstract Vector3D getReflectiveDirection(Vector3D startPoint, Vector3D direction, Vector3D hitPoint);

    protected abstract Vector3D getIntersectionPoint(Vector3D startPoint, Vector3D direction, Shape shapeToIgnore);

    protected abstract Vector3D getSmallestPointInBoundingBox();

    protected abstract Vector3D getLargestPointInBoundingBox();

    protected abstract Shape getShape();

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public void setReflectiveness(double reflectiveness) {
        this.reflectiveness = reflectiveness;
    }

    public double getReflectiveness() {
        return reflectiveness;
    }

    public void setTransparency(double transparency) {
        this.transparency = transparency;
    }

    public double getTransparency() {
        return transparency;
    }

    private void updateTransformationMatrix() {
        // first we apply the stretching, then the rotation
        transformationMatrix = new Matrix3x3(rotationX, rotationY, rotationZ).multiplyWithMatrix3x3(new Matrix3x3(new Vector3D(stretchX, stretchY, stretchZ)));
        inverseTransformationMatrix = transformationMatrix.getInverseMatrix();
    }

    public void setRotationX(double rotationX) {
        this.rotationX = rotationX;
        updateTransformationMatrix();
    }

    public void setRotationY(double rotationY) {
        this.rotationY = rotationY;
        updateTransformationMatrix();
    }

    public void setRotationZ(double rotationZ) {
        this.rotationZ = rotationZ;
        updateTransformationMatrix();
    }

    public void setStretchX(double stretchX) {
        this.stretchX = stretchX;
        updateTransformationMatrix();
    }

    public void setStretchY(double stretchY) {
        this.stretchY = stretchY;
        updateTransformationMatrix();
    }

    public void setStretchZ(double stretchZ) {
        this.stretchZ = stretchZ;
        updateTransformationMatrix();
    }

    public void setName(String name) {
        this.name = name;
    }

    public Shape getByName(String name) { // overridden in group
        if (this.name.equals(name)) {
            return this;
        }
        return null;
    }

    public void removeByName(String name) { // overridden in group
        return;
    }


    public List<Shape> getShapes() {
        return Arrays.asList(this);
    }
}