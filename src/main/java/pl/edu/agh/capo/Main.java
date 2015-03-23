package pl.edu.agh.capo;

import pl.edu.agh.capo.scheduler.Scheduler;
import pl.edu.agh.capo.ui.CapoMazeVisualizer;

public class Main {

    public static final int PERIOD_TIME = 4000;      //200 ms

    public static void main(String[] args) {
        CapoMazeVisualizer.getInstance().open();
        Scheduler scheduler = new Scheduler(PERIOD_TIME, 4);
        scheduler.start();

        while (true) {
            try {
                Thread.sleep(PERIOD_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            scheduler.update();
        }
    }
}
