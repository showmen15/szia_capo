package pl.edu.agh.capo.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.capo.hough.HoughTransform;
import pl.edu.agh.capo.hough.jni.KernelBasedHoughTransform;
import pl.edu.agh.capo.logic.Agent;
import pl.edu.agh.capo.logic.robot.CapoRobotConstants;
import pl.edu.agh.capo.logic.robot.Measure;
import pl.edu.agh.capo.scheduler.divider.TimeDivider;

public class Scheduler {
    private static final Logger logger = LoggerFactory.getLogger(Scheduler.class);
    private UpdateMeasureListener listener;
    private TimeDivider divider;
    private boolean updateMeasures = false;

    private Measure currentMeasure;
    private double millisSinceLastMeasure;

    public void setDivider(TimeDivider divider) {
        this.divider = divider;
    }

    private void printAndResetStatistics() {
        divider.printAndResetStatistics();
    }

    private boolean measuresFinished(Measure measure) {
        return updateMeasures && measure == null;
    }

    private void calculateMeasuresTimeDifference(Measure measure) {
        if (updateMeasures) {
            if (this.currentMeasure != null && measure != null) {
                millisSinceLastMeasure = measure.getDatetime() - this.currentMeasure.getDatetime();
            }
            this.currentMeasure = measure;
        }
    }

    private Thread createWorker() {
        return new Thread(new Worker(currentMeasure));
    }

    public void update(Measure measure) {
        if (measuresFinished(measure)) {
            printAndResetStatistics();
        } else if (divider != null) {
            calculateMeasuresTimeDifference(measure);
            divider.updateFactors();
            Thread worker = createWorker();
            worker.start();
            divider.recalculate();
            try {
                worker.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isUpdateMeasures() {
        return updateMeasures;
    }

    public void setUpdateMeasures(boolean updateMeasures) {
        this.updateMeasures = updateMeasures;
    }

    public void setListener(UpdateMeasureListener listener) {
        this.listener = listener;
    }

    public interface UpdateMeasureListener {
        void onUpdate();
    }

    private class Worker implements Runnable {

        private final int[] times;
        private final Measure measure;
        private final HoughTransform houghTransform = new KernelBasedHoughTransform();

        private Agent currentAgent;
        private int currentTime;
        private long currentStartTime;

        private Worker(Measure measure) {
            this.measure = measure;
            this.times = divider.getTimes();
        }

        private void updateMeasure(TimeDivider.AgentFactorInfo info) {
            this.currentStartTime = System.currentTimeMillis();
            this.currentAgent = info.getAgent();
            this.currentTime = times[info.getIndex()];
            if (currentTime > 0) {
                updateAgentWithMeasure(measure);
                estimateRandomUntilTimeLeft();
            }
        }

        private void updateAgentWithMeasure(Measure measure) {
            if (updateMeasures && measure != null) {
                currentAgent.setMeasure(measure, houghTransform.getLines(), millisSinceLastMeasure);
                //agent.setMeasure(measure, new ArrayList<>());
                currentAgent.estimateFitness();
            }
        }

        private void estimateRandomUntilTimeLeft() {
            try {
                checkTime();
                while (true) {
                    currentAgent.estimateRandom();
                    checkTime();
                }
            } catch (TimeoutException e) {
            }
        }

        @Override
        public void run() {
            //  long time = System.currentTimeMillis();
            houghTransform.run(measure, CapoRobotConstants.HOUGH_THRESHOLD, CapoRobotConstants.HOUGH_MAX_LINES_COUNT);

            divider.getAgentFactorInfos().forEach(this::updateMeasure);
            if (listener != null) {
                new Thread(listener::onUpdate).start();
            }
            //     long end = System.currentTimeMillis();
            //    logger.debug("ovetime: " + (end - time - CapoRobotConstants.INTERVAL_TIME));
        }

        private void checkTime() throws TimeoutException {
            long diff = System.currentTimeMillis() - currentStartTime;
            if (diff >= currentTime) {
                throw new TimeoutException();
            }
        }

        private class TimeoutException extends Exception {
        }
    }
}