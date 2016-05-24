package pl.edu.agh.capo;

import pl.edu.agh.capo.scheduler.Scheduler;
import pl.edu.agh.capo.simulation.MeasureFileReader;
import pl.edu.agh.capo.simulation.MeasureSimulator;
import pl.edu.agh.capo.simulation.files.IMeasureFile;
import pl.edu.agh.capo.simulation.files.SimpleMazeMeasureFile;
import pl.edu.agh.capo.statistics.IStatisticsPrinter;
import pl.edu.agh.capo.statistics.StatisticsPrinter;
import pl.edu.agh.capo.ui.CapoMazeVisualizer;

public class Main {

    public static void main(String[] args) {
        Scheduler scheduler = new Scheduler();
        IMeasureFile measureFile = new SimpleMazeMeasureFile();
        MeasureSimulator simulator = new MeasureSimulator(new MeasureFileReader(measureFile.getMeasures()), scheduler);
        IStatisticsPrinter statisticsPrinter = new StatisticsPrinter(measureFile.getPath());
        simulator.start();
        CapoMazeVisualizer.getInstance().open(scheduler, statisticsPrinter);
    }
}
