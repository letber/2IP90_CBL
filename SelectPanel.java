import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class SelectPanel extends JPanel {
    public int white_score = 10;
    public int black_score = 10;
    public HashMap<String, SelectFigureButton> Figures = new HashMap<String,SelectFigureButton>(){    };
    private Dimension ButtonSize = new Dimension(50, 50);
    private JLabel whiteScoreLabel = new JLabel("White Score: " + white_score);
    private JLabel blackScoreLabel = new JLabel("Black Score: " + black_score);

    public SelectPanel() {
        // Frame setup
        setSize(600, 600);
        // setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // setLocationRelativeTo(null);

        // Main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Create top and bottom button panels with margin
        JPanel topButtonPanel = new JPanel();
        topButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Add margin

        AddFigure("w_king.png", topButtonPanel, 0);
        AddFigure("w_queen.png", topButtonPanel, 7);
        AddFigure("w_rook.png", topButtonPanel, 5);
        AddFigure("w_bishop.png", topButtonPanel, 3);
        AddFigure("w_night.png", topButtonPanel, 3);
        AddFigure("w_pawn.png", topButtonPanel, 1);


        JPanel bottomButtonPanel = new JPanel();
        bottomButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Add margin

        AddFigure("b_king.png", bottomButtonPanel, 0);
        AddFigure("b_queen.png", bottomButtonPanel, 7);
        AddFigure("b_rook.png", bottomButtonPanel, 5);
        AddFigure("b_bishop.png", bottomButtonPanel, 3);
        AddFigure("b_night.png", bottomButtonPanel, 3);
        AddFigure("b_pawn.png", bottomButtonPanel, 1);


        // Add top and bottom button panels to the main panel
        mainPanel.add(topButtonPanel, BorderLayout.NORTH);  // Top button
        mainPanel.add(bottomButtonPanel, BorderLayout.SOUTH);  // Bottom button

        // Optionally, add some content in the center
        JPanel centerPanel = new JPanel();
        centerPanel.add(whiteScoreLabel);
        centerPanel.add(blackScoreLabel);
        centerPanel.setBackground(Color.LIGHT_GRAY);
        mainPanel.add(centerPanel, BorderLayout.CENTER);  // Center panel for additional content

        // Add main panel to the frame
        add(mainPanel);
    }

    public void UpdateLabels(String color, int price) {
        if (color.contains("w")) {
            white_score -= price;
            whiteScoreLabel.setText("White Score: " + white_score);
        } else if (color.contains("b")) {
            black_score -= price;
            blackScoreLabel.setText("Black Score: " + black_score);
        }
    }

    private void AddFigure(String path, JPanel Panel, int score) {
        SelectFigureButton Button = new SelectFigureButton(score, path);
        Button.setPreferredSize(ButtonSize);
        Button.setIcon(new ImageIcon("smallFigures/" + path));
        Panel.add(Button);
        this.Figures.put(path, Button);
    }

    public void DisableBlackFigures(){
        for (Map.Entry<String, SelectFigureButton> entry : Figures.entrySet()) {
            if(entry.getKey().split("_")[0].contains("b")){
                entry.getValue().setEnabled(false);
            }
        }
    }

    public void DisableWhiteFigures(){
        for (Map.Entry<String, SelectFigureButton> entry : Figures.entrySet()) {
            if(entry.getKey().split("_")[0].contains("w")){
                entry.getValue().setEnabled(false);
            }
        }
    }

    public void EnableBlackFigures(){
        for (Map.Entry<String, SelectFigureButton> entry : Figures.entrySet()) {
            if(entry.getKey().split("_")[0].contains("b")){
                SelectFigureButton figure = entry.getValue();
                if (black_score > 29 && figure.figure.contains("pawn")) {
                    figure.setEnabled(true);
                } else if (black_score < 30 && black_score > 0 && figure.price <= black_score && !figure.figure.contains("king")) {
                    figure.setEnabled(true);
                }
                else if (figure.figure.contains("king") && black_score == 0){
                    figure.setEnabled(true);
                }
            }
        }
    }

    public void EnableWhiteFigures(){
        for (Map.Entry<String, SelectFigureButton> entry : Figures.entrySet()) {
            if(entry.getKey().split("_")[0].contains("w")){
                SelectFigureButton figure = entry.getValue();
                if (white_score > 29 && figure.figure.contains("pawn")) {
                    figure.setEnabled(true);
                } else if (white_score < 30 && white_score > 0 && figure.price <= white_score && !figure.figure.contains("king")) {
                    figure.setEnabled(true);
                }
                else if (figure.figure.contains("king") && white_score == 0){
                    figure.setEnabled(true);
                }
            }
        }
    }
}