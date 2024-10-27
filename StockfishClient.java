import java.io.*;
import java.nio.file.*;

public class StockfishClient {
    private Process stockfishProcess;
    private BufferedReader processReader;
    private BufferedWriter processWriter;
    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");

    public static void main(String[] args) {
        StockfishClient client = new StockfishClient();

        String stockfishPath = client.setupStockfishExecutable();
        if (stockfishPath != null && client.startStockfish(stockfishPath)) {
            // Example: Get best move for a given position in FEN notation
            String fen = "r1bqkbnr/pppppppp/n7/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"; // Example position
            String bestMove = client.getBestMove(fen, 1000); // search for 1000 ms
            System.out.println("Best Move: " + bestMove);

            client.stopStockfish();
        } else {
            System.out.println("Failed to start Stockfish.");
        }
    }

    // Start the Stockfish engine
    public boolean startStockfish(String stockfishPath) {
        try {
            stockfishProcess = new ProcessBuilder(stockfishPath).start();
            processReader = new BufferedReader(new InputStreamReader(stockfishProcess.getInputStream()));
            processWriter = new BufferedWriter(new OutputStreamWriter(stockfishProcess.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // Determine the Stockfish path based on the operating system and setup executable
    private String setupStockfishExecutable() {
        String os = System.getProperty("os.name").toLowerCase();
        String stockfishResourcePath;

        if (os.contains("win")) {
            stockfishResourcePath = "/resources/stockfish_windows.exe";
        } else if (os.contains("mac")) {
            stockfishResourcePath = "/resources/stockfish_mac";
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            stockfishResourcePath = "/resources/stockfish_linux";
        } else {
            throw new UnsupportedOperationException("Operating system not supported");
        }

        try {
            // Copy the Stockfish binary to a temp directory
            Path stockfishTempPath = Paths.get(TEMP_DIR, new File(stockfishResourcePath).getName());
            InputStream inputStream = StockfishClient.class.getResourceAsStream(stockfishResourcePath);
            if (inputStream == null) {
                throw new FileNotFoundException("Stockfish binary not found in resources.");
            }

            Files.copy(inputStream, stockfishTempPath, StandardCopyOption.REPLACE_EXISTING);

            // Make the file executable on macOS and Linux
            if (!os.contains("win")) {
                stockfishTempPath.toFile().setExecutable(true);
            }

            return stockfishTempPath.toString();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Send a command to Stockfish
    public void sendCommand(String command) {
        try {
            processWriter.write(command + "\n");
            processWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Read Stockfish's output
    public String getOutput() {
        StringBuilder output = new StringBuilder();
        try {
            String line;
            while ((line = processReader.readLine()) != null && !line.equals("readyok")) {
                output.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output.toString();
    }

    // Stop the Stockfish engine
    public void stopStockfish() {
        sendCommand("quit");
        try {
            processReader.close();
            processWriter.close();
            stockfishProcess.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Get the best move for a position in FEN format
    public String getBestMove(String fen, int searchTime) {
        sendCommand("position fen " + fen);
        sendCommand("go movetime " + searchTime);
        return parseBestMove(getOutput());
    }

    // Parse the best move from Stockfish's output
    private String parseBestMove(String output) {
        for (String line : output.split("\n")) {
            if (line.startsWith("bestmove")) {
                return line.split(" ")[1];
            }
        }
        return null;
    }
}
