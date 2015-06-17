package pl.edu.agh.capo.hough.visualizatin;

import javafx.util.Pair;
import pl.edu.agh.capo.hough.Line;
import pl.edu.agh.capo.hough.Transform;
import pl.edu.agh.capo.logic.common.Vision;

import java.awt.image.BufferedImage;
import java.awt.*;
import java.io.IOException;
import java.io.File;

/**
 * <p/>
 * Java Implementation of the Hough Transform.<br />
 * Used for finding straight lines in an image.<br />
 * by Olly Oechsle
 * </p>
 * <p/>
 * Note: This class is based on original code from:<br />
 * <a href="http://homepages.inf.ed.ac.uk/rbf/HIPR2/hough.htm">http://homepages.inf.ed.ac.uk/rbf/HIPR2/hough.htm</a>
 * </p>
 * <p/>
 * If you represent a line as:<br />
 * x cos(theta) + y sin (theta) = r
 * </p>
 * <p/>
 * ... and you know values of x and y, you can calculate all the values of r by going through
 * all the possible values of theta. If you plot the values of r on a graph for every value of
 * theta you get a sinusoidal curve. This is the Hough transformation.
 * </p>
 * <p/>
 * The hough tranform works by looking at a number of such x,y coordinates, which are usually
 * found by some kind of edge detection. Each of these coordinates is transformed into
 * an r, theta curve. This curve is discretised so we actually only look at a certain discrete
 * number of theta values. "Accumulator" cells in a hough array along this curve are incremented
 * for X and Y coordinate.
 * </p>
 * <p/>
 * The accumulator space is plotted rectangularly with theta on one axis and r on the other.
 * Each point in the array represents an (r, theta) value which can be used to represent a line
 * using the formula above.
 * </p>
 * <p/>
 * Once all the points have been added should be full of curves. The algorithm then searches for
 * local peaks in the array. The higher the peak the more values of x and y crossed along that curve,
 * so high peaks give good indications of a line.
 * </p>
 *
 * @author Olly Oechsle, University of Essex
 */

public class HoughTransform {

    private final static int SIZE = Transform.SIZE;
    private final static int HALF_SIZE = SIZE / 2;
    private final static int NORMALIZATION = 6;

    public static void run(java.util.List<Vision> visionList) {
        String filename = "C:\\Users\\Ucash\\Desktop\\lol.png";

        // load the file using Java's imageIO library
        File file = new File(filename);
        BufferedImage image = null;
        try {
            image = javax.imageio.ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Transform h = new Transform();
        h.run(visionList);

        // get the lines out
        java.util.List<Line> lines = h.getLines(5);
        System.out.println(lines.size());

        // draw the lines back onto the image
        for (int j = 0; j < 1; j++) {
            HoughLine line = new HoughLine(lines.get(j));
            line.draw(image, SIZE, Color.RED.getRGB());
        }

        image.setRGB(HALF_SIZE, HALF_SIZE, Color.blue.getRGB());
        image.setRGB(HALF_SIZE -1, HALF_SIZE, Color.blue.getRGB());
        image.setRGB(HALF_SIZE+1, HALF_SIZE, Color.blue.getRGB());
        image.setRGB(HALF_SIZE, HALF_SIZE-1, Color.blue.getRGB());
        image.setRGB(HALF_SIZE-1, HALF_SIZE-1, Color.blue.getRGB());
        image.setRGB(HALF_SIZE+1, HALF_SIZE-1, Color.blue.getRGB());
        image.setRGB(HALF_SIZE, HALF_SIZE+1, Color.blue.getRGB());
        image.setRGB(HALF_SIZE-1, HALF_SIZE+1, Color.blue.getRGB());
        image.setRGB(HALF_SIZE+1, HALF_SIZE+1, Color.blue.getRGB());

        for (Vision vision : visionList){
            Pair<Integer, Integer> pair = addPoint(vision);
            int x = pair.getKey();
            int y = pair.getValue();
            
            image.setRGB(x, y, Color.green.getRGB());
            image.setRGB(x-1, y, Color.black.getRGB());
            image.setRGB(x+1, y, Color.black.getRGB());
            image.setRGB(x-1, y-1, Color.black.getRGB());
            image.setRGB(x+1, y-1, Color.black.getRGB());
            image.setRGB(x-1, y+1, Color.black.getRGB());
            image.setRGB(x+1, y+1, Color.black.getRGB());
            image.setRGB(x, y-1, Color.black.getRGB());
            image.setRGB(x, y+1, Color.black.getRGB());
        }

        File f = new File("C:\\Users\\Ucash\\Desktop\\lol1.png");
        try {
            javax.imageio.ImageIO.write(image, "png", f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Pair<Integer, Integer> addPoint(Vision vision) {
        double x = (Math.cos(Math.toRadians(vision.getAngle())) * vision.getDistance());
        double y = (Math.sin(Math.toRadians(vision.getAngle())) * vision.getDistance());
        return new Pair<>(normalizeCoordinateX(x), normalizeCoordinateY(y));
    }
    
    private static int normalizeCoordinateY(double x) {
        return (int)(((x * HALF_SIZE)/NORMALIZATION) + HALF_SIZE);
    }

    private static int normalizeCoordinateX(double x) {
        return HALF_SIZE - (int)(((x * HALF_SIZE)/NORMALIZATION));
    }
}


