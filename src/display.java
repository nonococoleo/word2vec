import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.io.*;
import javax.swing.JFrame;
import javax.swing.JPanel;

import Jama.Matrix;

public class display extends JPanel {
    public String[] wordlist;//需要显示的单词表
    public Matrix C;		 //PCA降维后的矩阵

    Polygon po = new Polygon();//直角坐标系
    Font fn = new Font("Bradley Hand", Font.BOLD, 22);
    Font fn2 = new Font("Bradley Hand", Font.BOLD, 20);
    int x = 100;
    int y = 100;
    int[] pox = {590, 600, 600};
    int[] poy = {40, 20, 30};
    int[] poxx = {610, 600, 600};
    int[] poyy = {40, 20, 30};

    int[] poxB = {1110, 1120, 1130};
    int[] poyB = {440, 450, 450};
    int[] poxBB = {1110, 1120, 1130};
    int[] poyBB = {460, 450, 450};

    public display(String[] wordlist, Matrix C) {
        this.wordlist = wordlist;
        this.C = C;
    }

    public display() {
        setSize(1200, 900);
    }

    public void paint(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.black);
        g2d.fillRect(600, 25, 2, 850);
        g2d.fillRect(25, 450, 1100, 2);
        g2d.setFont(fn2);
        g2d.setColor(Color.white);
        g2d.setFont(fn);
        g2d.setColor(Color.black);
        g2d.drawString("0", 580, 475);
        g2d.drawString("X", 1110, 480);
        g2d.drawString("Y", 580, 40);
        g2d.fillPolygon(pox, poy, 3);
        g2d.fillPolygon(poxx, poyy, 3);
        g2d.fillPolygon(poxB, poyB, 3);
        g2d.fillPolygon(poxBB, poyBB, 3);

        //在坐标系上画单词
        for (int i = 0; i < this.C.getRowDimension(); i++) {
            if (i == 0)
                g2d.setColor(Color.red);
            else
                g2d.setColor(Color.black);
            int strWidth = g.getFontMetrics().stringWidth(this.wordlist[i]);
            g.drawString(this.wordlist[i], (int) (this.C.get(i, 0) * 1000) + 625 + strWidth / 2, (int) (-this.C.get(i, 1) * 1000) + 475);
        }

        g2d.dispose();
    }
}
