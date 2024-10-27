import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SquareButton extends JButton {
    int row;
    int col;
    Color defaultColor;

    public SquareButton(int row, int col) {
        this.row = row;
        this.col = col;

        addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onSquareClicked(row, col);
            }
        });
    }

    private void onSquareClicked(int row, int col) {
        // Handle square click events here
        // Implement custom logic for selecting and moving pieces
        System.out.println("Square clicked: " + row + ", " + col);
    }
}