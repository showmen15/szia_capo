package pl.edu.agh.capo.ui;

import pl.edu.agh.capo.logic.Agent;
import pl.edu.agh.capo.logic.common.Vision;
import pl.edu.agh.capo.maze.Gate;
import pl.edu.agh.capo.maze.MazeMap;
import pl.edu.agh.capo.maze.Wall;
import pl.edu.agh.capo.maze.helper.MazeHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.Collections;
import java.util.Comparator;

public class MazePanel extends JPanel {

    private final static double MAZE_SIZE = 500.0;
    private final static double START_MAZE_COORDINATE = 50.0;

    private MazeMap map;

    private double minY;
    private double minX;
    private double ratio;

    private Agent agent;

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public void setMaze(MazeMap map) {
        this.map = map;
    }

    @Override
    public void paintComponent(Graphics g) {
        if (map == null) {
            return;
        }
        super.paintComponent(g);
        getNormalizationData();
        Graphics2D g2 = (Graphics2D) g;
        printGates(g2, map.getGates(), Color.cyan);
        printWalls(g2, map.getWalls(), Color.gray);
        printAgent(agent, g2);
        printGates(g2, agent.getRoom().getGates(), Color.blue);
        printWalls(g2, agent.getRoom().getWalls(), Color.black);
    }

    private void printAgent(Agent agent, Graphics2D g2) {
        g2.setColor(countColor(agent.getFitness()));
        Polygon vision = getVisionPolygon(agent.getVisions());
        g2.draw(vision);
        g2.fill(vision);

        double x = normalizeCoordinate(agent.getX(), minX, ratio);
        double y = normalizeCoordinate(agent.getY(), minY, ratio);

//        g2.setStroke(new BasicStroke(1));
//        java.util.List<Vision> visions = new ArrayList<>(agent.getVisions());
//        for (Vision vision : visions) {
//            double result = vision.getFitness();
//            if (result < 0.0) {
//                g2.setColor(Color.yellow);
//            } else {
//                g2.setColor(countColor(result));
//            }
//            g2.draw(new Line2D.Double(x, y,
//                    getVisionXCoordinate(agent.getX(),agent.getAlpha(), vision.getAngle(), vision.getDistance()),
//                    getVisionYCoordinate(agent.getY(),agent.getAlpha(), vision.getAngle(), vision.getDistance())));
//        }

        g2.setColor(Color.blue);
        Ellipse2D.Double ellipse = new Ellipse2D.Double(x - 3.5, y - 3.5, 7.0, 7.0);
        g2.draw(ellipse);
        g2.fill(ellipse);
        g2.draw(new Line2D.Double(x, y, getVisionXCoordinate(agent.getX(), agent.getAlpha(), 0, 0.1),
                getVisionYCoordinate(agent.getY(), agent.getAlpha(), 0, 0.1)));
        g2.setColor(Color.black);
    }

    private Polygon getVisionPolygon(java.util.List<Vision> visions){

        Collections.sort(visions, new Comparator<Vision>() {
            @Override
            public int compare(Vision o1, Vision o2) {
                double diff = o1.getAngle() - o2.getAngle();
                if (diff > 0) return 1;
                if (diff < 0) return -1;
                return 0;
            }
        });

        int[] xpoints = new int[visions.size() + 2];
        int[] ypoints = new int[visions.size() + 2];
        Long x = Math.round(normalizeCoordinate(agent.getX(), minX, ratio));
        xpoints[0] = Integer.valueOf(x.intValue());
        Long y = Math.round(normalizeCoordinate(agent.getY(), minY, ratio));
        ypoints[0] = Integer.valueOf(y.intValue());

        for (int i = 0; i < visions.size(); i++){
            Vision vision = visions.get(i);
            x = Math.round(getVisionXCoordinate(agent.getX(), agent.getAlpha(), vision.getAngle(), vision.getDistance()));
            y = Math.round(getVisionYCoordinate(agent.getY(), agent.getAlpha(), vision.getAngle(), vision.getDistance()));
            xpoints[i+1] = Integer.valueOf(x.intValue());
            ypoints[i+1] = Integer.valueOf(y.intValue());
        }
        xpoints[xpoints.length -1] = xpoints[0];
        ypoints[ypoints.length -1] = ypoints[0];
        return new Polygon(xpoints, ypoints, visions.size());
    }

    private Color countColor(double result) {
        Long r = Math.round((255 * (1 - result)));
        Long g = Math.round((255 * result));
        return new Color(Integer.valueOf(r.intValue()), Integer.valueOf(g.intValue()), 0, 127);
    }

    private double getVisionXCoordinate(double agentX, double agentAlpha, double visionAlpha, double visionRange) {
        double sinus = Math.sin(Math.toRadians(visionAlpha + agentAlpha));
        double visionX = agentX + (sinus * visionRange);
        return normalizeCoordinate(visionX, minX, ratio);
    }

    private double getVisionYCoordinate(double agentY, double agentAlpha, double visionAlpha, double visionRange) {
        double cosinus = Math.cos(Math.toRadians(visionAlpha + agentAlpha));
        double visionY = agentY - (cosinus * visionRange);
        return normalizeCoordinate(visionY, minY, ratio);
    }

    private void printGates(Graphics2D g, java.util.List<Gate> gates, Color color) {
        g.setStroke(new BasicStroke(1));
        g.setColor(color);
        for (Gate gate : gates) {
            printGate(gate, g);
        }
    }

    private void printGate(Gate gate, Graphics2D g) {
        double x1 = normalizeCoordinate(gate.getFrom().getX(), minX, ratio);
        double x2 = normalizeCoordinate(gate.getTo().getX(), minX, ratio);
        double y1 = normalizeCoordinate(gate.getFrom().getY(), minY, ratio);
        double y2 = normalizeCoordinate(gate.getTo().getY(), minY, ratio);

        g.draw(new Line2D.Double(x1, y1, x2, y2));
    }

    private void printWalls(Graphics2D g, java.util.List<Wall> walls, Color color) {
        g.setStroke(new BasicStroke(3));
        g.setColor(color);
        for (Wall wall : walls) {
            printWall(wall, g);
        }
    }

    private void printWall(Wall wall, Graphics2D g) {
        double x1 = normalizeCoordinate(wall.getFrom().getX(), minX, ratio);
        double x2 = normalizeCoordinate(wall.getTo().getX(), minX, ratio);
        double y1 = normalizeCoordinate(wall.getFrom().getY(), minY, ratio);
        double y2 = normalizeCoordinate(wall.getTo().getY(), minY, ratio);

        g.draw(new Line2D.Double(x1, y1, x2, y2));
    }

    private double normalizeCoordinate(double val, double min, double ratio) {
        return ((val - min) * ratio) + START_MAZE_COORDINATE;
    }

    private void getNormalizationData() {
        double minY = MazeHelper.getMinY(map.getWalls());
        double maxY = MazeHelper.getMaxY(map.getWalls());
        double minX = MazeHelper.getMinX(map.getWalls());
        double maxX = MazeHelper.getMaxX(map.getWalls());

        double height = maxY - minY;
        double width = maxX - minX;
        this.minX = minX;
        this.minY = minY;
        if (height > width) {
            ratio = MAZE_SIZE / height;
        } else {
            ratio = MAZE_SIZE / width;
        }
    }

}
