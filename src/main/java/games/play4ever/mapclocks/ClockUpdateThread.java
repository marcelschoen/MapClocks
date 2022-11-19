package games.play4ever.mapclocks;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class ClockUpdateThread implements Runnable {

    private static boolean running = false;

    public static void launch() {
        if(!running) {
            Thread mapClocksUpdaterThread = new Thread(new ClockUpdateThread());
            mapClocksUpdaterThread.setName("MapClocks Updater");
            mapClocksUpdaterThread.start();
            running = true;
        }
    }

    @Override
    public void run() {
        while(running) {
            try {
                MapClocks.logInfo("*** Update clocks ***");
                ClockManager.updateAllClockImages();
                ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
                Thread.sleep(1000 * (60 - now.getSecond()));
            } catch(Exception e) {
                running = false;
            }
        }
    }

    public static void stopUpdates() {
        running = false;
    }
}
