package pl.edu.agh.capo.simulation;


import org.slf4j.LoggerFactory;
import pl.edu.agh.capo.simulation.ui.CapoMazeVisualizer;

public class Main {

    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> LoggerFactory.getLogger(Main.class).error("Unexpected error", e));
        CapoMazeVisualizer.getInstance().open();
    }
}
