package games.play4ever.mapclocks;

public class ClockUpdateThread implements Runnable {

    private static boolean running = true;

    public static void launch() {
        Thread mapClocksUpdaterThread = new Thread(new ClockUpdateThread());
        mapClocksUpdaterThread.setName("MapClocks Updater");
        mapClocksUpdaterThread.start();
    }

    @Override
    public void run() {
        while(running) {
            try {
                for (int i = 0; i < 60 && running; i++) {
                    Thread.sleep(1000);
                }
                MapClocks.getClocks().stream().forEach(clock -> clock.updateImage());
            } catch(Exception e) {
                running = false;
            }
        }
    }

    public static void stopUpdates() {
        running = false;
    }
}
