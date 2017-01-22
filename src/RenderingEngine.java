import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by madsbjoern on 02/12/2016.
 */
public class RenderingEngine {
    private Vector3D cameraPosition;
    private Vector3D cameraDirection;
    private int imageHeight;
    private int imageWidth;

    private List<Shape> shapes;
    private double minRayCastingColorFraction;
    private int maxRayCastingRecursionDepth;

    private List<Light> lights;
    private double backgroundLight; // Kind'a like background radiation, this is light that will always influence all object

    private double currentRayCastingColorFraction;
    private int currentRayCastingRecursionDepth; // we can max make 2^n calls (if all objects is some part transparent and som part reflective)

    public RenderingEngine(int width, int height) {
        imageHeight = height;
        imageWidth = width;

        shapes = new ArrayList<>();
        lights = new ArrayList<>();
        maxRayCastingRecursionDepth = 10;
        minRayCastingColorFraction = 0.01;

        cameraPosition = new Vector3D(0, 0, 0);
        cameraDirection = new Vector3D(0, 0, 1);

        backgroundLight = .5;
    }

    public void addShape(Shape s) {
        shapes.add(s);
    }

    public void addLight(Light l) {
        lights.add(l);
    }

    protected double getLightIntensity(Vector3D hitPoint, Vector3D normalVector, Shape shapeToIgnore) {
        double lightIntensity = 0;
        for (Light l : lights) {
            Vector3D directionToLight = l.getPosition().sub(hitPoint);
            double distanceToLight = directionToLight.getMagnitude();
            directionToLight = directionToLight.getUnitVector();
            double angle = normalVector.getAngleToVector(directionToLight);
            double Influence = Math.cos(angle);
            if (Influence <= 0) { // Light source is behind the object
                continue;
            }
            Shape firstShapeInDirectionToLightSource = getShapeByRaycast(hitPoint, directionToLight, shapeToIgnore);
            Vector3D tempHitPoint = hitPoint;
            double blockedByObjects = 0;
            int count = 0;
            while (firstShapeInDirectionToLightSource != null) {
                if (count >= maxRayCastingRecursionDepth) {
                    break;
                }
                tempHitPoint = firstShapeInDirectionToLightSource.getIntersectionPoint(tempHitPoint, directionToLight);
                if (tempHitPoint.sub(hitPoint).getMagnitude() > distanceToLight) {
                    break;
                }
                blockedByObjects = 1 - (1 - blockedByObjects) * firstShapeInDirectionToLightSource.transparency;
                if (blockedByObjects >= 1 - minRayCastingColorFraction) {
                    break;
                }
                firstShapeInDirectionToLightSource = getShapeByRaycast(tempHitPoint, directionToLight, firstShapeInDirectionToLightSource);
                count ++;
            }
            lightIntensity = 1 - (1 - lightIntensity) * (1 - l.getLightInfluence(hitPoint) * (1 - blockedByObjects) * Influence);
            if (lightIntensity == 1) { // we have hit maximum light intensity, this will never get brighter
                return lightIntensity;
            }
        }
        return 1 - (1 - lightIntensity) * (1 - backgroundLight);
    }

    private Shape getShapeByRaycast(Vector3D startPoint, Vector3D direction, Shape shapeToIgnore) {
        double smallestDistance = Double.MAX_VALUE;
        Shape closestShape = null;
        for (Shape s : shapes) {
            if (s == shapeToIgnore) { // yes i mean a reference comparison -_-
                continue;
            }
            double distance = s.getDistance(startPoint, direction);
            if (distance < smallestDistance) {
                smallestDistance = distance;
                closestShape = s;
            }
        }
        return closestShape;
    }

    protected Color getColorByRaycast(Vector3D startPoint, Vector3D direction, double fractionThisCountsFor, Shape shapeToIgnore) {
        currentRayCastingRecursionDepth++;
        currentRayCastingColorFraction *= fractionThisCountsFor;
        Color color;
        if (currentRayCastingColorFraction < minRayCastingColorFraction || currentRayCastingRecursionDepth > maxRayCastingRecursionDepth) {
            color = Color.black;
        } else {
            color = getColorByRaycast(startPoint, direction, shapeToIgnore);
        }
        currentRayCastingColorFraction /= fractionThisCountsFor;
        currentRayCastingRecursionDepth--;
        return color;
    }

