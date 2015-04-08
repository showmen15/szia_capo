package pl.edu.agh.capo.ui;

import pl.edu.agh.capo.logic.Agent;
import pl.edu.agh.capo.logic.common.AgentMove;
import pl.edu.agh.capo.logic.common.MeasureResult;
import pl.edu.agh.capo.logic.common.MeasurementReader;
import pl.edu.agh.capo.logic.interfaces.IAgentMoveListener;
import pl.edu.agh.capo.maze.Gate;
import pl.edu.agh.capo.maze.MazeMap;
import pl.edu.agh.capo.maze.Space;
import pl.edu.agh.capo.maze.Wall;
import pl.edu.agh.capo.maze.helper.MazeHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Map;

public class MazePanel extends JPanel implements IAgentMoveListener {

    private final static double MAZE_SIZE = 500.0;
    private final static double START_MAZE_COORDINATE = 50.0;

    private MazeMap map;
    private java.util.List<Agent> agents;

    private double minY;
    private double minX;
    private double ratio;

    private int currentMeasureIndex;
    private int currentAgentIndex;

    private MeasurementReader measurementReader;

    public MazePanel(MeasurementReader measurementReader) {
        super();
        this.measurementReader = measurementReader;
        currentMeasureIndex = 0;
        currentAgentIndex = 0;
        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(new CapoKeyListener(this));
        agents = new ArrayList<Agent>();
    }

    public void updateMaze(MazeMap map) {
        this.map = map;
        createAgents();
        repaint();
    }

    private void createAgents() {
        for (Space space : map.getSpaces()) {
            Agent agent = new Agent(MazeHelper.getRoom(space.getId(), map));
            agent.setMeasure(measurementReader.getMeasure(currentMeasureIndex));
            agents.add(agent);
        }
    }

    public void paintComponent(Graphics g) {
        if (map == null) {
            return;
        }
        super.paintComponent(g);
        getNormalizationData();
        Graphics2D g2 = (Graphics2D) g;
        printGates(g2, map.getGates(), Color.cyan);
        printWalls(g2, map.getWalls(), Color.gray);
        printCurrentAgent(g2);
        printGates(g2, agents.get(currentAgentIndex).getRoom().getGates(), Color.blue);
        printWalls(g2, agents.get(currentAgentIndex).getRoom().getWalls(), Color.black);
    }

    private void printCurrentAgent(Graphics2D g2) {
        printAgent(agents.get(currentAgentIndex), g2);
    }

    private void printAgent(Agent agent, Graphics2D g2) {
        //g2.setColor(new Color(0, 255, 0, 127));
        //Polygon vision = getVisionPolygon(agent);
        //g2.draw(vision);
        //g2.fill(vision);
        double x = normalizeCoordinate(agent.getX(), minX, ratio);
        double y = normalizeCoordinate(agent.getY(), minY, ratio);
        if (agent.analyzeMeasure()) {

            Map<Double, MeasureResult> measureResults = agent.getMeasureResults();
            g2.setStroke(new BasicStroke(1));
            for (Map.Entry<Double, Double> vision : agent.getVision().entrySet()) {
                switch (measureResults.get(vision.getKey())) {
                    case IGNORE:
                        g2.setColor(Color.yellow);
                        break;
                    case VALID:
                        g2.setColor(Color.green);
                        break;
                    case INVALID:
                        g2.setColor(Color.orange);
                        break;
                }
                g2.draw(new Line2D.Double(x, y,
                        getVisionXCoordinate(agent.getX(), agent.getAlpha(), vision.getKey(), vision.getValue()),
                        getVisionYCoordinate(agent.getY(), agent.getAlpha(), vision.getKey(), vision.getValue())));
            }
        }
        g2.setColor(Color.red);
        Ellipse2D.Double ellipse = new Ellipse2D.Double(x - 3.5, y - 3.5, 7.0, 7.0);
        g2.draw(ellipse);
        g2.fill(ellipse);
        g2.draw(new Line2D.Double(x, y, getVisionXCoordinate(agent.getX(), agent.getAlpha(), 0, 0.1), getVisionYCoordinate(agent.getY(), agent.getAlpha(), 0, 0.1)));
        g2.setColor(Color.black);
        Map<MeasureResult, Integer> measureCounts = agent.getMeasureCounts();
        g2.drawString(String.format("Pasuje: %d, Niepasuje: %d, Brama: %d",
                        measureCounts.get(MeasureResult.VALID),
                        measureCounts.get(MeasureResult.INVALID),
                        measureCounts.get(MeasureResult.IGNORE)),
                20, 20);
    }

    private Polygon getVisionPolygon(Agent agent) {
        ArrayList<Double> angles = new ArrayList<Double>(agent.getVision().keySet());
        java.util.Collections.sort(angles);

        int[] xpoints = new int[angles.size() + 2];
        int[] ypoints = new int[angles.size() + 2];
        Long x = Math.round(normalizeCoordinate(agent.getX(), minX, ratio));
        xpoints[0] = Integer.valueOf(x.intValue());
        Long y = Math.round(normalizeCoordinate(agent.getY(), minY, ratio));
        ypoints[0] = Integer.valueOf(y.intValue());
        Map<Double, Double> vision = agent.getVision();

        for (int i = 0; i < angles.size(); i++) {
            double angle = angles.get(i);
            x = Math.round(getVisionXCoordinate(agent.getX(), agent.getAlpha(), angle, vision.get(angle)));
            y = Math.round(getVisionYCoordinate(agent.getY(), agent.getAlpha(), angle, vision.get(angle)));
            xpoints[i + 1] = Integer.valueOf(x.intValue());
            ypoints[i + 1] = Integer.valueOf(y.intValue());
        }
        xpoints[xpoints.length - 1] = xpoints[0];
        ypoints[ypoints.length - 1] = ypoints[0];
        return new Polygon(xpoints, ypoints, angles.size());
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

    @Override
    public void onAgentMoved(AgentMove move) {
        Agent agent = agents.get(currentAgentIndex);

        switch (move) {
            case UP:
                agent.setY(agent.getY() - 0.05);
                break;
            case DOWN:
                agent.setY(agent.getY() + 0.05);
                break;
            case LEFT:
                agent.setX(agent.getX() - 0.05);
                break;
            case RIGHT:
                agent.setX(agent.getX() + 0.05);
                break;
            case ROTATE_LEFT:
                agent.setAlpha(agent.getAlpha() - 2);
                break;
            case ROTATE_RIGHT:
                agent.setAlpha(agent.getAlpha() + 2);
                break;
        }
        repaint();
    }

    public void nextMeasure() {
        currentMeasureIndex = (currentMeasureIndex + 1) % measurementReader.getSize();
        for (Agent agent : agents) {
            agent.setMeasure(measurementReader.getMeasure(currentMeasureIndex));
        }
        repaint();
        setFocusable(true);
        requestFocusInWindow();

    }

    public void nextAgent() {
        currentAgentIndex = (currentAgentIndex + 1) % agents.size();
        repaint();
        setFocusable(true);
        requestFocusInWindow();
    }
}
