package pl.edu.agh.capo.ui;

import pl.edu.agh.capo.logic.Agent;
import pl.edu.agh.capo.logic.common.AgentMove;
import pl.edu.agh.capo.logic.listener.IAgentMoveListener;
import pl.edu.agh.capo.maze.MazeMap;
import pl.edu.agh.capo.maze.Space;
import pl.edu.agh.capo.maze.helper.MazeHelper;
import pl.edu.agh.capo.scheduler.FitnessTimeDivider;
import pl.edu.agh.capo.scheduler.Scheduler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class InfoPanel extends JPanel implements IAgentMoveListener {

    private final Scheduler scheduler;
    private final MazePanel mazePanel;
    private final int periodTime;

    private JButton nextMeasureButton;
    private JButton nextAgentButton;
    private JButton prevAgentButton;

    private JLabel agentsLabel;
    private JLabel measuredProbability;

    private java.util.List<Agent> agents;
    private int currentAgentIndex;


    public InfoPanel(MazePanel mazePanel, Scheduler scheduler, int periodTime) {
        super();
        this.mazePanel = mazePanel;
        this.scheduler = scheduler;
        this.periodTime = periodTime;

        buildView();
        addKeyListener(new CapoKeyListener(this));
        setFocusable(true);
        requestFocusInWindow();
    }

    public void updateAgents(MazeMap map) {
        agents = new ArrayList<>();
        FitnessTimeDivider fitnessTimeDivider = new FitnessTimeDivider(periodTime, map.getSpaces().size());
        for (Space space : map.getSpaces()) {
            Agent agent = new Agent(MazeHelper.buildRoom(space.getId(), map));
            agents.add(agent);
            fitnessTimeDivider.addAgent(agent);
        }
        mazePanel.setMaze(map);
        scheduler.setDivider(fitnessTimeDivider);
        scheduler.setListener(this::onMeasure);
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

        if (agent.getRoom().coordinatesMatches(x, y)) {
            agent.setX(x);
            agent.setY(y);
            agent.estimateFitness();
            onMeasure();
        }
    }

    private void onMeasure() {
        measuredProbability.setText(String.format("Fitnes: %f", currentAgent().getFitness()));
        mazePanel.repaint();
        setFocusable(true);
        requestFocusInWindow();
    }

    private void buildView() {
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1));

        agentsLabel = buildLabel("Agent");
        buttonPanel.add(agentsLabel);

        JPanel panel = new JPanel(new GridLayout(1, 2));

        prevAgentButton = buildButton("< Poprzedni", prevAgentButtonListener());
        panel.add(prevAgentButton);

        nextAgentButton = buildButton(" Następny >", nextAgentButtonListener());
        panel.add(nextAgentButton);

        buttonPanel.add(panel);

        buttonPanel.add(Box.createHorizontalStrut(5)); // Fixed width invisible separator.

/*        JLabel measuresLabel = buildLabel("Odczyty");
        buttonPanel.add(measuresLabel);*/

/*        nextMeasureButton = buildButton("Pobierz nowy", nextMeasureButtonListener());
        buttonPanel.add(nextMeasureButton);*/

        measuredProbability = buildLabel("");
        buttonPanel.add(measuredProbability);

        JCheckBox checkBox = new JCheckBox("Pobieraj pomiary cyklicznie co 2 sek.");
        checkBox.setMnemonic(KeyEvent.VK_C);
        checkBox.setSelected(true);
        checkBox.addActionListener(a -> scheduler.setUpdateMeasures(checkBox.isSelected()));

        buttonPanel.add(checkBox);

        this.add(buttonPanel);

        setButtonsEnable(false);

        JLabel measureLabel = new JLabel("");
        this.add(measureLabel);
        this.invalidate();
    }

    private void setButtonsEnable(boolean enable) {
        //nextMeasureButton.setEnabled(enable);
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

    /* private ActionListener nextMeasureButtonListener() {
         return new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e) {
                 currentMeasureIndex = (currentMeasureIndex + 1) % measurementReader.getSize();
                 for (Agent agent : agents) {
                     agent.setMeasure(measurementReader.getMeasure(currentMeasureIndex));
                 }
                 Agent agent = currentAgent();
                 onMeasure(agent.getX(), agent.getY());
             }
         };
     }
 */
    private ActionListener nextAgentButtonListener() {
        return e -> showAgent((currentAgentIndex + 1) % agents.size());
    }

    private ActionListener prevAgentButtonListener() {
        return e -> {
            if (currentAgentIndex > 0) {
                showAgent(currentAgentIndex - 1);
            } else {
                showAgent(currentAgentIndex + agents.size() - 1);
            }
        };
    }

    private void showAgent(int index) {
        currentAgentIndex = index;
        Agent agent = currentAgent();
        agentsLabel.setText(String.format("Agent - %s", agent.getRoom().getSpaceId()));
        mazePanel.setAgent(agent);
        onMeasure();
    }

}
