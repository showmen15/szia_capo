package pl.edu.agh.capo.simulation.logic.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MultiThreadedScheduler {
    public static final int OPERATION_TIME = 500;
    private static final Logger logger = LoggerFactory.getLogger(MultiThreadedScheduler.class);

    private final int periodTime;
    private final int count;

    private final List<Printer> printers;
    private final List<Integer> printerRuntimes;

    private final Random random = new Random();

    public MultiThreadedScheduler(int periodTime, int count) {
        this.periodTime = periodTime;
        this.count = count;
        printerRuntimes = Collections.synchronizedList(new ArrayList<>(count));
        printers = new ArrayList<>(count);
        initializePrinters(count);
    }

    private void initializePrinters(int count) {
        for (int i = 0; i < count; i++) {
            Printer printer = new Printer(i);
            printers.add(printer);
            printerRuntimes.add(periodTime / count);
        }
    }

    public void start() {
        startPrinter(0);
    }

    public void update() {
        //update data
        recalculatePrintersRuntime();
        startPrinter(0);
    }

    private void recalculatePrintersRuntime() {
        logger.info("Recalculating...");
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

    private void startPrinter(int index) {
        if (index < count) {
            new Thread(printers.get(index)).start();
        }
    }

    private int getPrinterRuntime(int index) {
        return printerRuntimes.get(index);
    }

    private class Printer implements Runnable {

        private final int id;

        private Printer(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            int numberOfIterations = getPrinterRuntime(id) / OPERATION_TIME;
            for (int i = 0; i < numberOfIterations; i++) {
                try {
                    Thread.sleep(490);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                logger.debug(String.valueOf(id));
            }
            startPrinter(id + 1);
        }
    }
}