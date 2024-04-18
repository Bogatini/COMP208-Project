package sudoku;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DottedTextField extends JTextField {

    private Color dotColor;
    private List<Point> cage;

    public DottedTextField() {
        super();
        dotColor = Color.BLACK;
        setOpaque(false);
    }

    public DottedTextField(int cols) {
        super(cols);
        dotColor = Color.BLACK;
        setOpaque(false);
    }

    public DottedTextField(String text) {
        super(text);
        dotColor = Color.BLACK;
        setOpaque(false);
    }

    public DottedTextField(String text, int cols) {
        super(text, cols);
        dotColor = Color.BLACK;
        setOpaque(false);
    }

    public DottedTextField(int cols, Color dotColor) {
        super(cols);
        this.dotColor = dotColor;
        setOpaque(false);
    }

    public DottedTextField(String text, Color dotColor) {
        super(text);
        this.dotColor = dotColor;
        setOpaque(false);
    }

    public DottedTextField(String text, int cols, Color dotColor) {
        super(text, cols);
        this.dotColor = dotColor;
        setOpaque(false);
    }

    public void setCage(List<Point> cage) {
        this.cage = cage;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (cage != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(dotColor);
            int dotGap = 4; // Adjust the gap between dots
            int dotSize = 2; // Adjust the size of dots
            int cellWidth = getWidth();
            int cellHeight = getHeight();
            int startX = cellWidth / 2 - dotSize / 2;
            int startY = cellHeight / 2 - dotSize / 2;
            for (int x = startX; x < cellWidth; x += dotGap) {
                for (int y = startY; y < cellHeight; y += dotGap) {
                    g2d.fill(new Rectangle(x, y, dotSize, dotSize));
                }
            }
        }
    }
}
