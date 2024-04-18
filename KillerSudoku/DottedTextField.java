import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DottedTextField extends JTextField {

    private List<Point> cage;
    private JLabel sumLabel;

    public DottedTextField() {
        super();
        setOpaque(true);
        sumLabel = new JLabel();
        sumLabel.setFont(new Font("Serif", Font.PLAIN, 15)); // Adjust font size and style as needed
        sumLabel.setForeground(Color.BLACK); // Adjust color as needed
        setLayout(new BorderLayout());
        add(sumLabel, BorderLayout.NORTH);
    }

    public DottedTextField(int cols) {
        super(cols);
        setOpaque(false);
    }

    public DottedTextField(String text) {
        super(text);
        setOpaque(false);
    }

    public DottedTextField(String text, int cols) {
        super(text, cols);
        setOpaque(false);
    }

    public DottedTextField(int cols, Color dotColor) {
        super(cols);
        setOpaque(false);
    }

    public DottedTextField(String text, Color dotColor) {
        super(text);
        setOpaque(false);
    }

    public DottedTextField(String text, int cols, Color dotColor) {
        super(text, cols);
        setOpaque(false);
    }

    public void setCage(List<Point> cage) {

        this.cage = cage;

    }

    public JLabel getSumLabel() {
        return sumLabel;
    }
}

