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
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class game extends JFrame {
	private static final String SCREEN_HOME = "home";
	private static final String SCREEN_PLAY = "play";
	private static final String SCREEN_RULES = "rules";
	private static final String SCREEN_BOARD_1V1 = "board-1v1";

	private final CardLayout cardLayout;
	private final JPanel cardPanel;

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
		cardPanel.add(createRulesScreen(), SCREEN_RULES);
		cardPanel.add(createBoard1v1Screen(), SCREEN_BOARD_1V1);

		GradientBackgroundPanel root = new GradientBackgroundPanel();
		root.setLayout(new BorderLayout());
		root.add(cardPanel, BorderLayout.CENTER);
		setContentPane(root);

		showScreen(SCREEN_HOME);
	}

	private JPanel createHomeScreen() {
		RoundedCardPanel card = createCardBase("Projet Java", "Jeu de Dames", "Choisis ton mode et lance une partie.");

		JButton playButton = createPrimaryButton("Jouer");
		playButton.addActionListener(e -> showScreen(SCREEN_PLAY));

		JButton rulesButton = createPrimaryButton("Regle");
		rulesButton.addActionListener(e -> showScreen(SCREEN_RULES));

		card.add(Box.createVerticalStrut(22));
		card.add(playButton);
		card.add(Box.createVerticalStrut(12));
		card.add(rulesButton);

		return wrapCentered(card);
	}

	private JPanel createPlayScreen() {
		RoundedCardPanel card = createCardBase("Selection", "Mode de Jeu", "Choisis comment tu veux jouer.");

		JButton oneVsOne = createModeButton("1v1");
		oneVsOne.addActionListener((ActionEvent e) -> showScreen(SCREEN_BOARD_1V1));

		JButton vsAi = createModeButton("Jouer contre IA");
		vsAi.addActionListener((ActionEvent e) -> JOptionPane.showMessageDialog(
				this,
				"Mode IA selectionne.",
				"Info",
				JOptionPane.INFORMATION_MESSAGE));

		JButton back = createSecondaryButton("Retour accueil");
		back.addActionListener(e -> showScreen(SCREEN_HOME));

		card.add(Box.createVerticalStrut(18));
		card.add(oneVsOne);
		card.add(Box.createVerticalStrut(10));
		card.add(vsAi);
		card.add(Box.createVerticalStrut(14));
		card.add(back);

		return wrapCentered(card);
	}

	private JPanel createBoard1v1Screen() {
		RoundedCardPanel card = createCardBase("1v1", "Plateau", "Partie locale joueur contre joueur.");

		CheckersBoardPanel boardPanel = new CheckersBoardPanel();
		boardPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

		JButton back = createSecondaryButton("Retour modes");
		back.addActionListener(e -> showScreen(SCREEN_PLAY));

		card.add(Box.createVerticalStrut(14));
		card.add(boardPanel);
		card.add(Box.createVerticalStrut(14));
		card.add(back);

		return wrapCentered(card);
	}

	private JPanel createRulesScreen() {
		RoundedCardPanel card = createCardBase("Infos", "Regles", "Regles de base du jeu de dames.");

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

		RoundedRulesPanel rulesWrap = new RoundedRulesPanel();
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

		JButton back = createSecondaryButton("Retour accueil");
		back.addActionListener(e -> showScreen(SCREEN_HOME));

		card.add(Box.createVerticalStrut(14));
		card.add(rulesWrap);
		card.add(Box.createVerticalStrut(14));
		card.add(back);

		return wrapCentered(card);
	}

	private RoundedCardPanel createCardBase(String badgeText, String titleText, String subtitleText) {
		RoundedCardPanel card = new RoundedCardPanel();
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

	private JButton createPrimaryButton(String text) {
		JButton button = new JButton(text) {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				LinearGradientPaint gradient = new LinearGradientPaint(
						0,
						0,
						getWidth(),
						0,
						new float[] { 0f, 1f },
						new Color[] { new Color(247, 184, 68), new Color(255, 122, 61) });
				g2.setPaint(gradient);
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
				g2.dispose();
				super.paintComponent(g);
			}
		};
		button.setAlignmentX(Component.CENTER_ALIGNMENT);
		button.setForeground(new Color(35, 19, 0));
		button.setFont(new Font("SansSerif", Font.BOLD, 18));
		button.setOpaque(false);
		button.setContentAreaFilled(false);
		button.setBorderPainted(false);
		button.setFocusPainted(false);
		button.setMargin(new Insets(14, 38, 14, 38));
		button.setMaximumSize(new Dimension(260, 54));
		return button;
	}

	private JButton createSecondaryButton(String text) {
		JButton button = new JButton(text) {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

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

				LinearGradientPaint gradient = new LinearGradientPaint(
						0,
						0,
						0,
						getHeight(),
						new float[] { 0f, 1f },
						new Color[] { topColor, bottomColor });

				g2.setPaint(gradient);
				g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
				g2.setColor(new Color(247, 184, 68, 230));
				g2.setStroke(new BasicStroke(2f));
				g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 14, 14);

				g2.dispose();
				super.paintComponent(g);
			}
		};
		button.setAlignmentX(Component.CENTER_ALIGNMENT);
		button.setForeground(new Color(255, 244, 218));
		button.setFont(new Font("SansSerif", Font.BOLD, 16));
		button.setOpaque(false);
		button.setContentAreaFilled(false);
		button.setBorderPainted(false);
		button.setFocusPainted(false);
		button.setRolloverEnabled(true);
		button.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
		button.setMaximumSize(new Dimension(300, 50));
		return button;
	}

	private JButton createModeButton(String text) {
		JButton button = createSecondaryButton(text);
		button.setMaximumSize(new Dimension(340, 52));
		return button;
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

	private static class RoundedCardPanel extends JPanel {
		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			RoundRectangle2D shape = new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 24, 24);
			g2.setColor(new Color(245, 234, 206, 28));
			g2.fill(shape);
			g2.setColor(new Color(247, 184, 68, 128));
			g2.setStroke(new BasicStroke(1f));
			g2.draw(shape);
			g2.dispose();

			super.paintComponent(g);
		}

		@Override
		public boolean isOpaque() {
			return false;
		}
	}

	private static class RoundedRulesPanel extends JPanel {
		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			RoundRectangle2D shape = new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
			g2.setColor(new Color(24, 18, 12, 230));
			g2.fill(shape);
			g2.setColor(new Color(247, 184, 68, 210));
			g2.setStroke(new BasicStroke(1.6f));
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
		private static final int BOARD_SIZE = 8;
		private final char[][] pieces;

		CheckersBoardPanel() {
			setOpaque(false);
			setPreferredSize(new Dimension(420, 420));
			setMinimumSize(new Dimension(320, 320));
			setMaximumSize(new Dimension(900, 900));
			this.pieces = createInitialPosition();
		}

		private char[][] createInitialPosition() {
			char[][] grid = new char[BOARD_SIZE][BOARD_SIZE];
			for (int row = 0; row < BOARD_SIZE; row++) {
				for (int col = 0; col < BOARD_SIZE; col++) {
					grid[row][col] = '.';
				}
			}

			for (int row = 0; row < 3; row++) {
				for (int col = 0; col < BOARD_SIZE; col++) {
					if ((row + col) % 2 == 1) {
						grid[row][col] = 'b';
					}
				}
			}

			for (int row = 5; row < BOARD_SIZE; row++) {
				for (int col = 0; col < BOARD_SIZE; col++) {
					if ((row + col) % 2 == 1) {
						grid[row][col] = 'r';
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

			int size = Math.min(getWidth(), getHeight());
			int xOffset = (getWidth() - size) / 2;
			int yOffset = (getHeight() - size) / 2;
			int cell = size / BOARD_SIZE;

			Color light = new Color(233, 210, 173);
			Color dark = new Color(120, 72, 40);

			g2.setColor(new Color(247, 184, 68, 230));
			g2.setStroke(new BasicStroke(3f));
			g2.drawRoundRect(xOffset - 6, yOffset - 6, size + 12, size + 12, 16, 16);

			for (int row = 0; row < BOARD_SIZE; row++) {
				for (int col = 0; col < BOARD_SIZE; col++) {
					int x = xOffset + col * cell;
					int y = yOffset + row * cell;
					g2.setColor((row + col) % 2 == 0 ? light : dark);
					g2.fillRect(x, y, cell, cell);

					char piece = pieces[row][col];
					if (piece == 'r' || piece == 'b') {
						int margin = Math.max(6, cell / 7);
						int d = cell - (2 * margin);
						g2.setColor(piece == 'r' ? new Color(208, 58, 46) : new Color(32, 32, 32));
						g2.fillOval(x + margin, y + margin, d, d);
						g2.setColor(new Color(250, 236, 206, 180));
						g2.setStroke(new BasicStroke(2f));
						g2.drawOval(x + margin, y + margin, d, d);
					}
				}
			}

			g2.dispose();
		}
	}
}
