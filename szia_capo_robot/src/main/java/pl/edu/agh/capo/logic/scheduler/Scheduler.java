package pl.edu.agh.capo.logic.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.capo.hough.HoughTransform;
import pl.edu.agh.capo.hough.jni.KernelBasedHoughTransform;
import pl.edu.agh.capo.logic.Agent;
import pl.edu.agh.capo.logic.scheduler.divider.AbstractTimeDivider;
import pl.edu.agh.capo.robot.CapoRobotConstants;
import pl.edu.agh.capo.robot.Measure;

public class Scheduler {
    private static final Logger logger = LoggerFactory.getLogger(Scheduler.class);
    private final AbstractTimeDivider divider;

    private UpdateMeasureListener listener;
    private Measure currentMeasure;
    private double millisSinceLastMeasure = 0.0;

    public Scheduler(AbstractTimeDivider divider) {
        this.divider = divider;
    }

    public void update() {
        if (currentMeasure != null) {
            divider.updateFactors();
            Thread worker = createWorker();
            worker.start();
            divider.recalculate();
            try {
                worker.join();
            } catch (InterruptedException e) {
                logger.error("Could not wait for worker", e);
            }
        }
    }

    public void update(Measure measure) {
        if (measuresFinished(measure)) {
            divider.reset();
        } else {
            calculateMeasuresTimeDifference(measure);
            update();
        }
    }

    private boolean measuresFinished(Measure measure) {
        return measure == null;
    }

    private void calculateMeasuresTimeDifference(Measure measure) {
        if (this.currentMeasure != null) {
            millisSinceLastMeasure = measure.getDatetime() - this.currentMeasure.getDatetime();
        }
        this.currentMeasure = measure;
    }

    private Thread createWorker() {
        return new Thread(new Worker(currentMeasure));
    }

    public void setListener(UpdateMeasureListener listener) {
        this.listener = listener;
    }

    public interface UpdateMeasureListener {
        void onUpdate();
    }

    private class Worker implements Runnable {
        private final long[] nanoTimes;
        private final Measure measure;
        private final HoughTransform houghTransform = new KernelBasedHoughTransform();

        private Agent currentAgent;
        private long currentTime;
        private long currentStartTime;

        private Worker(Measure measure) {
            this.measure = measure;
            this.nanoTimes = divider.getTimes();
        }

        private void updateMeasure(AbstractTimeDivider.AgentFactorInfo info) {
            this.currentAgent = info.getAgent();
            long currentTime = nanoTimes[info.getIndex()];
            if (currentTime > 0) {
                updateAgentWithMeasure(measure);
                double countFactor = CapoRobotConstants.COUNT_TIME_FACTOR_MIN +
                        CapoRobotConstants.COUNT_TIME_FACTOR_RANGE_SIZE * currentAgent.getFitness();
                long countTime = (long) (countFactor * currentTime);
                resetTime(countTime);
                calculateUntilTimeLeft();
                int timeLeft = (int) (currentTime - (System.nanoTime() - currentStartTime));
                resetTime(timeLeft);
                estimateRandomUntilTimeLeft();
            }
        }

        private void calculateUntilTimeLeft() {
            try {
                checkTime();
                while (true) {
                    currentAgent.estimateInNeighbourhood();
                    checkTime();
                }
            } catch (TimeoutException e) {
            }
        }

        private void resetTime(long time) {
            this.currentTime = time;
            this.currentStartTime = System.nanoTime();
        }

        private void updateAgentWithMeasure(Measure measure) {
            if (measure != null) {
                measure.setLines(houghTransform.getLines());
                measure.setSections(houghTransform.getSections());
                currentAgent.setMeasure(measure, millisSinceLastMeasure);
            }
        }

        private void estimateRandomUntilTimeLeft() {
            try {
                currentAgent.prepareCalculations();
                while (currentAgent.calculate()) {
                    checkTime();
                }
                while (true) {
                    currentAgent.estimateRandom();
                    checkTime();
                }
            } catch (TimeoutException e) {
            }
        }

        private void checkTime() throws TimeoutException {
            long diff = System.nanoTime() - currentStartTime;
            if (diff >= currentTime) {
                throw new TimeoutException();
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

        private class TimeoutException extends Exception {
        }
    }
}