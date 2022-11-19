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
                ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
                MapClocks.logInfo("*** Update clocks / current server time: " + now.getHour() + ":" + now.getMinute() + " ***");
                ClockManager.updateAllClockImages();
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
