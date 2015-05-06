package pl.edu.agh.capo.ui;

import pl.edu.agh.capo.logic.Agent;
import pl.edu.agh.capo.logic.MeasureAnalyzer;
import pl.edu.agh.capo.logic.common.AgentMove;
import pl.edu.agh.capo.logic.common.MeasureResult;
import pl.edu.agh.capo.logic.common.MeasurementReader;
import pl.edu.agh.capo.logic.exception.CoordinateOutOfRoomException;
import pl.edu.agh.capo.logic.interfaces.IAgentMoveListener;
import pl.edu.agh.capo.maze.MazeMap;
import pl.edu.agh.capo.maze.Space;
import pl.edu.agh.capo.maze.helper.MazeHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Map;

public class InfoPanel extends JPanel implements IAgentMoveListener {

    private MazePanel mazePanel;
    private JButton nextMeasureButton;
    private JButton nextAgentButton;
    private JButton prevAgentButton;

    private java.util.List<Agent> agents;

    private final MeasurementReader measurementReader;

    private int currentMeasureIndex;
    private int currentAgentIndex;
    private JLabel measureValidCount;
    private JLabel measureInvalidCount;
    private JLabel measureIgnoredCount;
    private JLabel agentsLabel;
    private JLabel measuredProbability;

    public InfoPanel(MazePanel mazePanel, MeasurementReader measurementReader) {
        super();
        this.mazePanel = mazePanel;
        this.measurementReader = measurementReader;

        buildView();
        addKeyListener(new CapoKeyListener(this));
        setFocusable(true);
        requestFocusInWindow();
    }

    public void updateAgents(MazeMap map) {
        agents = new ArrayList<Agent>();
        for (Space space : map.getSpaces()) {
            Agent agent = new Agent(MazeHelper.buildRoom(space.getId(), map));
            agent.setMeasure(measurementReader.getMeasure(currentMeasureIndex));
            agents.add(agent);
        }
        mazePanel.setMaze(map);
        showAgent(0);

        setButtonsEnable(true);
    }

    private Agent currentAgent() {
        return agents.get(currentAgentIndex);
    }

    @Override
    public void onAgentMoved(AgentMove move) {
        Agent agent = currentAgent();
        double x = agent.getX();
        double y = agent.getY();

        switch (move) {
            case UP:
                y = y - 0.05;
                break;
            case DOWN:
                y = y + 0.05;
                break;
            case LEFT:
                x = x - 0.05;
                break;
            case RIGHT:
                x = x + 0.05;
                break;
            case ROTATE_LEFT:
                agent.setAlpha(agent.getAlpha() - 2);
                break;
            case ROTATE_RIGHT:
                agent.setAlpha(agent.getAlpha() + 2);
                break;
        }
        measure(x, y);
    }

    private void measure(double x, double y) {
        Agent agent = currentAgent();

        try {
            MeasureAnalyzer analyzer = new MeasureAnalyzer(agent.getRoom(), x, y);
            agent.setX(x);
            agent.setY(y);
            double probability = agent.analyzeMeasure(analyzer);
            measuredProbability.setText(String.format("Stosunek: %f", probability));

            mazePanel.repaint();
            Map<MeasureResult, Integer> measureCounts = agent.getMeasureCounts();

            measureValidCount.setText(String.format("Pasujących: %d", measureCounts.get(MeasureResult.VALID)));
            measureInvalidCount.setText(String.format("Niepasujących: %d", measureCounts.get(MeasureResult.INVALID)));
            measureIgnoredCount.setText(String.format("Brama: %d", measureCounts.get(MeasureResult.IGNORE)));

            setFocusable(true);
            requestFocusInWindow();
        } catch (CoordinateOutOfRoomException ignored) {
        }
    }

    private void buildView() {
        JPanel buttonPanel = new JPanel(new GridLayout(9, 1));

        agentsLabel = buildLabel("Agent");
        buttonPanel.add(agentsLabel);

        JPanel panel = new JPanel(new GridLayout(1, 2));

        prevAgentButton = buildButton("< Poprzedni", prevAgentButtonListener());
        panel.add(prevAgentButton);

        nextAgentButton = buildButton(" Następny >", nextAgentButtonListener());
        panel.add(nextAgentButton);

        buttonPanel.add(panel);

        buttonPanel.add(Box.createHorizontalStrut(5)); // Fixed width invisible separator.

        JLabel measuresLabel = buildLabel("Odczyty");
        buttonPanel.add(measuresLabel);

        nextMeasureButton = buildButton("Pobierz nowy", nextMeasureButtonListener());
        buttonPanel.add(nextMeasureButton);

        measureValidCount = buildLabel("");
        measureInvalidCount = buildLabel("");
        measureIgnoredCount = buildLabel("");
        buttonPanel.add(measureValidCount);
        buttonPanel.add(measureInvalidCount);
        buttonPanel.add(measureIgnoredCount);

        measuredProbability = buildLabel("");
        buttonPanel.add(measuredProbability);

        this.add(buttonPanel);

        setButtonsEnable(false);

        JLabel measureLabel = new JLabel("");
        this.add(measureLabel);
        this.invalidate();
    }

    private void setButtonsEnable(boolean enable) {
        nextMeasureButton.setEnabled(enable);
        nextAgentButton.setEnabled(enable);
        prevAgentButton.setEnabled(enable);
    }

    private JButton buildButton(String title, ActionListener listener) {
        JButton button = new JButton(title);
        button.addActionListener(listener);
        return button;
    }

    private JLabel buildLabel(String text) {
        JLabel label = new JLabel(text);
        label.setHorizontalAlignment(JLabel.CENTER);
        return label;
    }

    private ActionListener nextMeasureButtonListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentMeasureIndex = (currentMeasureIndex + 1) % measurementReader.getSize();
                for (Agent agent : agents) {
                    agent.setMeasure(measurementReader.getMeasure(currentMeasureIndex));
                }
                Agent agent = currentAgent();
                measure(agent.getX(), agent.getY());
            }
        };
    }

    private ActionListener nextAgentButtonListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAgent((currentAgentIndex + 1) % agents.size());
            }
        };
    }

    private ActionListener prevAgentButtonListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentAgentIndex > 0) {
                    showAgent(currentAgentIndex - 1);
                } else {
                    showAgent(currentAgentIndex + agents.size() - 1);
                }
            }
        };
    }

    private void showAgent(int index) {
        currentAgentIndex = index;
        Agent agent = currentAgent();
        agentsLabel.setText(String.format("Agent - %s", agent.getRoom().getSpaceId()));
        mazePanel.setAgent(agent);
        measure(agent.getX(), agent.getY());
    }
}
