import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.awt.*;
import java.awt.event.*;

public class design {

    public design() {
        JFrame frame = new JFrame();
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER,10,5));;
        Dimension buttonSize = new Dimension(200, 50);

        // button

        JButton start = new JButton("Play");
        start.setBounds(300,220,125,40);
        start.setPreferredSize(buttonSize);
        panel.add(start);

        // links the start and menu page
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new menu();
            }
        });

        // icon image
        JLabel imageLabel = new JLabel();
        ImageIcon imageIcon = new ImageIcon("src/gameLogo.png");
        imageLabel.setIcon(imageIcon);
        panel.add(imageLabel, BorderLayout.CENTER);

        panel.setBackground(Color.BLACK);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Home Page");
        frame.setSize(800,500);
        frame.add(panel,BorderLayout.CENTER);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new design();
    }
}
