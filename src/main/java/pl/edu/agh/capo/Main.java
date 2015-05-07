package pl.edu.agh.capo;

import pl.edu.agh.capo.scheduler.Scheduler;
import pl.edu.agh.capo.simulation.MeasureFileReader;
import pl.edu.agh.capo.simulation.MeasureSimulator;
import pl.edu.agh.capo.ui.CapoMazeVisualizer;

public class Main {

    public static final int PERIOD_TIME = 2000;      //200 ms

    public static void main(String[] args) {
        Scheduler scheduler = new Scheduler();
        MeasureSimulator simulator = new MeasureSimulator(new MeasureFileReader("DaneLabirynt1.csv"), scheduler, PERIOD_TIME);
        simulator.start();
        CapoMazeVisualizer.getInstance().open(scheduler, PERIOD_TIME);
    }
}
