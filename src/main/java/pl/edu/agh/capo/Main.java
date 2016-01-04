package pl.edu.agh.capo;

import pl.edu.agh.capo.scheduler.Scheduler;
import pl.edu.agh.capo.simulation.MeasureFileReader;
import pl.edu.agh.capo.simulation.MeasureSimulator;
import pl.edu.agh.capo.ui.CapoMazeVisualizer;

public class Main {

    public static final int PERIOD_TIME = 200;      //200 ms
    public static final double robotMaxLinearVelocity = 700; // estimated on given data(DaneLabirynt(1|2|3).csv)

    public static void main(String[] args) {
        Scheduler scheduler = new Scheduler(robotMaxLinearVelocity);
        MeasureSimulator simulator = new MeasureSimulator(new MeasureFileReader("DaneLabirynt3.csv"), scheduler, PERIOD_TIME);
        simulator.start();
        CapoMazeVisualizer.getInstance().open(scheduler, PERIOD_TIME);
    }
}
