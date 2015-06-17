package pl.edu.agh.capo.hough.visualizatin;

import pl.edu.agh.capo.hough.Line;

import java.awt.image.BufferedImage;

public class HoughLine {

    protected double theta;
    protected double r;

    public HoughLine(Line line) {
        this.theta = line.getTheta();
        this.r = line.getRadius();
    }

    /**
     * Draws the line on the image of your choice with the RGB colour of your choice.
     */
    public void draw(BufferedImage image, int size, int color) {

        // During processing h_h is doubled so that -ve r values
        int houghHeight = (int) (Math.sqrt(2) * size) / 2;

        // Find edge points and vote in array
        float centerX = size / 2;
        float centerY = size / 2;

        // Draw edges in output array
        double tsin = Math.sin(theta);
        double tcos = Math.cos(theta);

        if (theta < Math.PI * 0.25 || theta > Math.PI * 0.75) {
            // Draw vertical-ish lines
            for (int y = 0; y < size; y++) {
                int x = (int) ((((r - houghHeight) - ((y - centerY) * tsin)) / tcos) + centerX);
                if (x < size && x >= 0) {
                    image.setRGB(x, y, color);
                }
            }
        } else {
            // Draw horizontal-sh lines
            for (int x = 0; x < size; x++) {
                int y = (int) ((((r - houghHeight) - ((x - centerX) * tcos)) / tsin) + centerY);
                if (y < size && y >= 0) {
                    image.setRGB(x, y, color);
                }
            }
        }
    }
}
