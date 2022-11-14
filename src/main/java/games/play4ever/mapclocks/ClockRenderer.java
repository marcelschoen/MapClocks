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

public class ClockRenderer extends MapRenderer {

    private Clock clock = null;

    public ClockRenderer(Clock clock) {
        this.clock = clock;
    }

    @Override
    public void render(MapView view, MapCanvas canvas, Player player) {
        view.setTrackingPosition(false);
        canvas.drawImage(10,10, renderClock());
    }

    private BufferedImage renderClock() {
        MapClocks.logInfo("> Render clock: " + clock.getClockType()
                + " / background: " + clock.getBackground().getWidth() + "," + clock.getBackground().getHeight());
        BufferedImage image = deepCopy(clock.getBackground());
        Graphics g = image.getGraphics();
        if(clock.getClockType() == Clock.TYPES.analog) {
            drawAnalogHand(g, clock.getHourHand(), 37);
            drawAnalogHand(g, clock.getMinuteHand(), 136);
        } else {
            int startX = 10; // TODO - implement correct

        }
        g.dispose();

        return image;
    }

    private void drawAnalogHand(Graphics g, BufferedImage image, int angle) {
        // The required drawing location
        // TODO - Implement optional configured offset
        int drawLocationX = 64;
        int drawLocationY = 64;
        // Rotation information
        double rotationRequired = Math.toRadians (37);
        AffineTransform tx = AffineTransform.getRotateInstance(rotationRequired, 0, image.getHeight());
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
