package pl.edu.agh.capo.ui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InfoPanel extends JPanel {

    private MazePanel mazePanel;

    public InfoPanel(MazePanel mazePanel){
        super();
        this.mazePanel = mazePanel;
        JButton readDataButton = new JButton("Odczytaj pomiar");
        readDataButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Juz czytom");
            }
        });
        this.add(readDataButton);
        this.validate();
    }
}
