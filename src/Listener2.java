import java.awt.event.*;
import javax.swing.*;

public class Listener2 implements ActionListener {
    JTextArea textShow;
    JTextField textInput;
    Word2VEC vec;

    public Listener2(Word2VEC vec) {
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
        if (!str.matches(".*[^a-zA-Z].*"))
            for (WordEntry word : vec.distance(str.toLowerCase()))
                textShow.append(word.toString() + "\n");
        else
            textShow.append("please input an english word!\n");

    }
}