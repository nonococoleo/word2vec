public abstract class Neuron implements Comparable<Neuron> {
    public double freq;
    public Neuron parent;
    public int code;


    @Override
    public int compareTo(Neuron neuron) {
        if (this.freq > neuron.freq)
            return 1;
        else
            return -1;
    }
}
