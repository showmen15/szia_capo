package pl.edu.agh.capo.simulation.ui;

import pl.edu.agh.capo.maze.MazeMap;
import pl.edu.agh.capo.simulation.simulation.SimulationEnvironment;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class InfoPanel extends JPanel {
    private final MazePanel mazePanel;

    private JButton nextAgentButton;

    private JButton prevAgentButton;
    private JLabel agentsLabel;

    private JLabel measuredFitness;
    private JLabel measuredEnergy;

    private JCheckBox bestAgentCheckbox;
    private JCheckBox measureCheckbox;
    private JCheckBox showAllCheckbox;

    private SimulationEnvironment environment;

    public InfoPanel(MazePanel mazePanel) {
        super();
        this.mazePanel = mazePanel;
        buildView();
        setFocusable(true);
        requestFocusInWindow();
    }

    public void buildEnvironment(MazeMap map) {
        mazePanel.setMaze(map);
        environment = SimulationEnvironment.build(map);
        environment.setUpdateMeasures(measureCheckbox.isSelected());
        environment.setUpdateListener(this::updateView);
        environment.setShowBestAgent(bestAgentCheckbox.isSelected());
        updateView();
        setButtonsEnable(true);
    }

    private void updateView() {
        mazePanel.setAgents(environment.getViewAgents());
        mazePanel.repaint();
        agentsLabel.setText(String.format("Agent - %s", environment.getCurrentAgent().getRoom().getSpaceId()));
        measuredFitness.setText(String.format("Fitnes: %f", environment.getCurrentAgent().getFitness()));
        measuredEnergy.setText(String.format("Energy: %f", environment.getCurrentAgent().getEnergy()));
        setFocusable(true);
        requestFocusInWindow();
    }

    private void buildView() {
        JPanel buttonPanel = new JPanel(new GridLayout(8, 1));

        agentsLabel = buildLabel("Agent");
        buttonPanel.add(agentsLabel);

        JPanel panel = new JPanel(new GridLayout(1, 2));

        prevAgentButton = buildButton("< Poprzedni", prevAgentButtonListener());
        panel.add(prevAgentButton);

        nextAgentButton = buildButton(" Następny >", nextAgentButtonListener());
        panel.add(nextAgentButton);

        buttonPanel.add(panel);

        buttonPanel.add(Box.createHorizontalStrut(5)); // Fixed width invisible separator.

        measuredFitness = buildLabel("");
        buttonPanel.add(measuredFitness);

        measuredEnergy = buildLabel("");
        buttonPanel.add(measuredEnergy);

        measureCheckbox = buildCheckbox("Pobieraj pomiary cyklicznie");
        measureCheckbox.addActionListener(a -> environment.setUpdateMeasures(measureCheckbox.isSelected()));
        buttonPanel.add(measureCheckbox);

        showAllCheckbox = buildCheckbox("Pokaż wszystkich agentów");
        showAllCheckbox.doClick();
        showAllCheckbox.addActionListener(this::onShowAllClick);
        buttonPanel.add(showAllCheckbox);

        bestAgentCheckbox = buildCheckbox("Pokazuj najlepszego agenta");
        bestAgentCheckbox.addActionListener(a -> showBestAgent(bestAgentCheckbox.isSelected()));

        buttonPanel.add(bestAgentCheckbox);

        this.add(buttonPanel);

        setButtonsEnable(false);

        JLabel measureLabel = new JLabel("");
        this.add(measureLabel);
        this.invalidate();
    }

    private void onShowAllClick(ActionEvent actionEvent) {
        boolean showAllSelected = showAllCheckbox.isSelected();
        enableAgentNavigationButtons(!showAllSelected);
        environment.setShowAll(showAllSelected);
    }

    private void enableAgentNavigationButtons(boolean enable) {
        nextAgentButton.setEnabled(enable);
        prevAgentButton.setEnabled(enable);
        bestAgentCheckbox.setEnabled(enable);
    }

    private void showBestAgent(boolean isSelected) {
        enableGoToAgentButtons(!isSelected);
        environment.setShowBestAgent(isSelected);
        updateView();
    }

    private void setButtonsEnable(boolean enable) {
        bestAgentCheckbox.setEnabled(enable);
        measureCheckbox.setEnabled(enable);
        showAllCheckbox.setEnabled(enable);
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

    private ActionListener nextAgentButtonListener() {
        return e -> {
            environment.nextAgent();
            updateView();
        };
    }

    private ActionListener prevAgentButtonListener() {
        return e -> {
            environment.previousAgent();
            updateView();
        };
    }
}
