package games.play4ever.mapclocks;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class ClockRenderer extends MapRenderer {

    private Clock clock = null;

    public ClockRenderer(Clock clock) {
        this.clock = clock;
    }

    @Override
    public void render(MapView view, MapCanvas canvas, Player player) {
        view.setTrackingPosition(false);
        canvas.drawImage(0,0, renderClock());
    }

    public BufferedImage renderClock() {
        BufferedImage image = deepCopy(clock.getBackground());
        Graphics g = image.getGraphics();
        if(clock.getClockType() == Clock.TYPES.analog) {
            ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
            int currentHour = now.getHour() > 12 ? now.getHour() - 12 : now.getHour();
            int currentMinute = now.getMinute();
            MapClocks.logInfo("> Hour: " + currentHour + " / minute: " + currentMinute);

            int hourAngle = currentHour * (360 / 12); // TODO - add minutes
            drawAnalogHand(g, clock.getHourHand(), hourAngle);

            int minuteAngle = currentMinute * (360 / 60);
            drawAnalogHand(g, clock.getMinuteHand(), minuteAngle);

            // draw center element over hands
            int centerX = 64 - clock.getCenter().getWidth() / 2;
            int centerY = 64 - clock.getCenter().getHeight() / 2;
            g.drawImage(clock.getCenter(), centerX, centerY, null);
        } else {
            int startX = 10; // TODO - implement correct

        }
        g.dispose();

        return image;
    }

    private void drawAnalogHand(Graphics g, BufferedImage image, int angle) {
        MapClocks.logInfo("Angle: " + angle);
        // The required drawing location
        // TODO - Implement optional configured offset
        int drawLocationX = 64 - image.getWidth() / 2;
        int drawLocationY = 64 - image.getHeight();

        AffineTransform tx = new AffineTransform();
        tx.rotate(Math.toRadians(angle), image.getWidth() / 2, image.getHeight());

        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
        // Drawing the rotated image at the required drawing locations
        g.drawImage(op.filter(image, null), drawLocationX, drawLocationY, null);
    }

    private static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(bi.getRaster().createCompatibleWritableRaster());
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }
}
