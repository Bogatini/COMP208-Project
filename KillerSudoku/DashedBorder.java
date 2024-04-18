package sudoku;

import javax.swing.border.AbstractBorder;
import java.awt.*;

public class DashedBorder extends AbstractBorder {
    private final Color color;
    private final float[] dash;
    private final int thickness;

    public DashedBorder(Color color, float[] dash, int thickness) {
        this.color = color;
        this.dash = dash;
        this.thickness = thickness;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        super.paintBorder(c, g, x, y, width, height);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(thickness, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
        g2d.drawRect(x, y, width - 1, height - 1);
    }
}
