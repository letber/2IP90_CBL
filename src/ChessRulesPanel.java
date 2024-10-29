package src;
import javax.swing.*;
import java.awt.*;

public class ChessRulesPanel extends JPanel {
    public ChessRulesPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        JLabel titleLabel = createLabel("AUTOMATE RULES", 24);
        JLabel introLabel = createLabel("In Automate Chess, you and your opponent both set up pieces \n"
                                        + "and then watch as the computer plays best moves to the end.", 16);
        JLabel howToPlayLabel = createLabel("HOW TO PLAY", 20);
        JLabel pointsLabel = createLabel("Each player has 35 points to spend on pieces:", 16);
        
        JLabel queenLabel = createLabel("- Queen = 7", 16);
        JLabel rookLabel = createLabel("- Rook = 4", 16);
        JLabel bishopLabel = createLabel("- Bishop = 3", 16);
        JLabel knightLabel = createLabel("- Knight = 3", 16);
        JLabel pawnLabel = createLabel("- Pawn = 1", 16);
        JLabel kingLabel = createLabel("- King = Not counted toward score", 16);
        
        JLabel placementRulesLabel = createLabel("Pawns must be placed first. At least 6 but no more than 12 pawns must be used.\n"
                + "Pawns may be placed at 2nd or 3rd rank.\n"
                + "Pieces may be placed at 1st or 2nd rank.\n"
                + "The king is placed last. The game starts when the last player places his king.", 16);

        add(titleLabel);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(introLabel);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(howToPlayLabel);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(pointsLabel);
        add(queenLabel);
        add(rookLabel);
        add(bishopLabel);
        add(knightLabel);
        add(pawnLabel);
        add(kingLabel);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(placementRulesLabel);
    }
    
    private JLabel createLabel(String text, int fontSize) {
        JLabel label = new JLabel("<html>" + text.replaceAll("\n", "<br>") + "</html>");
        label.setFont(new Font("Arial", Font.PLAIN, fontSize));
        return label;
    }
    
    public static void main(String[] args) {
        JFrame frame = new JFrame("Chess Rules");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        
        ChessRulesPanel rulesPanel = new ChessRulesPanel();
        frame.add(rulesPanel);
        
        frame.setVisible(true);
    }
}
