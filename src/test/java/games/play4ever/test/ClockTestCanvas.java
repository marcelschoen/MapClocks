package games.play4ever.test;

import games.play4ever.mapclocks.Clock;
import games.play4ever.mapclocks.ClockManager;
import games.play4ever.mapclocks.ClockUpdateThread;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ClockTestCanvas extends Canvas{


    public static int showHour = 1;
    public static int showOffset = 0;

    public static Clock showClock = null;


    public void paint(Graphics g) {
        g.drawImage(showClock.getUpdated(showHour, showOffset), 0,0, 256, 256, this);
    }

    private ClockTestCanvas() throws Exception {
    }

    public static void main(String[] args) throws Exception {
        try {
            File clockDir = new File("src/main/resources/clocks");
            List<String> clocks = Arrays.asList(clockDir.list()).stream().collect(Collectors.toList());
            clocks.forEach(clockName -> {
                try {
                    Clock clock = new Clock(clockName);
                    clock.configure(new File(clockDir, clockName));
                    ClockManager.addClock(clock);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } );
            ClockManager.initializeAllClocks();
            ClockManager.updateAllClockImages();
            ClockUpdateThread.launch();
            showClock = ClockManager.getClockByName(clocks.get(0));

            System.out.println("Clocks: " + clocks.size());
            JFrame frame = new JFrame();
            ClockTestCanvas clockCanvas = new ClockTestCanvas();

            JComboBox clockList = new JComboBox(clocks.toArray());
            clockList.setSelectedIndex(0);
            clockList.addActionListener(e -> {
                JComboBox cb = (JComboBox) e.getSource();
                String clockName = (String) cb.getSelectedItem();
                System.out.println("Selected clock: " + clockName);
                showClock = ClockManager.getClockByName(clockName);
                clockCanvas.repaint();
            });

            Integer[] hours = new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
            JComboBox hourList = new JComboBox(hours);
            hourList.addActionListener(e -> {
                JComboBox cb = (JComboBox) e.getSource();
                Integer hour = (Integer) cb.getSelectedItem();
                showHour = hour;
                System.out.println("Selected hour: " + hour);
                clockCanvas.repaint();
            });
            ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
            hourList.setSelectedItem(now.getHour());
            System.out.println("Hour now: " + now.getHour());

            Integer[] offsets = new Integer[] { -11, -10, -9, -8, -7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };
            JComboBox offsetList = new JComboBox(offsets);
            offsetList.addActionListener(e -> {
                JComboBox cb = (JComboBox) e.getSource();
                Integer offset = (Integer) cb.getSelectedItem();
                showOffset = offset;
                System.out.println("Selected offset: " + offset);
                clockCanvas.repaint();
            });
            offsetList.setSelectedItem(0);

            System.out.println("Show clock: " + clockList.getSelectedItem().toString());

            JPanel panel = new JPanel();
            panel.setBackground(Color.white);
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            panel.add(clockList);
            panel.add(hourList);
            panel.add(offsetList);

            frame.add(panel);

            JPanel clockPanel = new JPanel();
            clockPanel.add(clockCanvas);
            clockCanvas.setSize(new Dimension(300, 400));


            frame.add(clockPanel);
            frame.setLayout(new FlowLayout());
            frame.setBounds(300, 300, 400, 400);
            frame.addWindowListener(new WindowAdapter(){
                public void windowClosing(WindowEvent we){
                    ClockUpdateThread.stopUpdates(); System.exit(0);
                }
            });

            frame.setVisible(true);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}