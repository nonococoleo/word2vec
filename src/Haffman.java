import java.util.Collection;
import java.util.TreeSet;


public class Haffman {
    private int layerSize;//向量维度

    public Haffman(int layerSize) {//构造函数
        this.layerSize = layerSize;
    }

    private TreeSet<Neuron> set = new TreeSet<>();

    public void make(Collection<Neuron> neurons) {//建立哈弗曼树
        set.addAll(neurons);
        while (set.size() > 1) {
            merger();
        }
    }

    private void merger() {//合并节点
        HiddenNeuron hn = new HiddenNeuron(layerSize);
        Neuron min1 = set.pollFirst();//检索权值最小的节点
        Neuron min2 = set.pollFirst();
        hn.freq = min1.freq + min2.freq;//父亲节点权值是孩子节点之和
        min1.parent = hn;//父亲
        min2.parent = hn;
        min1.code = 0;//编码
        min2.code = 1;
        set.add(hn);//重新加入集合
    }
}
