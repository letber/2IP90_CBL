import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;

import java.awt.*;
import java.awt.event.*;
import java.io.Console;
import java.util.Map;

public class AutoChess extends JFrame {

    private static final int BOARD_SIZE = 8; // Adjust for custom board sizes

    private JPanel chessBoard;
    private SelectPanel selectPanel;
    private SquareButton[][] squares;
    private String turn = "white";
    private SelectFigureButton selectedButton;
    private boolean kingsPlaced = false;
    private Color grayYellowColor = new Color(190, 190, 100);
    private Color lightYellowColor = new Color(255, 255, 127);

    public AutoChess() {
        setTitle("AutoChess");
        setSize(1200, 600);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        chessBoard = new JPanel(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        selectPanel = new SelectPanel();

        setLayout(new GridLayout(1, 2)); // 1 row, 2 columns

        squares = new SquareButton[BOARD_SIZE][BOARD_SIZE];

        initializeBoard();
        
        add(chessBoard);
        add(selectPanel);
        setVisible(true);

        PlaceFigures();
        
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
                    square.defaultColor = Color.WHITE;
                } else {
                    square.setBackground(Color.GRAY);
                    square.defaultColor = Color.GRAY;
                }
                isWhite = !isWhite;

                squares[row][col] = square;
                chessBoard.add(square);

                square.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (selectedButton != null) {
                            /////////////////
                            System.out.println("Square clicked: " + square.row + ", " + square.col);
                            /////////////////
                            square.setIcon(new ImageIcon("smallFigures/" + selectedButton.figurePath));
                            selectPanel.UpdateLabels(selectedButton.color, selectedButton.price);                           
                            selectedButton.DeselectFigure();

                            
                            if (turn == "white") {
                                PlayTurnBlack();
                                turn = "black";
                                if (selectPanel.white_score == 0 && selectPanel.black_score == 0){
                                    kingsPlaced = true;
                                    turn = "";
                                }
                            } else if (turn == "black") {
                                PlayTurnWhite();
                                turn = "white";
                            } else {
                                System.out.println("Launching Stockfish");
                                selectPanel.DisableBlackFigures();
                                selectedButton = null;
                                EnableAllSquares(true);
                                // LaunchStockfish();
                            }
                        }
                    };
                });
            }
        }

            for (Map.Entry<String, SelectFigureButton> entry : selectPanel.Figures.entrySet()) {
                SelectFigureButton figure = entry.getValue();
                figure.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        selectedButton = figure;
                        EnableAllSquares(false);

                        if (selectedButton.figure.contains("pawn")){
                            if (selectedButton.color.contains("w")){
                                EnableWhiteSquares(3);
                            } else if (selectedButton.color.contains("b")){
                                EnableBlackSquares(3);
                            }
                        } else {
                            if (selectedButton.color.contains("w")){
                                EnableWhiteSquares(2);
                            } else if (selectedButton.color.contains("b")){
                                EnableBlackSquares(2);
                            }
                        }

                        // if (turn == "white") {
                        //     PlayTurnBlack();
                        //     turn = "black";
                        // } else if (turn == "black"){
                        //     PlayTurnWhite();
                        //     turn = "white";
                        // } else {
                        //     // LaunchStockfish();
                        // }
                    }
                });
            }
    }

    private void PlaceFigures(){
        selectPanel.DisableBlackFigures();
        selectPanel.DisableWhiteFigures();
        EnableAllSquares(false);
        PlayTurnWhite();
    }


    private void PlayTurnWhite(){
        selectPanel.EnableWhiteFigures();
        selectPanel.DisableBlackFigures();
    }

    private void PlayTurnBlack(){
        selectPanel.EnableBlackFigures();
        selectPanel.DisableWhiteFigures();
    }

    private void EnableAllSquares(boolean mode){
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                SquareButton square = squares[row][col];
                square.setEnabled(mode);
                square.setBackground(square.defaultColor);
            }
        }
    }

    private void EnableWhiteSquares(int rows){
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                SquareButton square = squares[row][col];
                square.setEnabled(true);

                if (square.defaultColor == Color.WHITE){
                    square.setBackground(lightYellowColor);
                } else {
                    square.setBackground(grayYellowColor);
                }
            }
        }
    }

    private void EnableBlackSquares(int rows){
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                SquareButton square = squares[BOARD_SIZE - 1 - row][col];
                square.setEnabled(true);

                if (square.defaultColor == Color.WHITE){
                    square.setBackground(lightYellowColor);
                } else {
                    square.setBackground(grayYellowColor);
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AutoChess();
        });
    }
}
