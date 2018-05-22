import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GenerateEarthDemo {
    public static void main(String[] args) throws IOException {
        createImage();
    }

    public static void createImage() throws IOException {
        RenderingEngine r = new RenderingEngine(2060, 2060);

        BufferedImage map = ImageIO.read(new File("Equirectangular_projection_SW.png"));

        int radius = 50;
        Light l = new Light(new Vector3D(2 * radius, 2 * radius, 2 * radius), Light.LIGHT_FADING_LINEAR, 6 * radius);
        l.setName("light");
        r.addLight(l);

        for (int x = -radius - 1; x <= radius + 1; x ++) {
            for (int y = -radius - 1; y <= radius + 1; y ++) {
                for (int z = -radius - 1; z <= radius + 1; z ++) {
                    if (x * x + y * y + z * z < radius * radius && x * x + y * y + z * z >= (radius - 1) * (radius - 1)) {
                        double angle = Math.atan2(z, x);
                        int pixelX , pixelY;
                        pixelY = (int) ((y + radius - 1 + .5) * (map.getHeight() / (2 * radius - 1)));
                        pixelX = (int) ((angle / (Math.PI * 2) + .5) * map.getWidth());

                        pixelX = Math.max(0, Math.min(map.getWidth() - 1, pixelX));
                        pixelY = Math.max(0, Math.min(map.getHeight() - 1, pixelY));

                        pixelY = map.getHeight() - 1 - pixelY;

                        // get median color
                        List<Integer> reds = new ArrayList<>();
                        List<Integer> greens = new ArrayList<>();
                        List<Integer> blues = new ArrayList<>();
                        for (int dx = -5; dx <= 5; dx ++) {
                            for (int dy = -5; dy <= 5; dy ++) {
                                int dPixelX = pixelX + dx;
                                int dPixelY = pixelY + dy;

                                dPixelX = Math.max(0, Math.min(map.getWidth() - 1, dPixelX));
                                dPixelY = Math.max(0, Math.min(map.getHeight() - 1, dPixelY));

                                int rgb = map.getRGB(dPixelX, dPixelY);
                                reds.add((rgb >> 16) & 0xff);
                                greens.add((rgb >> 8) & 0xff);
                                blues.add(rgb & 0xff);
                            }
                        }
                        Collections.sort(reds);
                        Collections.sort(greens);
                        Collections.sort(blues);

                        int red = reds.get(reds.size() / 3);
                        int green = greens.get(greens.size() / 3);
                        int blue = blues.get(blues.size() / 3);

                        addCube(r, new Vector3D(x, y, z), new Color(red, green, blue));
                    }
                }
            }
        }

        r.groupUp();

        for (int imageNumber = 12; imageNumber < 16; imageNumber ++) {
            double i = imageNumber + .5;
            r.setCameraPosition(new Vector3D(Math.cos(i / 16.0 * Math.PI * 2) * radius * 1.3, radius * 2, Math.sin(i / 16.0 * Math.PI * 2) * radius * 1.3));
            r.setCameraDirection(r.getCameraPosition().scale(-1).add(new Vector3D(0, radius * 1, 0)));

            l.setPosition(r.getCameraPosition().rotateAroundAxis(
                    new Vector3D(0, 1, 0),
                    new Vector3D(0, 0, 0),
                    -Math.PI / 16
            ));

            BufferedImage image = r.renderImage(1);
            File file = new File("image" + imageNumber + ".png");
            try {
                ImageIO.write(image, "png", file);
                System.out.println(imageNumber);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void addTriangle(Group group, Vector3D p1, Vector3D p2, Vector3D p3, Color c) {
        Triangle t = new Triangle(p1, p2, p3);
        t.setColor(c);
        group.addShape(t);
    }

    public static void addCube(RenderingEngine r, Vector3D pos, Color c) {
        Group g = new Group();

        Vector3D p1 = new Vector3D(pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5);
        Vector3D p2 = new Vector3D(pos.getX() + .5, pos.getY() + .5, pos.getZ() - .5);
        Vector3D p3 = new Vector3D(pos.getX() + .5, pos.getY() - .5, pos.getZ() - .5);
        Vector3D p4 = new Vector3D(pos.getX() + .5, pos.getY() - .5, pos.getZ() + .5);
        Vector3D p5 = new Vector3D(pos.getX() - .5, pos.getY() + .5, pos.getZ() + .5);
        Vector3D p6 = new Vector3D(pos.getX() - .5, pos.getY() + .5, pos.getZ() - .5);
        Vector3D p7 = new Vector3D(pos.getX() - .5, pos.getY() - .5, pos.getZ() - .5);
        Vector3D p8 = new Vector3D(pos.getX() - .5, pos.getY() - .5, pos.getZ() + .5);

        addTriangle(g, p1, p2, p3, c);
        addTriangle(g, p1, p3, p4, c);
        addTriangle(g, p5, p6, p7, c);
        addTriangle(g, p5, p7, p8, c);
        addTriangle(g, p1, p5, p6, c);
        addTriangle(g, p1, p6, p2, c);
        addTriangle(g, p2, p6, p7, c);
        addTriangle(g, p2, p7, p3, c);
        addTriangle(g, p3, p7, p8, c);
        addTriangle(g, p3, p8, p4, c);
        addTriangle(g, p4, p8, p1, c);
        addTriangle(g, p1, p8, p5, c);

        r.addShape(g);
    }
}
