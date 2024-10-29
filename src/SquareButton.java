package src;
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

    }
}