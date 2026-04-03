import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.GridLayout;
import java.awt.LinearGradientPaint;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.geom.RoundRectangle2D;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class game extends JFrame {
	private static final String SCREEN_HOME = "home";
	private static final String SCREEN_PLAY = "play";
	private static final String SCREEN_PLAYERS = "players";
	private static final String SCREEN_RULES = "rules";
	private static final String SCREEN_SELECT_MAP = "select-map";
	private static final String SCREEN_BOARD_LEVEL1 = "board-level1";
	private static final String SCREEN_BOARD_LEVEL2 = "board-level2";

	private final CardLayout cardLayout;
	private final JPanel cardPanel;
	private String playerOneName = "Joueur 1";
	private String playerTwoName = "Joueur 2";

	private enum ButtonStyle {
		PRIMARY,
		SECONDARY
	}

	public game() {
		super("Jeu de Dames");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(980, 640);
		setMinimumSize(new Dimension(800, 560));
		setLocationRelativeTo(null);

		cardLayout = new CardLayout();
		cardPanel = new JPanel(cardLayout);
		cardPanel.setOpaque(false);

		cardPanel.add(createHomeScreen(), SCREEN_HOME);
		cardPanel.add(createPlayScreen(), SCREEN_PLAY);
		cardPanel.add(createPlayersScreen(), SCREEN_PLAYERS);
		cardPanel.add(createRulesScreen(), SCREEN_RULES);
		cardPanel.add(createSelectMapScreen(), SCREEN_SELECT_MAP);
		cardPanel.add(createBoard1v1Screen(1), SCREEN_BOARD_LEVEL1);
		cardPanel.add(createBoard1v1Screen(2), SCREEN_BOARD_LEVEL2);

		GradientBackgroundPanel root = new GradientBackgroundPanel();
		root.setLayout(new BorderLayout());
		root.add(cardPanel, BorderLayout.CENTER);
		setContentPane(root);

		showScreen(SCREEN_HOME);
	}

	private JPanel createHomeScreen() {
		RoundedPanel card = createCardBase("Projet Java", "Jeu de Dames", "Choisis ton mode et lance une partie.");

		JButton playButton = createButton("Jouer", ButtonStyle.PRIMARY);
		playButton.addActionListener(e -> showScreen(SCREEN_PLAY));

		JButton rulesButton = createButton("Regle", ButtonStyle.PRIMARY);
		rulesButton.addActionListener(e -> showScreen(SCREEN_RULES));

		card.add(Box.createVerticalStrut(22));
		card.add(playButton);
		card.add(Box.createVerticalStrut(12));
		card.add(rulesButton);

		return wrapCentered(card);
	}

	private JPanel createPlayScreen() {
		RoundedPanel card = createCardBase("Selection", "Mode de Jeu", "Choisis comment tu veux jouer.");

		JButton oneVsOne = createModeButton("1v1");
		oneVsOne.addActionListener((ActionEvent e) -> showScreen(SCREEN_PLAYERS));

		JButton vsAi = createModeButton("Jouer contre IA");
		vsAi.addActionListener((ActionEvent e) -> JOptionPane.showMessageDialog(
				this,
				"Mode IA selectionne.",
				"Info",
				JOptionPane.INFORMATION_MESSAGE));

		JButton back = createButton("Retour accueil", ButtonStyle.SECONDARY);
		back.addActionListener(e -> showScreen(SCREEN_HOME));

		card.add(Box.createVerticalStrut(18));
		card.add(oneVsOne);
		card.add(Box.createVerticalStrut(10));
		card.add(vsAi);
		card.add(Box.createVerticalStrut(14));
		card.add(back);

		return wrapCentered(card);
	}

	private JPanel createPlayersScreen() {
		RoundedPanel card = createCardBase("1v1", "Prenoms des joueurs", "Entre les deux prenoms avant de choisir l'arene.");

		JTextField playerOneField = createNameField("Joueur 1");
		JTextField playerTwoField = createNameField("Joueur 2");

		card.add(Box.createVerticalStrut(18));
		card.add(createLabeledField("Prenom du joueur 1", playerOneField));
		card.add(Box.createVerticalStrut(12));
		card.add(createLabeledField("Prenom du joueur 2", playerTwoField));
		card.add(Box.createVerticalStrut(18));

		JButton continueButton = createButton("Continuer", ButtonStyle.PRIMARY);
		continueButton.addActionListener((ActionEvent e) -> {
			String first = playerOneField.getText().trim();
			String second = playerTwoField.getText().trim();

			playerOneName = first.isEmpty() ? "Joueur 1" : first;
			playerTwoName = second.isEmpty() ? "Joueur 2" : second;
			showScreen(SCREEN_SELECT_MAP);
		});

		JButton back = createButton("Retour modes", ButtonStyle.SECONDARY);
		back.addActionListener(e -> showScreen(SCREEN_PLAY));

		card.add(continueButton);
		card.add(Box.createVerticalStrut(10));
		card.add(back);

		return wrapCentered(card);
	}

	private JPanel createSelectMapScreen() {
		RoundedPanel card = createCardBase("1v1", "Choix de map", playerOneName + " contre " + playerTwoName);

		JButton back = createButton("Retour prenoms", ButtonStyle.SECONDARY);
		back.addActionListener(e -> showScreen(SCREEN_PLAYERS));

		card.add(Box.createVerticalStrut(18));

		JPanel mapsGrid = new JPanel(new GridLayout(1, 2, 18, 18));
		mapsGrid.setOpaque(false);
		mapsGrid.add(createMapPreview(
				1,
				SCREEN_BOARD_LEVEL1));
		mapsGrid.add(createMapPreview(
				2,
				SCREEN_BOARD_LEVEL2));
		card.add(mapsGrid);
		card.add(Box.createVerticalStrut(14));
		card.add(back);

		return wrapCentered(card);
	}

	private JPanel createMapPreview(int level, String targetScreen) {
		RoundedPanel preview = new RoundedPanel(
				new Color(24, 18, 12, 225),
				new Color(247, 184, 68, 180),
				1.4f,
				20);
		preview.setLayout(new BoxLayout(preview, BoxLayout.Y_AXIS));
		preview.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

		CheckersBoardPanel previewBoard = new CheckersBoardPanel(level);
		previewBoard.setPreferredSize(new Dimension(level == 1 ? 220 : 170, level == 1 ? 220 : 170));
		previewBoard.setMinimumSize(new Dimension(170, 170));
		previewBoard.setMaximumSize(new Dimension(240, 240));

		JButton choose = createButton("Choisir", ButtonStyle.PRIMARY);
		choose.setMaximumSize(new Dimension(180, 46));
		choose.addActionListener((ActionEvent e) -> showScreen(targetScreen));

		preview.add(Box.createVerticalStrut(8));
		preview.add(previewBoard);
		preview.add(Box.createVerticalStrut(12));
		preview.add(choose);
		preview.setAlignmentX(Component.CENTER_ALIGNMENT);
		preview.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				showScreen(targetScreen);
			}
		});

		return preview;
	}

	private JPanel createBoard1v1Screen(int level) {
		RoundedPanel card = new RoundedPanel(
				new Color(245, 234, 206, 28),
				new Color(247, 184, 68, 128),
				1f,
				24);
		card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
		card.setPreferredSize(new Dimension(760, 650));
		card.setMinimumSize(new Dimension(620, 560));
		card.setMaximumSize(new Dimension(1400, 1200));
		card.setBorder(BorderFactory.createEmptyBorder(24, 30, 24, 30));

		CheckersBoardPanel boardPanel = new CheckersBoardPanel(level);
		boardPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

		JButton back = createButton("Retour au mode de jeu", ButtonStyle.SECONDARY);
		back.addActionListener(e -> showScreen(SCREEN_PLAY));

		card.add(Box.createVerticalStrut(8));
		card.add(boardPanel);
		card.add(Box.createVerticalStrut(16));
		card.add(back);

		return wrapCentered(card);
	}

	private JPanel createRulesScreen() {
		RoundedPanel card = createCardBase("Infos", "Regles", "Regles de base du jeu de dames.");

		JTextArea rules = new JTextArea(
				"- Les pions se deplacent en diagonale sur les cases sombres.\n"
						+ "- Une capture est obligatoire quand elle est possible.\n"
						+ "- Un pion devient dame en atteignant la derniere ligne.\n"
						+ "- Une dame peut avancer et reculer en diagonale.\n"
						+ "- Tu gagnes si l'adversaire n'a plus de pieces ou de coups.");
		rules.setLineWrap(true);
		rules.setWrapStyleWord(true);
		rules.setEditable(false);
		rules.setFocusable(false);
		rules.setOpaque(false);
		rules.setForeground(new Color(249, 242, 220));
		rules.setFont(new Font("SansSerif", Font.PLAIN, 16));
		rules.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

		RoundedPanel rulesWrap = new RoundedPanel(
				new Color(24, 18, 12, 230),
				new Color(247, 184, 68, 210),
				1.6f,
				16);
		rulesWrap.setLayout(new BorderLayout());
		rulesWrap.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
		rulesWrap.setPreferredSize(new Dimension(560, 230));
		rulesWrap.setMaximumSize(new Dimension(560, 230));

		JScrollPane rulesScroll = new JScrollPane(rules);
		rulesScroll.setBorder(BorderFactory.createEmptyBorder());
		rulesScroll.setOpaque(false);
		rulesScroll.getViewport().setOpaque(false);
		rulesScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		rulesScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		rulesWrap.add(rulesScroll, BorderLayout.CENTER);

		JButton back = createButton("Retour accueil", ButtonStyle.SECONDARY);
		back.addActionListener(e -> showScreen(SCREEN_HOME));

		card.add(Box.createVerticalStrut(14));
		card.add(rulesWrap);
		card.add(Box.createVerticalStrut(14));
		card.add(back);

		return wrapCentered(card);
	}

	private RoundedPanel createCardBase(String badgeText, String titleText, String subtitleText) {
		RoundedPanel card = new RoundedPanel(
				new Color(245, 234, 206, 28),
				new Color(247, 184, 68, 128),
				1f,
				24);
		card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
		card.setPreferredSize(new Dimension(760, 500));
		card.setMinimumSize(new Dimension(620, 420));
		card.setMaximumSize(new Dimension(1400, 1000));
		card.setBorder(BorderFactory.createEmptyBorder(28, 30, 28, 30));

		JLabel badge = new JLabel(badgeText, SwingConstants.CENTER);
		badge.setAlignmentX(Component.CENTER_ALIGNMENT);
		badge.setForeground(new Color(247, 184, 68));
		badge.setFont(new Font("SansSerif", Font.BOLD, 13));

		JLabel title = new JLabel(titleText, SwingConstants.CENTER);
		title.setAlignmentX(Component.CENTER_ALIGNMENT);
		title.setForeground(new Color(249, 242, 220));
		title.setFont(new Font("SansSerif", Font.BOLD, 52));

		JLabel subtitle = new JLabel(subtitleText, SwingConstants.CENTER);
		subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
		subtitle.setForeground(new Color(216, 203, 168));
		subtitle.setFont(new Font("SansSerif", Font.PLAIN, 16));

		card.add(badge);
		card.add(Box.createVerticalStrut(8));
		card.add(title);
		card.add(Box.createVerticalStrut(10));
		card.add(subtitle);
		return card;
	}

	private JButton createButton(String text, ButtonStyle style) {
		JButton button = new JButton(text) {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				LinearGradientPaint gradient;
				if (style == ButtonStyle.PRIMARY) {
					gradient = new LinearGradientPaint(
							0,
							0,
							getWidth(),
							0,
							new float[] { 0f, 1f },
							new Color[] { new Color(247, 184, 68), new Color(255, 122, 61) });
				} else {
					Color topColor;
					Color bottomColor;
					if (getModel().isPressed()) {
						topColor = new Color(95, 49, 22);
						bottomColor = new Color(70, 36, 16);
					} else if (getModel().isRollover()) {
						topColor = new Color(133, 68, 30);
						bottomColor = new Color(96, 49, 22);
					} else {
						topColor = new Color(116, 60, 28);
						bottomColor = new Color(82, 42, 19);
					}

					gradient = new LinearGradientPaint(
							0,
							0,
							0,
							getHeight(),
							new float[] { 0f, 1f },
							new Color[] { topColor, bottomColor });
				}

				g2.setPaint(gradient);
				int arc = style == ButtonStyle.PRIMARY ? getHeight() : 14;
				g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);

				if (style == ButtonStyle.SECONDARY) {
					g2.setColor(new Color(247, 184, 68, 230));
					g2.setStroke(new BasicStroke(2f));
					g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 14, 14);
				}

				g2.dispose();
				super.paintComponent(g);
			}
		};

		button.setAlignmentX(Component.CENTER_ALIGNMENT);
		if (style == ButtonStyle.PRIMARY) {
			button.setForeground(new Color(35, 19, 0));
			button.setFont(new Font("SansSerif", Font.BOLD, 18));
			button.setMargin(new Insets(14, 38, 14, 38));
			button.setMaximumSize(new Dimension(260, 54));
		} else {
			button.setForeground(new Color(255, 244, 218));
			button.setFont(new Font("SansSerif", Font.BOLD, 16));
			button.setRolloverEnabled(true);
			button.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
			button.setMaximumSize(new Dimension(300, 50));
		}

		button.setOpaque(false);
		button.setContentAreaFilled(false);
		button.setBorderPainted(false);
		button.setFocusPainted(false);
		return button;
	}

	private JButton createModeButton(String text) {
		JButton button = createButton(text, ButtonStyle.SECONDARY);
		button.setMaximumSize(new Dimension(340, 52));
		return button;
	}

	private JTextField createNameField(String placeholder) {
		JTextField field = new JTextField(20);
		field.setMaximumSize(new Dimension(340, 44));
		field.setFont(new Font("SansSerif", Font.PLAIN, 16));
		field.setForeground(new Color(249, 242, 220));
		field.setCaretColor(new Color(249, 242, 220));
		field.setBackground(new Color(24, 18, 12, 230));
		field.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(247, 184, 68, 180), 2),
				BorderFactory.createEmptyBorder(8, 12, 8, 12)));
		field.setToolTipText(placeholder);
		return field;
	}

	private JPanel createLabeledField(String labelText, JTextField field) {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setAlignmentX(Component.CENTER_ALIGNMENT);

		JLabel label = new JLabel(labelText, SwingConstants.CENTER);
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		label.setForeground(new Color(249, 242, 220));
		label.setFont(new Font("SansSerif", Font.BOLD, 15));

		panel.add(label);
		panel.add(Box.createVerticalStrut(8));
		panel.add(field);
		return panel;
	}

	private JPanel wrapCentered(JPanel content) {
		JPanel wrapper = new JPanel(new BorderLayout());
		wrapper.setOpaque(false);

		JPanel center = new JPanel(new BorderLayout());
		center.setOpaque(false);
		center.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
		center.add(content, BorderLayout.CENTER);

		wrapper.add(center, BorderLayout.CENTER);
		return wrapper;
	}

	private void showScreen(String screen) {
		cardLayout.show(cardPanel, screen);
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) {
			// Keep default look and feel if system look and feel is not available.
		}

		SwingUtilities.invokeLater(() -> {
			game window = new game();
			window.setVisible(true);
		});
	}

	private static class GradientBackgroundPanel extends JPanel {
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			GradientPaint base = new GradientPaint(
					0,
					0,
					new Color(21, 21, 21),
					getWidth(),
					getHeight(),
					new Color(42, 28, 15));
			g2.setPaint(base);
			g2.fillRect(0, 0, getWidth(), getHeight());

			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.38f));
			g2.setColor(new Color(255, 122, 61));
			g2.fillOval(-120, getHeight() - 260, 360, 360);
			g2.setColor(new Color(247, 184, 68));
			g2.fillOval(getWidth() - 260, -120, 320, 320);

			g2.dispose();
		}
	}

	private static class RoundedPanel extends JPanel {
		private final Color fillColor;
		private final Color borderColor;
		private final float borderWidth;
		private final int arc;

		RoundedPanel(Color fillColor, Color borderColor, float borderWidth, int arc) {
			this.fillColor = fillColor;
			this.borderColor = borderColor;
			this.borderWidth = borderWidth;
			this.arc = arc;
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			RoundRectangle2D shape = new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
			g2.setColor(fillColor);
			g2.fill(shape);
			g2.setColor(borderColor);
			g2.setStroke(new BasicStroke(borderWidth));
			g2.draw(shape);
			g2.dispose();

			super.paintComponent(g);
		}

		@Override
		public boolean isOpaque() {
			return false;
		}
	}

	private static class CheckersBoardPanel extends JPanel {
		private final int boardSize;
		private final char[][] pieces;
		private final int level;

		CheckersBoardPanel(int level) {
			setOpaque(false);
			this.boardSize = 10;
			int baseSize = 520;
			setPreferredSize(new Dimension(baseSize, baseSize));
			setMinimumSize(new Dimension(340, 340));
			setMaximumSize(new Dimension(760, 760));
			this.level = level;
			this.pieces = createInitialPosition();
		}

		private char[][] createInitialPosition() {
			char[][] grid = new char[boardSize][boardSize];
			for (int row = 0; row < boardSize; row++) {
				for (int col = 0; col < boardSize; col++) {
					grid[row][col] = '.';
				}
			}

			if (level == 1) {
				for (int row = 0; row < 4; row++) {
					for (int col = 0; col < boardSize; col++) {
						if ((row + col) % 2 == 1) {
							grid[row][col] = 'b';
						}
					}
				}

				for (int row = boardSize - 4; row < boardSize; row++) {
					for (int col = 0; col < boardSize; col++) {
						if ((row + col) % 2 == 1) {
							grid[row][col] = 'r';
						}
					}
				}
			} else {
				for (int row = 0; row < 4; row++) {
					for (int col = 0; col < boardSize; col++) {
						if ((row + col) % 2 == 1) {
							grid[row][col] = 'b';
						}
					}
				}

				for (int row = boardSize - 4; row < boardSize; row++) {
					for (int col = 0; col < boardSize; col++) {
						if ((row + col) % 2 == 1) {
							grid[row][col] = 'r';
						}
					}
				}
			}
			return grid;
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			int available = Math.max(boardSize, Math.min(getWidth(), getHeight()) - 26);
			int boardPixels = (available / boardSize) * boardSize;
			int xOffset = (getWidth() - boardPixels) / 2;
			int yOffset = (getHeight() - boardPixels) / 2;
			int cell = boardPixels / boardSize;

			Color light, dark;
			if (level == 2) {
				light = new Color(220, 50, 50);    // Rouge vif
				dark = new Color(60, 60, 60);       // Gris foncé
			} else {
				light = new Color(233, 210, 173);   // Beige clair
				dark = new Color(120, 72, 40);      // Marron foncé
			}

			int framePadding = 10;
			g2.setColor(new Color(52, 32, 18, 230));
			g2.fillRoundRect(
					xOffset - framePadding,
					yOffset - framePadding,
					boardPixels + (framePadding * 2),
					boardPixels + (framePadding * 2),
					20,
					20);

			g2.setColor(new Color(247, 184, 68, 240));
			g2.setStroke(new BasicStroke(2.5f));
			g2.drawRoundRect(
					xOffset - framePadding,
					yOffset - framePadding,
					boardPixels + (framePadding * 2),
					boardPixels + (framePadding * 2),
					20,
					20);

			for (int row = 0; row < boardSize; row++) {
				for (int col = 0; col < boardSize; col++) {
					int x = xOffset + col * cell;
					int y = yOffset + row * cell;
					g2.setColor((row + col) % 2 == 0 ? light : dark);
					g2.fillRect(x, y, cell, cell);

					char piece = pieces[row][col];
					if (piece == 'r' || piece == 'b') {
						int margin = Math.max(6, cell / 7);
						int d = cell - (2 * margin);
						Color pieceColor;
						if (piece == 'b' && level == 1 && row < boardSize / 2) {
							pieceColor = Color.BLACK;
						} else if (piece == 'r') {
							pieceColor = new Color(208, 58, 46);
						} else {
							pieceColor = new Color(32, 32, 32);
						}
						g2.setColor(pieceColor);
						g2.fillOval(x + margin, y + margin, d, d);
						g2.setColor(new Color(250, 236, 206, 180));
						g2.setStroke(new BasicStroke(2f));
						g2.drawOval(x + margin, y + margin, d, d);
					}
				}
			}

			g2.setColor(new Color(42, 24, 12, 210));
			g2.setStroke(new BasicStroke(2f));
			g2.drawRect(xOffset, yOffset, boardPixels, boardPixels);

			g2.dispose();
		}
	}
}
