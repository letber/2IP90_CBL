package src;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

public class StockfishClient {
    private Process stockfishProcess;
    private BufferedReader processReader;
    private BufferedWriter processWriter;
    private static final String TEMP_DIR = System.getProperty("user.home") + "/stockfish_temp";
    public boolean gameFinished = false;

    public static void main(String[] args) {
        StockfishClient client = new StockfishClient();

        String stockfishPath = client.setupStockfishExecutable();
        if (stockfishPath != null && client.startStockfish(stockfishPath)) {
            // String fen = "r1bqkbnr/pppppppp/n7/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"; // Example position
            String fen = "3k4/2kkk3/4p3/8/8/4P3/2KKK3/3K4 w KQkq - 0 1";
            System.out.println(fen);
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
    public String setupStockfishExecutable() {
        String os = System.getProperty("os.name").toLowerCase();
        String stockfishResourcePath;

        if (os.contains("win")) {
            stockfishResourcePath = "/resources/stockfish-windows-x86-64-avx2.exe";
        } else if (os.contains("mac")) {
            stockfishResourcePath = "/resources/stockfish-macos-m1-apple-silicon";
            // stockfishResourcePath = "/resources/stockfish-macos-x86-64-bmi2";  // For Intel Macs
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            stockfishResourcePath = "/resources/stockfish-ubuntu-x86-64-avx2";
        } else {
            throw new UnsupportedOperationException("Operating system not supported");
        }

        try {
            Files.createDirectories(Paths.get(TEMP_DIR));

            Path stockfishTempPath = Paths.get(TEMP_DIR, new File(stockfishResourcePath).getName());
            InputStream inputStream = StockfishClient.class.getResourceAsStream(stockfishResourcePath);
            if (inputStream == null) {
                throw new FileNotFoundException("Stockfish binary not found in resources.");
            }

            Files.copy(inputStream, stockfishTempPath, StandardCopyOption.REPLACE_EXISTING);

            if (!os.contains("win")) {
                // stockfishTempPath.toFile().setExecutable(true);
                Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxr-xr-x");
                Files.setPosixFilePermissions(stockfishTempPath, perms);
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
            long startTime = System.currentTimeMillis();
            while ((line = processReader.readLine()) != null) {
                output.append(line).append("\n");
                // System.out.println(line);
                
                // Exit if we find the 'bestmove' response or if we exceed a timeout
                if (line.startsWith("bestmove") || System.currentTimeMillis() - startTime > 30000) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            gameFinished = true;
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
