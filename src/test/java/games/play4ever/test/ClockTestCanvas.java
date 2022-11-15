package games.play4ever.test;

import games.play4ever.mapclocks.Clock;
import games.play4ever.mapclocks.ClockManager;
import games.play4ever.mapclocks.ClockUpdateThread;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

public class ClockTestCanvas extends Canvas{

    private Clock clock;



    public void paint(Graphics g) {
        g.drawImage(this.clock.getUpdated(), 0,0, 256, 256, this);
//        g.drawImage(this.clockRenderer.renderClock(), 0,0,this);
    }

    private ClockTestCanvas(File clockDir) throws Exception {
        this.clock = new Clock(clockDir);
        ClockManager.addClock(this.clock);
        ClockManager.initializeAllClocks();
        ClockManager.updateAllClockImages();
        ClockUpdateThread.launch();
    }

    public static void main(String[] args) {
        try {

            File clockDir = new File("src/main/resources/clocks/analog");
            ClockTestCanvas m = new ClockTestCanvas(clockDir);
            JFrame f = new JFrame();
            f.add(m);
            f.setBounds(300, 300, 260, 340);
            f.addWindowListener(new WindowAdapter(){
                public void windowClosing(WindowEvent we){
                    ClockUpdateThread.stopUpdates(); System.exit(0);
                }
            });
            f.setVisible(true);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}