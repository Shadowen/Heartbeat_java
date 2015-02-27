package graphicalInterface;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class LocalizationPanel extends JPanel {
	private int calLeft = 0;
	private int calRight = 0;
	private int calInt1 = 0;
	private int calInt2 = 0;

	public void updateSensorReadings(short left, short right, short int1,
			short int2) {
		calLeft = left;
		calRight = right;
		calInt1 = int1;
		calInt2 = int2;
		repaint();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.setColor(Color.BLACK);
		g.drawString("Left: " + calLeft, 10, 15);
		g.drawString("Right: " + calRight, 10, 30);
		g.drawString("Int1: " + calInt1, 10, 45);
		g.drawString("Int2: " + calInt2, 10, 60);
	}
}
