public class Layer {
    public Node[] nodes;
    public int numNodes;
    public double[] error;

    public Layer(int numNodes){
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