package games.play4ever.test;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class MyCanvas extends Canvas{



    public void paint(Graphics g) {
        Toolkit t=Toolkit.getDefaultToolkit();
        Image i=t.getImage("p3.gif");
        g.drawImage(i, 120,100,this);

    }

    public static void main(String[] args) {

        File clockDir = new File(args[0]);


        MyCanvas m=new MyCanvas();
        JFrame f=new JFrame();
        f.add(m);
        f.setSize(400,400);
        f.setVisible(true);
    }

}