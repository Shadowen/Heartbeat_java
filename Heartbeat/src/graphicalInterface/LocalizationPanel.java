package graphicalInterface;

import java.awt.Color;
import java.awt.Graphics;
import java.nio.ByteBuffer;

import javax.swing.JPanel;

public class LocalizationPanel extends JPanel {
	private int calLeft = 0;
	private int calRight = 0;
	private int calInt1 = 0;
	private int calInt2 = 0;

	public void updateSensorReadings(ByteBuffer buffer) {
		calLeft = buffer.get();
		calRight = buffer.get();
		calInt1 = buffer.get();
		calInt2 = buffer.get();
		repaint();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.setColor(Color.BLACK);
		g.drawString("Left Int: " + calInt2, 10, 60);
		g.fillRect(100, 50, calInt2, 10);
		g.drawString("Left: " + calLeft, 10, 15);
		g.fillRect(100, 5, calLeft, 10);
		g.drawString("Right: " + calRight, 10, 30);
		g.fillRect(100, 20, calRight, 10);
		g.drawString("Right Int: " + calInt1, 10, 45);
		g.fillRect(100, 35, calInt1, 10);
	}
}
