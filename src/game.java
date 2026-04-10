import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.*;

public class game extends JFrame {
    private static final String SCREEN_HOME = "home";
    private static final String SCREEN_PLAY = "play";
    private static final String SCREEN_PLAYERS = "players";
    private static final String SCREEN_RULES = "rules";
    private static final String SCREEN_SELECT_MAP = "select-map";
    private static final String SCREEN_BOARD_LEVEL1 = "board-level1";
    private static final String SCREEN_BOARD_LEVEL2 = "board-level2";
    private static final String SCREEN_BOARD_AI = "board-ai";

    private final CardLayout cardLayout;
    private final JPanel cardPanel;
    private String playerOneName = "Joueur 1";
    private String playerTwoName = "Joueur 2";

    private enum ButtonStyle { PRIMARY, SECONDARY }

    public game() {
        super("Jeu de Dames");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(980, 700);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false);

        // Initialisation des écrans
        cardPanel.add(createHomeScreen(), SCREEN_HOME);
        cardPanel.add(createPlayScreen(), SCREEN_PLAY);
        cardPanel.add(createPlayersScreen(), SCREEN_PLAYERS);
        cardPanel.add(createRulesScreen(), SCREEN_RULES);
        
        refreshNamedScreens(); // Crée les écrans de jeu avec les noms par défaut

        GradientBackgroundPanel root = new GradientBackgroundPanel();
        root.setLayout(new BorderLayout());
        root.add(cardPanel, BorderLayout.CENTER);
        setContentPane(root);

