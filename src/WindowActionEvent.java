import javax.swing.*;
import java.awt.*;

public class WindowActionEvent extends JFrame {
    JTextField inputText1_1, inputText1_2, inputText1_3, inputText2_1,inputText3_1, inputText3_2, inputText3_3;
    JTextArea textShow1_1, textShow2_1, textShow3_1;
    JButton button1_1, button2_1, button2_2, button3_1;
    //设置输入输出文本框以及按钮

    public WindowActionEvent() {
        init1();
        init2();
        init3();    //三个展示项目
        setVisible(true);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    void init1() {
        setLayout(new FlowLayout());
        inputText1_1 = new JTextField(8);
        inputText1_2 = new JTextField(8);
        inputText1_3 = new JTextField(8);   //设置输入区域
        button1_1 = new JButton("confirm");     //设置按钮
        textShow1_1 = new JTextArea(9, 34);     //设置输出区域
        JScrollPane text1=new JScrollPane(textShow1_1);
        text1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); //  设置滚动条
        add(inputText1_1);
        add(inputText1_2);
        add(inputText1_3);
        add(button1_1);
        add(text1);     //加入各个组件
    }

    void init2() {
        inputText2_1 = new JTextField(8);
        button2_1 = new JButton("confirm");
        button2_2 = new JButton("draw");
        textShow2_1 = new JTextArea(9, 34);
        JScrollPane text2=new JScrollPane(textShow2_1);
        text2.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(inputText2_1);
        add(button2_1);
        add(button2_2);
        add(text2);
    }

    void init3() {
        inputText3_1 = new JTextField(8);
        inputText3_2 = new JTextField(8);
        inputText3_3 = new JTextField(8);
        textShow3_1 = new JTextArea(9, 34);
        button3_1 = new JButton("confirm");
        JScrollPane text3=new JScrollPane(textShow3_1);
        text3.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(inputText3_1);
        add(inputText3_2);
        add(inputText3_3);
        add(button3_1);
        add(text3);
    }

    void setMyCommandListener(Word2VEC vec) {
        Listener1 listener = new Listener1(vec);
        Listener2 listener2 = new Listener2(vec);
        Listener3 listener3 = new Listener3(vec);
        Listener4 listener4 = new Listener4(vec);

        listener.setJTextArea(textShow1_1);
        listener.setJTextField(inputText1_1, inputText1_2, inputText1_3);
        button1_1.addActionListener(listener);
        //设置监视器
        //button是事件源listener是监视器

        listener2.setJTextArea(textShow2_1);
        listener2.setJTextField(inputText2_1);
        listener3.setJTextArea(textShow2_1);
        listener3.setJTextField(inputText2_1);
        button2_1.addActionListener(listener2);
        button2_2.addActionListener(listener3);

        listener4.setJTextField(inputText3_1, inputText3_2, inputText3_3);
        listener4.setJTextArea(textShow3_1);
        button3_1.addActionListener(listener4);
    }
}