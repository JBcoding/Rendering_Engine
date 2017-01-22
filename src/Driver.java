import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Created by madsbjoern on 01/12/2016.
 */
public class Driver {
    public static void main(String[] args) throws IOException {
        createImages(args[0], Integer.parseInt(args[1]));
    }

    public static void createImages(String fileName, int imageNumber) throws IOException {
        long startTime = System.nanoTime();
        RenderingEngine r = FileParser.parseFileToImageNumber(new File(fileName), imageNumber);
        BufferedImage image = r.renderImage();
        File file = new File("image" + imageNumber + ".png");
        ImageIO.write(image, "png", file);
        System.out.println("Frame: " + imageNumber + "\t\tTime: " + (System.nanoTime() - startTime) / 1e9 + " seconds");
    }
}
