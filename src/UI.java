import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class UI {

	private JFrame boardGUI = new JFrame();
	private JLabel time;
	private JFrame currentGameWindow;
	final JSpinner heightSpinner = new JSpinner();
	final JSpinner widthSpinner = new JSpinner();

	UI() {
		createMainMenuUI();
	}

	UI(JLabel time, JFrame currentGameWindow) {
		this.time = time;
		this.currentGameWindow = currentGameWindow;
	}

	public void createMainMenuUI() {
		JFrame gui = new JFrame();
		gui.setLocationRelativeTo(null);
		JPanel menuPanel = new JPanel();
		JButton beginnerButton = new JButton("Beginner");
		addNewMenuComponentListener(beginnerButton, "beginner", gui);
		JButton intermediateButton = new JButton("Intermediate");
		addNewMenuComponentListener(intermediateButton, "intermediate", gui);
		JButton advancedButton = new JButton("Advanced");
		addNewMenuComponentListener(advancedButton, "advanced", gui);
		JButton customButton = new JButton("Custom");
		addNewMenuComponentListener(customButton, "custom", gui);
		menuPanel.add(beginnerButton);
		menuPanel.add(intermediateButton);
		menuPanel.add(advancedButton);
		menuPanel.add(customButton);
		menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
		gui.setLayout(new GridBagLayout());
		gui.add(menuPanel);
		gui.setMinimumSize(new Dimension(250, 200));
		gui.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		gui.setTitle("Difficulty");
		gui.pack();
		gui.setVisible(true);
	}

	private void addNewMenuComponentListener(JButton button, String actionName,
			final JFrame frame) {
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				menuClickHandling(e);
			}
		});
		button.setActionCommand(actionName);
	}

	private void menuClickHandling(ActionEvent e) {
		switch (e.getActionCommand()) {
		case "beginner":
			createBoardUI(initMinesweeper(9, 9, 10));
			break;
		case "intermediate":
			createBoardUI(initMinesweeper(16, 16, 40));
			break;
		case "advanced":
			createBoardUI(initMinesweeper(30, 16, 99));
			break;
		case "custom":
			createCustomBoardMenuUI();
			break;
		}
	}

	private Board initMinesweeper(int row, int column, int bombCount) {
		Board minesweeper = new Board(row, column, bombCount, boardGUI);
		minesweeper.setBombLocations();
		minesweeper.surroundBombLocations();
		System.out.println(minesweeper.printArray());
		return minesweeper;
	}

	private void createBoardUI(Board minesweeper) {
		boardGUI.setLayout(new GridBagLayout());
		boardGUI.setLocationRelativeTo(null);
		boardGUI.setTitle("Minesweeper");
		boardGUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagConstraints c = new GridBagConstraints();
		JPanel mineLabelPanel = new JPanel();
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		boardGUI.add(mineLabelPanel, c);
		JLabel mineLabel = minesweeper.getMineLabel();
		c.gridx = 0;
		c.gridy = 0;
		mineLabelPanel.add(mineLabel, c);
		JPanel timeLabelPanel = new JPanel();
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.EAST;
		boardGUI.add(timeLabelPanel, c);
		JLabel timeLabel = minesweeper.getTimerLabel();
		c.gridx = 0;
		c.gridy = 0;
		timeLabel.setText("0");
		timeLabelPanel.add(timeLabel, c);
		JPanel cellPanel = new JPanel();
		c.gridx = 0;
		c.gridy = 1;
		cellPanel.setBorder(null);
		cellPanel.add(minesweeper.generateImageButtons());
		boardGUI.add(cellPanel, c);
		boardGUI.pack();
		Dimension size = boardGUI.getBounds().getSize();
		boardGUI.setMinimumSize(size);
		boardGUI.setVisible(true);
	}

	public void winLossUI(String state) {
		JFrame winLossFrame = new JFrame();
		winLossFrame.setLocationRelativeTo(null);
		winLossFrame.setLayout(new GridBagLayout());
		winLossFrame.setMinimumSize(new Dimension(240, 65));
		GridBagConstraints c = new GridBagConstraints();
		winLossFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel timePanel = new JPanel();
		JPanel winLossPanel = new JPanel();
		JButton play = new JButton("Play Again");
		addWinLossListener(play, "play", winLossFrame);
		JButton exit = new JButton("Exit");
		addWinLossListener(exit, "exit", winLossFrame);
		// so the time doesn't disappear from the main UI
		JLabel timePlayed = new JLabel("Time: " + time.getText() + " seconds");
		if (state.equals("win")) {
			winLossFrame.setTitle("You Win!");
		} else if (state.equals("loss")) {
			winLossFrame.setTitle("You Lose!");
		}
		timePanel.add(timePlayed);
		winLossPanel.add(exit);
		winLossPanel.add(play);
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		winLossFrame.add(timePanel, c);
		c.gridx = 0;
		c.gridy = 1;
		winLossFrame.add(winLossPanel, c);
		winLossFrame.pack();
		winLossFrame.setVisible(true);
	}

	private void addWinLossListener(JButton button, String actionName,
			final JFrame frame) {
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				winLossListenerClick(e);
			}
		});
		button.setActionCommand(actionName);
	}

	private void winLossListenerClick(ActionEvent e) {
		if (e.getActionCommand().equals("play")) {
			currentGameWindow.dispose();
			createMainMenuUI();
		} else if (e.getActionCommand().equals("exit")) {
			currentGameWindow.dispose();
		}
	}

	private void createCustomBoardMenuUI() {
		final JFrame customMenuFrame = new JFrame();
		customMenuFrame.setLocationRelativeTo(null);
		customMenuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		customMenuFrame.setMinimumSize(new Dimension(240, 150));
		customMenuFrame.setLayout(new GridBagLayout());
		customMenuFrame.setTitle("Custom");
		GridBagConstraints c = new GridBagConstraints();
		JLabel heightLabel = new JLabel("Height (9 - 24): ");
		c.gridx = 0;
		c.gridy = 0;
		customMenuFrame.add(heightLabel, c);
		JLabel widthLabel = new JLabel("Width (9 - 30): ");
		c.gridx = 0;
		c.gridy = 1;
		customMenuFrame.add(widthLabel, c);
		JLabel bombsLabel = new JLabel("Bombs (10 - 667): ");
		c.gridx = 0;
		c.gridy = 2;
		customMenuFrame.add(bombsLabel, c);
		SpinnerNumberModel height = new SpinnerNumberModel(9, 9, 24, 1);
		heightSpinner.setModel(height);
		c.gridx = 1;
		c.gridy = 0;
		customMenuFrame.add(heightSpinner, c);
		SpinnerNumberModel width = new SpinnerNumberModel(9, 9, 30, 1);
		widthSpinner.setModel(width);
		c.gridx = 1;
		c.gridy = 1;
		customMenuFrame.add(widthSpinner, c);
		SpinnerNumberModel bombs = new SpinnerNumberModel(10, 10,
				getMaxBombsAllowed((int) heightSpinner.getValue(),
						(int) widthSpinner.getValue()), 1);
		final JSpinner bombSpinner = new JSpinner(bombs);
		bombSpinner.setToolTipText("Max bombs = (height - 1) * (width - 1)");
		c.gridx = 1;
		c.gridy = 2;
		customMenuFrame.add(bombSpinner, c);
		JButton playButton = new JButton("Play");
		playButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				customMenuFrame.dispose();
				int height = (int) heightSpinner.getValue();
				int width = (int) widthSpinner.getValue();
				int bombCount = (int) bombSpinner.getValue();
				createBoardUI(initMinesweeper(height, width, bombCount));
			}
		});
		c.gridx = 1;
		c.gridy = 3;

		spinnerListener(height, bombs);
		spinnerListener(width, bombs);
		customMenuFrame.add(playButton, c);
		customMenuFrame.pack();
		customMenuFrame.setVisible(true);
	}

	private void spinnerListener(SpinnerNumberModel spinner,
			final SpinnerNumberModel bombs) {
		spinner.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				int heightValue = (int) heightSpinner.getValue();
				int widthValue = (int) widthSpinner.getValue();
				int bombsValue = (int) bombs.getValue();
				bombs.setMaximum(getMaxBombsAllowed(heightValue, widthValue));
				if (bombsValue > getMaxBombsAllowed(heightValue,widthValue)) {
					bombs.setValue(getMaxBombsAllowed(heightValue,widthValue));
				}
			}
		});
	}

	private int getMaxBombsAllowed(int a, int b) {
		return ((a - 1) * (b - 1));
	}
}
