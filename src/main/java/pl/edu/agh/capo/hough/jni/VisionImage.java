package pl.edu.agh.capo.hough.jni;

import pl.edu.agh.capo.hough.common.Line;
import pl.edu.agh.capo.logic.common.Vision;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class VisionImage {


    private static final int VISION_PER_PIXEL = (int) (255.0 / 3);
    private final byte[] bytes;
    private final int size;
    private final int halfSize;

    public VisionImage(List<Vision> visions, int size) {
        this.bytes = new byte[size * size];
        this.size = size;
        this.halfSize = size / 2;
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = 0;
        }
        addVisions(visions);
    }

    private void addVisions(List<Vision> visions) {
        Vision vision = visions.stream().max((v1, v2) -> Double.compare(v1.getDistance(), v2.getDistance())).get();
        double maxDistance = vision.getDistance();
        visions.forEach(v -> addPoint(v, maxDistance));
    }

    private void addPoint(Vision vision, double maxDistance) {
        double distance = vision.getDistance() / maxDistance * (halfSize - 1); // distance <- [0,1)
        double angleInRadians = Math.toRadians(vision.getAngle());
        addPoint(distance, angleInRadians);
    }

    private void addPoint(double distance, double angleInRadians) {
        int x = (int) (halfSize + (Math.sin(angleInRadians) * distance));
        int y = (int) (halfSize - (Math.cos(angleInRadians) * distance));
        bytes[y * size + x] += VISION_PER_PIXEL;
    }

    public byte[] toByteArray() throws IOException {
        return bytes;
    }

    @SuppressWarnings("unused")
    public void writeToFile(String filename) throws IOException {
        BufferedImage pixelImage = createImage();
        drawBytes(pixelImage);
        drawRobot(pixelImage);
        saveImage(pixelImage, filename);
    }

    private void saveImage(BufferedImage image, String filename) throws IOException {
        ImageIO.write(image, "BMP", new File(filename));
    }

    private BufferedImage createImage() {
        return new BufferedImage(size, size, BufferedImage.TYPE_BYTE_GRAY);
    }

    private void drawBytes(BufferedImage pixelImage) {
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                byte b = bytes[y * size + x];
                pixelImage.setRGB(x, y, b);
            }
        }
    }

    private void drawLines(BufferedImage bufferedImage, List<Line> lines) {
        lines.forEach(line -> drawLine(bufferedImage, line, 255));
    }

    private void drawRobot(BufferedImage bufferedImage) {
        bufferedImage.setRGB(halfSize, halfSize, 255);
    }

    @SuppressWarnings("unused")
    public void writeToFileWithLines(String filename, List<Line> lines) throws IOException {
        BufferedImage pixelImage = createImage();
        drawBytes(pixelImage);
        drawRobot(pixelImage);
        drawLines(pixelImage, lines);
        saveImage(pixelImage, filename);
    }

    /**
     * Draws the line on the image of your choice with the RGB colour of your choice.
     */
    public void drawLine(BufferedImage image, Line line, int color) {
        double theta = Math.toRadians(line.getTheta());
        double rho = line.getRho();

        // Find edge points and vote in array
        float centerX = halfSize;
        float centerY = halfSize;

        // Draw edges in output array
        double tsin = Math.sin(theta);
        double tcos = Math.cos(theta);

        if (theta < Math.PI * 0.25 || theta > Math.PI * 0.75) {
            // Draw vertical-ish lines
            for (int y = 0; y < size; y++) {
                int x = (int) (((rho - ((y - centerY) * tsin)) / tcos) + centerX);
                if (x < size && x >= 0) {
                    image.setRGB(x, y, color);
                }
            }
        } else {
            // Draw horizontal-sh lines
            for (int x = 0; x < size; x++) {
                int y = (int) (((rho - ((x - centerX) * tcos)) / tsin) + centerY);
                if (y < size && y >= 0) {
                    image.setRGB(x, y, color);
                }
            }
        }
    }
}
