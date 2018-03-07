import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.JFrame;
import Jama.Matrix;
import java.math.BigDecimal;


public class Word2VEC {
    private HashMap<String, float[]> wordMap = new HashMap<String, float[]>();
    private int words;
    private int size;
    private int topNSize = 101;
    private static final int MAX_SIZE = 50;

    public static void main(String[] args) throws IOException {
        Word2VEC vec = new Word2VEC();
        vec.loadJavaModel("vec");
        System.out.println("load model ok!");
        vec.window();
    }

    public void window() {
        WindowActionEvent win = new WindowActionEvent();
        win.setMyCommandListener(this);
        win.setBounds(100, 100, 460, 620);
    }

    public void loadGoogleModel(String path) throws IOException {
        DataInputStream dis = null;
        BufferedInputStream bis = null;
        double len = 0;
        float vector = 0;
        try {
            bis = new BufferedInputStream(new FileInputStream(path));
            dis = new DataInputStream(bis);

            words = Integer.parseInt(readString(dis));
            size = Integer.parseInt(readString(dis));
            String word;
            float[] vectors = null;
            for (int i = 0; i < words; i++) {
                word = readString(dis);
                vectors = new float[size];
                len = 0;
                for (int j = 0; j < size; j++) {
                    vector = readFloat(dis);
                    len += vector * vector;
                    vectors[j] = (float) vector;
                }

                len = Math.sqrt(len);
                for (int j = 0; j < size; j++)
                    vectors[j] /= len;
                wordMap.put(word, vectors);
                dis.read();
            }
        } finally {
            bis.close();
            dis.close();
        }
    }

