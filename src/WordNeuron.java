import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class WordNeuron extends Neuron {
    public String name;
    public double[] syn0 = null; // 词向量
    public List<Neuron> neurons = null;// 节点路径
    public int[] codeArr = null; //哈夫曼编码

    public WordNeuron(String name, double freq, int layerSize) {
        this.name = name;//词
        this.freq = freq;//词频
        this.syn0 = new double[layerSize];
        Random random = new Random();//词向量初始随机数
        for (int i = 0; i < syn0.length; i++)
            syn0[i] = (random.nextDouble() - 0.5) / layerSize;
    }

    public List<Neuron> makeNeurons() {//生成节点哈夫曼编码
        if (neurons != null)
            return neurons;

        Neuron neuron = this;
        neurons = new LinkedList<>();
        while ((neuron = neuron.parent) != null) //组成哈夫曼路径节点
            neurons.add(neuron);
        Collections.reverse(neurons);

        codeArr = new int[neurons.size()];//生成哈夫曼编码
        for (int i = 1; i < neurons.size(); i++)
            codeArr[i - 1] = neurons.get(i).code;
        codeArr[codeArr.length - 1] = this.code;

        return neurons;
    }
}