package pl.edu.agh.capo.scheduler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Scheduler {

    public static final int OPERATION_TIME = 500;

    private final int periodTime;
    private final int count;

    private final Worker worker = new Worker();
    private final List<Integer> printerRuntimes;

    private final Random random = new Random();

    public Scheduler(int periodTime, int count) {
        this.periodTime = periodTime;
        this.count = count;
        printerRuntimes = Collections.synchronizedList(new ArrayList<Integer>(count));
        initializeRuntimes(count);
    }

    private void initializeRuntimes(int count) {
        for (int i = 0; i < count; i++) {
            printerRuntimes.add(periodTime / count);
        }
    }

    private void startWorker() {
        new Thread(worker).start();
    }

    public void start() {
        startWorker();
    }

    public void update() {
        //update data
        startWorker();
        recalculatePrintersRuntime();
    }

    private void recalculatePrintersRuntime() {
        System.out.println("Recalculating...");
        int leftTime = periodTime;
        for (int i = 0; i < count - 1; i++) {
            int time = random.nextInt(count) * OPERATION_TIME;
            if (time > leftTime) {
                time = leftTime;
            }
            printerRuntimes.set(i, time);
            leftTime -= time;
        }
        printerRuntimes.set(count - 1, leftTime);
    }

    private int getPrinterRuntime(int index) {
        return printerRuntimes.get(index);
    }

    private class Worker implements Runnable {

        private void count(int id) {
            int numberOfIterations = getPrinterRuntime(id) / OPERATION_TIME;
            for (int i = 0; i < numberOfIterations; i++) {
                try {
                    Thread.sleep(490);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(id);
            }
        }

        @Override
        public void run() {
            for (int id = 0; id < count; id++) {
                count(id);
            }
        }
    }
}