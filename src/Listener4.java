import java.awt.event.*;
import java.util.Map;
import javax.swing.*;


public class Listener4 implements ActionListener {
    JTextField textInput, textInput2, textInput3;
    JTextArea textShow;
    Word2VEC vec;

    public Listener4(Word2VEC vec) {
        this.vec = vec;
    }

    public void setJTextField(JTextField text, JTextField text2, JTextField text3) {
        textInput = text;
        textInput2 = text2;
        textInput3 = text3;
    }

    public void setJTextArea(JTextArea area) {
        textShow = area;
    }

    public void actionPerformed(ActionEvent e) {
        int a, b, c;
        textShow.setText("");
        String str = textInput.getText();
        String str2 = textInput2.getText();
        String str3 = textInput3.getText();
        a = Integer.valueOf(str);
        b = Integer.valueOf(str2);
        c = Integer.valueOf(str3);
        try{
            a = Integer.parseInt(str);
            b = Integer.parseInt(str2);
            c = Integer.parseInt(str3);  //从textInput中读取参数
        }catch(NumberFormatException w)
        {
            textShow.append("please input number\n");
        }
        Classes[] explain = this.vec.keymeans(a, c);    //设置输出的类数量，迭代次数
        for (int i = 0; i < explain.length; i++) {
            textShow.append("--------" + i + "---------\n");
            for (Map.Entry<String, Double> word : explain[i].getTop(b)) {   //设置每个类的词数量
                textShow.append(word.toString() + "\n");
            }
        }
    }
}