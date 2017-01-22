import java.awt.*;

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
}
