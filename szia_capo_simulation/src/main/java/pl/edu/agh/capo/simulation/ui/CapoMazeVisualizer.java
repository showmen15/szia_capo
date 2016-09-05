package pl.edu.agh.capo.simulation.ui;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.capo.maze.MazeMap;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class CapoMazeVisualizer extends JFrame {

    private static final Dimension FRAME_SIZE = new Dimension(900, 660);
    private static final int SPLIT_DIVIDER_LOCATION = 600;

    private final Logger logger = LoggerFactory.getLogger(CapoMazeVisualizer.class);

    private static final CapoMazeVisualizer instance = new CapoMazeVisualizer();

    private InfoPanel infoPanel;
    private MazePanel mazePanel;

    private CapoMazeVisualizer() {
        super("CAPO maze editor");
    }

    public static CapoMazeVisualizer getInstance() {
        return instance;
    }

    public void open() {
        setJMenuBar(createMenuBar());

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        mazePanel = new MazePanel();
        infoPanel = new InfoPanel(mazePanel);
        setContentPane(createSplitPanel());
        setSize(FRAME_SIZE);
        setVisible(true);
        setResizable(false);
    }

    private JMenuBar createMenuBar() {

        JMenu menu = new JMenu("Plik");

        JMenuItem menuItem = new JMenuItem("Wczytaj plik...", KeyEvent.VK_T);
        menuItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser("./");
            fileChooser.setFileFilter(new FileNameExtensionFilter("RoboMaze map (*.roson)", "roson"));
            if (fileChooser.showOpenDialog(CapoMazeVisualizer.this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                logger.debug("Opening file: " + file.getName());
                Gson gson = new Gson();
                try {
                    MazeMap mazeMap = gson.fromJson(new FileReader(file), MazeMap.class);
                    infoPanel.buildEnvironment(mazeMap);
                } catch (FileNotFoundException e1) {
                    logger.debug("Could not read file: " + file.getName());
                }
            }
        });

        menu.add(menuItem);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(menu);
        return menuBar;
    }

    private JSplitPane createSplitPanel() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mazePanel, infoPanel);
        splitPane.setDividerSize(5);
        splitPane.setDividerLocation(SPLIT_DIVIDER_LOCATION);
        splitPane.setEnabled(false);

        return splitPane;
    }

}
