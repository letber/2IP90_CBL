public class ChessFenConverter {
    public static String toFEN(String[][] board) {
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
        
        fen.append(" w KQkq - 0 1");
        
        return fen.toString();
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
        
        String fen = toFEN(board);
        System.out.println("FEN Position: " + fen);
    }
}
