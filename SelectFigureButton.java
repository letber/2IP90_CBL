import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SelectFigureButton extends JButton {
    public int price;
    public boolean selected = false;
    public String figure;
    public String color;
    public String figurePath;
    
    public SelectFigureButton(int price, String figurePath) {
        this.price = price;
        this.color = figurePath.split("_")[0];
        this.figure = figurePath.split("_")[1];
        this.figurePath = figurePath;
    
        addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onSquareClicked();
            }
        });
    }

    public void onSquareClicked() {
        SelectFigureButton();
    }

    private void SelectFigureButton() {
        // System.out.println("Figure selected: " + price);
        selected = true;
        setBackground(Color.GREEN);
    }

    public void DeselectFigure() {
        selected = false;
        setBackground(null);
    }

    public String getFenString() {
        String firsString = figure.split("")[0];
        if (color.equals("w")) {
            return firsString.toUpperCase();
        } else {
            return firsString;
        }
    }
}
