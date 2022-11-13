package games.play4ever.mapclocks;

public class ClockUpdateThread implements Runnable {

    private boolean running = true;

    @Override
    public void run() {
        while(running) {
            try {
                for (int i = 0; i < 60 && running; i++) {
                    Thread.sleep(1000);
                }

            } catch(Exception e) {
                running = false;
            }
        }
    }

    public void stopUpdates() {
        running = false;
    }
}
