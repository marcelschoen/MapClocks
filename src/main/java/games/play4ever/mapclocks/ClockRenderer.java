package games.play4ever.mapclocks;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.time.LocalDateTime;

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

    private BufferedImage renderClock() {
        BufferedImage image = deepCopy(clock.getBackground());
        Graphics g = image.getGraphics();
        if(clock.getClockType() == Clock.TYPES.analog) {
            LocalDateTime now = LocalDateTime.now();
            int currentHour = now.getHour() > 11 ? now.getHour() / 2 : now.getHour();
            int currentMinute = now.getMinute();
            MapClocks.logInfo("> Hour: " + currentHour + " / minute: " + currentMinute);

            int hourAngle = currentHour * (360 / 12); // TODO - add minutes
            //drawAnalogHand(g, clock.getHourHand(), hourAngle);

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
        int drawLocationX = 64;
        int drawLocationY = 64;
        // Rotation information

        // create the transform, note that the transformations happen
        // in reversed order (so check them backwards)
        AffineTransform at = new AffineTransform();

        // 4. translate it to the center of the component
        at.translate(image.getWidth() / 2, image.getHeight() / 2);

        // 3. do the actual rotation
        at.rotate(Math.toRadians(angle));

        // 1. translate the object to rotate around the center
        at.translate(drawLocationX + (-image.getWidth() / 2), drawLocationY - image.getHeight());

        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(image, at, null);
    }

    // Rotates clockwise 90 degrees. Uses rotation on center and then translating it to origin
    private AffineTransform rotateClockwise(BufferedImage source, int angle) {
        AffineTransform transform = new AffineTransform();
        transform.rotate(angle, source.getWidth()/2, source.getHeight()/2);
        double offset = (source.getWidth()-source.getHeight())/2;
        transform.translate(offset,offset);
        return transform;
    }

    public static BufferedImage rotateImage(BufferedImage imageToRotate, int angle) {
        int widthOfImage = imageToRotate.getWidth();
        int heightOfImage = imageToRotate.getHeight();
        int typeOfImage = imageToRotate.getType();

        BufferedImage newImageFromBuffer = new BufferedImage(widthOfImage, heightOfImage, typeOfImage);

        Graphics2D graphics2D = newImageFromBuffer.createGraphics();

        graphics2D.rotate(Math.toRadians(angle), widthOfImage / 2, heightOfImage / 2);
        graphics2D.drawImage(imageToRotate, null, 0, 0);

        return newImageFromBuffer;
    }

    private static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(bi.getRaster().createCompatibleWritableRaster());
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }
}