    public void loadJavaModel(String path) throws IOException {
        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(path)))) {
            words = dis.readInt();
            size = dis.readInt();

            float vector = 0;
            String key = null;
            float[] value = null;
            for (int i = 0; i < words; i++) {
                double len = 0;
                key = dis.readUTF();
                value = new float[size];
                for (int j = 0; j < size; j++) {
                    vector = dis.readFloat();
                    len += vector * vector;
                    value[j] = vector;
                }

                len = Math.sqrt(len);
                for (int j = 0; j < size; j++)
                    value[j] /= len;
                wordMap.put(key, value);
            }
        }
    }

    public TreeSet<WordEntry> analogy(String word0, String word1, String word2) {
        float[] wv0 = getWordVector(word0);
        float[] wv1 = getWordVector(word1);
        float[] wv2 = getWordVector(word2);
        if (wv1 == null || wv2 == null || wv0 == null)
            return null;

        float[] wordVector = new float[size];
        for (int i = 0; i < size; i++)
            wordVector[i] = wv1[i] - wv0[i] + wv2[i];

        float[] tempVector;
        String name;
        List<WordEntry> wordEntrys = new ArrayList<WordEntry>(topNSize);
        for (Entry<String, float[]> entry : wordMap.entrySet()) {
            name = entry.getKey();
            if (name.equals(word0) || name.equals(word1) || name.equals(word2))
                continue;

            float dist = 0;
            tempVector = entry.getValue();
            for (int i = 0; i < wordVector.length; i++)
                dist += wordVector[i] * tempVector[i];
            insertTopN(name, dist, wordEntrys);
        }
        return new TreeSet<WordEntry>(wordEntrys);
    }

    private void insertTopN(String name, float score, List<WordEntry> wordsEntrys) {
        if (wordsEntrys.size() < topNSize) {
            wordsEntrys.add(new WordEntry(name, score));
            return;
        }
        float min = Float.MAX_VALUE;
        int minOffe = 0;
        for (int i = 0; i < topNSize; i++) {
            WordEntry wordEntry = wordsEntrys.get(i);
            if (min > wordEntry.score) {
                min = wordEntry.score;
                minOffe = i;
            }
        }

        if (score > min)
            wordsEntrys.set(minOffe, new WordEntry(name, score));
    }

    public Set<WordEntry> distance(String queryWord) {
        float[] center = wordMap.get(queryWord);
        if (center == null)
            return Collections.emptySet();

        int resultSize = wordMap.size() < topNSize ? wordMap.size() : topNSize;
        TreeSet<WordEntry> result = new TreeSet<WordEntry>();

        double min = Float.MIN_VALUE;
        for (Map.Entry<String, float[]> entry : wordMap.entrySet()) {
            float[] vector = entry.getValue();
            float dist = 0;
            for (int i = 0; i < vector.length; i++)
                dist += center[i] * vector[i];

            if (dist > min) {
                result.add(new WordEntry(entry.getKey(), dist));
                if (resultSize < result.size())
                    result.pollLast();
                min = result.last().score;
            }
        }
        result.pollFirst();
        return result;
    }

    private float[] sum(float[] center, float[] fs) {
        if (center == null && fs == null)
            return null;
        if (fs == null)
            return center;
        if (center == null)
            return fs;
        for (int i = 0; i < fs.length; i++)
            center[i] += fs[i];
        return center;
    }

    public float[] getWordVector(String word) {
        return wordMap.get(word);
    }

    public static float readFloat(InputStream is) throws IOException {
        byte[] bytes = new byte[4];
        is.read(bytes);
        return getFloat(bytes);
    }

    public static float getFloat(byte[] b) {
        int accum = 0;
        accum = accum | (b[0] & 0xff) << 0;
        accum = accum | (b[1] & 0xff) << 8;
        accum = accum | (b[2] & 0xff) << 16;
        accum = accum | (b[3] & 0xff) << 24;
        return Float.intBitsToFloat(accum);
    }

    private static String readString(DataInputStream dis) throws IOException {
        byte[] bytes = new byte[MAX_SIZE];
        byte b = dis.readByte();
        int i = -1;
        StringBuilder sb = new StringBuilder();
        while (b != 32 && b != 10) {
            i++;
            bytes[i] = b;
            b = dis.readByte();
            if (i == 49) {
                sb.append(new String(bytes));
                i = -1;
                bytes = new byte[MAX_SIZE];
            }
        }
        sb.append(new String(bytes, 0, i + 1));
        return sb.toString();
    }

    public String[] getWordlist(String word, int SIZE) {
        String[] wordlist = new String[SIZE];
        wordlist[0] = word;
        int i = 1;
        for (WordEntry wordentry : this.distance(word)) {
            wordlist[i] = wordentry.name;
            i++;
            if (i >= SIZE)
                break;
        }
        return wordlist;
    }

    public Matrix get2dMat(String[] wordlist) {
        int SIZE = wordlist.length;
        float[][] veclist1 = new float[SIZE][200];
        double[][] veclist2 = new double[SIZE][200];
        int i = 0;
        for (String s : wordlist) {
            veclist1[i] = this.getWordVector(s);
            i++;
        }
        for (i = 0; i < veclist1.length; i++)
            for (int j = 0; j < 200; j++) {
                BigDecimal b = new BigDecimal(String.valueOf(veclist1[i][j]));
                veclist2[i][j] = b.doubleValue();
            }
        PCA test = new PCA();
        Matrix C = test.analyse(veclist2);
        return C;
    }

    public void Show2D(String keyword) {
        JFrame jf = new JFrame();
        jf.setSize(1200, 900);
        jf.setVisible(true);
        jf.setDefaultCloseOperation(2);
        String[] wordlist = new String[50];
        wordlist = this.getWordlist(keyword, 50);
        display D = new display(wordlist, this.get2dMat(wordlist));
        jf.getContentPane().add(D);
    }

    public Classes[] keymeans(int clcn, int iter) {
        WordKmeans wordKmeans = new WordKmeans(this.getWordMap(), clcn, iter);
        Classes[] explain = wordKmeans.explain();
        return explain;
    }

    public int getTopNSize() {
        return topNSize;
    }

    public void setTopNSize(int topNSize) {
        this.topNSize = topNSize;
    }

    public HashMap<String, float[]> getWordMap() {
        return wordMap;
    }

    public int getWords() {
        return words;
    }

    public int getSize() {
        return size;
    }
}
