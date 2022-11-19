package games.play4ever.test;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClockListSelectionHandler implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        JComboBox cb = (JComboBox) e.getSource();
        String clockName = (String) cb.getSelectedItem();
        System.out.println("Selected clock: " + clockName);
    }
}
