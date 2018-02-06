import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;

public class WordKmeans {
    public static void main(String[] args) throws IOException {
        Word2VEC vec = new Word2VEC();
        vec.loadJavaModel("newvector.bin");
        System.out.println("load model ok!");
        WordKmeans wordKmeans = new WordKmeans(vec.getWordMap(), 50, 50);
        Classes[] explain = wordKmeans.explain();

        for (int i = 0; i < explain.length; i++) {
            System.out.println("--------" + i + "---------");
            System.out.println(explain[i].getTop(10));
        }
    }

    private HashMap<String, float[]> wordMap = null;
    private int iter;
    private Classes[] cArray = null;

    public WordKmeans(HashMap<String, float[]> wordMap, int clcn, int iter) {
        this.wordMap = wordMap;
        this.iter = iter;
        cArray = new Classes[clcn];
    }

    public Classes[] explain() {
        String[] words=wordMap.keySet().toArray(new String[0]);
        Random random=new Random();
        for (int i = 0; i < cArray.length; i++) {
//            Entry<String, float[]> next = iterator.next();
            String randomKey = words[random.nextInt(words.length)];
            cArray[i] = new Classes(i, wordMap.get(randomKey));
            System.out.println(randomKey);
        }

        Iterator<Entry<String, float[]>> iterator = wordMap.entrySet().iterator();
        for (int i = 0; i < iter; i++) {
            for (Classes classes : cArray)
                classes.clean();

            iterator = wordMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, float[]> next = iterator.next();
                double miniScore = Double.MAX_VALUE;
                double tempScore;
                int classesId = 0;
                for (Classes classes : cArray) {
                    tempScore = classes.distance(next.getValue());
                    if (miniScore > tempScore) {
                        miniScore = tempScore;
                        classesId = classes.id;
                    }
                }
                cArray[classesId].putValue(next.getKey(), miniScore);
            }

            for (Classes classes : cArray)
                classes.updateCenter(wordMap);

            System.out.println("iter " + i + " ok!");
        }
        return cArray;
    }
}