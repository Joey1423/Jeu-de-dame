import java.awt.*; // imports AWT UI
import java.awt.event.*; // imports events
import javax.swing.*; // imports Swing

public class game extends JFrame { // fenetre principale
    private static final String SCREEN_HOME = "home"; // id accueil
    private static final String SCREEN_PLAY = "play"; // id choix mode
    private static final String SCREEN_PLAYERS = "players"; // id saisie noms
    private static final String SCREEN_RULES = "rules"; // id regles
    private static final String SCREEN_SELECT_MAP = "select-map"; // id choix map
    private static final String SCREEN_BOARD_LEVEL1 = "board-level1"; // id plateau map1
    private static final String SCREEN_BOARD_LEVEL2 = "board-level2"; // id plateau map2
    private static final String SCREEN_BOARD_AI = "board-ai"; // id plateau IA

    private final CardLayout cardLayout; // navigation ecrans
    private final JPanel cardPanel; // conteneur cartes
    private String playerOneName = "Joueur 1"; // nom rouge
    private String playerTwoName = "Joueur 2"; // nom noir

    private enum ButtonStyle { PRIMARY, SECONDARY } // style boutons

    public game() { // constructeur UI
        super("Jeu de Dames"); // titre fenetre
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // fermeture app
        setSize(980, 700); // taille initiale
        setMinimumSize(new Dimension(800, 600)); // taille min
        setLocationRelativeTo(null); // centrer ecran

        cardLayout = new CardLayout(); // init layout cartes
        cardPanel = new JPanel(cardLayout); // panel central
        cardPanel.setOpaque(false); // fond transparent

        // Initialisation des écrans
        cardPanel.add(createHomeScreen(), SCREEN_HOME);
        cardPanel.add(createPlayScreen(), SCREEN_PLAY);
        cardPanel.add(createPlayersScreen(), SCREEN_PLAYERS);
        cardPanel.add(createRulesScreen(), SCREEN_RULES);
        
        refreshNamedScreens(); // Crée les écrans de jeu avec les noms par défaut

        GradientBackgroundPanel root = new GradientBackgroundPanel(); // fond degrade
        root.setLayout(new BorderLayout()); // layout racine
        root.add(cardPanel, BorderLayout.CENTER); // injecter cartes
        setContentPane(root); // poser racine

        showScreen(SCREEN_HOME); // ecran initial
    }

