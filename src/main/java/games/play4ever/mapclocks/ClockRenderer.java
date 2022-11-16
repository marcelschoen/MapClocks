package games.play4ever.mapclocks;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Renders the clock faces on the maps.
 *
 * @author Marcel Schoen
 */
public class ClockRenderer extends MapRenderer {

    private Clock clock = null;

    public ClockRenderer(Clock clock) {
        this.clock = clock;
    }

    @Override
    public void render(MapView view, MapCanvas canvas, Player player) {
        view.setTrackingPosition(false);
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        int currentHour = now.getHour() > 12 ? now.getHour() - 12 : now.getHour();
        canvas.drawImage(0,0, clock.getUpdated(currentHour, 0)); // TODO - OFFSET
    }

    public BufferedImage renderClock(int hour) {
        BufferedImage image = deepCopy(clock.getBackground());
        Graphics g = image.getGraphics();
        if(clock.getClockType() == Clock.TYPES.analog) {
            ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
            int currentMinute = now.getMinute();

            if(clock.getBackgroundColor() != null) {
                BufferedImage newBackground = deepCopy(clock.getBackground());
                g = newBackground.getGraphics();
                g.setColor(clock.getBackgroundColor());
                g.fillRect(0, 0, image.getWidth(), image.getHeight());
                g.drawImage(clock.getBackground(), 0, 0, null);
                image = newBackground;
            }

            Graphics2D g2 = (Graphics2D) g;
            g2.translate(clock.getCenterOffsetX(), clock.getCenterOffsetY());

            int radius = clock.getRadius();

            // Drawing the hour hand
            float hourHandLen = radius / 2f;
            Shape hourHand = new Line2D.Float(0f, 0f, 0f, -hourHandLen);
            double minuteRot = currentMinute * Math.PI / 30d;
            double hourRot = hour * Math.PI / 6d + minuteRot / 12d;
            g2.setStroke(new BasicStroke(clock.getHourHandWidth()));
            g2.setPaint(clock.getHourHandColor().getJavaColor());
            g2.draw(AffineTransform.getRotateInstance(hourRot).createTransformedShape(hourHand));

            // Drawing the minute hand
            float minuteHandLen = 5f * radius / 6f;
            Shape minuteHand = new Line2D.Float(0f, 0f, 0f, -minuteHandLen);
            g2.setStroke(new BasicStroke(clock.getMinuteHandWidth()));
            g2.setPaint(clock.getMinuteHandColor().getJavaColor());
            g2.draw(AffineTransform.getRotateInstance(minuteRot).createTransformedShape(minuteHand));

            // Drawing the center
            Shape center = new Ellipse2D.Float(-clock.getCenterWidth() / 2, -clock.getCenterWidth() / 2, clock.getCenterWidth(), clock.getCenterWidth());
            g2.setStroke(new BasicStroke(1f));
            g2.setPaint(clock.getCenterColor().getJavaColor());
            g2.fill(center);

        } else {
            int startX = 10; // TODO - implement correct

        }
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
