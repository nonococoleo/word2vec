import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class Learn {
    private Map<String, Neuron> wordMap = new HashMap<>();
    private int layerSize = 200;//向量维度
    private int window = 5;//上下文窗口
    private double sample = 1e-3;
    private double alpha = 0.025;//步长（学习率）
    private double startingAlpha = alpha;
    private int trainWordsCount = 0;//训练词数统计
    private Boolean isCbow = true;//CBOW/SKIPGRAM

    private int EXP_TABLE_SIZE = 2000;//EXP函数表大小
    private double[] expTable = new double[EXP_TABLE_SIZE];
    private int MAX_EXP = 6;

    public static void main(String[] args) throws IOException {
        String text, model;
        if (args.length == 0) {
            text = "text8";
            model = "vector.bin";
        } else {
            text = args[0];
            model = args[1];
        }
        Learn learn = new Learn();
        learn.learnFile(new File(text));
        learn.saveModel(new File(model));
    }

    public Learn() {
        createExpTable();
    }

    private void createExpTable() { //f(x) = x / (x + 1)
        for (int i = 0; i < EXP_TABLE_SIZE; i++) {
            expTable[i] = Math.exp(((i / (double) EXP_TABLE_SIZE * 2 - 1) * MAX_EXP));
            expTable[i] = expTable[i] / (expTable[i] + 1);
        }
    }

    public void learnFile(File file) throws IOException {//创建神经网络
        readVocab(file);//读入单词，统计词频
        new Haffman(layerSize).make(wordMap.values());//按照词频构建哈弗曼树

        for (Neuron neuron : wordMap.values())
            ((WordNeuron) neuron).makeNeurons();//确定每个词的路径

        trainModel(file);//训练参数
    }

    private void readVocab(File file) throws IOException {//读入单词，统计词频
        MapCount<String> mc = new MapCount<>();//词频表
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file)))) {
            String temp = null;
            while ((temp = br.readLine()) != null) {
                String[] split = temp.split(" ");//以空格分割单词
                trainWordsCount += split.length;//训练词数增加
                for (String string : split) {//加单词频次1
                    mc.add(string);
                }
            }
        }

        for (Entry<String, Integer> element : mc.get().entrySet()) //遍历表
            wordMap.put(element.getKey(), new WordNeuron(
                    element.getKey(), (double) element.getValue() / mc.size(), layerSize));
        //每个词创建神经元
    }

    private void trainModel(File file) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(file)))) {
            String temp = null;
            long nextRandom = 5;
            int wordCount = 0;
            int lastWordCount = 0;
            int wordCountActual = 0;
            while ((temp = br.readLine()) != null) {
                if (wordCount - lastWordCount > 10000) {
                    System.out.println("alpha:" + alpha + "\tProgress: "
                            + (int) (wordCountActual / (double) (trainWordsCount + 1) * 100)
                            + "%");
                    wordCountActual += wordCount - lastWordCount;
                    lastWordCount = wordCount;
                    alpha = startingAlpha
                            * (1 - wordCountActual / (double) (trainWordsCount + 1));
                    if (alpha < startingAlpha * 0.0001)
                        alpha = startingAlpha * 0.0001;
                }
                String[] strs = temp.split(" ");
                wordCount += strs.length;
                List<WordNeuron> sentence = new ArrayList<WordNeuron>();

                for (int i = 0; i < strs.length; i++) {
                    Neuron entry = wordMap.get(strs[i]);
                    if (entry == null)
                        continue;
                    if (sample > 0) {//简化样本
                        double ran = (Math.sqrt(entry.freq / (sample * trainWordsCount)) + 1)
                                * (sample * trainWordsCount) / entry.freq;
                        nextRandom = nextRandom * 25214903917L + 11;
                        if (ran < (nextRandom & 0xFFFF) / (double) 65536) {
                            continue;
                        }
                    }
                    sentence.add((WordNeuron) entry);
                }

                int size = sentence.size();
                for (int index = 0; index < size; index++) {
                    if (isCbow)
                        cbowGram(index, sentence);
                    else
                        skipGram(index, sentence);
                }
            }
            System.out.println("Vocab size: " + wordMap.size());
            System.out.println("Words in train file: " + trainWordsCount);
        }
    }

    private void skipGram(int index, List<WordNeuron> sentence) {
        WordNeuron word = sentence.get(index);//中心词
        int a, c = 0;
        for (a = 0; a < window * 2 + 1; a++) {//中心词左右各window-b个词
            if (a == window) //中心词跳过
                continue;
            c = index - window + a;//当前词
            if (c < 0 || c >= sentence.size()) //如果超出当前文本（开头/结尾）
                continue;

            double[] neu1e = new double[layerSize];// 误差项，用于更新词向量
            List<Neuron> neurons = word.neurons;//当前词haffman路径上的神经元
            WordNeuron we = sentence.get(c);//当前词神经元
            for (int i = 0; i < neurons.size(); i++) {//遍历路径上每个
                HiddenNeuron out = (HiddenNeuron) neurons.get(i);
                double f = 0;//当前词向量和参数向量的乘积

                for (int j = 0; j < layerSize; j++) //计算词向量和参数向量的乘积
                    f += we.syn0[j] * out.syn1[j];//1 x size * size x 1 = 1 x 1
                if (f <= -MAX_EXP || f >= MAX_EXP) //查表如超出范围略过
                    continue;
                else //得出结果
                    f = expTable[(int) ((f + MAX_EXP) * (EXP_TABLE_SIZE / MAX_EXP / 2))];

                double g = (1 - word.codeArr[i] - f) * alpha;//梯度乘以步长

                for (c = 0; c < layerSize; c++) //计算词向量的更新值
                    neu1e[c] += g * out.syn1[c];
                for (c = 0; c < layerSize; c++) //更新参数向量
                    out.syn1[c] += g * we.syn0[c];
            }
            for (int j = 0; j < layerSize; j++) //更新词向量
                we.syn0[j] += neu1e[j];
        }
    }

    private void cbowGram(int index, List<WordNeuron> sentence) {
        WordNeuron word = sentence.get(index);//中心词
        WordNeuron cur_word;
        List<Neuron> neurons = word.neurons;//中心词左右各window-b个词
        double[] neu1e = new double[layerSize];// 误差项，用于更新词向量
        double[] neu1 = new double[layerSize];// 词袋中词向量相加
        int a, c = 0;

        for (a = 0; a < window * 2 + 1; a++)
            if (a != window) {//中心词跳过
                c = index - window + a;//当前词
                if (c < 0 || c >= sentence.size())//如果超出当前文本（开头/结尾）
                    continue;
                cur_word = sentence.get(c);
                if (cur_word == null)
                    continue;
                for (c = 0; c < layerSize; c++)//加入词袋词向量
                    neu1[c] += cur_word.syn0[c];
            }

        // HIERARCHICAL SOFTMAX
        for (int i = 0; i < neurons.size(); i++) {//遍历路径上每个
            HiddenNeuron out = (HiddenNeuron) neurons.get(i);
            double f = 0;//当前词向量和参数向量的乘积

            for (c = 0; c < layerSize; c++)//计算词向量和参数向量的乘积
                f += neu1[c] * out.syn1[c];//1 x size * size x 1 = 1 x 1
            if (f <= -MAX_EXP || f >= MAX_EXP)//查表如超出范围略过
                continue;
            else//得出结果
                f = expTable[(int) ((f + MAX_EXP) * (EXP_TABLE_SIZE / MAX_EXP / 2))];

            double g = f * (1 - f) * (word.codeArr[i] - f) * alpha;//梯度乘以步长

            for (c = 0; c < layerSize; c++) //计算词向量的更新值
                neu1e[c] += g * out.syn1[c];
            for (c = 0; c < layerSize; c++) //更新参数向量
                out.syn1[c] += g * neu1[c];
        }
        for (a = 0; a < window * 2 + 1; a++) {//每个词袋中的词都更新词向量
            if (a != window) {
                c = index - window + a;
                if (c < 0 || c >= sentence.size())
                    continue;
                cur_word = sentence.get(c);
                if (cur_word == null)
                    continue;
                for (c = 0; c < layerSize; c++)
                    cur_word.syn0[c] += neu1e[c];
            }

        }
    }

    public void saveModel(File file) {
        try (DataOutputStream dataOutputStream = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(file)))) {
            dataOutputStream.writeInt(wordMap.size());
            dataOutputStream.writeInt(layerSize);
            double[] syn0 = null;
            for (Entry<String, Neuron> element : wordMap.entrySet()) {
                dataOutputStream.writeUTF(element.getKey());
                syn0 = ((WordNeuron) element.getValue()).syn0;
                for (double d : syn0) {
                    dataOutputStream.writeFloat(((Double) d).floatValue());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getLayerSize() {
        return layerSize;
    }

    public void setLayerSize(int layerSize) {
        this.layerSize = layerSize;
    }

    public int getWindow() {
        return window;
    }

    public void setWindow(int window) {
        this.window = window;
    }

    public double getSample() {
        return sample;
    }

    public void setSample(double sample) {
        this.sample = sample;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
        this.startingAlpha = alpha;
    }

    public Boolean getIsCbow() {
        return isCbow;
    }

    public void setIsCbow(Boolean isCbow) {
        this.isCbow = isCbow;
    }
}
