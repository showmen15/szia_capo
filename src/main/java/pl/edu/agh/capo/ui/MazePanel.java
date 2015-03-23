package pl.edu.agh.capo.ui;

import pl.edu.agh.capo.maze.MazeMap;

import javax.swing.*;
import java.awt.*;

public class MazePanel extends JPanel {

    private MazeMap map;

    public void updateMaze(MazeMap map) {
        this.map = map;
        repaint();
    }

    public void paintComponent(Graphics g) {
        if (map != null) {
            /**
             * draw maze
             */
            int width = getWidth();
            int height = getHeight();
            g.setColor(Color.black);
            g.drawOval(0, 0, width, height);
        }
    }
}