    // --- LOGIQUE DE NAVIGATION ---
    private void showScreen(String screen) { // changer ecran
        cardLayout.show(cardPanel, screen); // afficher carte
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

    private JPanel createBoard1v1Screen(int level) { // plateau humain vs humain
        return createBoardScreen(level, false); // IA off
    }

    private JPanel createBoardScreen(int level, boolean aiEnabled) { // fabrique ecran partie
        RoundedPanel card = createGameCard(); // carte de jeu
        CheckersBoardPanel board = new CheckersBoardPanel(level, aiEnabled); // plateau custom
        JLabel turnLabel = createStatusLabel("Au tour de " + playerOneName); // label tour
        board.bindStatusLabels(turnLabel, playerOneName, playerTwoName); // noms joueurs

        JPanel arenaPanel = new JPanel(new BorderLayout(15, 0)); // zone centrale
        arenaPanel.setOpaque(false); // fond transparent

        JList<String> redMoves = createMovesList(); // histo rouge
        JList<String> blackMoves = createMovesList(); // histo noir
        board.bindMoveHistory(redMoves, blackMoves); // lier histo

        arenaPanel.add(createMovesColumn(playerOneName + " (Rouge)", redMoves), BorderLayout.WEST);
        arenaPanel.add(board, BorderLayout.CENTER);
        arenaPanel.add(createMovesColumn(playerTwoName + " (Noir)", blackMoves), BorderLayout.EAST);

        JButton back = createButton("Abandonner", ButtonStyle.SECONDARY); // bouton quitter
        back.addActionListener(e -> showScreen(SCREEN_HOME)); // retour accueil

        card.add(turnLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(arenaPanel);
        card.add(Box.createVerticalStrut(15));
        card.add(back);
        return wrapCentered(card);
    }

    private JPanel createBoardAiScreen() { // plateau mode IA
        return createBoardScreen(1, true); // IA on
    }

    private JPanel createRulesScreen() { // ecran regles
        RoundedPanel card = createCardBase("Regles"); // carte regles
        JTextArea area = new JTextArea("1. Déplacement en diagonale.\n2. Capture obligatoire.\n3. Atteindre le bord = Dame."); // texte
        area.setOpaque(false); // fond transparent
        area.setForeground(Color.WHITE); // texte blanc
        card.add(area); // ajouter texte
        JButton back = createButton("Retour", ButtonStyle.SECONDARY); // bouton retour
        back.addActionListener(e -> showScreen(SCREEN_HOME)); // action retour
        card.add(back); // ajouter bouton
        return wrapCentered(card); // centrer carte
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
        list.setOpaque(true);
        list.setBackground(new Color(24, 18, 12));
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
        panel.setOpaque(true);

        JLabel header = new JLabel(title, SwingConstants.CENTER);
        header.setForeground(new Color(247, 184, 68));
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        panel.add(header, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(movesList);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setOpaque(true);
        scroll.getViewport().setBackground(new Color(24, 18, 12));
        scroll.setOpaque(true);
        scroll.setBackground(new Color(24, 18, 12));
        panel.add(scroll, BorderLayout.CENTER);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(panel, BorderLayout.CENTER);
        return wrapper;
    }

    // --- CLASSES INTERNES DE DESSIN ---

    private static class CheckersBoardPanel extends JPanel { // composant plateau
        private final int boardSize = 10; // taille cases
        private final GameController controller = new GameController(); // logique partie
        private final int mapLevel; // theme map
        private final boolean aiEnabled; // mode IA
        private final char aiPlayer = 'b'; // camp IA
        private int cell, xOffset, yOffset; // geometrie plateau
        private JLabel turnLabel; // label tour
        private String redName, blackName; // noms joueurs
        private DefaultListModel<String> redMovesModel; // histo rouge
        private DefaultListModel<String> blackMovesModel; // histo noir

        public CheckersBoardPanel(int level, boolean aiEnabled) { // constructeur panneau
            this.mapLevel = level; // set map
            this.aiEnabled = aiEnabled; // set mode IA
            setOpaque(false); // fond transparent
            setPreferredSize(new Dimension(450, 450)); // taille preferee
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    handleBoardClick(e.getX(), e.getY()); // relayer clic
                }
            });
        }

        public void bindStatusLabels(JLabel l, String r, String b) { // lier labels
            this.turnLabel = l; this.redName = r; this.blackName = b; // stock refs
        }

        public void bindMoveHistory(JList<String> redMoves, JList<String> blackMoves) { // lier historiques
            this.redMovesModel = (DefaultListModel<String>) redMoves.getModel(); // model rouge
            this.blackMovesModel = (DefaultListModel<String>) blackMoves.getModel(); // model noir
        }

        public void recordMove(String move, boolean isRedPlayer) { // push coup histo
            DefaultListModel<String> targetModel = isRedPlayer ? redMovesModel : blackMovesModel; // choisir camp
            if (targetModel == null) {
                return; // securite
            }
            if (targetModel.size() == 1 && "Aucun déplacement".equals(targetModel.get(0))) {
                targetModel.clear(); // retirer placeholder
            }
            targetModel.addElement(move); // ajouter coup
        }

        private void handleBoardClick(int mouseX, int mouseY) { // clic utilisateur
            if (cell <= 0 || controller.isGameOver()) {
                return; // plateau inactif
            }

            if (aiEnabled && controller.getCurrentPlayer() == aiPlayer) {
                return; // bloquer clic pendant tour IA
            }

            int col = (mouseX - xOffset) / cell;
            int row = (mouseY - yOffset) / cell;
            if (!isInsideBoard(row, col)) {
                return; // clic hors plateau
            }

            GameController.ClickResult result = controller.handleClick(row, col); // deleguer controleur
            if (result.moveExecuted) {
                recordMove(result.moveText, result.wasRedTurn); // log coup
                updateTurnLabel(); // maj label tour
                checkGameOver(result); // popup fin si besoin
                if (aiEnabled && !controller.isGameOver() && controller.getCurrentPlayer() == aiPlayer) {
                    playAiTurn(); // enchainer tour IA
                }
            }
            if (result.selectionChanged || result.moveExecuted) {
                repaint(); // rafraichir rendu
            }
        }

        private void playAiTurn() { // tour auto IA
            Timer timer = new Timer(300, e -> {
                GameController.ClickResult aiResult = controller.playRandomMoveForCurrentPlayer(); // coup IA
                if (aiResult.moveExecuted) {
                    recordMove(aiResult.moveText, aiResult.wasRedTurn); // log IA
                    updateTurnLabel(); // maj tour humain
                }
                checkGameOver(aiResult); // verifier fin
                repaint(); // redraw
            });
            timer.setRepeats(false); // tir unique
            timer.start(); // lancer timer
        }

        private boolean isInsideBoard(int row, int col) { // borne plateau
            return row >= 0 && row < boardSize && col >= 0 && col < boardSize; // test bornes
        }

        private void updateTurnLabel() { // maj texte tour
            if (turnLabel == null) {
                return; // pas de label
            }
            boolean redTurn = controller.getCurrentPlayer() == 'r'; // camp courant
            String currentName = redTurn ? redName : blackName; // nom actif
            turnLabel.setText("Au tour de " + currentName); // afficher
        }

        private void checkGameOver(GameController.ClickResult result) { // popup fin
            if (result.winner != null) {
                String winnerName = result.winner == 'r' ? redName : blackName; // nom gagnant
                JOptionPane.showMessageDialog(this, winnerName + " a gagné la partie.", "Fin de partie", JOptionPane.INFORMATION_MESSAGE); // popup
                if (turnLabel != null) {
                    turnLabel.setText("Victoire de " + winnerName); // figer label
                }
            }
        }

        @Override
        protected void paintComponent(Graphics g) { // rendu plateau
            super.paintComponent(g); // paint parent
            Graphics2D g2 = (Graphics2D) g.create(); // copie contexte
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // anti alias

            int margin = 40; // marge externe
            int size = Math.min(getWidth() - margin * 2, getHeight() - margin * 2); // taille utile
            cell = size / boardSize; // taille case
            xOffset = (getWidth() - (cell * boardSize)) / 2; // decalage x
            yOffset = (getHeight() - (cell * boardSize)) / 2; // decalage y

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

                    if (r == controller.getSelectedRow() && c == controller.getSelectedCol()) {
                        g2.setColor(new Color(247, 184, 68, 120));
                        g2.fillRect(x, y, cell, cell);
                    }

                    // LETTRES
                    if (r == boardSize - 1) {
                        g2.setColor(new Color(247, 184, 68));
                        g2.drawString(String.valueOf((char)('a' + c)), x + cell / 2 - 5, yOffset + boardSize * cell + 20);
                    }

                    // PIONS
                    char piece = controller.getBoard()[r][c];
                    if (piece != '.') drawPiece(g2, x, y, piece);
                }
            }
            g2.dispose(); // lib context
        }

        private void drawPiece(Graphics2D g2, int x, int y, char piece) { // dessiner pion/dame
            int p = cell / 8; // padding piece
            int s = cell - p * 2; // taille piece
            boolean redPiece = Character.toLowerCase(piece) == 'r'; // camp piece
            g2.setColor(redPiece ? new Color(200, 50, 50) : Color.BLACK); // couleur base
            g2.fillOval(x + p, y + p, s, s); // cercle piece
            g2.setColor(new Color(255, 255, 255, 50)); // reflet
            g2.drawOval(x + p + 4, y + p + 4, s - 8, s - 8); // contour reflet
            if (Character.isUpperCase(piece)) {
                g2.setColor(redPiece ? Color.WHITE : new Color(247, 184, 68)); // couleur marque dame
                g2.setFont(new Font("SansSerif", Font.BOLD, Math.max(10, s / 3))); // font marque
                String mark = "D"; // label dame
                FontMetrics fm = g2.getFontMetrics(); // metrics texte
                int tx = x + (cell - fm.stringWidth(mark)) / 2; // x centre
                int ty = y + (cell + fm.getAscent()) / 2 - 2; // y centre
                g2.drawString(mark, tx, ty); // dessiner marque
            }
        }
    }

    private static class GradientBackgroundPanel extends JPanel { // fond applique
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

    private static class RoundedPanel extends JPanel { // panel arrondi
        private Color bg, border; private float w; private int arc; // style panel
        public RoundedPanel(Color bg, Color brd, float w, int arc) { // constructeur style
            this.bg = bg; this.border = brd; this.w = w; this.arc = arc; setOpaque(false); // init + transparent
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

    public static void main(String[] args) { // entree app
        SwingUtilities.invokeLater(() -> new game().setVisible(true)); // lancer UI EDT
    }
}