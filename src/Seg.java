import jdk.nashorn.internal.runtime.WithObject;

import java.io.*;
import java.util.*;

public class Seg {
    public static void main(String[] args) throws IOException {
        double[][] data, target;
        data = readin("train_word.txt", 8000);
        target = readout("train_class.txt", 8000);
        BpDeep bp = new BpDeep(new int[]{200, 10, 5}, 0.015, 0.8);

        //迭代训练5000次
        for (int n = 0; n < 5000; n++) {
            for (int i = 0; i < data.length; i++)
                if (!Double.isNaN(data[i][0]))
                    bp.train(data[i], target[i]);
            if (n % 100 == 0) {
//                bp.dera();
                System.out.println(n);
                double[] result = bp.computeOut(data[400]);
                System.out.println(Arrays.toString(target[400]) + ":" + Arrays.toString(result));
            }
        }

        //根据训练结果来检验样本数据
//        data = readin("test_word.txt", 3748);
//        target = readout("test_class.txt", 3748);
        int right = 0;
        for (int i = 0; i < data.length; i++) {
            if (!Double.isNaN(data[i][0])) {
                double[] result = bp.computeOut(data[i]);
//                System.out.println(Arrays.toString(target[i]) + ":" + Arrays.toString(result));
                if (max_idx(result) > -1 && target[i][max_idx(result)] == 1.0) {
                    right += 1;
                    System.out.println(max_idx(result));
                }
            }
        }
        System.out.println();
        System.out.println(right);
    }

    static public int max_idx(double[] r) {
        double max = 0;
        int index = -1;
        for (int i = 0; i < 5; i++) {
            if (r[i] > max) {
                index = i;
                max = r[i];
            }
        }
        return index;
    }

    static public double[][] readin(String path, int l) throws IOException {
        Word2VEC vec = new Word2VEC();
        vec.loadJavaModel("vec");
        System.out.println("load model ok!");
        HashMap<String, float[]> wordMap = vec.getWordMap();
        File file = new File(path);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String s = null;
        double[][] X = new double[l][200];
        int row = 0, word;
        while ((s = br.readLine()) != null) {
            if (s.length() == 0)
                continue;
            word = 0;
            StringBuilder sb = new StringBuilder();
            ArrayList<String> list = new ArrayList<>();
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) == '|') {
                    list.add(sb.toString());
                    sb = new StringBuilder();
                } else {
                    sb.append(s.charAt(i));
                }
            }
            if (sb.length() > 0) {
                list.add(sb.toString());
                sb = new StringBuilder();
            }
            for (String ss : list) {
                if (ss.matches("^[a-zA-Z]*")) {
                    String temp = ss.toLowerCase();
                    float[] vector = wordMap.get(temp);
                    if (vector != null && vector.length == 200) {
                        for (int i = 0; i < 200; i++)
                            X[row][i] += vector[i];
                        word += 1;
                    }
                }
            }
            for (int i = 0; i < 200; i++)
                X[row][i] /= word;
            row += 1;
            if (row == l)
                break;
        }
        return X;
    }

    static double[][] readout(String path, int l) throws IOException {
        File file = new File(path);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String s = null;
        double[][] X = new double[l][5];
        int row = 0, temp;
        while ((s = br.readLine()) != null) {
            temp = Integer.valueOf(s);
            for (int i = 0; i < 5; i++)
                X[row][i] = 0.0;
            X[row][temp] = 1.0;
            row += 1;
            if (row == l)
                break;
        }
        return X;
    }
}
