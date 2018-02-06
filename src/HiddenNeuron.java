public class HiddenNeuron extends Neuron{
    public double[] syn1 ; //参数向量
    public HiddenNeuron(int layerSize){
        syn1 = new double[layerSize] ;//初值全为0.0
    }
}
