public class Layer {
    public Node[] nodes;
    public int numNodes;
    public double[] error;
    int layer_number;

    public Layer(int layer_number, int numNodes){
        this.layer_number = layer_number;
        this.numNodes = numNodes;
        nodes = new Node[numNodes];
        for(int i=0 ; i < numNodes ; i++){
            nodes[i] = new Node();
        }
        this.error = new double[numNodes];
    }

    public void displayNodeData(){
        for(int node=0;node<numNodes;node++){
            System.out.println("Node "+node +  ": "+nodes[node].getSignal());
        }
    }

    public void giveInput(double[] input){
        for(int i = 0 ; i < numNodes ; i++){
            nodes[i].giveInput(input[i]);
        }
    }

    void setActivations(double[] activations){
        for(int i = 0 ; i < numNodes ; i++){
            nodes[i].setActivation(activations[i]);
        }

    }

    double[] getActivation(){
        double[] activations = new double[numNodes];
        for(int i = 0 ; i < numNodes ; i++){
            activations[i] = nodes[i].getActivation();
        }
        return activations;
    }

    double[] getSignal(){
            double[] signals = new double[numNodes /*+ bias*/];
            for(int i = 0 ; i < numNodes ; i++){
                signals[i] = nodes[i].getSignal();
            }
            return signals;
    }

    void clearActivations(){
        for(Node node : nodes){
            node.clearActivation();
        }
    }

    void normalize_error(){
        double max_abs = 0;
        for(int i = 0 ; i < error.length ; i++){
            if(max_abs < Math.abs(error[i])) max_abs = Math.abs(error[i]);
        }
        double eps = 1e-300;
        if(max_abs < eps) max_abs =1;
        for(int i = 0 ; i < error.length ; i++){
            error[i] = numNodes*(error[i]/max_abs);
        }
    }

    void setError(double[] error){
        this.error = error;
    }

    void addError(double[] error){

        /*for(double num : error){
            if(Double.isNaN(num)) throw new IllegalStateException();
        }
         */
        Function.NaN_Test(error);

        for (int i = 0 ; i < error.length ; i++) {
            this.error[i] += error[i];
        }
    }

    void clearError(){
        for (int i = 0 ; i < error.length ; i++) {
            this.error[i] = 0;
        }
    }

    double[] getError(){
        return error;
    }

    /*
    void setLables(double[] lables){
        this.lables = lables;
    }

    double[] getLables(){
        return lables;
    }

    void clearLables(){
        for (int i = 0 ; i < lables.length ; i++) {
            this.lables[i] = 0;
        }
    }
     */
}