    private Color getColorByRaycast(Vector3D startPoint, Vector3D direction, Shape shapeToIgnore) {
        Shape closestShape = getShapeByRaycast(startPoint, direction, shapeToIgnore);
        if (closestShape == null) {
            return Color.black;
        } else {
            return closestShape.getColor(startPoint, direction, this);
        }
    }

    public synchronized BufferedImage renderImage() {
        double aspectRatio = (double)imageWidth / imageHeight;
        double cameraWidthInRadians = 1.5; // in range ]0; pi / 2[,   note pi / 2 is NOT in the acceptable interval
        double cameraHeightInRadians = Math.asin(Math.sin(cameraWidthInRadians) / aspectRatio);
        int[] pixels = new int[imageWidth * imageHeight];

        // Yaw, pitch, and roll rotations
        // https://www.grc.nasa.gov/www/k-12/airplane/Images/rotations.gif
        Vector3D axisToPitchAround = cameraDirection.cross(cameraDirection.add(new Vector3D(0, 1, 0)));
        Vector3D axisToYawAround = new Vector3D(0, -1, 0);

        Vector3D leftSide = cameraDirection.rotateAroundAxis(axisToYawAround, new Vector3D(), cameraWidthInRadians);
        Vector3D rightSide = cameraDirection.rotateAroundAxis(axisToYawAround, new Vector3D(), -cameraWidthInRadians);
        Vector3D topSide = cameraDirection.rotateAroundAxis(axisToPitchAround, new Vector3D(), cameraHeightInRadians);
        Vector3D bottomSide = cameraDirection.rotateAroundAxis(axisToPitchAround, new Vector3D(), -cameraHeightInRadians);

        currentRayCastingColorFraction = 1;

        for (int x = 0; x < imageWidth; x ++) {
            for (int y = 0; y < imageHeight; y ++) {
                double px = (x + 0.5) / imageWidth;
                double py = (y + 0.5) / imageHeight;
                Vector3D direction = new Vector3D();
                direction = direction.add(leftSide.scale(1 - px));
                direction = direction.add(rightSide.scale(px));
                direction = direction.add(topSide.scale(1 - py));
                direction = direction.add(bottomSide.scale(py));
                direction = direction.getUnitVector();
                pixels[y * imageWidth + x] = getColorByRaycast(cameraPosition, direction, null).getRGB();
            }
        }
        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_4BYTE_ABGR);
        image.setRGB(0, 0, imageWidth, imageHeight, pixels, 0, imageWidth);

        return image;
    }

    public void setCameraPosition(Vector3D cameraPosition) {
        this.cameraPosition = cameraPosition;
    }

    public Vector3D getCameraPosition() {
        return cameraPosition;
    }

    public void setCameraDirection(Vector3D cameraDirection) {
        this.cameraDirection = cameraDirection;
    }

    public Vector3D getCameraDirection() {
        return cameraDirection;
    }

    public void setBackgroundLight(double backgroundLight) {
        this.backgroundLight = backgroundLight;
    }

    public List<Shape> getShapes() {
        return shapes;
    }

    public Shape getShapeByName(String name) {
        for (Shape s : shapes) {
            Shape shape = s.getByName(name);
            if (shape != null) {
                return shape;
            }
        }
        return null;
    }

    public Light getLightByName(String name) {
        for (Light l : lights) {
            if (l.getName().equals(name)) {
                return l;
            }
        }
        return null;
    }

    public void removeByName(String name) {
        Shape s = getShapeByName(name);
        if (s != null) {
            shapes.remove(s);
            return;
        }
        Light l = getLightByName(name);
        if (l != null) {
            lights.remove(l);
            return;
        }
        for (Shape shape : shapes) {
            shape.removeByName(name);
        }
    }
}