package games.play4ever.mapclocks;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

public class ClockRenderer {


    public BufferedImage renderClock(Clock clock) {
        BufferedImage image = deepCopy(clock.getBackground());

        Graphics g = image.getGraphics();
        g.drawImage(clock.getHourHand(), 64, 64, null);
        g.dispose();

        return image;
    }

    private static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(bi.getRaster().createCompatibleWritableRaster());
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }
}
