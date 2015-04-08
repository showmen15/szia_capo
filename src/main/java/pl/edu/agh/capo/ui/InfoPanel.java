package pl.edu.agh.capo.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InfoPanel extends JPanel {

    private MazePanel mazePanel;
    private final JButton nextMeasureButton;
    private final JButton nextAgentButton;
    private final JButton prevAgentButton;

    public InfoPanel(MazePanel mazePanel){
        super();
        this.mazePanel = mazePanel;

        JPanel buttonPanel = new JPanel(new GridLayout(5, 1));

        JLabel label = new JLabel("Odczyty");
        label.setHorizontalAlignment(JLabel.CENTER);
        buttonPanel.add(label);
        nextMeasureButton = buildButton("Pobierz nowy", nextMeasureButtonListener());
        buttonPanel.add(nextMeasureButton);

        buttonPanel.add(Box.createHorizontalStrut(5)); // Fixed width invisible separator.

        label = new JLabel("Agenci");
        label.setHorizontalAlignment(JLabel.CENTER);
        buttonPanel.add(label);

        JPanel panel = new JPanel(new GridLayout(1, 2));

        prevAgentButton = buildButton("< Poprzedni", prevAgentButtonListener());
        panel.add(prevAgentButton);

        nextAgentButton = buildButton(" Następny >", nextAgentButtonListener());
        panel.add(nextAgentButton);

        buttonPanel.add(panel);

        this.add(buttonPanel);

        setButtonsEnable(false);

        JLabel measureLabel = new JLabel("");
        this.add(measureLabel);
        this.invalidate();

    }

    public void enableButtons() {
        setButtonsEnable(true);
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

    private ActionListener nextMeasureButtonListener(){
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mazePanel.nextMeasure();
            }
        };
    }

    private ActionListener nextAgentButtonListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mazePanel.nextAgent();
            }
        };
    }

    private ActionListener prevAgentButtonListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mazePanel.prevAgent();
            }
        };
    }
}
