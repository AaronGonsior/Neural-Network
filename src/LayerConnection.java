public class LayerConnection {

    Layer layerLeft;
    Layer layerRight;
    int numLeft;
    int numRight;
    WeightedConnections weightedConnections;

    public LayerConnection(Layer layerLeft, Layer layerRight, WeightedConnections weightedConnections){
        this.layerLeft=layerLeft;
        this.layerRight=layerRight;
        this.numLeft=layerLeft.numNodes;
        this.numRight=layerRight.numNodes;
        this.weightedConnections=weightedConnections;
    }

    public void propagate(){
        layerRight.clearActivations();
        double[] leftSignals = layerLeft.getSignal();
        for(int nodeLeft=0;nodeLeft<numLeft;nodeLeft++){
            for(int nodeRight=0 ; nodeRight < numRight ; nodeRight++){
                layerRight.nodes[nodeRight].giveInput(weightedConnections.getWeightedconnections()[nodeLeft][nodeRight]*leftSignals[nodeLeft]);
            }
        }
    }

    void backprop(Gradient gradient_weights,Gradient gradient_biases) throws Exception {

        double[] error = layerRight.getError();
        double[][] connections = weightedConnections.getWeightedconnections();
        double[] leftLayerFeedback = new double[numLeft];

        gradient_biases.reset();
        for(int i = 0 ; i < numRight ; i++){
            gradient_biases.add(error[i]);
        }

        for(int j = 0 ; j < numRight ; j++){
            double[] leftSignals = layerLeft.getSignal();
            gradient_weights.reset();
            for(int i = 0 ; i < numLeft ; i++){
                gradient_weights.add(error[j]*leftSignals[i]);
            }
        }

        for(int i = 0 ; i < numLeft ; i++){
            for(int j = 0 ; j < numRight ; j++){
                leftLayerFeedback[i] += connections[i][j] * error[j];
            }
            leftLayerFeedback[i] *= Function.squish_prime(layerLeft.nodes[i].getActivation());
        }

        layerLeft.setError(leftLayerFeedback);
        weightedConnections.setWeightedConnections(connections);
    }


    void displayWeights(){
        double[][] weights = weightedConnections.getWeightedconnections();
        for (int i = 0 ; i < numLeft ; i++) {
            for(int j = 0 ; j < numRight ; j++){
                System.out.print(weights[i][j]+" ");
            }
            System.out.println("");
        }
    }

    void loadWeights(double[][] weights) throws Exception {
        weightedConnections.setWeightedConnections(weights);
    }
}