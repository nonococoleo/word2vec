import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class Listener1 implements ActionListener {
    JTextArea textShow;
    JTextField textInput, textInput2, textInput3;
    Word2VEC vec;

    public Listener1(Word2VEC vec) {
        this.vec = vec;
    }   //训练文本

    public void setJTextField(JTextField text, JTextField text2, JTextField text3) {
        textInput = text;
        textInput2 = text2;
        textInput3 = text3;
    }   //添加输入

    public void setJTextArea(JTextArea area) {
        textShow = area;
    }   //添加输出

    public void actionPerformed(ActionEvent e) {
        textShow.setText("");   //清空原来输出的内容
        String str = textInput.getText();
        String str2 = textInput2.getText();
        String str3 = textInput3.getText();     //读取输入的文本内容转化为string格式
        if (!str.matches(".*[^a-zA-Z].*") && !str2.matches(".*[^a-zA-Z].*") && !str3.matches(".*[^a-zA-Z].*"))
            //判断输入是否为英文字符
            for (WordEntry word : vec.analogy(str.toLowerCase(), str2.toLowerCase(), str3.toLowerCase()))
                textShow.append(word.toString() + "\n");
        else
            textShow.append("please input an english word!\n");

    }
}