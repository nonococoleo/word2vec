import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Classes {
    public int id;
    private float[] center;
    Map<String, Double> values = new HashMap<>();

    public Classes(int id, float[] center) {
        this.id = id;
        this.center = center.clone();
    }

    public double distance(float[] value) {
        double sum = 0;
        for (int i = 0; i < value.length; i++)
            sum += (center[i] - value[i]) * (center[i] - value[i]);
        return sum;
    }

    public void putValue(String word, double score) {
        values.put(word, score);
    }

    public void updateCenter(HashMap<String, float[]> wordMap) {
        for (int i = 0; i < center.length; i++)
            center[i] = 0;

        float[] value = null;
        for (String keyWord : values.keySet()) {
            value = wordMap.get(keyWord);
            for (int i = 0; i < value.length; i++)
                center[i] += value[i];
        }
        for (int i = 0; i < center.length; i++)
            center[i] = center[i] / values.size();
    }

    public void clean() {
        values.clear();
    }

    public List<Map.Entry<String, Double>> getTop(int n) {
        List<Map.Entry<String, Double>> arrayList =
                new ArrayList<Map.Entry<String, Double>>(values.entrySet());
        Collections.sort(arrayList, new Comparator<Map.Entry<String, Double>>() {
            @Override
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                return o1.getValue() > o2.getValue() ? 1 : -1;
            }
        });
        int min = Math.min(n, arrayList.size() - 1);
        if (min <= 1) return Collections.emptyList();
        return arrayList.subList(0, min);
    }
}