        showScreen(SCREEN_HOME);
    }

    // --- LOGIQUE DE NAVIGATION ---
    private void showScreen(String screen) {
        cardLayout.show(cardPanel, screen);
    }

    private void refreshNamedScreens() {
        // Nettoyage des anciens écrans de jeu
        Component[] components = cardPanel.getComponents();
        for (Component c : components) {
            if (c.getName() != null && c.getName().startsWith("game-")) cardPanel.remove(c);
        }

        cardPanel.add(createSelectMapScreen(), SCREEN_SELECT_MAP);
        cardPanel.add(createBoard1v1Screen(1), SCREEN_BOARD_LEVEL1);
        cardPanel.add(createBoard1v1Screen(2), SCREEN_BOARD_LEVEL2);
        cardPanel.add(createBoardAiScreen(), SCREEN_BOARD_AI);
        cardPanel.revalidate();
        cardPanel.repaint();
    }

    // --- CRÉATION DES ÉCRANS ---
    private JPanel createHomeScreen() {
        RoundedPanel card = createCardBase("Projet Java", "Jeu de Dames", "Choisis ton mode et lance une partie.");
        JButton playButton = createButton("Jouer", ButtonStyle.PRIMARY);
        playButton.addActionListener(e -> showScreen(SCREEN_PLAY));
        JButton rulesButton = createButton("Regles", ButtonStyle.PRIMARY);
        rulesButton.addActionListener(e -> showScreen(SCREEN_RULES));

        card.add(Box.createVerticalStrut(20));
        card.add(playButton);
        card.add(Box.createVerticalStrut(10));
        card.add(rulesButton);
        return wrapCentered(card);
    }

    private JPanel createPlayScreen() {
        RoundedPanel card = createCardBase("Selection", "Mode de Jeu", "Choisis ta façon de jouer.");
        JButton oneVsOne = createModeButton("1v1");
        oneVsOne.addActionListener(e -> showScreen(SCREEN_PLAYERS));
        JButton vsAi = createModeButton("Jouer contre IA");
        vsAi.addActionListener(e -> {
            playerTwoName = "IA";
            refreshNamedScreens();
            showScreen(SCREEN_BOARD_AI);
        });
        JButton back = createButton("Retour", ButtonStyle.SECONDARY);
        back.addActionListener(e -> showScreen(SCREEN_HOME));

        card.add(Box.createVerticalStrut(15));
        card.add(oneVsOne);
        card.add(Box.createVerticalStrut(10));
        card.add(vsAi);
        card.add(Box.createVerticalStrut(10));
        card.add(back);
        return wrapCentered(card);
    }

    private JPanel createPlayersScreen() {
        RoundedPanel card = createCardBase("Joueurs", "Identité", "Entrez vos prénoms.");
        JTextField p1Field = createNameField("Joueur 1");
        JTextField p2Field = createNameField("Joueur 2");
        JButton start = createButton("Continuer", ButtonStyle.PRIMARY);
        start.addActionListener(e -> {
            playerOneName = p1Field.getText().trim().isEmpty() ? "Joueur 1" : p1Field.getText();
            playerTwoName = p2Field.getText().trim().isEmpty() ? "Joueur 2" : p2Field.getText();
            refreshNamedScreens();
            showScreen(SCREEN_SELECT_MAP);
        });

        card.add(Box.createVerticalStrut(15));
        card.add(createLabeledField("Prénom Joueur 1 (Rouge)", p1Field));
        card.add(Box.createVerticalStrut(10));
        card.add(createLabeledField("Prénom Joueur 2 (Noir)", p2Field));
        card.add(Box.createVerticalStrut(20));
        card.add(start);
        return wrapCentered(card);
    }

    private JPanel createSelectMapScreen() {
        RoundedPanel card = createCardBase("Arène", "Choix de la Map", playerOneName + " vs " + playerTwoName);
        JPanel grid = new JPanel(new GridLayout(1, 2, 20, 0));
        grid.setOpaque(false);
        grid.add(createMapPreview(1, SCREEN_BOARD_LEVEL1));
        grid.add(createMapPreview(2, SCREEN_BOARD_LEVEL2));
        card.add(grid);
        return wrapCentered(card);
    }

    private JPanel createMapPreview(int level, String target) {
        RoundedPanel p = new RoundedPanel(new Color(24, 18, 12, 200), new Color(247, 184, 68), 1f, 15);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(new JLabel("Niveau " + level, SwingConstants.CENTER));
        JButton btn = createButton("Choisir", ButtonStyle.PRIMARY);
        btn.addActionListener(e -> showScreen(target));
        p.add(btn);
        return p;
    }

    private JPanel createBoard1v1Screen(int level) {
        RoundedPanel card = createGameCard();
        CheckersBoardPanel board = new CheckersBoardPanel(level, false);
        JLabel turnLabel = createStatusLabel("Au tour de " + playerOneName);
        board.bindStatusLabels(turnLabel, playerOneName, playerTwoName);

        JButton back = createButton("Abandonner", ButtonStyle.SECONDARY);
        back.addActionListener(e -> showScreen(SCREEN_HOME));

        card.add(turnLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(board);
        card.add(Box.createVerticalStrut(15));
        card.add(back);
        return wrapCentered(card);
    }

    private JPanel createBoardAiScreen() {
        return createBoard1v1Screen(1); // Simplifié pour l'exemple
    }

    private JPanel createRulesScreen() {
        RoundedPanel card = createCardBase("Infos", "Regles", "");
        JTextArea area = new JTextArea("1. Déplacement en diagonale.\n2. Capture obligatoire.\n3. Atteindre le bord = Dame.");
        area.setOpaque(false);
        area.setForeground(Color.WHITE);
        card.add(area);
        JButton back = createButton("Retour", ButtonStyle.SECONDARY);
        back.addActionListener(e -> showScreen(SCREEN_HOME));
        card.add(back);
        return wrapCentered(card);
    }

    // --- COMPOSANTS CUSTOMS ---
    private RoundedPanel createCardBase(String badge, String title, String sub) {
        RoundedPanel card = new RoundedPanel(new Color(245, 234, 206, 25), new Color(247, 184, 68, 100), 1f, 25);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        card.setPreferredSize(new Dimension(600, 450));
        
        JLabel lTitle = new JLabel(title, SwingConstants.CENTER);
        lTitle.setFont(new Font("SansSerif", Font.BOLD, 40));
        lTitle.setForeground(Color.WHITE);
        lTitle.setAlignmentX(CENTER_ALIGNMENT);
        card.add(lTitle);
        return card;
    }

    private RoundedPanel createGameCard() {
        RoundedPanel card = new RoundedPanel(new Color(20, 20, 20, 150), new Color(247, 184, 68, 50), 1f, 20);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        return card;
    }

    private JButton createButton(String text, ButtonStyle style) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (style == ButtonStyle.PRIMARY) {
                    g2.setPaint(new LinearGradientPaint(0, 0, getWidth(), 0, new float[]{0, 1}, new Color[]{new Color(247, 184, 68), new Color(255, 122, 61)}));
                } else {
                    g2.setColor(new Color(116, 60, 28));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(style == ButtonStyle.PRIMARY ? Color.BLACK : Color.WHITE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setAlignmentX(CENTER_ALIGNMENT);
        return btn;
    }

    private JButton createModeButton(String t) { return createButton(t, ButtonStyle.SECONDARY); }
    private JLabel createStatusLabel(String t) {
        JLabel l = new JLabel(t, SwingConstants.CENTER);
        l.setForeground(Color.WHITE);
        l.setFont(new Font("SansSerif", Font.BOLD, 18));
        l.setAlignmentX(CENTER_ALIGNMENT);
        return l;
    }

    private JTextField createNameField(String p) {
        JTextField f = new JTextField(15);
        f.setMaximumSize(new Dimension(300, 40));
        f.setBackground(new Color(30, 30, 30));
        f.setForeground(Color.WHITE);
        f.setCaretColor(Color.WHITE);
        return f;
    }

    private JPanel createLabeledField(String text, JTextField f) {
        JPanel p = new JPanel(); p.setOpaque(false);
        p.add(new JLabel(text)); p.add(f); return p;
    }

    private JPanel wrapCentered(JPanel c) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false); p.add(c); return p;
    }

    // --- CLASSES INTERNES DE DESSIN ---

    private static class CheckersBoardPanel extends JPanel {
        private final int boardSize = 10;
        private final Plateau plateau = new Plateau();
        private int cell, xOffset, yOffset;
        private JLabel turnLabel;
        private String redName, blackName;

        public CheckersBoardPanel(int level, boolean ai) {
            setOpaque(false);
            setPreferredSize(new Dimension(450, 450));
        }

        public void bindStatusLabels(JLabel l, String r, String b) {
            this.turnLabel = l; this.redName = r; this.blackName = b;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int margin = 40;
            int size = Math.min(getWidth() - margin * 2, getHeight() - margin * 2);
            cell = size / boardSize;
            xOffset = (getWidth() - (cell * boardSize)) / 2;
            yOffset = (getHeight() - (cell * boardSize)) / 2;

            // Cadre
            g2.setColor(new Color(116, 60, 28));
            g2.fillRoundRect(xOffset - 10, yOffset - 10, (cell * boardSize) + 20, (cell * boardSize) + 20, 15, 15);
            g2.setColor(new Color(247, 184, 68));
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(xOffset - 10, yOffset - 10, (cell * boardSize) + 20, (cell * boardSize) + 20, 15, 15);

            for (int r = 0; r < boardSize; r++) {
                // CHIFFRES
                g2.setColor(new Color(247, 184, 68));
                g2.drawString(String.valueOf(10 - r), xOffset - 25, yOffset + r * cell + cell / 2 + 5);

                for (int c = 0; c < boardSize; c++) {
                    int x = xOffset + c * cell;
                    int y = yOffset + r * cell;

                    // CASES
                    g2.setColor((r + c) % 2 == 0 ? new Color(235, 210, 180) : new Color(133, 68, 30));
                    g2.fillRect(x, y, cell, cell);

                    // LETTRES
                    if (r == boardSize - 1) {
                        g2.setColor(new Color(247, 184, 68));
                        g2.drawString(String.valueOf((char)('a' + c)), x + cell / 2 - 5, yOffset + boardSize * cell + 20);
                    }

                    // PIONS
                    char piece = plateau.getPiece(r, c);
                    if (piece != '.') drawPiece(g2, x, y, piece);
                }
            }
            g2.dispose();
        }

        private void drawPiece(Graphics2D g2, int x, int y, char piece) {
            int p = cell / 8;
            int s = cell - p * 2;
            g2.setColor(Character.toLowerCase(piece) == 'r' ? new Color(200, 50, 50) : Color.BLACK);
            g2.fillOval(x + p, y + p, s, s);
            g2.setColor(new Color(255, 255, 255, 50));
            g2.drawOval(x + p + 4, y + p + 4, s - 8, s - 8);
        }
    }

    private static class GradientBackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setPaint(new GradientPaint(0, 0, new Color(21, 21, 21), getWidth(), getHeight(), new Color(42, 28, 15)));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
            g2.setColor(new Color(247, 184, 68));
            g2.fillOval(getWidth()-200, -100, 400, 400);
            g2.dispose();
        }
    }

    private static class RoundedPanel extends JPanel {
        private Color bg, border; private float w; private int arc;
        public RoundedPanel(Color bg, Color brd, float w, int arc) {
            this.bg = bg; this.border = brd; this.w = w; this.arc = arc; setOpaque(false);
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg); g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            g2.setColor(border); g2.setStroke(new BasicStroke(w));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, arc, arc);
            g2.dispose();
        }
    }

    // --- CLASSES SIMULÉES (À REMPLACER PAR TES VRAIES CLASSES) ---
    private static class Plateau {
        public char getPiece(int r, int c) { 
            if (r < 3 && (r+c)%2 != 0) return 'b';
            if (r > 6 && (r+c)%2 != 0) return 'r';
            return '.'; 
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new game().setVisible(true));
    }
}