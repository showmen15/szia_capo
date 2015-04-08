package pl.edu.agh.capo.ui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InfoPanel extends JPanel {

    private MazePanel mazePanel;

    public InfoPanel(MazePanel mazePanel){
        super();
        this.mazePanel = mazePanel;
        JButton nextMeasureButton = new JButton("Kolejny pomiar");
        nextMeasureButton.addActionListener(nextMeasureButtonListener());
        this.add(nextMeasureButton);
        this.validate();

        JButton nextAgentButton = new JButton("Kolejny agent");
        nextAgentButton.addActionListener(nexAgentButtonListener());
        this.add(nextAgentButton);
        this.validate();

        JLabel measureLabel = new JLabel("");
        this.add(measureLabel);
        this.validate();

    }

    private ActionListener nextMeasureButtonListener(){
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mazePanel.nextMeasure();
            }
        };
    }

    private ActionListener nexAgentButtonListener(){
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mazePanel.nextAgent();
            }
        };
    }
}
