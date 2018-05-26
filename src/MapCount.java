import java.util.HashMap;

public class MapCount<T> {
    private HashMap<T, Integer> hm = null;//词频统计表

    public MapCount() {
        this.hm = new HashMap();
    }

    public void add(T t, int n) {//目标单词次数+n
        Integer integer = null;
        if ((integer = this.hm.get(t)) != null)
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

}
