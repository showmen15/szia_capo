public class Main {

    public static final int PERIOD_TIME = 4000;      //200 ms

    public static void main(String[] args) {
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
