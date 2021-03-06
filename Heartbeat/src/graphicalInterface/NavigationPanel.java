package graphicalInterface;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Queue;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

public class NavigationPanel extends JPanel {
	private int lineSpacing = 30;
	private int lineWeight = 2;

	private int robotX = 0;
	private int robotY = 0;
	private int robotFacing = 0;
	private int robotLocationSize = 5;

	private int dFromStart[][] = new int[7][7];
	private int heuristicDistance[][] = new int[7][7];
	private Point parents[][] = new Point[7][7];
	private boolean isClosed[][] = new boolean[7][7];

	public NavigationPanel() {
		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		// Background
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, 8 * lineSpacing, 8 * lineSpacing);

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

		// Distance from start
		/*
		 * g.setColor(Color.BLUE); for (int x = 0; x < 7; x++) { for (int y = 0;
		 * y < 7; y++) { g.drawString(String.valueOf(dFromStart[x][y]), (x + 1)
		 * lineSpacing - lineWeight / 2, (7 - y) * lineSpacing + lineWeight /
		 * 2); } }
		 */

		// Parents
		g.setColor(Color.BLUE);
		for (int x = 0; x < 7; x++) {
			for (int y = 0; y < 7; y++) {
				if (parents[x][y] == null) {
					continue;
				}
				int gx = (x + 1) * lineSpacing;
				int gy = (7 - y) * lineSpacing;
				Point p = parents[x][y];
				if (p.x == x - 1) {
					// Left
					g.drawLine(gx, gy, gx - 5, gy);
					g.drawLine(gx - 5, gy, gx - 3, gy + 3);
					g.drawLine(gx - 5, gy, gx - 3, gy - 3);
				} else if (p.x == x + 1) {
					// Right
					g.drawLine(gx, gy, gx + 5, gy);
					g.drawLine(gx + 5, gy, gx + 3, gy + 3);
					g.drawLine(gx + 5, gy, gx + 3, gy - 3);
				} else if (p.y == y - 1) {
					// Down
					g.drawLine(gx, gy, gx, gy - 5);
					g.drawLine(gx, gy + 5, gx + 3, gy + 3);
					g.drawLine(gx, gy + 5, gx - 3, gy + 3);
				} else if (p.y == y + 1) {
					// Up
					g.drawLine(gx, gy, gx, gy + 5);
					g.drawLine(gx, gy - 5, gx + 3, gy - 3);
					g.drawLine(gx, gy - 5, gx - 3, gy - 3);
				}
			}
		}

		// Current location
		int rgx = (robotX + 1) * lineSpacing - lineWeight;
		int rgy = (7 - robotY) * lineSpacing - lineWeight;
		g.setColor(Color.GREEN);
		g.fillOval(rgx, rgy, robotLocationSize, robotLocationSize);
		if (robotFacing == 3) {
			// Left
			g.drawLine(rgx, rgy, rgx - 5, rgy);
			g.drawLine(rgx - 5, rgy, rgx - 3, rgy + 3);
			g.drawLine(rgx - 5, rgy, rgx - 3, rgy - 3);
		} else if (robotFacing == 1) {
			// Right
			g.drawLine(rgx, rgy, rgx + 5, rgy);
			g.drawLine(rgx + 5, rgy, rgx + 3, rgy + 3);
			g.drawLine(rgx + 5, rgy, rgx + 3, rgy - 3);
		} else if (robotFacing == 2) {
			// Down
			g.drawLine(rgx, rgy, rgx, rgy - 5);
			g.drawLine(rgx, rgy + 5, rgx + 3, rgy + 3);
			g.drawLine(rgx, rgy + 5, rgx - 3, rgy + 3);
		} else if (robotFacing == 0) {
			// Up
			g.drawLine(rgx, rgy, rgx, rgy + 5);
			g.drawLine(rgx, rgy - 5, rgx + 3, rgy - 3);
			g.drawLine(rgx, rgy - 5, rgx - 3, rgy - 3);
		}
		g.setColor(Color.BLACK);
		g.drawString("(" + robotX + ", " + robotY + ") " + robotFacing, 10,
				lineSpacing * 9);
	}

	public void updateGrid(int id, ByteBuffer data) {
		switch (id) {
		case 10:
			break;
		case 11:
			for (int x = 0; x < 7; x++) {
				for (int y = 0; y < 7; y++) {
					dFromStart[x][y] = data.get();
					heuristicDistance[x][y] = data.get();
				}
			}
			break;
		case 12:
			for (int x = 0; x < 7; x++) {
				for (int y = 0; y < 7; y++) {
					parents[x][y] = new Point(data.get(), data.get());
					isClosed[x][y] = (data.get() == 1);
				}
			}
			break;
		case 13:
			robotX = data.get();
			robotY = data.get();
			break;
		case 14:
			robotFacing = data.get();
		default:
			System.err.println("Nav Panel got wrong data! (" + id + ")");
		}
		repaint();
	}
}
