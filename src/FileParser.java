import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.IllegalFormatException;
import java.util.Scanner;

/**
 * Created by madsbjoern on 22/01/2017.
 */
public final class FileParser {
    public static RenderingEngine parseFileToImageNumber(File file, int imageNumber) throws FileNotFoundException { // imageNumber = 0, for first image
        Scanner input = new Scanner(file);

        // First line must be "VERSION X", where X is an integer
        int version = Integer.parseInt(input.nextLine().replace("VERSION ", ""));

        // Second line must be "SIZE X Y", where X and Y is integers.
        String line = input.nextLine();
        int width = Integer.parseInt(line.split(" ")[1]);
        int height = Integer.parseInt(line.split(" ")[2]);

        RenderingEngine renderingEngine = new RenderingEngine(width, height);

        int frameCount = 0;

        Shape lastShape = null;
        Light lastLight = null;

        Group currentGroup = null;

        int i = 0;
        while (input.hasNextLine()) {
            if (i % 1000 == 0) {
                System.out.println(i);
            }
            i ++;
            line = input.nextLine();
            String[] parts = line.split(" ");
            if (parts[0].equals("NEW")) {
                if (parts[1].equals("TRIANGLE")) {
                    Triangle t = new Triangle(new Vector3D(Double.parseDouble(parts[2]), Double.parseDouble(parts[3]), Double.parseDouble(parts[4])),
                            new Vector3D(Double.parseDouble(parts[5]), Double.parseDouble(parts[6]), Double.parseDouble(parts[7])),
                            new Vector3D(Double.parseDouble(parts[8]), Double.parseDouble(parts[9]), Double.parseDouble(parts[10])));
                    if (parts.length >= 12) {
                        t.setName(parts[11]);
                    }
                    lastShape = t;
                    if (currentGroup != null) {
                        currentGroup.addShape(t);
                    } else {
                        renderingEngine.addShape(t);
                    }
                } else if (parts[1].equals("SPHERE")) {
                    Sphere s = new Sphere(new Vector3D(Double.parseDouble(parts[2]), Double.parseDouble(parts[3]), Double.parseDouble(parts[4])),
                            Double.parseDouble(parts[5]));
                    if (parts.length >= 7) {
                        s.setName(parts[6]);
                    }
                    lastShape = s;
                    if (currentGroup != null) {
                        currentGroup.addShape(s);
                    } else {
                        renderingEngine.addShape(s);
                    }
                } else if (parts[1].equals("GROUP")) {
                    Group g = new Group();
                    if (parts.length >= 3) {
                        g.setName(parts[2]);
                    }
                    lastShape = g;
                    if (currentGroup != null) {
                        currentGroup.addShape(g);
                    } else {
                        renderingEngine.addShape(g);
                    }
                    currentGroup = g;
                } else if (parts[1].equals("LIGHT")) {
                    Light l = new Light(new Vector3D(Double.parseDouble(parts[2]), Double.parseDouble(parts[3]), Double.parseDouble(parts[4])),
                            Integer.parseInt(parts[5]), Double.parseDouble(parts[6]));
                    if (parts.length >= 8) {
                        l.setName(parts[7]);
                    }
                    renderingEngine.addLight(l);
                    lastLight = l;
                } else {
                    throw new IllegalArgumentException("ERROR");
                }
            } else if (parts[0].equals("EDIT")) {
                if (parts[1].equals("SHAPE")) {
                    String name = parts[2];
                    Shape s = renderingEngine.getShapeByName(name);
                    if (name.equals("NULL")) {
                        s = lastShape;
                    }
                    if (parts[3].equals("COLOR")) {
                        Color c = new Color(Integer.parseInt(parts[4]), Integer.parseInt(parts[5]), Integer.parseInt(parts[6]));
                        s.setColor(c);
                    } else if (parts[3].equals("TRANSPARENCY")) {
                        s.setTransparency(Double.parseDouble(parts[4]));
                    } else if (parts[3].equals("REFLECTIVENESS")) {
                        s.setReflectiveness(Double.parseDouble(parts[4]));
                    } else if (parts[3].equals("STRETCH_X")) {
                        s.setStretchX(Double.parseDouble(parts[4]));
                    } else if (parts[3].equals("STRETCH_Y")) {
                        s.setStretchY(Double.parseDouble(parts[4]));
                    } else if (parts[3].equals("STRETCH_Z")) {
                        s.setStretchZ(Double.parseDouble(parts[4]));
                    } else if (parts[3].equals("ROTATION_X")) {
                        s.setRotationX(Double.parseDouble(parts[4]));
                    } else if (parts[3].equals("ROTATION_Y")) {
                        s.setRotationY(Double.parseDouble(parts[4]));
                    } else if (parts[3].equals("ROTATION_Z")) {
                        s.setRotationZ(Double.parseDouble(parts[4]));
                    } else {
                        throw new IllegalArgumentException("ERROR");
                    }
                } else if (parts[1].equals("LIGHT")) {
                    String name = parts[2];
                    Light l = renderingEngine.getLightByName(name);
                    if (name.equals("NULL")) {
                        l = lastLight;
                    }
                    if (parts[3].equals("LIGHT_FADING")) {
                        l.setLightFading(Integer.parseInt(parts[4]));
                    } else if (parts[3].equals("INTENSITY")) {
                        l.setIntensity(Double.parseDouble(parts[4]));
                    } else if (parts[3].equals("POSITION")) {
                        l.setPosition(new Vector3D(Double.parseDouble(parts[4]), Double.parseDouble(parts[5]), Double.parseDouble(parts[6])));
                    } else {
                        throw new IllegalArgumentException("ERROR");
                    }
                } else if (parts[1].equals("CAMERA")) {
                    if (parts[2].equals("POSITION")) {
                        renderingEngine.setCameraPosition(new Vector3D(Double.parseDouble(parts[3]), Double.parseDouble(parts[4]), Double.parseDouble(parts[5])));
                    } else if (parts[2].equals("DIRECTION")) {
                        renderingEngine.setCameraDirection(new Vector3D(Double.parseDouble(parts[3]), Double.parseDouble(parts[4]), Double.parseDouble(parts[5])));
                    } else {
                        throw new IllegalArgumentException("ERROR");
                    }
                } else {
                    throw new IllegalArgumentException("ERROR");
                }
            } else if (parts[0].equals("REMOVE")) {
                String name = parts[1];
                renderingEngine.removeByName(name);
            } else if (parts[0].equals("FRAME")) {
                frameCount ++;
                if (frameCount > imageNumber) {
                    break;
                }
            } else if (parts[0].equals("END") && parts[1].equals("GROUP")) {
                currentGroup = currentGroup.getParent();
            } else {
                throw new IllegalArgumentException("ERROR");
            }
        }

        renderingEngine.groupUp();

        return renderingEngine;
    }
}
