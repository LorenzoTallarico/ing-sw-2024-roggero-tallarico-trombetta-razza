package it.polimi.ingsw;

import javax.swing.*;
import java.awt.*;

public class gui_temp {
    public static void main(String[] args) {
        JFrame f = new JFrame("Codex Naturalis");
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.setExtendedState(JFrame.MAXIMIZED_BOTH);
        JButton b1 = new JButton("NORTH");
        JButton b2 = new JButton("SOUTH");
        JButton b3 = new JButton("EAST");
        JTextArea b6 = new JTextArea(50, 50);
        b6.setEditable(true);
        JScrollPane b5 = new JScrollPane(b6, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        JPanel j5 = new JPanel();
        JPanel j1 = new JPanel();
        JPanel j2 = new JPanel();
        JPanel j3 = new JPanel();
        JPanel j4 = new ImagePanel();
        j1.add(b1);
        j2.add(b2);
        j3.add(b3);
        j5.add(b5);
        f.add(j1, BorderLayout.NORTH);
        f.add(j2, BorderLayout.SOUTH);
        f.add(j3, BorderLayout.EAST);
        f.add(j4, BorderLayout.WEST);
        f.add(j5, BorderLayout.CENTER);
        j4.setPreferredSize(new Dimension(400, 200));
        j3.setPreferredSize(new Dimension(400, 200));
        //f.setUndecorated(true);
        f.setVisible(true);
        f.pack();
    }
}
class ImagePanel extends JPanel {
    private final Image image1;
    private final Image image2;
    private final Image image3;
    int dimensionX,dimensionY,width,height;

    public ImagePanel() {
        image1 = new ImageIcon("src/main/resources/img/misc/img.png").getImage();
        image2 = new ImageIcon("src/main/resources/img/misc/red.png").getImage();
        image3= new ImageIcon("src/main/resources/img/misc/yellow.png").getImage();
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image1 != null) {
            g.drawImage(image1, 0, 0, this);
        }
        if (image2 != null) {
            dimensionX = 100;
            dimensionY = 100;
            width = 50;
            height = 50;
            g.drawImage(image2, dimensionX, dimensionY,width,height,this);
        }
        if (image3 != null) {
            dimensionX = 100;
            dimensionY = 500;
            width = 50;
            height = 50;
            g.drawImage(image3, dimensionX, dimensionY,width,height,this);
        }

    }
}
