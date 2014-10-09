import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Board {

	private int column;
	private int row;
	private int bombCount;
	private int flagCount;
	private boolean timerStarted;
	private JLabel mineLabel;
	private JLabel timerLabel;
	private Timer timer;
	private Square[][] cells;
	private static final int BOMB = 9;
	private static final int EMPTY = 0;
	private JFrame boardUI;

	Board(int column, int row, int bombCount, JFrame boardUI) {
		this.row = row;
		this.column = column;
		this.bombCount = bombCount;
		this.boardUI = boardUI;
		mineLabel = new JLabel();
		mineLabel.setText("" + bombCount);
		timerLabel = new JLabel();
		flagCount = bombCount;
		timerStarted = false;
		cells = new Square[row][column];
		for (int i = 0; i < cells.length; i++) {
			for (int j = 0; j < cells[i].length; j++) {
				cells[i][j] = new Square();
			}
		}
	}

	public JLabel getTimerLabel() {
		return this.timerLabel;
	}

	public JLabel getMineLabel() {
		return this.mineLabel;
	}

	public void setBombLocations() {
		int bombsToBeSet = bombCount;
		while (bombsToBeSet > 0) {
			int randomRow = (int) (Math.random() * cells.length);
			int randomColumn = (int) (Math.random() * cells[0].length);
			if (cells[randomRow][randomColumn].getNumber() != BOMB) {
				cells[randomRow][randomColumn].setNumber(BOMB);
				bombsToBeSet--;
			}
		}
	}

	public void surroundBombLocations() {

		for (int i = 0; i < cells.length; i++) {
			for (int j = 0; j < cells[i].length; j++) {
				if (cells[i][j].getNumber() == BOMB) {
					bombBoundsChecking(i, j);
				}
			}
		}
	}

	private void bombBoundsChecking(int i, int j) {
		// up one over left
		if (i != 0 && j != 0) {
			bombCheck(i - 1, j - 1);
		}
		// up one
		if (i != 0) {
			bombCheck(i - 1, j);
		}
		// up one over right
		if (i != 0 && j != column - 1) {
			bombCheck(i - 1, j + 1);
		}
		// over left
		if (j != 0) {
			bombCheck(i, j - 1);
		}
		// over right
		if (j != column - 1) {
			bombCheck(i, j + 1);
		}
		// down one over left
		if (i != row - 1 && j != 0) {
			bombCheck(i + 1, j - 1);
		}
		// down one
		if (i != row - 1) {
			bombCheck(i + 1, j);
		}
		// down one over right
		if (i != row - 1 && j != column - 1) {
			bombCheck(i + 1, j + 1);
		}
	}

	// ensures tiles surrounding bombs are not bombs (so bombs don't get
	// incremented)
	private void bombCheck(int i, int j) {
		if (cells[i][j].getNumber() != BOMB) {
			cells[i][j].setNumber(cells[i][j].getNumber() + 1);
		}
	}

	public JPanel generateImageButtons() {
		JPanel gridLayout = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		for (int i = 0; i < cells.length; i++) {
			for (int j = 0; j < cells[i].length; j++) {
				cells[i][j].setButton(new JButton());
				cells[i][j].getButton().addMouseListener(new MouseAdapter() {
					public void mouseReleased(MouseEvent e) {
						mouseClickHandling(e);
					}
				});
				cells[i][j].getButton().setActionCommand(i + "-" + j);
				ImageIcon ic = new ImageIcon(cells[i][j].getImagePath());
				cells[i][j].getButton().setIcon(ic);
				c.gridx = j;
				c.gridy = i;
				gridLayout.add(cells[i][j].getButton(), c);
			}
		}
		return gridLayout;
	}

	public StringBuilder printArray() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < cells.length; i++) {
			for (int j = 0; j < cells[i].length; j++) {
				sb.append(cells[i][j].getNumber() + " ");
			}
			sb.append("\n");
		}
		return sb;
	}

	private void mouseClickHandling(MouseEvent e) {
		JButton source = (JButton) e.getSource();
		if (!timerStarted) {
			startTimer();
		}
		int row = Integer.parseInt(source.getActionCommand().substring(0,
				source.getActionCommand().indexOf("-")));
		int column = Integer.parseInt(source.getActionCommand().substring(
				source.getActionCommand().indexOf("-") + 1,
				source.getActionCommand().length()));
		if (cells[row][column].getButton().isEnabled()
				&& SwingUtilities.isLeftMouseButton(e)
				&& !cells[row][column].getImagePath().equals(imageURL("/flag.png"))) {
			if (cells[row][column].getNumber() == EMPTY) {
				revealBlanks(row, column);
				// handles non-empty reveals for empty spaces that only span one
				// cell
				setNextCell(row, column);
			}
			cells[row][column].revealImage();
			disable(row, column);
			if (getRevealsUntilWin() == bombCount) {
				gameWon();
			}
			if (cells[row][column].getNumber() == BOMB) {
				gameOver();
			}
		} else if (SwingUtilities.isRightMouseButton(e)
				&& cells[row][column].getButton().isEnabled()) {
			// flag handling
			if (cells[row][column].getImagePath().equals(
					imageURL("/unclicked.png"))) {
				cells[row][column].setImage(imageURL("/flag.png"));
				flagCount--;
			} else if (cells[row][column].getImagePath().equals(
					imageURL("/flag.png"))) {
				cells[row][column].setImage(imageURL("/questionMark.png"));
				flagCount++;
			} else if (cells[row][column].getImagePath().equals(
					imageURL("/questionMark.png"))) {
				cells[row][column].setImage(imageURL("/unclicked.png"));
			}
			mineLabel.setText("" + flagCount);
		}

	}

	// reveals blank spots when necessary
	private void revealBlanks(int i, int j) {
		if ((i != 0 && cells[i - 1][j].getNumber() != 0)
				&& (j != 0 && cells[i][j - 1].getNumber() != 0)
				&& (j != column - 1 && cells[i][j + 1].getNumber() != 0)
				&& (i != row - 1 && cells[i + 1][j].getNumber() != 0)) {
			return;
		} else {
			if (i != 0 && cells[i - 1][j].getNumber() == EMPTY) {
				if (!cells[i - 1][j].getImagePath().equals(
						imageURL("/flag.png"))) {
					setNextCell(i - 1, j);
					disable(i - 1, j);
				}
				revealBlanks(i - 1, j);
			}
			if (j != 0 && cells[i][j - 1].getNumber() == EMPTY) {
				if (!cells[i][j - 1].getImagePath().equals(
						imageURL("/flag.png"))) {
					setNextCell(i, j - 1);
					disable(i, j - 1);
				}
				revealBlanks(i, j - 1);
			}
			if (j != column - 1 && cells[i][j + 1].getNumber() == EMPTY) {
				if (!cells[i][j + 1].getImagePath().equals(
						imageURL("/flag.png"))) {
					setNextCell(i, j + 1);
					disable(i, j + 1);
				}
				revealBlanks(i, j + 1);

			}
			if (i != row - 1 && cells[i + 1][j].getNumber() == EMPTY) {
				if (!cells[i + 1][j].getImagePath().equals(
						imageURL("/flag.png"))) {
					setNextCell(i + 1, j);
					disable(i + 1, j);
				}
				revealBlanks(i + 1, j);
			}
		}
	}

	// sets attributes to cells handled in the revealBlanks function
	private void setNextCell(int i, int j) {
		// ensures no infinite recursion
		cells[i][j].setNumber(-1);
		cells[i][j].setImage(imageURL("/empty.png"));
		// reveals the closest block that isn't empty at the end of the revealed
		// blank spots
		if (i != 0
				&& j != 0
				&& cells[i - 1][j - 1].getNumber() != EMPTY
				&& !cells[i - 1][j - 1].getImagePath().equals(
						imageURL("/flag.png"))) {
			cells[i - 1][j - 1].revealImage();
			disable(i - 1, j - 1);
		}
		if (i != 0
				&& cells[i - 1][j].getNumber() != EMPTY
				&& !cells[i - 1][j].getImagePath()
						.equals(imageURL("/flag.png"))) {
			cells[i - 1][j].revealImage();
			disable(i - 1, j);
		}
		if (i != 0
				&& j != column - 1
				&& cells[i - 1][j + 1].getNumber() != EMPTY
				&& !cells[i - 1][j + 1].getImagePath().equals(
						imageURL("/flag.png"))) {
			cells[i - 1][j + 1].revealImage();
			disable(i - 1, j + 1);
		}
		if (j != 0 && cells[i][j - 1].getNumber() != EMPTY
				&& !cells[i][j - 1].getImagePath().equals(("/flag.png"))) {
			cells[i][j - 1].revealImage();
			disable(i, j - 1);
		}
		if (j != column - 1
				&& cells[i][j + 1].getNumber() != EMPTY
				&& !cells[i][j + 1].getImagePath()
						.equals(imageURL("/flag.png"))) {
			cells[i][j + 1].revealImage();
			disable(i, j + 1);
		}
		if (i != row - 1
				&& j != 0
				&& cells[i + 1][j - 1].getNumber() != EMPTY
				&& !cells[i + 1][j - 1].getImagePath().equals(
						imageURL("/flag.png"))) {
			cells[i + 1][j - 1].revealImage();
			disable(i + 1, j - 1);
		}
		if (i != row - 1
				&& cells[i + 1][j].getNumber() != EMPTY
				&& !cells[i + 1][j].getImagePath()
						.equals(imageURL("/flag.png"))) {
			cells[i + 1][j].revealImage();
			disable(i + 1, j);
		}
		if (i != row - 1
				&& j != column - 1
				&& cells[i + 1][j + 1].getNumber() != EMPTY
				&& !cells[i + 1][j + 1].getImagePath().equals(
						imageURL("/flag.png"))) {
			cells[i + 1][j + 1].revealImage();
			disable(i + 1, j + 1);
		}
	}

	// stops cells from being able to be clicked and processed multiple times
	private void disable(int i, int j) {
		ImageIcon ic = new ImageIcon(cells[i][j].getImagePath());
		cells[i][j].getButton().setDisabledIcon(ic);
		cells[i][j].getButton().setEnabled(false);
	}

	private void startTimer() {
		timerStarted = true;
		timer = new Timer();
		timer.scheduleAtFixedRate(new Task(), 1, 1000);
	}

	class Task extends TimerTask {

		public void run() {
			int i = Integer.parseInt(timerLabel.getText());
			i++;
			timerLabel.setText("" + i);
		}

	}

	// for determining when a player wins
	private int getRevealsUntilWin() {
		int totalCells = column * row;
		for (int i = 0; i < cells.length; i++) {
			for (int j = 0; j < cells[i].length; j++) {
				if (!(cells[i][j].getButton().isEnabled())
						&& cells[i][j].getNumber() != BOMB) {
					totalCells--;
				}
			}
		}
		return totalCells;
	}

	private void gameWon() {
		UI ui = new UI(timerLabel, boardUI);
		ui.winLossUI("win");
		timer.cancel();
		for (int i = 0; i < cells.length; i++) {
			for (int j = 0; j < cells[i].length; j++) {
				if (cells[i][j].getNumber() == BOMB) {
					cells[i][j].setImage(imageURL("/flag.png"));
				}
				disable(i, j);
			}
		}
	}

	private void gameOver() {
		UI ui = new UI(timerLabel, boardUI);
		ui.winLossUI("loss");
		timer.cancel();
		ImageIcon ic;
		for (int i = 0; i < cells.length; i++) {
			for (int j = 0; j < cells[i].length; j++) {
				ic = new ImageIcon(cells[i][j].getImagePath());
				// reveals all bombs
				if (cells[i][j].getNumber() == BOMB) {
					ic = new ImageIcon(imageURL("/bomb.png"));
					cells[i][j].getButton().setIcon(ic);
				}
				cells[i][j].getButton().setDisabledIcon(ic);
				cells[i][j].getButton().setEnabled(false);
			}
		}
	}

	private URL imageURL(String image) {
		return getClass().getResource(image);
	}
}