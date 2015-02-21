package graphicalInterface;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayDeque;
import java.util.Queue;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

public class NavigationPanel extends JPanel {
	private int lineSpacing = 30;
	private int lineWeight = 2;

	private int pathPointSize = 5;

	private int robotLocationX = 0;
	private int robotLocationY = 0;
	private int robotLocationSize = 3;

	private Queue<Point> path = new ArrayDeque<>();

	public NavigationPanel() {
		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.BLACK);
		// Horizontal lines
		for (int y = 0; y <= 8; y++) {
			g.fillRect(0, y * lineSpacing, 8 * lineSpacing + lineWeight,
					lineWeight);
		}
		// Vertical lines
		for (int x = 0; x <= 8; x++) {
			g.fillRect(x * lineSpacing, 0, lineWeight, 8 * lineSpacing
					+ lineWeight);
		}
		// Center line
		g.setColor(Color.RED);
		g.fillRect(4 * lineSpacing, 0, lineWeight, 8 * lineSpacing + lineWeight);

		// Path
		g.setColor(Color.BLUE);
		for (Point p : path) {
			g.fillOval(p.x, p.y, pathPointSize, pathPointSize);
		}

		// Current location
		g.setColor(Color.GREEN);
		g.fillOval(robotLocationX, robotLocationY, robotLocationSize,
				robotLocationSize);
	}
}
