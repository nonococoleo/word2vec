import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;

public class WordKmeans {
    private HashMap<String, float[]> wordMap = null;
    private int iter;
    private Classes[] cArray = null;

    public WordKmeans(HashMap<String, float[]> wordMap, int clcn, int iter) {
        this.wordMap = wordMap;
        this.iter = iter;
        cArray = new Classes[clcn];
    }

    public Classes[] explain() {
        String[] words = wordMap.keySet().toArray(new String[0]);
        Random random = new Random();
        for (int i = 0; i < cArray.length; i++) {
            String randomKey = words[random.nextInt(words.length)];
            cArray[i] = new Classes(i, wordMap.get(randomKey));
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