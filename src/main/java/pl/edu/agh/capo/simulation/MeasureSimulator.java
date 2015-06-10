package pl.edu.agh.capo.simulation;

import pl.edu.agh.capo.logic.common.Measure;
import pl.edu.agh.capo.scheduler.Scheduler;

import java.util.Iterator;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MeasureSimulator implements Runnable {

    private final Iterator<Measure> measures;
    private final ScheduledExecutorService executorService;
    private final Scheduler scheduler;
    private final int periodTime;

    public MeasureSimulator(Iterator<Measure> measures, Scheduler scheduler, int periodTime) {
        this.measures = measures;
        this.scheduler = scheduler;
        this.periodTime = periodTime;
        executorService = new ScheduledThreadPoolExecutor(1);
    }

    public void start() {
        executorService.scheduleAtFixedRate(this, 0, periodTime, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        executorService.shutdown();
    }

    @Override
    public void run() {
        if (scheduler.isUpdateMeasures()) {
            scheduler.update(measures.next());
        } else {
            scheduler.update(null);
        }
    }
}
