import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class MapCount<T> {
    private HashMap<T, Integer> hm = null;//词频统计表

    public MapCount() {
        this.hm = new HashMap();
    }

    public void add(T t, int n) {//目标单词次数+n
        Integer integer = null;
        if ((integer = (Integer) this.hm.get(t)) != null)
            this.hm.put(t, Integer.valueOf(integer.intValue() + n));
        else
            this.hm.put(t, Integer.valueOf(n));
    }

    public void add(T t) {//目标单词次数+1
        this.add(t, 1);
    }

    public int size() {
        return this.hm.size();
    }

    public void remove(T t) {
        this.hm.remove(t);
    }

    public HashMap<T, Integer> get() {
        return this.hm;
    }

    public String getDic() {
        Iterator iterator = this.hm.entrySet().iterator();
        StringBuilder sb = new StringBuilder();
        Entry next = null;
        while (iterator.hasNext()) {
            next = (Entry) iterator.next();
            sb.append(next.getKey());
            sb.append("\t");
            sb.append(next.getValue());
            sb.append("\n");
        }
        return sb.toString();
    }
}
