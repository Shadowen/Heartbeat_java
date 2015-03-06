package graphicalInterface;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.JPanel;

public class HeartbeatPanel extends JPanel {
	private static final int NUM_PREVIOUS_TIMES = 100;
	/** Scaling factor to convert milliseconds to pixels on the graph **/
	private double timeScaling = 0.20;
	/** Y-coordinate of the bottom of the graph **/
	private static final double GRAPH_BOTTOM = 300;

	private Deque<Long> previousTimes;

	private long lastHeartbeat;
	/** Horizontal spacing between points on the graph **/
	private int graphHorizontalSpacing = 500 / NUM_PREVIOUS_TIMES;

	public HeartbeatPanel() {
		previousTimes = new ArrayDeque<Long>();
		lastHeartbeat = System.currentTimeMillis();
		for (int i = 0; i < NUM_PREVIOUS_TIMES; i++) {
			previousTimes.add(0L);
		}
	}

	public void heartbeatReceived() {
		long currentTime = System.currentTimeMillis();
		previousTimes.add(currentTime - lastHeartbeat);
		lastHeartbeat = currentTime;

		// Maintain maximum size of buffer
		if (previousTimes.size() > NUM_PREVIOUS_TIMES) {
			previousTimes.poll();
		}

		// Increase the scale if necessary
		while (previousTimes.peek() * timeScaling > GRAPH_BOTTOM) {
			timeScaling /= 2;
		}

		// Request repaint
		repaint();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		// Draw axis
		g.setColor(Color.BLUE);
		for (int i = 1; i < GRAPH_BOTTOM / timeScaling; i *= 10) {
			// 100
			g.drawLine(previousTimes.size() * graphHorizontalSpacing,
					(int) (GRAPH_BOTTOM - i * timeScaling),
					previousTimes.size() * graphHorizontalSpacing + 10,
					(int) (GRAPH_BOTTOM - i * timeScaling));
			g.drawString(String.valueOf(i), previousTimes.size()
					* graphHorizontalSpacing + 20, (int) (GRAPH_BOTTOM - (i
					* timeScaling - 5)));
		}

		// Draw points
		g.setColor(Color.RED);
		Iterator<Long> it = previousTimes.iterator();
		int i = 0;
		long lastTime = it.next();
		while (it.hasNext()) {
			long thisTime = it.next();
			g.drawLine(i * graphHorizontalSpacing,
					(int) (GRAPH_BOTTOM - lastTime * timeScaling), (i + 1)
							* graphHorizontalSpacing,
					(int) (GRAPH_BOTTOM - thisTime * timeScaling));
			lastTime = thisTime;
			i++;
		}
	}
}
