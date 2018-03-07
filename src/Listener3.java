import java.awt.event.*;
import javax.swing.*;

public class Listener3 implements ActionListener{
    JTextArea textShow;
    JTextField textInput;
    Word2VEC vec;

    public Listener3(Word2VEC vec) {
        this.vec = vec;
    }

    public void setJTextField(JTextField text) {
        textInput = text;
    }

    public void setJTextArea(JTextArea area) {
        textShow = area;
    }

    public void actionPerformed(ActionEvent e) {
        textShow.setText("");
        String str = textInput.getText();
        if(!str.matches(".*[^a-zA-Z].*"))
            this.vec.Show2D(str.toLowerCase());
        else
            textShow.append("please input an english word!\n");
    }
}