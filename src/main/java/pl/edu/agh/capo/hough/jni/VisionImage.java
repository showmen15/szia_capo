package pl.edu.agh.capo.hough.jni;

import pl.edu.agh.capo.logic.common.Vision;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class VisionImage {


    private static final int VISION_PER_PIXEL = (int) (255.0 / 3);
    private final byte[] bytes;
    private final int size;

    public VisionImage(List<Vision> visions, int size) {
        this.bytes = new byte[size * size];
        this.size = size;
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = 0;
        }
        addVisions(visions);
    }

    private void addVisions(List<Vision> visions) {
        Vision vision = visions.stream().max((v1, v2) -> Double.compare(v1.getDistance(), v2.getDistance())).get();
        double maxDistance = vision.getDistance();
        double halfSize = size / 2;
        visions.forEach(v -> addPoint(v, halfSize, maxDistance));
    }

    private void addPoint(Vision vision, double halfSize, double maxDistance) {
        double distance = vision.getDistance() / (maxDistance + 1); // distance <- [0,1)
        int x = (int) (halfSize - (Math.cos(Math.toRadians(vision.getAngle())) * distance * halfSize));
        int y = (int) (halfSize + (Math.sin(Math.toRadians(vision.getAngle())) * distance * halfSize));
        bytes[y * size + x] += VISION_PER_PIXEL;
    }

    public byte[] toByteArray() throws IOException {
        return bytes;
    }

    @SuppressWarnings("unused")
    public void writeToFile(String filename) throws IOException {
        BufferedImage pixelImage = new BufferedImage(size, size, BufferedImage.TYPE_BYTE_GRAY);
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Color color;
                byte b = bytes[y * size + x];
                pixelImage.setRGB(x, y, b);
            }
        }
        ImageIO.write(pixelImage, "BMP", new File(filename));
    }
}
