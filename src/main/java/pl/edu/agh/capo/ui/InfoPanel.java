package pl.edu.agh.capo.ui;

import pl.edu.agh.capo.logic.Agent;
import pl.edu.agh.capo.logic.Room;
import pl.edu.agh.capo.logic.common.AgentMove;
import pl.edu.agh.capo.logic.common.Location;
import pl.edu.agh.capo.logic.listener.IAgentMoveListener;
import pl.edu.agh.capo.logic.robot.CapoRobotConstants;
import pl.edu.agh.capo.maze.MazeMap;
import pl.edu.agh.capo.maze.helper.MazeHelper;
import pl.edu.agh.capo.scheduler.Scheduler;
import pl.edu.agh.capo.scheduler.divider.EnergyTimeDivider;
import pl.edu.agh.capo.scheduler.divider.TimeDivider;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class InfoPanel extends JPanel implements IAgentMoveListener {

    private final Scheduler scheduler;
    private final MazePanel mazePanel;

    private JButton nextAgentButton;
    private JButton prevAgentButton;

    private JLabel agentsLabel;
    private JLabel measuredProbability;

    // private int currentAgentIndex;
    private Agent currentAgent;
    private JCheckBox bestAgentCheckbox;
    private JCheckBox measureCheckbox;
    private TimeDivider timeDivider;


    public InfoPanel(MazePanel mazePanel, Scheduler scheduler) {
        super();
        this.mazePanel = mazePanel;
        this.scheduler = scheduler;

        buildView();
        addKeyListener(new CapoKeyListener(this));
        setFocusable(true);
        requestFocusInWindow();
    }

    public void updateAgents(MazeMap map) {
        java.util.List<Room> rooms = MazeHelper.buildRooms(map);
        timeDivider = new EnergyTimeDivider(rooms, map.getSpaces().size(), CapoRobotConstants.INTERVAL_TIME);
        for (Room room : rooms) {
            Agent agent = new Agent(room);
            timeDivider.addAgent(agent);
        }
        mazePanel.setMaze(map);
        mazePanel.setAgents(timeDivider.getAgents());
        scheduler.setDivider(timeDivider);
        scheduler.setListener(this::onMeasure);
        scheduler.setUpdateMeasures(measureCheckbox.isSelected());

        showAgent(timeDivider.getAgents().get(0));

        setButtonsEnable(true);
    }

    private Agent currentAgent() {
        // return timeDivider.getAgents().get(currentAgentIndex);
        return currentAgent;
    }

    @Override
    public void onAgentMoved(AgentMove move) {
        Agent agent = currentAgent();
        Location location = agent.getLocation();

        double x = location.positionX;
        double y = location.positionY;
        double alpha = location.alpha;

        switch (move) {
            case UP:
                y -= 0.05;
                break;
            case DOWN:
                y += 0.05;
                break;
            case LEFT:
                x -= 0.05;
                break;
            case RIGHT:
                x += 0.05;
                break;
            case ROTATE_LEFT:
                alpha -= 2;
                break;
            case ROTATE_RIGHT:
                alpha += 2;
                break;
        }

        if (agent.getRoom().coordinatesMatches(x, y)) {
            agent.setLocation(new Location(x, y, alpha));
            agent.estimateFitness();
            onMeasure();
        }
    }

    private void onMeasure() {
        if (bestAgentCheckbox.isSelected()) {
            showAgent(timeDivider.updateTheBest());
        } else {
            updateView();
        }
    }

    private void updateView() {
        measuredProbability.setText(String.format("Fitnes: %f", currentAgent().getFitness()));
        mazePanel.repaint();
        setFocusable(true);
        requestFocusInWindow();
    }

    private void buildView() {
        JPanel buttonPanel = new JPanel(new GridLayout(6, 1));

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

        measureCheckbox = buildCheckbox("Pobieraj pomiary cyklicznie co 2 sek.");
        measureCheckbox.addActionListener(a -> scheduler.setUpdateMeasures(measureCheckbox.isSelected()));

        buttonPanel.add(measureCheckbox);

        bestAgentCheckbox = buildCheckbox("Pokazuj najlepszego agenta");
        bestAgentCheckbox.addActionListener(a -> showBestAgent(bestAgentCheckbox.isSelected()));

        buttonPanel.add(bestAgentCheckbox);

        this.add(buttonPanel);

        setButtonsEnable(false);

        JLabel measureLabel = new JLabel("");
        this.add(measureLabel);
        this.invalidate();
    }

    private void showBestAgent(boolean isSelected) {
        enableGoToAgentButtons(!isSelected);
    }

    private void setButtonsEnable(boolean enable) {
        //nextMeasureButton.setEnabled(enable);
        bestAgentCheckbox.setEnabled(enable);
        measureCheckbox.setEnabled(enable);
        enableGoToAgentButtons(enable);
    }

    private void enableGoToAgentButtons(boolean enable) {
        if (!bestAgentCheckbox.isSelected() || !enable) {
            nextAgentButton.setEnabled(enable);
            prevAgentButton.setEnabled(enable);
        }
    }

    private JButton buildButton(String title, ActionListener listener) {
        JButton button = new JButton(title);
        button.addActionListener(listener);
        return button;
    }

    private JCheckBox buildCheckbox(String title) {
        JCheckBox checkBox = new JCheckBox(title);
        checkBox.setMnemonic(KeyEvent.VK_C);
        checkBox.setSelected(true);
        return checkBox;
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
        //return e -> showAgent((currentAgentIndex + 1) % timeDivider.getAgentCount());
        return e -> {
        };
    }

    private ActionListener prevAgentButtonListener() {
        /*return e -> {
            if (currentAgentIndex > 0) {
                showAgent(currentAgentIndex - 1);
            } else {
                showAgent(currentAgentIndex + timeDivider.getAgentCount() - 1);
            }
        };*/
        return e -> {
        };
    }
/*
    private void showAgent(int index) {
        currentAgentIndex = index;
        Agent agent = currentAgent();
        agentsLabel.setText(String.format("Agent - %s", agent.getRoom().getSpaceId()));
        mazePanel.setAgent(agent);
        updateView();
    }*/

    private void showAgent(Agent agent) {
        currentAgent = agent;
        agentsLabel.setText(String.format("Agent - %s", agent.getRoom().getSpaceId()));
        mazePanel.setAgent(agent, timeDivider.getFactor(agent));
        updateView();
    }

}
