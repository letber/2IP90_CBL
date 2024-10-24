import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CustomChessVariantApp extends JFrame {

    private static final int BOARD_SIZE = 8; // Adjust for custom board sizes

    private JPanel boardPanel;
    private SquareButton[][] squares;

    public CustomChessVariantApp() {
        setTitle("Custom Chess Variant");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        boardPanel = new JPanel(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        squares = new SquareButton[BOARD_SIZE][BOARD_SIZE];

        initializeBoard();

        add(boardPanel);
        setVisible(true);
    }

    private void initializeBoard() {
        boolean isWhite = true;

        for (int row = 0; row < BOARD_SIZE; row++) {
            isWhite = !isWhite;
            for (int col = 0; col < BOARD_SIZE; col++) {
                SquareButton square = new SquareButton(row, col);

                // Set square color
                if (isWhite) {
                    square.setBackground(Color.WHITE);
                } else {
                    square.setBackground(Color.GRAY);
                }
                isWhite = !isWhite;

                // Initialize pieces for custom variant
                initializePiece(square, row, col);

                squares[row][col] = square;
                boardPanel.add(square);
            }
        }
    }

    private void initializePiece(SquareButton square, int row, int col) {
        // Example: Place custom pieces based on your variant rules
        // You can use custom icons or labels to represent pieces
        if (row == 0) {
            square.setText("R"); // Custom piece representation
        } else if (row == 1) {
            square.setText("P"); // Another custom piece
        }
        // Add more custom initialization as needed
    }

    // Inner class for squares
    private class SquareButton extends JButton {
        int row;
        int col;

        public SquareButton(int row, int col) {
            this.row = row;
            this.col = col;

            addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    onSquareClicked(row, col);
                }
            });
        }
    }

    private void onSquareClicked(int row, int col) {
        // Handle square click events here
        // Implement custom logic for selecting and moving pieces
        System.out.println("Square clicked: " + row + ", " + col);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CustomChessVariantApp();
        });
    }
}
