import java.awt.*;
import java.awt.event.*;
import java.util.List;
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
        RoundedPanel card = createCardBase("Jeu de Dames");
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
        RoundedPanel card = createCardBase("Mode de Jeu");
        JButton oneVsOne = createButton("1v1", ButtonStyle.SECONDARY);
        oneVsOne.addActionListener(e -> showScreen(SCREEN_PLAYERS));
        JButton vsAi = createButton("Jouer contre IA", ButtonStyle.SECONDARY);
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
        RoundedPanel card = createCardBase("Pseudo");
        card.setPreferredSize(new Dimension(640, 500));

        JTextField p1Field = createNameField();
        JTextField p2Field = createNameField();
        JButton start = createButton("Continuer", ButtonStyle.PRIMARY);
        start.setMaximumSize(new Dimension(220, 45));
        start.addActionListener(e -> {
            playerOneName = p1Field.getText().trim().isEmpty() ? "Joueur 1" : p1Field.getText();
            playerTwoName = p2Field.getText().trim().isEmpty() ? "Joueur 2" : p2Field.getText();
            refreshNamedScreens();
            showScreen(SCREEN_SELECT_MAP);
        });

        JLabel hint = new JLabel("Choisissez les noms des deux joueurs", SwingConstants.CENTER);
        hint.setForeground(new Color(247, 184, 68));
        hint.setFont(new Font("SansSerif", Font.PLAIN, 16));
        hint.setAlignmentX(CENTER_ALIGNMENT);

        card.add(Box.createVerticalStrut(12));
        card.add(hint);
        card.add(Box.createVerticalStrut(20));
        card.add(createPlayerInputBlock("Joueur Rouge", p1Field));
        card.add(Box.createVerticalStrut(12));
        card.add(createPlayerInputBlock("Joueur Noir", p2Field));
        card.add(Box.createVerticalStrut(24));
        card.add(start);
        return wrapCentered(card);
    }

    private JPanel createSelectMapScreen() {
        RoundedPanel card = createCardBase("Choix de la Map");
        card.setPreferredSize(new Dimension(840, 560));

        JPanel grid = new JPanel(new GridLayout(1, 2, 24, 0));
        grid.setOpaque(false);
        grid.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        grid.setMaximumSize(new Dimension(10000, 10000));
        grid.setAlignmentX(CENTER_ALIGNMENT);
        grid.add(createMapPreview(1, SCREEN_BOARD_LEVEL1));
        grid.add(createMapPreview(2, SCREEN_BOARD_LEVEL2));
        card.add(grid);
        return wrapCentered(card);
    }

    private JPanel createMapPreview(int level, String target) {
        RoundedPanel p = new RoundedPanel(new Color(24, 18, 12, 200), new Color(247, 184, 68), 1f, 15);
        p.setLayout(new BorderLayout(0, 10));
        p.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("Niveau " + level, SwingConstants.CENTER);
        title.setForeground(new Color(247, 184, 68));
        title.setFont(new Font("SansSerif", Font.BOLD, 18));

        JPanel preview = createMapVisual(level);

        JButton btn = createButton("Choisir", ButtonStyle.PRIMARY);
        btn.setPreferredSize(new Dimension(160, 40));
        btn.setMaximumSize(new Dimension(220, 50));
        btn.addActionListener(e -> showScreen(target));

        JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        btnWrap.setOpaque(false);
        btnWrap.add(btn);

        p.add(title, BorderLayout.NORTH);
        p.add(preview, BorderLayout.CENTER);
        p.add(btnWrap, BorderLayout.SOUTH);
        return p;
    }

    private JPanel createMapVisual(int level) {
        JPanel preview = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int padding = 6;
                int size = Math.min(getWidth(), getHeight()) - padding * 2;
                int boardX = (getWidth() - size) / 2;
                int boardY = (getHeight() - size) / 2;
                int cells = 10;
                int cellSize = Math.max(1, size / cells);

                Color light = level == 1 ? new Color(235, 210, 180) : new Color(214, 0, 0);
                Color dark = level == 1 ? new Color(133, 68, 30) : new Color(43, 57, 72);

                g2.setColor(new Color(247, 184, 68, 170));
                g2.drawRoundRect(boardX - 2, boardY - 2, cellSize * cells + 4, cellSize * cells + 4, 8, 8);

                for (int r = 0; r < cells; r++) {
                    for (int c = 0; c < cells; c++) {
                        int x = boardX + c * cellSize;
                        int y = boardY + r * cellSize;
                        g2.setColor((r + c) % 2 == 0 ? light : dark);
                        g2.fillRect(x, y, cellSize, cellSize);

                        if ((r + c) % 2 == 1 && (r < 3 || r > 6)) {
                            g2.setColor(r < 3 ? new Color(30, 30, 30) : new Color(200, 50, 50));
                            int piece = Math.max(2, cellSize - 4);
                            g2.fillOval(x + (cellSize - piece) / 2, y + (cellSize - piece) / 2, piece, piece);
                        }
                    }
                }

                g2.dispose();
            }
        };
        preview.setOpaque(false);
        preview.setPreferredSize(new Dimension(260, 260));
        preview.setMinimumSize(new Dimension(140, 140));
        preview.setMaximumSize(new Dimension(10000, 10000));
        return preview;
    }

    private JPanel createBoard1v1Screen(int level) {
        RoundedPanel card = createGameCard();
        CheckersBoardPanel board = new CheckersBoardPanel(level);
        JLabel turnLabel = createStatusLabel("Au tour de " + playerOneName);
        board.bindStatusLabels(turnLabel, playerOneName, playerTwoName);

        JPanel arenaPanel = new JPanel(new BorderLayout(15, 0));
        arenaPanel.setOpaque(false);

        JList<String> redMoves = createMovesList();
        JList<String> blackMoves = createMovesList();
        board.bindMoveHistory(redMoves, blackMoves);

        arenaPanel.add(createMovesColumn(playerOneName + " (Rouge)", redMoves), BorderLayout.WEST);
        arenaPanel.add(board, BorderLayout.CENTER);
        arenaPanel.add(createMovesColumn(playerTwoName + " (Noir)", blackMoves), BorderLayout.EAST);

        JButton back = createButton("Abandonner", ButtonStyle.SECONDARY);
        back.addActionListener(e -> showScreen(SCREEN_HOME));

        card.add(turnLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(arenaPanel);
        card.add(Box.createVerticalStrut(15));
        card.add(back);
        return wrapCentered(card);
    }

    private JPanel createBoardAiScreen() {
        return createBoard1v1Screen(1); // Simplifié pour l'exemple
    }

    private JPanel createRulesScreen() {
        RoundedPanel card = createCardBase("Regles");
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
    private RoundedPanel createCardBase(String title) {
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

    private JLabel createStatusLabel(String t) {
        JLabel l = new JLabel(t, SwingConstants.CENTER);
        l.setForeground(Color.WHITE);
        l.setFont(new Font("SansSerif", Font.BOLD, 18));
        l.setAlignmentX(CENTER_ALIGNMENT);
        return l;
    }

    private JTextField createNameField() {
        JTextField f = new JTextField(15);
        f.setMaximumSize(new Dimension(280, 42));
        f.setPreferredSize(new Dimension(280, 42));
        f.setBackground(new Color(18, 18, 18));
        f.setForeground(Color.WHITE);
        f.setCaretColor(Color.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(247, 184, 68, 120), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        f.setFont(new Font("SansSerif", Font.PLAIN, 15));
        return f;
    }

    private JPanel createPlayerInputBlock(String text, JTextField f) {
        RoundedPanel wrapper = new RoundedPanel(new Color(24, 18, 12, 180), new Color(247, 184, 68, 80), 1f, 14);
        wrapper.setLayout(new BorderLayout(0, 8));
        wrapper.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        wrapper.setPreferredSize(new Dimension(430, 90));
        wrapper.setMinimumSize(new Dimension(430, 90));
        wrapper.setMaximumSize(new Dimension(430, 90));
        wrapper.setAlignmentX(CENTER_ALIGNMENT);

        JLabel label = new JLabel(text);
        label.setForeground(new Color(247, 184, 68));
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setHorizontalAlignment(SwingConstants.LEFT);

        f.setPreferredSize(new Dimension(400, 42));
        f.setMaximumSize(new Dimension(400, 42));

        wrapper.add(label, BorderLayout.NORTH);
        wrapper.add(f, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel wrapCentered(JPanel c) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false); p.add(c); return p;
    }

    private JList<String> createMovesList() {
        DefaultListModel<String> model = new DefaultListModel<>();
        model.addElement("Aucun déplacement");
        JList<String> list = new JList<>(model);
        list.setBackground(new Color(24, 18, 12, 220));
        list.setForeground(Color.WHITE);
        list.setSelectionBackground(new Color(116, 60, 28));
        list.setSelectionForeground(Color.WHITE);
        list.setFocusable(false);
        return list;
    }

    private JPanel createMovesColumn(String title, JList<String> movesList) {
        RoundedPanel panel = new RoundedPanel(new Color(24, 18, 12, 200), new Color(247, 184, 68, 120), 1f, 15);
        panel.setLayout(new BorderLayout(0, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setPreferredSize(new Dimension(180, 360));

        JLabel header = new JLabel(title, SwingConstants.CENTER);
        header.setForeground(new Color(247, 184, 68));
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        panel.add(header, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(movesList);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(panel, BorderLayout.CENTER);
        return wrapper;
    }

    // --- CLASSES INTERNES DE DESSIN ---

    private static class CheckersBoardPanel extends JPanel {
        private final int boardSize = 10;
        private final Plateau plateau = new Plateau();
        private final int mapLevel;
        private int cell, xOffset, yOffset;
        private JLabel turnLabel;
        private String redName, blackName;
        private DefaultListModel<String> redMovesModel;
        private DefaultListModel<String> blackMovesModel;
        private int selectedRow = -1;
        private int selectedCol = -1;

        public CheckersBoardPanel(int level) {
            this.mapLevel = level;
            setOpaque(false);
            setPreferredSize(new Dimension(450, 450));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    handleBoardClick(e.getX(), e.getY());
                }
            });
        }

        public void bindStatusLabels(JLabel l, String r, String b) {
            this.turnLabel = l; this.redName = r; this.blackName = b;
        }

        public void bindMoveHistory(JList<String> redMoves, JList<String> blackMoves) {
            this.redMovesModel = (DefaultListModel<String>) redMoves.getModel();
            this.blackMovesModel = (DefaultListModel<String>) blackMoves.getModel();
        }

        public void recordMove(String move, boolean isRedPlayer) {
            DefaultListModel<String> targetModel = isRedPlayer ? redMovesModel : blackMovesModel;
            if (targetModel == null) {
                return;
            }
            if (targetModel.size() == 1 && "Aucun déplacement".equals(targetModel.get(0))) {
                targetModel.clear();
            }
            targetModel.addElement(move);
        }

        private void handleBoardClick(int mouseX, int mouseY) {
            if (cell <= 0) {
                return;
            }

            int col = (mouseX - xOffset) / cell;
            int row = (mouseY - yOffset) / cell;
            if (!isInsideBoard(row, col)) {
                return;
            }

            char clickedPiece = plateau.getBoard()[row][col];
            char currentPlayer = plateau.getCurrentPlayer();

            if (selectedRow < 0) {
                if (clickedPiece != '.' && Character.toLowerCase(clickedPiece) == currentPlayer) {
                    selectedRow = row;
                    selectedCol = col;
                    repaint();
                }
                return;
            }

            if (clickedPiece != '.' && Character.toLowerCase(clickedPiece) == currentPlayer) {
                selectedRow = row;
                selectedCol = col;
                repaint();
                return;
            }

            Move selectedMove = findLegalMove(selectedRow, selectedCol, row, col);
            if (selectedMove != null) {
                boolean isRedTurn = currentPlayer == 'r';
                String moveText = formatMove(selectedMove);
                plateau.executeMove(selectedMove);
                recordMove(moveText, isRedTurn);
                updateTurnLabel();
            }

            selectedRow = -1;
            selectedCol = -1;
            repaint();
        }

        private Move findLegalMove(int fromRow, int fromCol, int toRow, int toCol) {
            List<Move> legalMoves = plateau.getLegalMoves();
            for (Move move : legalMoves) {
                if (move.fromRow == fromRow && move.fromCol == fromCol && move.toRow == toRow && move.toCol == toCol) {
                    return move;
                }
            }
            return null;
        }

        private String formatMove(Move move) {
            return positionToNotation(move.fromRow, move.fromCol) + " -> " + positionToNotation(move.toRow, move.toCol);
        }

        private String positionToNotation(int row, int col) {
            char file = (char) ('a' + col);
            int rank = boardSize - row;
            return "" + file + rank;
        }

        private boolean isInsideBoard(int row, int col) {
            return row >= 0 && row < boardSize && col >= 0 && col < boardSize;
        }

        private void updateTurnLabel() {
            if (turnLabel == null) {
                return;
            }
            boolean redTurn = plateau.getCurrentPlayer() == 'r';
            String currentName = redTurn ? redName : blackName;
            turnLabel.setText("Au tour de " + currentName);
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

            Color lightSquare = mapLevel == 2 ? new Color(214, 0, 0) : new Color(235, 210, 180);
            Color darkSquare = mapLevel == 2 ? new Color(43, 57, 72) : new Color(133, 68, 30);

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
                    g2.setColor((r + c) % 2 == 0 ? lightSquare : darkSquare);
                    g2.fillRect(x, y, cell, cell);

                    if (r == selectedRow && c == selectedCol) {
                        g2.setColor(new Color(247, 184, 68, 120));
                        g2.fillRect(x, y, cell, cell);
                    }

                    // LETTRES
                    if (r == boardSize - 1) {
                        g2.setColor(new Color(247, 184, 68));
                        g2.drawString(String.valueOf((char)('a' + c)), x + cell / 2 - 5, yOffset + boardSize * cell + 20);
                    }

                    // PIONS
                    char piece = plateau.getBoard()[r][c];
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new game().setVisible(true));
    }
}