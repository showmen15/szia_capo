package pl.edu.agh.capo.scheduler;

import pl.edu.agh.capo.hough.HoughTransform;
import pl.edu.agh.capo.hough.test.NoHoughTransform;
import pl.edu.agh.capo.logic.Agent;
import pl.edu.agh.capo.logic.robot.Measure;

import java.util.List;

public class Scheduler {

    private UpdateMeasureListener listener;

    private FitnessTimeDivider divider;
    private boolean updateMeasures = false;
    private final double robotMaxLinearVelocity;
    private Measure lastMeasure;
    private double measureDiffInSeconds;

    public Scheduler(double robotMaxLinearVelocity) {
        this.robotMaxLinearVelocity = robotMaxLinearVelocity;
    }

    public void setDivider(FitnessTimeDivider divider) {
        this.divider = divider;
    }

    public void update(Measure measure) {

        //new HoughTransform().run(measure.getVisions());
        if (updateMeasures && measure == null) {
            System.out.println(Double.toString(divider.fitnessSumMedium / divider.fitnessSumMediumCount).replace('.', ','));
            divider.fitnessSumMedium = 0.0;
            divider.fitnessSumMediumCount = 0;
            lastMeasure = null;
            return;
            //System.exit(1);
        }

        if (divider != null) {
            if (updateMeasures) {
                if (lastMeasure != null) {
                    measureDiffInSeconds = (measure.getDatetime() - lastMeasure.getDatetime()) / 1000.0;
                }

                lastMeasure = measure;
            }

            divider.updateFitnesses();
            new Thread(new Worker(divider.getTimes(), measure)).start();
            divider.recalculate();
        }
    }

    public void setUpdateMeasures(boolean updateMeasures) {
        this.updateMeasures = updateMeasures;
    }

    public boolean isUpdateMeasures() {
        return updateMeasures;
    }


    public void setListener(UpdateMeasureListener listener) {
        this.listener = listener;
    }

    public double getRobotMaxLinearVelocity() {
        return this.robotMaxLinearVelocity;
    }

    private class Worker implements Runnable {

        private final List<FitnessTimeDivider.AgentInfo> infos;

        private Agent agent;
        private int time;
        private long startTime;

        private final Measure measure;
        private final HoughTransform houghTransform = new NoHoughTransform();

        private Worker(List<FitnessTimeDivider.AgentInfo> infos, Measure measure) {
            this.infos = infos;
            this.measure = measure;
        }

        private void updateMeasure(FitnessTimeDivider.AgentInfo info) {
            this.startTime = System.currentTimeMillis();
            this.agent = info.getAgent();
            this.time = info.getTime();

            if (updateMeasures && measure != null) {
                agent.setMeasure(measure, houghTransform.getLines(8, 4), measureDiffInSeconds);
                //agent.setMeasure(measure, new ArrayList<>());
                agent.estimateFitness();
            }

            try {
                checkTime();
                while (true) {
                    agent.estimateRandom();
                    checkTime();
                }
            } catch (TimeoutException e) {
            }
        }

        @Override
        public void run() {
            //System.out.println("starting worker with " + infos.size());
            long time = System.currentTimeMillis();
            houghTransform.run(measure.getVisions());

            infos.forEach(this::updateMeasure);
            if (listener != null) {
                new Thread(listener::onUpdate).start();
            }
            long end = System.currentTimeMillis();
            //System.out.println("took: " + (end - time));
        }

        private void checkTime() throws TimeoutException {
            long diff = System.currentTimeMillis() - startTime;
            if (diff >= time) {
                throw new TimeoutException();
            }
        }

        private class TimeoutException extends Exception {
        }
    }

    public interface UpdateMeasureListener {
        void onUpdate();
    }
}