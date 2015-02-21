package graphicalInterface;

import java.awt.Color;
import java.awt.Graphics;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import javax.swing.JPanel;

public class BoardStatePanel extends JPanel {
	private byte[][] boardState;

	private int rowSpacing = 20;
	private int columnSpacing = 20;
	private int pieceRadius = 20;

	private int myPieces;
	private int thierPieces;
	private int myConnects;
	private int thierConnects;
	private int myScore;
	private int thierScore;

	private Color myColour = Color.BLACK;
	private Color thierColour = Color.WHITE;

	public BoardStatePanel() {
		boardState = new byte[5][7];
	}

	/**
	 * Set the colour of the player. Only affects graphics.
	 * 
	 * @param isBlack
	 *            Am I the black player, or the white player?
	 */
	public void setColour(boolean isBlack) {
		if (isBlack) {
			myColour = Color.BLACK;
			thierColour = Color.WHITE;
		} else {
			myColour = Color.WHITE;
			thierColour = Color.BLACK;
		}
		repaint();
	}

	public void updateBoardState(ByteBuffer data) {
		for (int row = 0; row < 5; row++) {
			for (int col = 0; col < 7; col++) {
				boardState[row][col] = data.get();
			}
		}
		calculateScore();
		repaint();
	}

	/**
	 * Calculate the score of the current game state.
	 */
	private void calculateScore() {
		// Count pieces
		myPieces = 0;
		thierPieces = 0;
		for (int y = 0; y < 5; y++) {
			for (int x = 0; x < 7; x++) {
				if (boardState[y][x] == 1) {
					myPieces++;
				} else if (boardState[y][x] == 2) {
					thierPieces++;
				}
			}
		}

		// Count connects
		myConnects = 0;
		thierConnects = 0;
		// Horizontal
		for (int y = 0; y < 5; y++) {
			byte previousPiece = 0;
			int continuousCount = 0;
			for (int x = 0; x < 7; x++) {
				if (previousPiece == boardState[y][x]) {
					continuousCount++;
				} else {
					previousPiece = boardState[y][x];
					continuousCount = 1;
				}

				if (continuousCount == 4) {
					if (previousPiece == 1) {
						myConnects++;
					} else if (previousPiece == 2) {
						thierConnects++;
					}
					break;
				}
			}
		}
		// Vertical
		for (int x = 0; x < 7; x++) {
			byte previousPiece = 0;
			int continuousCount = 0;
			for (int y = 0; y < 5; y++) {
				if (previousPiece == boardState[y][x]) {
					continuousCount++;
				} else {
					previousPiece = boardState[y][x];
					continuousCount = 1;
				}

				if (continuousCount == 4) {
					if (previousPiece == 1) {
						myConnects++;
					} else if (previousPiece == 2) {
						thierConnects++;
					}
					break;
				}
			}
		}
		// North-east
		for (int t = -1; t < 3; t++) {
			byte previousPiece = 0;
			int continuousCount = 0;
			for (int s = 0; s < 5; s++) {
				int x = t + s;
				int y = s;
				if (x < 0) {
					continue;
				}

				if (previousPiece == boardState[y][x]) {
					continuousCount++;
				} else {
					previousPiece = boardState[y][x];
					continuousCount = 1;
				}

				if (continuousCount == 4) {
					if (previousPiece == 1) {
						myConnects++;
					} else if (previousPiece == 2) {
						thierConnects++;
					}
					break;
				}
			}
		}
		// South-east
		for (int t = -1; t < 3; t++) {
			byte previousPiece = 0;
			int continuousCount = 0;
			for (int s = 0; s < 5; s++) {
				int x = t + s;
				int y = 4 - s;
				if (x < 0) {
					continue;
				}

				if (previousPiece == boardState[y][x]) {
					continuousCount++;
				} else {
					previousPiece = boardState[y][x];
					continuousCount = 1;
				}

				if (continuousCount == 4) {
					if (previousPiece == 1) {
						myConnects++;
					} else if (previousPiece == 2) {
						thierConnects++;
					}
					break;
				}
			}
		}

		myScore = myPieces + 4 * myConnects - 2 * thierConnects;
		thierScore = thierPieces + 4 * thierConnects - 2 * myConnects;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, getWidth(), getHeight());
		for (int x = 0; x < 7; x++) {
			for (int y = 0; y < 5; y++) {
				byte s = boardState[y][x];
				int dx = x * columnSpacing;
				int dy = y * rowSpacing;
				if (s == 1) {
					g.setColor(myColour);
					g.fillOval(dx, dy, pieceRadius, pieceRadius);
				} else if (s == 2) {
					g.setColor(thierColour);
					g.fillOval(dx, dy, pieceRadius, pieceRadius);
				} else {
					g.setColor(Color.GRAY);
					g.drawOval(dx, dy, pieceRadius, pieceRadius);
				}
			}
		}

		g.setColor(Color.BLACK);
		g.drawString("Score", 0, rowSpacing * 7);
		g.setColor(myColour);
		g.drawString(myScore + " = " + myPieces + " + 4*" + myConnects
				+ " - 2*" + thierConnects, 0, rowSpacing * 7 + 30);
		g.setColor(thierColour);
		g.drawString(thierScore + " = " + thierPieces + " + 4*" + thierConnects
				+ " - 2*" + myConnects, 0, rowSpacing * 7 + 15);
	}
}
