public class LayerConnection {

    Layer layerLeft;
    Layer layerRight;
    int numLeft;
    int numRight;
    WeightedConnections weightedConnections;
    int layerconnection_number;

    public LayerConnection(int layerconnection_number, Layer layerLeft, Layer layerRight, WeightedConnections weightedConnections){
        this.layerconnection_number = layerconnection_number;
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

    /*
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
     */

    void backprop(Gradient gradient) throws Exception {

        double[] error = layerRight.getError();
        double[][] connections = weightedConnections.getWeightedconnections();
        double[] leftLayerFeedback = new double[numLeft];

        System.out.println("Attention! biases deactivated in LayerConnection: backprop()");
        for(int node = 0 ; node < numRight ; node++){
            //gradient.add_bias(layerRight.layer_number,node,error[node]);
        }

        for(int node_right = 0 ; node_right < numRight ; node_right++){
            double[] leftSignals = layerLeft.getSignal();
            for(int node_left = 0 ; node_left < numLeft ; node_left++){
                gradient.add_weight(layerconnection_number,node_left,node_right,error[node_right]*leftSignals[node_left]);
            }
        }

        for(int node_left = 0 ; node_left < numLeft ; node_left++){
            for(int node_right = 0 ; node_right < numRight ; node_right++){
                leftLayerFeedback[node_left] += connections[node_left][node_right] * error[node_right];
            }
            leftLayerFeedback[node_left] *= Function.squish_prime(layerLeft.nodes[node_left].getActivation());
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