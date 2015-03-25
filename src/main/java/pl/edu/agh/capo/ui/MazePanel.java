package pl.edu.agh.capo.ui;

import pl.edu.agh.capo.maze.Gate;
import pl.edu.agh.capo.maze.MazeMap;
import pl.edu.agh.capo.maze.Wall;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Map;

public class MazePanel extends JPanel {

    private final static double MAZE_SIZE = 500.0;
    private final static double START_MAZE_COORDINATE = 50.0;

    private MazeMap map;
    private java.util.List<Agent> agents;

    private double minY;
    private double minX;
    private double ratio;

    public MazePanel(){
        super();
        agents = new ArrayList<Agent>();
        agents.add(new Agent(1.2, 1.7, 42));
    }

    public void updateMaze(MazeMap map) {
        this.map = map;
        repaint();
    }

    public void paintComponent(Graphics g) {
        if (map == null) {
            return;
        }
        super.paintComponent(g);
        getNormalizationData();
        Graphics2D g2 = (Graphics2D) g;
        printGates(g2);
        printWalls(g2);
        printAgents(g2);
    }

    private void printAgents(Graphics2D g2) {
        for (Agent agent : agents){
            printAgent(agent, g2);
        }
    }

    private void printAgent(Agent agent, Graphics2D g2) {
        g2.setColor(new Color(0, 255, 0, 127));
        Polygon vision = getVisionPolygon(agent);
        g2.draw(vision);
        g2.fill(vision);

        g2.setColor(Color.red);
        double x = normalizeCoordinate(agent.getX(), minX, ratio);
        double y = normalizeCoordinate(agent.getY(), minY, ratio);
        Ellipse2D.Double ellipse = new Ellipse2D.Double(x - 3.5, y - 3.5, 7.0, 7.0);
        g2.draw(ellipse);
        g2.fill(ellipse);
        g2.draw(new Line2D.Double(x, y, getVisionXCoordinate(agent.getX(), agent.getAlpha(), 0, 0.1), getVisionYCoordinate(agent.getY(), agent.getAlpha(), 0, 0.1)));
    }

    private Polygon getVisionPolygon(Agent agent){
        ArrayList<Double> angles = new ArrayList<Double>(agent.getVision().keySet());
        java.util.Collections.sort(angles);

        int[] xpoints = new int[angles.size() + 2];
        int[] ypoints = new int[angles.size() + 2];
        Long x = Math.round(normalizeCoordinate(agent.getX(), minX, ratio));
        xpoints[0] = Integer.valueOf(x.intValue());
        Long y = Math.round(normalizeCoordinate(agent.getY(), minY, ratio));
        ypoints[0] = Integer.valueOf(y.intValue());
        Map<Double, Double> vision = agent.getVision();

        for (int i = 0; i < angles.size(); i++){
            double angle = angles.get(i);
            x = Math.round(getVisionXCoordinate(agent.getX(), agent.getAlpha(), angle, vision.get(angle)));
            y = Math.round(getVisionYCoordinate(agent.getY(), agent.getAlpha(), angle, vision.get(angle)));
            xpoints[i+1] = Integer.valueOf(x.intValue());
            ypoints[i+1] = Integer.valueOf(y.intValue());
        }
        xpoints[xpoints.length -1] = xpoints[0];
        ypoints[ypoints.length -1] = ypoints[0];
        return new Polygon(xpoints, ypoints, angles.size());
    }

    private double getVisionXCoordinate(double agentX, double agentAlpha, double visionAlpha, double visionRange){
        double sinus = Math.sin(Math.toRadians(visionAlpha + agentAlpha));
        double visionX = agentX + (sinus * visionRange);
        return normalizeCoordinate(visionX, minX, ratio);
    }

    private double getVisionYCoordinate(double agentY, double agentAlpha, double visionAlpha, double visionRange){
        double cosinus = Math.cos(Math.toRadians(visionAlpha + agentAlpha));
        double visionY = agentY - (cosinus * visionRange) ;
        return normalizeCoordinate(visionY, minY, ratio);
    }

    private void printGates(Graphics2D g) {
        g.setStroke(new BasicStroke(1));
        g.setColor(Color.blue);
        for (Gate gate : map.getGates()){
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

    private void printWalls(Graphics2D g) {
        g.setStroke(new BasicStroke(3));
        g.setColor(Color.black);
        for (Wall wall : map.getWalls()){
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

    private double normalizeCoordinate(double val, double min, double ratio){
        return ((val - min) * ratio) + START_MAZE_COORDINATE;
    }

    private void getNormalizationData(){
        double minY = getMinYInMazeMap();
        double maxY = getMaxYInMazeMap();
        double minX = getMinXInMazeMap();
        double maxX = getMaxXInMazeMap();

        double height = maxY - minY;
        double width = maxX - minX;
        this.minX = minX;
        this.minY = minY;
        if (height > width){
            ratio = MAZE_SIZE / height;
        } else {
            ratio = MAZE_SIZE / width;
        }
    }

    private double getMinYInMazeMap(){
        double min = Double.MAX_VALUE;
        for (Wall wall : map.getWalls()){
            if (wall.getFrom().getY() < min){
                min = wall.getFrom().getY();
            }
            if (wall.getTo().getY() < min){
                min = wall.getTo().getY();
            }
        }
        return min;
    }

    private double getMaxYInMazeMap(){
        double max = Double.MIN_VALUE;
        for (Wall wall : map.getWalls()){
            if (wall.getFrom().getY() > max){
                max = wall.getFrom().getY();
            }
            if (wall.getTo().getY() > max){
                max = wall.getTo().getY();
            }
        }
        return max;
    }

    private double getMinXInMazeMap(){
        double min = Double.MAX_VALUE;
        for (Wall wall : map.getWalls()){
            if (wall.getFrom().getX() < min){
                min = wall.getFrom().getX();
            }
            if (wall.getTo().getX() < min){
                min = wall.getTo().getX();
            }
        }
        return min;
    }

    private double getMaxXInMazeMap(){
        double max = Double.MIN_VALUE;
        for (Wall wall : map.getWalls()){
            if (wall.getFrom().getX() > max){
                max = wall.getFrom().getX();
            }
            if (wall.getTo().getX() > max){
                max = wall.getTo().getX();
            }
        }
        return max;
    }
}
