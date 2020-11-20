public class PropagateThread implements Runnable {
    NeuralNetwork nn;
    private Thread t;
    public int number;
    double[] input;
    double[] output;
    boolean running;

    public PropagateThread(int number, NeuralNetwork nn){
        this.nn = nn;
        this.number = number;
        input = new double[nn.layers[0].nodes.length];
        output = new double[nn.layers[nn.layers.length-1].nodes.length];
        //System.out.println("Thread " + number + " created.");
    }

    @Override
    public void run() {
        running = true;
        //System.out.println("Thread " + number + " is running.");
        nn.clearActivation();
        output = nn.propagate(input);
        //System.out.println("Thread " + number + " processing completed.");
        running = false;
    }

    void giveInput(double[] input){
        this.input = input;
    }

    double[] getOutput(){
        return output;
    }

    public void start(){
        running = true;
        //System.out.println("Thread " + number + " is starting.");
        if(t == null){
            t = new Thread(this, String.valueOf(number));
            t.start();
        }
    }

    public void sleep(int millis) throws InterruptedException {
        t.sleep(millis);
    }

    boolean isAlive(){
        return running;
        //return output==null;
        //return t.isAlive();
    }
}
