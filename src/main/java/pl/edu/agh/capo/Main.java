package pl.edu.agh.capo;

import pl.edu.agh.capo.scheduler.Scheduler;
import pl.edu.agh.capo.simulation.MeasureFileReader;
import pl.edu.agh.capo.simulation.MeasureSimulator;
import pl.edu.agh.capo.ui.CapoMazeVisualizer;

public class Main {

    public static void main(String[] args) {
        Scheduler scheduler = new Scheduler();
        MeasureSimulator simulator = new MeasureSimulator(new MeasureFileReader("DaneLabirynt3.csv"), scheduler);
        simulator.start();
        CapoMazeVisualizer.getInstance().open(scheduler);
    }
}
