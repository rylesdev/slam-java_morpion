package morpion;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class MorpionApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MorpionFrame().setVisible(true);
        });
    }
}

class MorpionFrame extends JFrame {
    private static final long serialVersionUID = 1L;
	private JButton[][] buttons = new JButton[3][3];
    private String[][] board = new String[3][3];
    private String currentPlayer = "X";
    private String player1Name = "Joueur 1";
    private String player2Name = "Joueur 2";
    private boolean vsComputer = false;
    private int scorePlayer1 = 0;
    private int scorePlayer2 = 0;
    private JLabel scoreLabel;
    private JComboBox<String> themeComboBox;
    private JButton switchModeButton;
    private Color borderColor = Color.WHITE;

    public MorpionFrame() {
        setTitle("Morpion");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        player1Name = JOptionPane.showInputDialog(this, "Entrez le nom du Joueur 1:", "Joueur 1", JOptionPane.PLAIN_MESSAGE);
        player2Name = JOptionPane.showInputDialog(this, "Entrez le nom du Joueur 2:", "Joueur 2", JOptionPane.PLAIN_MESSAGE);
        if (player1Name == null || player1Name.trim().isEmpty()) player1Name = "Joueur 1";
        if (player2Name == null || player2Name.trim().isEmpty()) player2Name = "Joueur 2";

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel gamePanel = new JPanel(new GridLayout(3, 3));
        JPanel controlPanel = new JPanel(new FlowLayout());

        scoreLabel = new JLabel("Score: " + player1Name + " " + scorePlayer1 + " - " + player2Name + " " + scorePlayer2);
        controlPanel.add(scoreLabel);

        JButton newGameButton = new JButton("Nouvelle Partie");
        newGameButton.addActionListener(e -> resetGame());
        controlPanel.add(newGameButton);

        JButton historyButton = new JButton("Historique des Parties");
        historyButton.addActionListener(e -> showHistory());
        controlPanel.add(historyButton);

        JButton clearHistoryButton = new JButton("Effacer l'historique");
        clearHistoryButton.addActionListener(e -> clearHistory());
        controlPanel.add(clearHistoryButton);

        switchModeButton = new JButton("Mode: 2 Joueurs");
        switchModeButton.addActionListener(e -> toggleMode());
        controlPanel.add(switchModeButton);

        themeComboBox = new JComboBox<>(new String[]{"Sombre", "Aléatoire"});
        themeComboBox.addActionListener(e -> changeTheme());
        controlPanel.add(themeComboBox);

        mainPanel.add(controlPanel, BorderLayout.NORTH);
        mainPanel.add(gamePanel, BorderLayout.CENTER);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new CustomButton();
                buttons[i][j].setFont(new Font("Arial", Font.PLAIN, 40));
                buttons[i][j].setOpaque(true);
                buttons[i][j].setBorderPainted(false);
                buttons[i][j].addActionListener(new ButtonClickListener(i, j));
                gamePanel.add(buttons[i][j]);
            }
        }

        add(mainPanel);
        resetGame();
        changeTheme();
    }

    private void resetGame() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
                board[i][j] = "";
            }
        }
        currentPlayer = "X";
        scoreLabel.setText("Score: " + player1Name + " " + scorePlayer1 + " - " + player2Name + " " + scorePlayer2);
    }

    private void showHistory() {
        try (BufferedReader reader = new BufferedReader(new FileReader("history.txt"))) {
            StringBuilder history = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                history.append(line).append("\n");
            }
            JOptionPane.showMessageDialog(this, history.toString(), "Historique des Parties", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Aucun historique disponible.", "Historique des Parties", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void clearHistory() {
        int confirm = JOptionPane.showConfirmDialog(this, "Êtes-vous sûr de vouloir effacer l'historique ?", "Effacer l'historique", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("history.txt"))) {
                writer.write("");
                JOptionPane.showMessageDialog(this, "Historique effacé avec succès.", "Effacer l'historique", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'effacement de l'historique.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void toggleMode() {
        vsComputer = !vsComputer;
        if (vsComputer) {
            switchModeButton.setText("Mode: Joueur vs Ordinateur");
            player2Name = "Ordinateur";
        } else {
            switchModeButton.setText("Mode: 2 Joueurs");
            player2Name = "Joueur 2";
        }
        resetGame();
    }

    private void changeTheme() {
        String selectedTheme = (String) themeComboBox.getSelectedItem();
        if (selectedTheme.equals("Sombre")) {
            getContentPane().setBackground(Color.DARK_GRAY);
            borderColor = Color.WHITE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    buttons[i][j].setBackground(Color.DARK_GRAY);
                    buttons[i][j].setForeground(Color.WHITE);
                    ((CustomButton) buttons[i][j]).setBorderColor(borderColor);
                }
            }
        } else {
            Random rand = new Random();
            Color randomColor = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
            getContentPane().setBackground(randomColor);
            borderColor = Color.BLACK;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    buttons[i][j].setBackground(randomColor);
                    buttons[i][j].setForeground(Color.BLACK);
                    ((CustomButton) buttons[i][j]).setBorderColor(borderColor);
                }
            }
        }
        repaint();
    }

    private void checkWin() {
        for (int i = 0; i < 3; i++) {
            if (board[i][0].equals(currentPlayer) && board[i][1].equals(currentPlayer) && board[i][2].equals(currentPlayer)) {
                announceWin();
                return;
            }
            if (board[0][i].equals(currentPlayer) && board[1][i].equals(currentPlayer) && board[2][i].equals(currentPlayer)) {
                announceWin();
                return;
            }
        }
        if (board[0][0].equals(currentPlayer) && board[1][1].equals(currentPlayer) && board[2][2].equals(currentPlayer)) {
            announceWin();
            return;
        }
        if (board[0][2].equals(currentPlayer) && board[1][1].equals(currentPlayer) && board[2][0].equals(currentPlayer)) {
            announceWin();
            return;
        }
        if (isBoardFull()) {
            JOptionPane.showMessageDialog(this, "Match nul!", "Fin de la partie", JOptionPane.INFORMATION_MESSAGE);
            resetGame();
        }
    }

    private boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j].isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    private void announceWin() {
        String winnerName = currentPlayer.equals("X") ? player1Name : player2Name;
        JOptionPane.showMessageDialog(this, winnerName + " a gagné!", "Fin de la partie", JOptionPane.INFORMATION_MESSAGE);
        if (currentPlayer.equals("X")) {
            scorePlayer1++;
        } else {
            scorePlayer2++;
        }
        saveGameResult(winnerName);
        resetGame();
    }

    private void saveGameResult(String winnerName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("history.txt", true))) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String date = dateFormat.format(new Date());
            writer.write(date + " - " + player1Name + " vs " + player2Name + " - Gagnant: " + winnerName + "\n");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de l'enregistrement de la partie.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private class ButtonClickListener implements ActionListener {
        private int row, col;

        public ButtonClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (buttons[row][col].getText().isEmpty()) {
                buttons[row][col].setText(currentPlayer);
                board[row][col] = currentPlayer;
                checkWin();
                currentPlayer = currentPlayer.equals("X") ? "O" : "X";
                if (vsComputer && currentPlayer.equals("O")) {
                    computerMove();
                }
            }
        }
    }

    private void computerMove() {
        Random rand = new Random();
        int row, col;
        do {
            row = rand.nextInt(3);
            col = rand.nextInt(3);
        } while (!board[row][col].isEmpty());
        buttons[row][col].setText("O");
        board[row][col] = "O";
        checkWin();
        currentPlayer = "X";
    }

    private class CustomButton extends JButton {
        private static final long serialVersionUID = 1L;
		private Color borderColor;

        public CustomButton() {
            setContentAreaFilled(false);
        }

        public void setBorderColor(Color borderColor) {
            this.borderColor = borderColor;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(borderColor);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        }
    }
}
