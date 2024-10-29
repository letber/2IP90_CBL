import java.util.HashMap;

public class ChessFenConverter {
    private static HashMap<String, String> figureFenMap = new HashMap<String, String>() {
        {
            put("P", "w_pawn");
            put("N", "w_night");
            put("B", "w_bishop");
            put("R", "w_rook");
            put("Q", "w_queen");
            put("K", "w_king");
            put("p", "b_pawn");
            put("n", "b_night");
            put("b", "b_bishop");
            put("r", "b_rook");
            put("q", "b_queen");
            put("k", "b_king");
        }
    };


    public static String toFEN(String[][] board, String color) {
        StringBuilder fen = new StringBuilder();
        
        for (int i = 0; i < board.length; i++) {
            int emptyCount = 0;
            for (int j = 0; j < board[i].length; j++) {
                String cell = board[i][j];
                
                if (cell == null || cell.isEmpty()) {
                    emptyCount++;
                } else {
                    if (emptyCount > 0) {
                        fen.append(emptyCount);
                        emptyCount = 0;
                    }
                    fen.append(cell);
                }
            }
            
            if (emptyCount > 0) {
                fen.append(emptyCount);
            }
            
            if (i < board.length - 1) {
                fen.append("/");
            }
        }
        
        fen.append(" " + color +  " - - 0 1");
        
        return fen.toString();
    }

    public static String FigureFENtoString(String figureFen){
        return figureFenMap.get(figureFen);
    }

    public static void main(String[] args) {
        String[][] board = {
            {"r", "n", "b", "q", "k", "b", "n", "r"},
            {"p", "p", "p", "p", "p", "p", "p", "p"},
            {"", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", ""},
            {"P", "P", "P", "P", "P", "P", "P", "P"},
            {"R", "N", "B", "Q", "K", "B", "N", "R"}
        };
    }
}
