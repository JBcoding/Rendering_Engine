import java.awt.*;
import java.awt.List;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Created by madsbjoern on 22/01/2017.
 */
public class GenerateChessDemo {
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        createImage();
    }

    public static void createImage() throws FileNotFoundException, UnsupportedEncodingException {
        int i = 0;

        PrintWriter writer = new PrintWriter("ChessDemo.RE", "UTF-8");
        writer.println("VERSION 1");
        writer.println("SIZE 192 108");

        writer.println("NEW LIGHT 5 5 2 " + Light.LIGHT_FADING_LINEAR + " 20 light1");

        Color c1 = new Color(92, 75, 81);
        Color c2 = new Color(242, 235, 191);
        makeChessBord(null, c1, c2, writer);

        makeRook(new Vector3D(-8.75, 0, -8.75), c1, null, writer);
        makeKnight(new Vector3D(-8.75, 0, -6.25), c1, null, true, writer);
        makeBishop(new Vector3D(-8.75, 0, -3.75), c1, null, writer);
        makeQueen(new Vector3D(-8.75, 0, -1.25), c1, null, writer);
        makeKing(new Vector3D(-8.75, 0, 1.25), c1, null, writer);
        makeBishop(new Vector3D(-8.75, 0, 3.75), c1, null, writer);
        makeKnight(new Vector3D(-8.75, 0, 6.25), c1, null, true, writer);
        makeRook(new Vector3D(-8.75, 0, 8.75), c1, null, writer);

        makeRook(new Vector3D(8.75, 0, -8.75), c2, null, writer);
        makeKnight(new Vector3D(8.75, 0, -6.25), c2, null, false, writer);
        makeBishop(new Vector3D(8.75, 0, -3.75), c2, null, writer);
        makeQueen(new Vector3D(8.75, 0, -1.25), c2, null, writer);
        makeKing(new Vector3D(8.75, 0, 1.25), c2, null, writer);
        makeBishop(new Vector3D(8.75, 0, 3.75), c2, null, writer);
        makeKnight(new Vector3D(8.75, 0, 6.25), c2, null, false, writer);
        makeRook(new Vector3D(8.75, 0, 8.75), c2, null, writer);

        for (double ii = -8.75; ii < 9; ii += 2.5) {
            makePawn(new Vector3D(-6.25, 0, ii), c1, null, writer);
            makePawn(new Vector3D(6.25, 0, ii), c2, null, writer);
        }

        System.out.println("Done generating world!");

        for (; i < 300; i ++) {
            writer.println("EDIT CAMERA POSITION " + (8.60 - (Math.pow(i, 1.25) / 100)) + " " + (4.8 - i / 300.0 * 2) + " " + (1.25 - i / 300.0 * 1.25));
            writer.println("EDIT CAMERA DIRECTION " + 1 + " " + (-.05 - i / 300 * 0.2) + " " + 0);
            writer.println("FRAME");
        }

        writer.println("EDIT CAMERA POSITION 0 5 15");
        writer.println("EDIT CAMERA DIRECTION 0 -.25 -1");
        for (; i < 600; i ++) {
            writer.println("EDIT LIGHT light1 POSITION " + (Math.cos(i / 300.0 * Math.PI + Math.PI) * 20) + " " + (Math.sin(i / 300.0 * Math.PI + Math.PI) * 20) + " " + (0));
            writer.println("FRAME");
        }

        writer.close();
    }

    public static void addColorToShape(PrintWriter writer, Color color) {
        writer.println("EDIT SHAPE NULL COLOR " + color.getRed() + " " + color.getGreen() + " " + color.getBlue());
    }

    public static void addTriangleFromPoints(PrintWriter writer, Vector3D p1, Vector3D p2, Vector3D p3) {
        writer.println("NEW TRIANGLE " + p1.toStringEasy() + " " + p2.toStringEasy() + " " + p3.toStringEasy());
        writer.println("EDIT SHAPE NULL REFLECTIVENESS .5");
    }

    public static void addTrianglesFromPoints(PrintWriter writer, Vector3D p1, Vector3D p2, Vector3D p3, Vector3D p4, Color color) {
        addTriangleFromPoints(writer, p1, p2, p3);
        addColorToShape(writer, color);
        addTriangleFromPoints(writer, p2, p3, p4);
        addColorToShape(writer, color);
    }

    public static void makeChessBord(RenderingEngine renderingEngine, Color color1, Color color2, PrintWriter writer) {
        writer.println("NEW GROUP");
        writer.println("NEW TRIANGLE -12.5 -.1 -12.5 -12.5 -.1 12.5 12.5 -.1 12.5");
        writer.println("EDIT SHAPE NULL COLOR " + color1.getRed() + " " + color1.getGreen() + " " + color1.getBlue());
        writer.println("EDIT SHAPE NULL REFLECTIVENESS .5");
        writer.println("NEW TRIANGLE -12.5 -.1 -12.5 12.5 -.1 -12.5 12.5 -.1 12.5");
        writer.println("EDIT SHAPE NULL COLOR " + color1.getRed() + " " + color1.getGreen() + " " + color1.getBlue());
        writer.println("EDIT SHAPE NULL REFLECTIVENESS .5");
        for (double x = -10; x < 9; x += 2.5) {
            for (double y = -10; y < 9; y += 2.5) {
                Vector3D c1 = new Vector3D(x, 0, y);
                Vector3D c2 = new Vector3D(x + 2.5, 0, y);
                Vector3D c3 = new Vector3D(x, 0, y + 2.5);
                Vector3D c4 = new Vector3D(x + 2.5, 0, y + 2.5);
                writer.println("NEW TRIANGLE " + c1.toStringEasy() + " " + c2.toStringEasy() + " " + c3.toStringEasy());
                writer.println("EDIT SHAPE NULL REFLECTIVENESS .7");
                if (((x + y) / 2.5 + 50) % 2 == 1) {
                    writer.println("EDIT SHAPE NULL COLOR " + color1.getRed() + " " + color1.getGreen() + " " + color1.getBlue());
                } else {
                    writer.println("EDIT SHAPE NULL COLOR " + color2.getRed() + " " + color2.getGreen() + " " + color2.getBlue());
                }
                writer.println("NEW TRIANGLE " + c2.toStringEasy() + " " + c3.toStringEasy() + " " + c4.toStringEasy());
                writer.println("EDIT SHAPE NULL REFLECTIVENESS .7");
                if (((x + y) / 2.5 + 50) % 2 == 1) {
                    writer.println("EDIT SHAPE NULL COLOR " + color1.getRed() + " " + color1.getGreen() + " " + color1.getBlue());
                } else {
                    writer.println("EDIT SHAPE NULL COLOR " + color2.getRed() + " " + color2.getGreen() + " " + color2.getBlue());
                }
            }
        }
        writer.println("END GROUP");
    }

    public static void makePawn(Vector3D position, Color color, RenderingEngine renderingEngine, PrintWriter writer) {
        writer.println("NEW GROUP");
        int numberOfPoints = 16; // number of points around the pawn
        for (int i = 0; i < numberOfPoints; i ++) {
            // bottom
            Vector3D p1 = position.add(new Vector3D(Math.cos(i / (double)numberOfPoints * Math.PI * 2) * .75, 0, Math.sin(i / (double)numberOfPoints * Math.PI * 2) * .75));
            Vector3D p2 = position.add(new Vector3D(Math.cos((i + 1) / (double)numberOfPoints * Math.PI * 2) * .75, 0, Math.sin((i + 1) / (double)numberOfPoints * Math.PI * 2) * .75));
            addTriangleFromPoints(writer, position, p1, p2);
            addColorToShape(writer, color);

            // bottom sides
            Vector3D p3 = p1.add(new Vector3D(0, .35, 0));
            Vector3D p4 = p2.add(new Vector3D(0, .35, 0));
            addTrianglesFromPoints(writer, p1, p2, p3, p4, color);

            // next little bit
            Vector3D p5 = position.add(new Vector3D(Math.cos(i / (double)numberOfPoints * Math.PI * 2) * .4, .8, Math.sin(i / (double)numberOfPoints * Math.PI * 2) * .4));
            Vector3D p6 = position.add(new Vector3D(Math.cos((i + 1) / (double)numberOfPoints * Math.PI * 2) * .4, .8, Math.sin((i + 1) / (double)numberOfPoints * Math.PI * 2) * .4));
            addTrianglesFromPoints(writer, p3, p4, p5, p6, color);

            // next little bit
            Vector3D p7 = position.add(new Vector3D(Math.cos(i / (double)numberOfPoints * Math.PI * 2) * .25, 1.2, Math.sin(i / (double)numberOfPoints * Math.PI * 2) * .25));
            Vector3D p8 = position.add(new Vector3D(Math.cos((i + 1) / (double)numberOfPoints * Math.PI * 2) * .25, 1.2, Math.sin((i + 1) / (double)numberOfPoints * Math.PI * 2) * .25));
            addTrianglesFromPoints(writer, p5, p6, p7, p8, color);

            // next little bit
            Vector3D p9 = position.add(new Vector3D(Math.cos(i / (double)numberOfPoints * Math.PI * 2) * .25, 1.6, Math.sin(i / (double)numberOfPoints * Math.PI * 2) * .25));
            Vector3D p10 = position.add(new Vector3D(Math.cos((i + 1) / (double)numberOfPoints * Math.PI * 2) * .25, 1.6, Math.sin((i + 1) / (double)numberOfPoints * Math.PI * 2) * .25));
            addTrianglesFromPoints(writer, p7, p8, p9, p10, color);
        }

        writer.println("NEW SPHERE " + position.add(new Vector3D(0, (2 - .3122499 / 2), 0)).toStringEasy() +  " .4");
        writer.println("EDIT SHAPE NULL REFLECTIVENESS .5");
        addColorToShape(writer, color);

        writer.println("END GROUP");
    }

    public static void makeRook(Vector3D position, Color color, RenderingEngine renderingEngine, PrintWriter writer) {
        writer.println("NEW GROUP");
        int numberOfPoints = 16; // number of points around the pawn
        for (int i = 0; i < numberOfPoints; i ++) {
            // bottom
            Vector3D p1 = position.add(new Vector3D(Math.cos(i / (double)numberOfPoints * Math.PI * 2) * .87, 0, Math.sin(i / (double)numberOfPoints * Math.PI * 2) * .87));
            Vector3D p2 = position.add(new Vector3D(Math.cos((i + 1) / (double)numberOfPoints * Math.PI * 2) * .87, 0, Math.sin((i + 1) / (double)numberOfPoints * Math.PI * 2) * .87));
            addTriangleFromPoints(writer, position, p1, p2);
            addColorToShape(writer, color);

            // bottom sides
            Vector3D p3 = p1.add(new Vector3D(0, .43, 0));
            Vector3D p4 = p2.add(new Vector3D(0, .43, 0));
            addTriangleFromPoints(writer, p1, p2, p3);
            addColorToShape(writer, color);
            addTriangleFromPoints(writer, p2, p3, p4);
            addColorToShape(writer, color);

            // next little bit
            Vector3D p5 = position.add(new Vector3D(Math.cos(i / (double)numberOfPoints * Math.PI * 2) * .6, .96, Math.sin(i / (double)numberOfPoints * Math.PI * 2) * .6));
            Vector3D p6 = position.add(new Vector3D(Math.cos((i + 1) / (double)numberOfPoints * Math.PI * 2) * .6, .96, Math.sin((i + 1) / (double)numberOfPoints * Math.PI * 2) * .6));
            addTriangleFromPoints(writer, p3, p4, p5);
            addColorToShape(writer, color);
            addTriangleFromPoints(writer, p4, p5, p6);
            addColorToShape(writer, color);

            // next little bit
            Vector3D p7 = position.add(new Vector3D(Math.cos(i / (double)numberOfPoints * Math.PI * 2) * .45, 1.73, Math.sin(i / (double)numberOfPoints * Math.PI * 2) * .45));
            Vector3D p8 = position.add(new Vector3D(Math.cos((i + 1) / (double)numberOfPoints * Math.PI * 2) * .45, 1.73, Math.sin((i + 1) / (double)numberOfPoints * Math.PI * 2) * .45));
            addTriangleFromPoints(writer, p5, p6, p7);
            addColorToShape(writer, color);
            addTriangleFromPoints(writer, p6, p7, p8);
            addColorToShape(writer, color);

            // next little bit
            Vector3D p9 = position.add(new Vector3D(Math.cos(i / (double)numberOfPoints * Math.PI * 2) * .45, 2.25, Math.sin(i / (double)numberOfPoints * Math.PI * 2) * .45));
            Vector3D p10 = position.add(new Vector3D(Math.cos((i + 1) / (double)numberOfPoints * Math.PI * 2) * .45, 2.25, Math.sin((i + 1) / (double)numberOfPoints * Math.PI * 2) * .45));
            addTrianglesFromPoints(writer, p7, p8, p9, p10, color);

            // next little bit
            p7 = p9;
            p8 = p10;
            p9 = position.add(new Vector3D(Math.cos(i / (double)numberOfPoints * Math.PI * 2) * .6, 2.5, Math.sin(i / (double)numberOfPoints * Math.PI * 2) * .6));
            p10 = position.add(new Vector3D(Math.cos((i + 1) / (double)numberOfPoints * Math.PI * 2) * .6, 2.5, Math.sin((i + 1) / (double)numberOfPoints * Math.PI * 2) * .6));
            addTrianglesFromPoints(writer, p7, p8, p9, p10, color);

            // next little bit
            p7 = p9;
            p8 = p10;
            p9 = position.add(new Vector3D(Math.cos(i / (double)numberOfPoints * Math.PI * 2) * .6, 2.8, Math.sin(i / (double)numberOfPoints * Math.PI * 2) * .6));
            p10 = position.add(new Vector3D(Math.cos((i + 1) / (double)numberOfPoints * Math.PI * 2) * .6, 2.8, Math.sin((i + 1) / (double)numberOfPoints * Math.PI * 2) * .6));
            addTrianglesFromPoints(writer, p7, p8, p9, p10, color);

            // next little bit
            p7 = p9;
            p8 = p10;
            p9 = position.add(new Vector3D(0, 2.8, 0));
            p10 = position.add(new Vector3D(0, 2.8, 0));
            addTrianglesFromPoints(writer, p7, p8, p9, p10, color);

            // next little bit
            p9 = position.add(new Vector3D(Math.cos(i / (double)numberOfPoints * Math.PI * 2) * .6, 3.0, Math.sin(i / (double)numberOfPoints * Math.PI * 2) * .6));
            p10 = position.add(new Vector3D(Math.cos((i + 1) / (double)numberOfPoints * Math.PI * 2) * .6, 3.0, Math.sin((i + 1) / (double)numberOfPoints * Math.PI * 2) * .6));
            if ((i / (numberOfPoints / 8)) % 2 == 0) {
                addTrianglesFromPoints(writer, p7, p8, p9, p10, color);
            }

            // next little bit
            p7 = p9;
            p8 = p10;
            p9 = position.add(new Vector3D(Math.cos(i / (double)numberOfPoints * Math.PI * 2) * .4, 3.0, Math.sin(i / (double)numberOfPoints * Math.PI * 2) * .4));
            p10 = position.add(new Vector3D(Math.cos((i + 1) / (double)numberOfPoints * Math.PI * 2) * .4, 3.0, Math.sin((i + 1) / (double)numberOfPoints * Math.PI * 2) * .4));
            if ((i / (numberOfPoints / 8)) % 2 == 0) {
                addTrianglesFromPoints(writer, p7, p8, p9, p10, color);
            }

            // next little bit
            p7 = p9;
            p8 = p10;
            p9 = position.add(new Vector3D(Math.cos(i / (double)numberOfPoints * Math.PI * 2) * .4, 2.8, Math.sin(i / (double)numberOfPoints * Math.PI * 2) * .4));
            p10 = position.add(new Vector3D(Math.cos((i + 1) / (double)numberOfPoints * Math.PI * 2) * .4, 2.8, Math.sin((i + 1) / (double)numberOfPoints * Math.PI * 2) * .4));
            if ((i / (numberOfPoints / 8)) % 2 == 0) {
                addTrianglesFromPoints(writer, p7, p8, p9, p10, color);
            }

            // next little bit
            p7 = position.add(new Vector3D(Math.cos(i / (double)numberOfPoints * Math.PI * 2) * .4, 2.8, Math.sin(i / (double)numberOfPoints * Math.PI * 2) * .4));
            p8 = position.add(new Vector3D(Math.cos(i / (double)numberOfPoints * Math.PI * 2) * .4, 3.0, Math.sin(i / (double)numberOfPoints * Math.PI * 2) * .4));
            p9 = position.add(new Vector3D(Math.cos(i / (double)numberOfPoints * Math.PI * 2) * .6, 2.8, Math.sin(i / (double)numberOfPoints * Math.PI * 2) * .6));
            p10 = position.add(new Vector3D(Math.cos(i / (double)numberOfPoints * Math.PI * 2) * .6, 3.0, Math.sin(i / (double)numberOfPoints * Math.PI * 2) * .6));
            if ((i / (numberOfPoints / 8)) % 2 != ((i - 1) / (numberOfPoints / 8)) % 2) {
                addTrianglesFromPoints(writer, p7, p8, p9, p10, color);
            }
        }
        writer.println("END GROUP");
    }

    public static void makeKnight(Vector3D position, Color color, RenderingEngine renderingEngine, boolean rotation, PrintWriter writer) {
        writer.println("NEW GROUP");
        java.util.List<Vector3D> points = new ArrayList<>();
        points.add(new Vector3D(-.5, 0, 0));
        points.add(new Vector3D(.35, 0, 0));
        points.add(new Vector3D(.45, .2, 0));
        points.add(new Vector3D(-.3, .2, 0));
        points.add(new Vector3D(-.4, .6, 0));
        points.add(new Vector3D(0, .6, 0));
        points.add(new Vector3D(-.2, .85, 0));
        points.add(new Vector3D(0, 1, 0));
        points.add(new Vector3D(.2, .85, 0));
        points.add(new Vector3D(.4, .85, 0));
        points.add(new Vector3D(.5, .75, 0));
        points.add(new Vector3D(.45, .6, 0));
        points.add(new Vector3D(.3, .75, 0));
        points.add(new Vector3D(.3, .675, 0));
        points.add(new Vector3D(.5, .675, 0));

        java.util.List<Vector3D> p = new ArrayList<>();
        for (Vector3D v : points) {
            p.add(new Vector3D(v.getX() * ((rotation) ? 1 : -1) + position.getX(), v.getY() * 2.4 + position.getY() + .49, v.getZ() + position.getZ() - .25));
        }

        addTriangleFromPoints(writer, p.get(0), p.get(1), p.get(3)); addColorToShape(writer, color);
        addTriangleFromPoints(writer, p.get(1), p.get(2), p.get(3)); addColorToShape(writer, color);
        addTriangleFromPoints(writer, p.get(2), p.get(3), p.get(4)); addColorToShape(writer, color);
        addTriangleFromPoints(writer, p.get(2), p.get(4), p.get(5)); addColorToShape(writer, color);
        addTriangleFromPoints(writer, p.get(4), p.get(5), p.get(6)); addColorToShape(writer, color);
        addTriangleFromPoints(writer, p.get(5), p.get(6), p.get(8)); addColorToShape(writer, color);
        addTriangleFromPoints(writer, p.get(6), p.get(7), p.get(8)); addColorToShape(writer, color); // ear
        addTriangleFromPoints(writer, p.get(5), p.get(8), p.get(12)); addColorToShape(writer, color);
        addTriangleFromPoints(writer, p.get(8), p.get(9), p.get(12)); addColorToShape(writer, color);
        addTriangleFromPoints(writer, p.get(9), p.get(10), p.get(12)); addColorToShape(writer, color);
        addTriangleFromPoints(writer, p.get(5), p.get(12), p.get(13)); addColorToShape(writer, color);
        addTriangleFromPoints(writer, p.get(5), p.get(11), p.get(13)); addColorToShape(writer, color);
        addTriangleFromPoints(writer, p.get(11), p.get(13), p.get(14)); addColorToShape(writer, color);

        java.util.List<Vector3D> p2 = new ArrayList<>();
        for (Vector3D v : points) {
            p2.add(new Vector3D(v.getX() * ((rotation) ? 1 : -1) + position.getX(), v.getY() * 2.4 + position.getY() + .49, v.getZ() + position.getZ() + .25));
        }

        java.util.List<Vector3D> p3 = new ArrayList<>();
        for (Vector3D v : points) {
            p3.add(new Vector3D(v.getX() * ((rotation) ? 1 : -1) + position.getX(), v.getY() * 2.4 + position.getY() + .49, v.getZ() + position.getZ() - .10));
        }

        java.util.List<Vector3D> p4 = new ArrayList<>();
        for (Vector3D v : points) {
            p4.add(new Vector3D(v.getX() * ((rotation) ? 1 : -1) + position.getX(), v.getY() * 2.4 + position.getY() + .49, v.getZ() + position.getZ() + .10));
        }

        addTriangleFromPoints(writer, p2.get(0), p2.get(1), p2.get(3)); addColorToShape(writer, color);
        addTriangleFromPoints(writer, p2.get(1), p2.get(2), p2.get(3)); addColorToShape(writer, color);
        addTriangleFromPoints(writer, p2.get(2), p2.get(3), p2.get(4)); addColorToShape(writer, color);
        addTriangleFromPoints(writer, p2.get(2), p2.get(4), p2.get(5)); addColorToShape(writer, color);
        addTriangleFromPoints(writer, p2.get(4), p2.get(5), p2.get(6)); addColorToShape(writer, color);
        addTriangleFromPoints(writer, p2.get(5), p2.get(6), p2.get(8)); addColorToShape(writer, color);
        addTriangleFromPoints(writer, p2.get(6), p2.get(7), p2.get(8)); addColorToShape(writer, color); // ear
        addTriangleFromPoints(writer, p2.get(5), p2.get(8), p2.get(12)); addColorToShape(writer, color);
        addTriangleFromPoints(writer, p2.get(8), p2.get(9), p2.get(12)); addColorToShape(writer, color);
        addTriangleFromPoints(writer, p2.get(9), p2.get(10), p2.get(12)); addColorToShape(writer, color);
        addTriangleFromPoints(writer, p2.get(5), p2.get(12), p2.get(13)); addColorToShape(writer, color);
        addTriangleFromPoints(writer, p2.get(5), p2.get(11), p2.get(13)); addColorToShape(writer, color);
        addTriangleFromPoints(writer, p2.get(11), p2.get(13), p2.get(14)); addColorToShape(writer, color);

        makeCrossSquare(p, p2, color, writer, 0, 3);
        makeCrossSquare(p, p2, color, writer, 4, 3);
        makeCrossSquare(p, p2, color, writer, 4, 6);
        makeCrossSquare(p, p2, color, writer, 8, 6);
        makeCrossSquare(p, p2, color, writer, 8, 9);
        makeCrossSquare(p, p2, color, writer, 10, 9);
        makeCrossSquare(p, p2, color, writer, 10, 12);
        makeCrossSquare(p, p2, color, writer, 12, 13);
        makeCrossSquare(p, p2, color, writer, 13, 14);
        makeCrossSquare(p, p2, color, writer, 14, 11);
        makeCrossSquare(p, p2, color, writer, 11, 5);
        makeCrossSquare(p, p2, color, writer, 5, 2);
        makeCrossSquare(p, p2, color, writer, 2, 1);

        addTriangleFromPoints(writer, p3.get(6), p3.get(7), p3.get(8)); addColorToShape(writer, color); // ear
        addTriangleFromPoints(writer, p4.get(6), p4.get(7), p4.get(8)); addColorToShape(writer, color); // ear

        makeCrossSquare(p, p3, color, writer, 6, 7);
        makeCrossSquare(p2, p4, color, writer, 6, 7);
        makeCrossSquare(p, p3, color, writer, 7, 8);
        makeCrossSquare(p2, p4, color, writer, 7, 8);


        int numberOfPoints = 16; // number of points around the pawn
        for (int i = 0; i < numberOfPoints; i ++) {
            // next little bit
            Vector3D p7, p8, p9, p10;
            Triangle t4, t5;
            //p7 = p9;
            //p8 = p10;
            p7 = position.add(new Vector3D(Math.cos(i / (double) numberOfPoints * Math.PI * 2) * .85, 0, Math.sin(i / (double) numberOfPoints * Math.PI * 2) * .85));
            p8 = position.add(new Vector3D(Math.cos((i + 1) / (double) numberOfPoints * Math.PI * 2) * .85, 0, Math.sin((i + 1) / (double) numberOfPoints * Math.PI * 2) * .85));
            addTriangleFromPoints(writer, position, p7, p8);
            addColorToShape(writer, color);
            //r.addShape(t5);

            p9 = position.add(new Vector3D(Math.cos(i / (double) numberOfPoints * Math.PI * 2) * .85, .49, Math.sin(i / (double) numberOfPoints * Math.PI * 2) * .85));
            p10 = position.add(new Vector3D(Math.cos((i + 1) / (double) numberOfPoints * Math.PI * 2) * .85, .49, Math.sin((i + 1) / (double) numberOfPoints * Math.PI * 2) * .85));
            addTrianglesFromPoints(writer, p7, p8, p9, p10, color);

            //p7 = p9;
            //p8 = p10;
            p7 = position.add(new Vector3D(Math.cos(i / (double) numberOfPoints * Math.PI * 2) * .85, .49, Math.sin(i / (double) numberOfPoints * Math.PI * 2) * .85));
            p8 = position.add(new Vector3D(Math.cos((i + 1) / (double) numberOfPoints * Math.PI * 2) * .85, .49, Math.sin((i + 1) / (double) numberOfPoints * Math.PI * 2) * .85));
            addTriangleFromPoints(writer, (new Vector3D(0, .49, 0)).add(position), p7, p8);
            addColorToShape(writer, color);
            //r.addShape(t5);
        }
        writer.println("END GROUP");
    }

    private static void makeCrossSquare(java.util.List<Vector3D> p, java.util.List<Vector3D> p2, Color color, PrintWriter writer, int id1, int id2) {
        addTriangleFromPoints(writer, p.get(id1), p2.get(id1), p2.get(id2));
        addColorToShape(writer, color);
        addTriangleFromPoints(writer, p.get(id2), p2.get(id2), p.get(id1));
        addColorToShape(writer, color);
    }

    public static void makeBishop(Vector3D position, Color color, RenderingEngine renderingEngine, PrintWriter writer) {
        writer.println("NEW GROUP");
        int numberOfPoints = 16; // number of points around the pawn
        for (int i = 0; i < numberOfPoints; i ++) {
            // bottom
            Vector3D p1 = position.add(new Vector3D(Math.cos(i / (double)numberOfPoints * Math.PI * 2) * .87, 0, Math.sin(i / (double)numberOfPoints * Math.PI * 2) * .87));
            Vector3D p2 = position.add(new Vector3D(Math.cos((i + 1) / (double)numberOfPoints * Math.PI * 2) * .87, 0, Math.sin((i + 1) / (double)numberOfPoints * Math.PI * 2) * .87));
            addTriangleFromPoints(writer, position, p1, p2);
            addColorToShape(writer, color);

            // bottom sides
            Vector3D p3 = p1.add(new Vector3D(0, .43, 0));
            Vector3D p4 = p2.add(new Vector3D(0, .43, 0));
            addTrianglesFromPoints(writer, p1, p2, p3, p4, color);

            // next little bit
            Vector3D p5 = position.add(new Vector3D(Math.cos(i / (double)numberOfPoints * Math.PI * 2) * .5, .87, Math.sin(i / (double)numberOfPoints * Math.PI * 2) * .5));
            Vector3D p6 = position.add(new Vector3D(Math.cos((i + 1) / (double)numberOfPoints * Math.PI * 2) * .5, .87, Math.sin((i + 1) / (double)numberOfPoints * Math.PI * 2) * .5));
            addTrianglesFromPoints(writer, p3, p4, p5, p6, color);

            // next little bit
            Vector3D p7 = position.add(new Vector3D(Math.cos(i / (double)numberOfPoints * Math.PI * 2) * .3, 1.54, Math.sin(i / (double)numberOfPoints * Math.PI * 2) * .3));
            Vector3D p8 = position.add(new Vector3D(Math.cos((i + 1) / (double)numberOfPoints * Math.PI * 2) * .3, 1.54, Math.sin((i + 1) / (double)numberOfPoints * Math.PI * 2) * .3));
            addTrianglesFromPoints(writer, p5, p6, p7, p8, color);

            // next little bit
            Vector3D p9 = position.add(new Vector3D(Math.cos(i / (double)numberOfPoints * Math.PI * 2) * .3, 1.94, Math.sin(i / (double)numberOfPoints * Math.PI * 2) * .3));
            Vector3D p10 = position.add(new Vector3D(Math.cos((i + 1) / (double)numberOfPoints * Math.PI * 2) * .3, 1.94, Math.sin((i + 1) / (double)numberOfPoints * Math.PI * 2) * .3));
            addTrianglesFromPoints(writer, p7, p8, p9, p10, color);
        }

        writer.println("NEW SPHERE " + position.add(new Vector3D(0, 2, 0)).toStringEasy() + " .6");
        writer.println("EDIT SHAPE NULL REFLECTIVENESS .5");
        addColorToShape(writer, color);
        writer.println("EDIT SHAPE NULL STRETCH_Y " + 1/3.0);

        writer.println("NEW SPHERE " + position.add(new Vector3D(0, 2.4, 0)).toStringEasy() + " .4");
        writer.println("EDIT SHAPE NULL REFLECTIVENESS .5");
        addColorToShape(writer, color);
        writer.println("EDIT SHAPE NULL STRETCH_Y " + 1.5);

        writer.println("NEW SPHERE " + position.add(new Vector3D(0, 3, 0)).toStringEasy() + " .1");
        writer.println("EDIT SHAPE NULL REFLECTIVENESS .5");
        addColorToShape(writer, color);

        writer.println("END GROUP");
    }

    public static void makeQueen(Vector3D position, Color color, RenderingEngine renderingEngine, PrintWriter writer) {
        writer.println("NEW GROUP");
        int numberOfPoints = 16; // number of points around the pawn
        for (int i = 0; i < numberOfPoints; i ++) {
            // bottom
            Vector3D p1 = position.add(new Vector3D(Math.cos(i / (double)numberOfPoints * Math.PI * 2) * .9, 0, Math.sin(i / (double)numberOfPoints * Math.PI * 2) * .9));
            Vector3D p2 = position.add(new Vector3D(Math.cos((i + 1) / (double)numberOfPoints * Math.PI * 2) * .9, 0, Math.sin((i + 1) / (double)numberOfPoints * Math.PI * 2) * .9));
            addTriangleFromPoints(writer, position, p1, p2);
            addColorToShape(writer, color);

            // bottom sides
            Vector3D p3 = p1.add(new Vector3D(0, .45, 0));
            Vector3D p4 = p2.add(new Vector3D(0, .45, 0));
            addTrianglesFromPoints(writer, p1, p2, p3, p4, color);

            // next little bit
            Vector3D p5 = position.add(new Vector3D(Math.cos(i / (double)numberOfPoints * Math.PI * 2) * .6, .94, Math.sin(i / (double)numberOfPoints * Math.PI * 2) * .6));
            Vector3D p6 = position.add(new Vector3D(Math.cos((i + 1) / (double)numberOfPoints * Math.PI * 2) * .6, .94, Math.sin((i + 1) / (double)numberOfPoints * Math.PI * 2) * .6));
            addTrianglesFromPoints(writer, p3, p4, p5, p6, color);

            // next little bit
            Vector3D p7 = position.add(new Vector3D(Math.cos(i / (double)numberOfPoints * Math.PI * 2) * .25, 1.94, Math.sin(i / (double)numberOfPoints * Math.PI * 2) * .25));
            Vector3D p8 = position.add(new Vector3D(Math.cos((i + 1) / (double)numberOfPoints * Math.PI * 2) * .25, 1.94, Math.sin((i + 1) / (double)numberOfPoints * Math.PI * 2) * .25));
            addTrianglesFromPoints(writer, p5, p6, p7, p8, color);

            // next little bit
            Vector3D p9 = position.add(new Vector3D(Math.cos(i / (double)numberOfPoints * Math.PI * 2) * .3, 3.4, Math.sin(i / (double)numberOfPoints * Math.PI * 2) * .3));
            Vector3D p10 = position.add(new Vector3D(Math.cos((i + 1) / (double)numberOfPoints * Math.PI * 2) * .3, 3.4, Math.sin((i + 1) / (double)numberOfPoints * Math.PI * 2) * .3));
            addTrianglesFromPoints(writer, p7, p8, p9, p10, color);

            // next little bit
            p7 = p9;
            p8 = p10;
            p9 = position.add(new Vector3D(Math.cos(i / (double)numberOfPoints * Math.PI * 2) * (.55 - ((i % 2 == 0) ? 0 : .1)), 3.7, Math.sin(i / (double)numberOfPoints * Math.PI * 2) * (.55 - ((i % 2 == 0) ? 0 : .1))));
            p10 = position.add(new Vector3D(Math.cos((i + 1) / (double)numberOfPoints * Math.PI * 2) * (.55 - (((i + 1) % 2 == 0) ? 0 : .1)), 3.7, Math.sin((i + 1) / (double)numberOfPoints * Math.PI * 2) * (.55 - (((i + 1) % 2 == 0) ? 0 : .1))));
            addTrianglesFromPoints(writer, p7, p8, p9, p10, color);

            // next little bit
            p7 = p9;
            p8 = p10;
            p9 = position.add(new Vector3D(0, 3.7, 0));
            p10 = position.add(new Vector3D(0, 3.7, 0));
            addTrianglesFromPoints(writer, p7, p8, p9, p10, color);
        }

        writer.println("NEW SPHERE " + position.add(new Vector3D(0, 2.7, 0)).toStringEasy() +  " .6");
        writer.println("EDIT SHAPE NULL REFLECTIVENESS .5");
        addColorToShape(writer, color);
        writer.println("EDIT SHAPE NULL STRETCH_Y " + 1/3.0);

        writer.println("NEW SPHERE " + position.add(new Vector3D(0, 3.6, 0)).toStringEasy() +  " .3");
        writer.println("EDIT SHAPE NULL REFLECTIVENESS .5");
        addColorToShape(writer, color);

        writer.println("NEW SPHERE " + position.add(new Vector3D(0, 3.95, 0)).toStringEasy() +  " .1");
        writer.println("EDIT SHAPE NULL REFLECTIVENESS .5");
        addColorToShape(writer, color);

        writer.println("END GROUP");
    }

    public static void makeKing(Vector3D position, Color color, RenderingEngine renderingEngine, PrintWriter writer) {
        writer.println("NEW GROUP");
        int numberOfPoints = 16; // number of points around the pawn
        for (int i = 0; i < numberOfPoints; i++) {
            // bottom
            Vector3D p1 = position.add(new Vector3D(Math.cos(i / (double) numberOfPoints * Math.PI * 2) * 1.05, 0, Math.sin(i / (double) numberOfPoints * Math.PI * 2) * 1.05));
            Vector3D p2 = position.add(new Vector3D(Math.cos((i + 1) / (double) numberOfPoints * Math.PI * 2) * 1.05, 0, Math.sin((i + 1) / (double) numberOfPoints * Math.PI * 2) * 1.05));
            addTriangleFromPoints(writer, position, p1, p2);
            addColorToShape(writer, color);

            // bottom sides
            Vector3D p3 = p1.add(new Vector3D(0, .45, 0));
            Vector3D p4 = p2.add(new Vector3D(0, .45, 0));
            addTrianglesFromPoints(writer, p1, p2, p3, p4, color);

            // next little bit
            Vector3D p5 = position.add(new Vector3D(Math.cos(i / (double) numberOfPoints * Math.PI * 2) * .6, .94, Math.sin(i / (double) numberOfPoints * Math.PI * 2) * .6));
            Vector3D p6 = position.add(new Vector3D(Math.cos((i + 1) / (double) numberOfPoints * Math.PI * 2) * .6, .94, Math.sin((i + 1) / (double) numberOfPoints * Math.PI * 2) * .6));
            addTrianglesFromPoints(writer, p3, p4, p5, p6, color);

            // next little bit
            Vector3D p7 = position.add(new Vector3D(Math.cos(i / (double) numberOfPoints * Math.PI * 2) * .25, 1.94, Math.sin(i / (double) numberOfPoints * Math.PI * 2) * .25));
            Vector3D p8 = position.add(new Vector3D(Math.cos((i + 1) / (double) numberOfPoints * Math.PI * 2) * .25, 1.94, Math.sin((i + 1) / (double) numberOfPoints * Math.PI * 2) * .25));
            addTrianglesFromPoints(writer, p5, p6, p7, p8, color);

            // next little bit
            Vector3D p9 = position.add(new Vector3D(Math.cos(i / (double) numberOfPoints * Math.PI * 2) * .65, 4.2, Math.sin(i / (double) numberOfPoints * Math.PI * 2) * .65));
            Vector3D p10 = position.add(new Vector3D(Math.cos((i + 1) / (double) numberOfPoints * Math.PI * 2) * .65, 4.2, Math.sin((i + 1) / (double) numberOfPoints * Math.PI * 2) * .65));
            addTrianglesFromPoints(writer, p7, p8, p9, p10, color);

            // next little bit
            p7 = p9;
            p8 = p10;
            p9 = position.add(new Vector3D(Math.cos(i / (double) numberOfPoints * Math.PI * 2) * .6, 4.25, Math.sin(i / (double) numberOfPoints * Math.PI * 2) * .6));
            p10 = position.add(new Vector3D(Math.cos((i + 1) / (double) numberOfPoints * Math.PI * 2) * .6, 4.25, Math.sin((i + 1) / (double) numberOfPoints * Math.PI * 2) * .6));
            addTrianglesFromPoints(writer, p7, p8, p9, p10, color);

            // next little bit
            p7 = p9;
            p8 = p10;
            p9 = position.add(new Vector3D(0, 4.25, 0));
            p10 = position.add(new Vector3D(0, 4.25, 0));
            addTrianglesFromPoints(writer, p7, p8, p9, p10, color);
        }

        writer.println("NEW SPHERE " + position.add(new Vector3D(0, 2.8, 0)).toStringEasy() + " .65");
        writer.println("EDIT SHAPE NULL REFLECTIVENESS .5");
        addColorToShape(writer, color);
        writer.println("EDIT SHAPE NULL STRETCH_Y " + 1/3.0);

        java.util.List<Vector3D> p1 = new ArrayList<>();
        p1.add(new Vector3D(.10, 4.25, .15));
        p1.add(new Vector3D(.10, 4.7, .15));
        p1.add(new Vector3D(.10, 4.7, .3));
        p1.add(new Vector3D(.10, 5.0, .3));
        p1.add(new Vector3D(.10, 5.0, .15));
        p1.add(new Vector3D(.10, 5.15, .15));
        p1.add(new Vector3D(.10, 5.15, -.15));
        p1.add(new Vector3D(.10, 5.0, -.15));
        p1.add(new Vector3D(.10, 5.0, -.3));
        p1.add(new Vector3D(.10, 4.7, -.3));
        p1.add(new Vector3D(.10, 4.7, -.15));
        p1.add(new Vector3D(.10, 4.25, -.15));

        java.util.List<Vector3D> p2 = new ArrayList<>();
        p2.add(new Vector3D(-.10, 4.25, .15));
        p2.add(new Vector3D(-.10, 4.7, .15));
        p2.add(new Vector3D(-.10, 4.7, .3));
        p2.add(new Vector3D(-.10, 5.0, .3));
        p2.add(new Vector3D(-.10, 5.0, .15));
        p2.add(new Vector3D(-.10, 5.15, .15));
        p2.add(new Vector3D(-.10, 5.15, -.15));
        p2.add(new Vector3D(-.10, 5.0, -.15));
        p2.add(new Vector3D(-.10, 5.0, -.3));
        p2.add(new Vector3D(-.10, 4.7, -.3));
        p2.add(new Vector3D(-.10, 4.7, -.15));
        p2.add(new Vector3D(-.10, 4.25, -.15));

        for (int i = 0; i < p1.size(); i ++) {
            p1.set(i, p1.get(i).add(position));
            p2.set(i, p2.get(i).add(position));
        }

        addTriangleFromPoints(writer, p1.get(0), p1.get(1), p1.get(11)); addColorToShape(writer, color);
        addTriangleFromPoints(writer, p1.get(1), p1.get(10), p1.get(11)); addColorToShape(writer, color);
        addTriangleFromPoints(writer, p1.get(1), p1.get(2), p1.get(4)); addColorToShape(writer, color);
        addTriangleFromPoints(writer, p1.get(2), p1.get(3), p1.get(4)); addColorToShape(writer, color);
        addTriangleFromPoints(writer, p1.get(1), p1.get(4), p1.get(7)); addColorToShape(writer, color);
        addTriangleFromPoints(writer, p1.get(1), p1.get(10), p1.get(7)); addColorToShape(writer, color);
        addTriangleFromPoints(writer, p1.get(4), p1.get(5), p1.get(7)); addColorToShape(writer, color);
        addTriangleFromPoints(writer, p1.get(5), p1.get(6), p1.get(7)); addColorToShape(writer, color);
        addTriangleFromPoints(writer, p1.get(7), p1.get(8), p1.get(9)); addColorToShape(writer, color);
        addTriangleFromPoints(writer, p1.get(7), p1.get(9), p1.get(10)); addColorToShape(writer, color);

        addTriangleFromPoints(writer, p2.get(0), p2.get(1), p2.get(11)); addColorToShape(writer, color);
        addTriangleFromPoints(writer, p2.get(1), p2.get(10), p2.get(11)); addColorToShape(writer, color);
        addTriangleFromPoints(writer, p2.get(1), p2.get(2), p2.get(4)); addColorToShape(writer, color);
        addTriangleFromPoints(writer, p2.get(2), p2.get(3), p2.get(4)); addColorToShape(writer, color);
        addTriangleFromPoints(writer, p2.get(1), p2.get(4), p2.get(7)); addColorToShape(writer, color);
        addTriangleFromPoints(writer, p2.get(1), p2.get(10), p2.get(7)); addColorToShape(writer, color);
        addTriangleFromPoints(writer, p2.get(4), p2.get(5), p2.get(7)); addColorToShape(writer, color);
        addTriangleFromPoints(writer, p2.get(5), p2.get(6), p2.get(7)); addColorToShape(writer, color);
        addTriangleFromPoints(writer, p2.get(7), p2.get(8), p2.get(9)); addColorToShape(writer, color);
        addTriangleFromPoints(writer, p2.get(7), p2.get(9), p2.get(10)); addColorToShape(writer, color);

        makeCrossSquare(p1, p2, color, writer, 0, 1);
        makeCrossSquare(p1, p2, color, writer, 1, 2);
        makeCrossSquare(p1, p2, color, writer, 3, 2);
        makeCrossSquare(p1, p2, color, writer, 3, 4);
        makeCrossSquare(p1, p2, color, writer, 5, 4);
        makeCrossSquare(p1, p2, color, writer, 5, 6);
        makeCrossSquare(p1, p2, color, writer, 7, 6);
        makeCrossSquare(p1, p2, color, writer, 7, 8);
        makeCrossSquare(p1, p2, color, writer, 9, 8);
        makeCrossSquare(p1, p2, color, writer, 9, 10);
        makeCrossSquare(p1, p2, color, writer, 11, 10);

        writer.println("END GROUP");
    }
}
