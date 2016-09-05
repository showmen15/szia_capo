package pl.edu.agh.capo.simulation.simulation;

import pl.edu.agh.capo.logic.scheduler.Scheduler;
import pl.edu.agh.capo.robot.CapoRobotConstants;
import pl.edu.agh.capo.robot.Measure;

import java.util.Iterator;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MeasureSimulator implements Runnable {
    private final Iterator<Measure> measures;
    private final ScheduledExecutorService executorService;
    private final Scheduler scheduler;
    private boolean updateMeasures = false;

    public MeasureSimulator(Iterator<Measure> measures, Scheduler scheduler) {
        this.measures = measures;
        this.scheduler = scheduler;
        executorService = new ScheduledThreadPoolExecutor(1);
    }

    public void start() {
        executorService.scheduleAtFixedRate(this, 0, CapoRobotConstants.INTERVAL_TIME, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        executorService.shutdown();
    }

    public void setUpdateMeasures(boolean updateMeasures) {
        this.updateMeasures = updateMeasures;
    }

    @Override
    public void run() {
        if (updateMeasures) {
            scheduler.update(measures.next());
        } else {
            scheduler.update();
        }
    }
}
