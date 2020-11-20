public class Node {
    double activation;
    double signal;
    double bias;

    public Node(){
        activation = 0;
        signal = 0;
        //bias = (Math.random()-0.5)*2.0;
        bias = 0;
    }

    void setBias(double bias){
        this.bias = bias;
    }

    void setActivation(double input){
        if(Double.isNaN(input)) throw new IllegalStateException();
        activation = input;
        signal = Function.squish(activation+bias);
    }

    void giveInput(double input) {
        activation += input;
        signal = Function.squish(activation+bias);
    }

    double getActivation() {
        return activation;
    }

    void clearActivation(){
        activation = 0;
        signal = 0;
    }

    double getSignal(){
        return signal;
    }

}