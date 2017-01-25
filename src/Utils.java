import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by madsbjoern on 02/12/2016.
 */
public final class Utils {
    public static Color mergeColors(Color c1, Color c2, double p) {
        int newR = (int)(c2.getRed() * (1 - p) + c1.getRed() * p);
        int newG = (int)(c2.getGreen() * (1 - p) + c1.getGreen() * p);
        int newB = (int)(c2.getBlue() * (1 - p) + c1.getBlue() * p);
        return new Color(newR, newG, newB);
    }

    public static Shape groupUpShapes(List<Shape> shapes) {
        if (shapes.size() == 1) {
            return shapes.get(0);
        }
        List<Shape> newShapes = new ArrayList<>();
        while (shapes.size() > 1) {
            Shape shape = shapes.get(shapes.size() - 1);
            double smallestVolume = Double.MAX_VALUE;
            Shape other = null;
            Vector3D BBSmall = shape.getSmallestPointInBoundingBox();
            Vector3D BBLargest = shape.getLargestPointInBoundingBox();
            for (Shape s : shapes) {
                if (s != shape) {
                    Vector3D newBBSmall = BBSmall.getMinXYZ(s.getSmallestPointInBoundingBox());
                    Vector3D newBBLargest = BBLargest.getMaxXYZ(s.getLargestPointInBoundingBox());
                    double volume = newBBSmall.getBoundingBoxVolumen(newBBLargest);
                    if (volume < smallestVolume) {
                        smallestVolume = volume;
                        other = s;
                    }
                }
            }
            Group g = new Group();
            g.addShape(shape);
            g.addShape(other);
            newShapes.add(g);
            shapes.remove(shape);
            shapes.remove(other);
        }
        if (shapes.size() != 0) {
            newShapes.add(shapes.get(0));
            shapes.clear();
        }
        return groupUpShapes(newShapes);
    }
}
