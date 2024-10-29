package src;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.Console;
import java.util.ArrayList;
import java.util.Map;
import java.util.Collections;


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
    private String[][] FenBoard = {
        {"", "", "", "", "", "", "", ""},
        {"", "", "", "", "", "", "", ""},
        {"", "", "", "", "", "", "", ""},
        {"", "", "", "", "", "", "", ""},
        {"", "", "", "", "", "", "", ""},
        {"", "", "", "", "", "", "", ""},
        {"", "", "", "", "", "", "", ""},
        {"", "", "", "", "", "", "", ""},
    };
    private String movedFigure = null;
    private JButton StartGame = new JButton("Start Game");
    private SquareButton movedFromSquare = null;
    private SquareButton movedToSquare = null;
    private SquareButton lastMoveFromButton = null;
    private SquareButton lastMoveToButton = null;
    private ArrayList<String> playedFens = new ArrayList<String>();
    private String endingLabel = "";
    private JLabel endingLabelElement = new JLabel();
    private StockfishClient stockfish = new StockfishClient();
    private String stockfishPath = stockfish.setupStockfishExecutable();
    private JButton restart = new JButton("Restart");
    private String lastTurn = "";
    private SelectFigureButton lastSelectedButton = null;

    public AutoChess() {
        setTitle("AutoChess");
        setSize(1200, 600);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        chessBoard = new JPanel(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        selectPanel = new SelectPanel();
        selectPanel.add(endingLabelElement);

        setLayout(new GridLayout(1, 2)); // 1 row, 2 columns

        squares = new SquareButton[BOARD_SIZE][BOARD_SIZE];

        initializeBoard();
        
        add(chessBoard);
        add(selectPanel);
        setVisible(true);

        PlaceFigures();
        
        EndGame();
    }

    private void initializeBoard() {
        boolean isWhite = true;

        StartGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                LaunchStockfish();
                StartGame.setEnabled(false);
            }
        });
        selectPanel.add(StartGame);
        StartGame.setEnabled(false);


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
                        if (selectedButton != null && FenBoard[square.row][square.col] == "") {
                            /////////////////
                            // System.out.println("Square clicked: " + square.row + ", " + square.col);
                            /////////////////
                            square.setIcon(new ImageIcon("smallFigures/" + selectedButton.figurePath));
                            FenBoard[square.row][square.col] = selectedButton.getFenString();
                            selectPanel.UpdateLabels(selectedButton.color, selectedButton.price);                           
                            selectedButton.DeselectFigure();
                            lastTurn = turn;
                            
                            if (turn == "white") {
                                if (containsFigure(FenBoard, "k") && containsFigure(FenBoard, "K")){
                                    kingsPlaced = true;
                                    turn = "";
                                    // System.out.println("Launching Stockfish");
                                    selectPanel.DisableBlackFigures();
                                    selectedButton = null;
                                    EnableAllSquares(true);
                                    StartGame.setEnabled(true);
                                } else if (containsFigure(FenBoard, "k")  && !containsFigure(FenBoard, "K")){
                                    turn = "white";
                                    PlayTurnWhite();
                                } else {
                                    turn = "black";
                                    PlayTurnBlack();
                                }
                            } else if (turn == "black") {
                                if (containsFigure(FenBoard, "k") && containsFigure(FenBoard, "K")){
                                    kingsPlaced = true;
                                    turn = "";
                                    // System.out.println("Launching Stockfish");
                                    selectPanel.DisableBlackFigures();
                                    selectedButton = null;
                                    EnableAllSquares(true);
                                    StartGame.setEnabled(true);
                                } else if (containsFigure(FenBoard, "K") && !containsFigure(FenBoard, "k")){
                                    turn = "black";
                                    PlayTurnBlack();
                                } else {
                                    turn = "white";
                                    PlayTurnWhite();
                                }
                                
                            } else {
                                // System.out.println("Launching Stockfish");
                                selectPanel.DisableBlackFigures();
                                selectedButton = null;
                                EnableAllSquares(true);
                                StartGame.setEnabled(true);
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
                        if (lastSelectedButton != null){
                        lastSelectedButton.DeselectFigure();
                        }
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
                        lastSelectedButton = selectedButton;
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
        if (lastTurn.equals("black")){
            selectPanel.DisableBlackFigures();
        } else if (lastTurn.equals("white")){
            selectPanel.DisableWhiteFigures();
        }
        selectPanel.EnableWhiteFigures();
    }

    private void PlayTurnBlack(){
        if (lastTurn.equals("black")){
            selectPanel.DisableBlackFigures();
        } else if (lastTurn.equals("white")){
            selectPanel.DisableWhiteFigures();
        }
        selectPanel.EnableBlackFigures();
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

    private void EnableBlackSquares(int rows){
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

    private void EnableWhiteSquares(int rows){
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

    private void LaunchStockfish(){
        new Thread(() -> {
            turn = "w";
            // StockfishClient stockfish = new StockfishClient();
            // String stockfishPath = stockfish.setupStockfishExecutable();

            while (!stockfish.gameFinished){
                var fenb = FenBoard;
                String fen = ChessFenConverter.toFEN(FenBoard, turn);
                playedFens.add(fen);
                int occurence = Collections.frequency(playedFens, fen);
                if (occurence >= 3){
                    // System.out.println("Draw by repetition");
                    endingLabel = "Draw by repetition";
                    break;
                }
                // System.out.println("FEN: " + fen);

                if (stockfishPath != null && stockfish.startStockfish(stockfishPath)) {
                    String bestMove = stockfish.getBestMove(fen, 1000);
                    if (bestMove == null){
                        // System.out.println("Draw by insufficient material");
                        endingLabel = "Game Over: Chekmate by " + turn;
                        break;
                    }

                    // System.out.println("Best move: " + bestMove);
                    String[] move = bestMove.split("");

                    int fromRow = Integer.parseInt(move[1]) - 1;
                    int fromCol = move[0].charAt(0) - 'a';
                    int toRow = Integer.parseInt(move[3]) - 1;
                    int toCol = move[2].charAt(0) - 'a';
                    // System.out.println("From: " + fromRow + ", " + fromCol);
                    // System.out.println("To: " + toRow + ", " + toCol);
                    // System.out.println("Moved figure: " + FenBoard[7 - fromRow][fromCol]);
                    movedFigure = ChessFenConverter.FigureFENtoString(FenBoard[7 - fromRow][fromCol]);
                    movedFromSquare = squares[7 - fromRow][fromCol];
                    movedToSquare  = squares[7 - toRow][toCol];

                    SwingUtilities.invokeLater(() -> {
                        if (lastMoveFromButton != null && lastMoveToButton != null){
                            lastMoveFromButton.setBackground(lastMoveFromButton.defaultColor);
                            lastMoveToButton.setBackground(lastMoveToButton.defaultColor);
                        }

                        movedFromSquare.setIcon(new ImageIcon());
                        movedToSquare.setIcon(new ImageIcon("smallFigures/" + movedFigure + ".png"));

                        if (movedFromSquare.defaultColor == Color.WHITE){
                            movedFromSquare.setBackground(lightYellowColor);
                        } else {
                            movedFromSquare.setBackground(grayYellowColor);
                        }
                        if (movedToSquare.defaultColor == Color.WHITE){
                            movedToSquare.setBackground(lightYellowColor);
                        } else {
                            movedToSquare.setBackground(grayYellowColor);
                        }
                        lastMoveFromButton = movedFromSquare;
                        lastMoveToButton = movedToSquare;
                    });

                    FenBoard[7 - toRow][toCol] = FenBoard[7 - fromRow][fromCol];
                    FenBoard[7 - fromRow][fromCol] = "";

                    if (turn.contains("w")){
                        turn = "b";
                    } else {
                        turn = "w";
                    }
                }
            }
            selectPanel.add(restart);
            endingLabelElement.setText(endingLabel);
            EndGame();
        }).start();

        // selectPanel.UpdateLabels("black", 0);
        // selectPanel.UpdateLabels("white", 0);
        // PlaceFigures();
    }

    private void EndGame(){
        restart.setEnabled(true);

        restart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectPanel.UpdateLabels("w", -35);
                // selectPanel.UpdateLabels("b", -35);
                stockfish.gameFinished = false;

                for (int row = 0; row < BOARD_SIZE; row++) {
                    for (int col = 0; col < BOARD_SIZE; col++) {
                        SquareButton square = squares[row][col];
                        square.setIcon(new ImageIcon());
                        square.setBackground(square.defaultColor);
                    }
                }

                turn = "white";
                selectedButton = null;
                kingsPlaced = false;
                FenBoard = new String[][] {
                    {"", "", "", "", "", "", "", ""},
                    {"", "", "", "", "", "", "", ""},
                    {"", "", "", "", "", "", "", ""},
                    {"", "", "", "", "", "", "", ""},
                    {"", "", "", "", "", "", "", ""},
                    {"", "", "", "", "", "", "", ""},
                    {"", "", "", "", "", "", "", ""},
                    {"", "", "", "", "", "", "", ""},
                };
                movedFigure = null;
                movedFromSquare = null;
                movedToSquare = null;
                lastMoveFromButton = null;
                lastMoveToButton = null;
                playedFens = new ArrayList<String>();
                endingLabel = "";
                lastTurn = "";
                restart.setEnabled(false);
                endingLabelElement.setText("");
                selectPanel.EnableWhiteFigures();
            }
        });
        
    }

    public static boolean containsFigure(String[][] jaggedArray, String target) {
        for (String[] subArray : jaggedArray) {
            for (String element : subArray) {
                if (element.equals(target)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AutoChess();
        });
    }
}
