import java.io.*;
import java.util.Scanner;

public class NeuralNetwork {

    Layer[] layers;
    LayerConnection[] layerConnections;
    int[] layerDimensions;
    //Gradient gradient_weights;
    //Gradient gradient_biases;
    Gradient gradient;
    double averageError;
    double best_bet_accuracy;
    String name;
    Trainer trainer;

    public NeuralNetwork(Trainer trainer, int[] layerDimensions, WeightedConnections[] weightedConnectionsList){

        this.trainer = trainer;

        int numedges = 0;
        for(int layer = 0 ; layer < layerDimensions.length-2/*-2*/ ; layer++){
            numedges += layerDimensions[layer]*(layerDimensions[layer+1]);
        }
        numedges += layerDimensions[layerDimensions.length-2/*-2*/]*layerDimensions[layerDimensions.length-1];
        System.out.println("num_edges: " + numedges);

        //gradient_weights = new Gradient(numedges);
        //gradient = new Gradient();


        int numnodes = 0;
        for(int layer = 1 ; layer < layerDimensions.length ; layer++){
            numnodes += layerDimensions[layer];
        }
        System.out.println("num_biases: " + numnodes);

        //gradient_biases = new Gradient(numnodes);

        layers = new Layer[layerDimensions.length];
        for(int layer = 0 ; layer < layerDimensions.length ; layer++){
            layers[layer] = new Layer(layer,layerDimensions[layer]);
        }
        layers[layerDimensions.length-1] = new Layer(layerDimensions.length-1,layerDimensions[layerDimensions.length-1]);

        layerConnections = new LayerConnection[layerDimensions.length-1];
        for(int connection = 0 ; connection < layerDimensions.length-1 ; connection++){
            layerConnections[connection] = new LayerConnection(connection,layers[connection],layers[connection+1],weightedConnectionsList[connection]);
        }

        this.layerDimensions = layerDimensions;
        for(int i = 0 ; i < weightedConnectionsList.length; i++){
            layerConnections[i].weightedConnections = weightedConnectionsList[i];
        }

    }

    public NeuralNetwork(NeuralNetwork nn){
        this.layers = nn.layers;
        this.layerConnections = nn.layerConnections;
        this.layerDimensions = nn.layerDimensions;
        this.gradient = nn.gradient;
        //this.gradient_weights = nn.gradient_weights;
        this.averageError = nn.averageError;
        this.name = nn.name + "copy";
    }




    void setAverageError(double averageError){
        this.averageError = averageError;
    }

    double getAverageError(){
        return averageError;
    }

    void setName(String name){
        this.name = name;
    }

    String getName(){
        return name;
    }

    void clearActivation(){
        for(Layer layer : layers){
            layer.clearActivations();
        }
    }

    /*void clearError(){
        for(Layer layer : layers){
            layer.clearError();
        }
    }
     */






    void load(File file) throws Exception {

        Scanner sc = new Scanner(file);
        String[] weightsLineString;
        double[][] weights;
        int i=0;
        int j=0;


        /*
        if(biases){
            weights = new double[layers[0].numNodes+1][layers[1].numNodes];
        }
        else{
            weights = new double[layers[0].numNodes][layers[1].numNodes];
        }
         */
        weights = new double[layers[0].numNodes][layers[1].numNodes];

        int layer = 0;

        while(sc.hasNextLine() && layer < layers.length-1){
            String line = sc.nextLine();
            if(line.equals("-")){
                layerConnections[layer].loadWeights(weights);
                layer++;
                if(layer>=layers.length-1) break;

                /*
                if(biases){
                    if(layer == layers.length-2){
                        weights = new double[layers[layer].numNodes+1][layers[layer+1].numNodes];
                    }
                    else{
                        weights = new double[layers[layer].numNodes+1][layers[layer+1].numNodes];
                    }
                }
                else{
                    weights = new double[layers[layer].numNodes][layers[layer+1].numNodes];
                }
                 */

                weights = new double[layers[layer].numNodes][layers[layer+1].numNodes];

                i = 0;
                j = 0;
                continue;
            }

            if(line.equals("#")) break;

            weightsLineString = line.split(" ");
            for(String str : weightsLineString) {
                weights[i][j] = Double.valueOf(str);
                j++;
            }
            j=0;
            i++;
        }
        layerConnections[layers.length-2].loadWeights(weights);
    }

    void save(String path) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter(path, "UTF-8");
        /*
        //writer.println("The first line");
        for(int layer = 0 ; layer < this.layers.length ; layer++){

            for(int nodes = 0 ; nodes < this.layers[layer].numNodes ; nodes++){
                writer.println(this.layers[layer].nodes[nodes].getSensitivity() + " ");
            }

            if(layer < this.layers.length-1){
                writer.println("-");
            }
            else{
                writer.println("#");
            }

        }
        */

        //layerConnections
        for(int layerconnection = 0 ; layerconnection < layerConnections.length ; layerconnection++){
            LayerConnection layerConnection = layerConnections[layerconnection];
            double[][] matrix = layerConnections[layerconnection].weightedConnections.getWeightedconnections();

            /*
            if(layerConnections[layerconnection].layerRight.biases){
                for(int i = 0 ; i < layerConnection.numLeft ; i++){
                    for(int j = 0 ; j < layerConnection.numRight -1 ; j++){
                        writer.print(matrix[i][j] + " ");
                    }
                    writer.println("");
                }
                if(layerconnection < layerConnections.length-1){
                    writer.println("-");
                }
                else{
                    writer.println("#");
                }
            }
            else{
                for(int i = 0 ; i < layerConnection.numLeft ; i++){
                    for(int j = 0 ; j < layerConnection.numRight ; j++){
                        writer.print(matrix[i][j] + " ");
                    }
                    writer.println("");
                }
                if(layerconnection < layerConnections.length-1){
                    writer.println("-");
                }
                else{
                    writer.println("#");
                }
            }
             */

            for(int i = 0 ; i < layerConnection.numLeft ; i++){
                for(int j = 0 ; j < layerConnection.numRight ; j++){
                    writer.print(matrix[i][j] + " ");
                }
                writer.println("");
            }
            if(layerconnection < layerConnections.length-1){
                writer.println("-");
            }
            else{
                writer.println("#");
            }

            /*
            for(int i = 0 ; i < layerConnection.numLeft ; i++){
                for(int j = 0 ; j < layerConnection.numRight ; j++){
                    writer.print(matrix[i][j] + " ");
                }
                writer.println("");
            }
            if(layerconnection < layerConnections.length-1){
                writer.println("-");
            }
            else{
                writer.println("#");
            }
             */
        }

        writer.close();
    }

    /*
    void backprop(double[] error) throws Exception {
        gradient_weights.clear();
        gradient_biases.clear();

        layers[layers.length-1].setError(error);
        for(int back = layers.length-2 ; back >= 0 ; back--){
            layerConnections[back].backprop(gradient_weights,gradient_biases);
        }
    }
     */

    double[] propagate(double[] activation){
        layers[0].setActivations(activation);
        for (int connection = 0 ; connection < layerConnections.length ; connection++) {
            layerConnections[connection].propagate();
        }
        double[] out = layers[layers.length-1].getSignal();
        return out;
    }

    double[][] propagate(IdxReader idxReader) throws Exception {

        double[][] errors = new double[idxReader.data.length][layerDimensions[layerDimensions.length-1]];
        double errortotal = 0;
        double[] out;
        //double[] error = new double[layerDimensions[layerDimensions.length-1]];
        boolean[] best_bet_correct = new boolean[idxReader.data.length];

        int best_bet_index;
        int label;
        double best_bet_accuracy = 0;

        for (int img = 0; img < idxReader.data.length /* caution: -1 for testing! */; img++) {

            if(img == idxReader.data.length-1){
                GrayscaleImage testimg = new GrayscaleImage(idxReader.data[img],"1:1");
                testimg.makeJPG(System.getProperty("user.dir")+"\\test","last image");
                GreenRedImage testimg_gr = new GreenRedImage(idxReader.data[img]);
                testimg_gr.makeJPG(System.getProperty("user.dir")+"\\test","last image GR",28,false);
            }

            clearActivation();
            out = propagate(idxReader.data[img]);

            /*
            for (int i = 0; i < out.length; i++) {
                //error[i] = Function.error(out[i], idxReader.labels[img][i]) * Function.squish_prime(layers[layers.length - 1].nodes[i].getActivation());
                //errors[img][i] = Function.error(out[i], idxReader.labels[img][i]) * Function.squish_prime(layers[layers.length - 1].nodes[i].getActivation());
                errors[img][i] = Function.error(out[i], idxReader.labels[img][i]);
                //errortotal += Math.abs(errors[img][i]);
            }
             */


            best_bet_index = 0;
            label = 0;
            for(int i = 0 ; i < out.length ; i++){

                errors[img][i] = Function.error(out[i],idxReader.labels[img][i]);
                errortotal+=Math.abs(errors[img][i]);

                if(out[i] > out[best_bet_index]){
                    best_bet_index = i;
                }
                if(idxReader.labels[img][i] == 1) {
                    label = i;
                }
            }

            //if(best_bet_index == label) best_bet_correct[img] = true;
            best_bet_correct[img] = best_bet_index == label;

            best_bet_accuracy += best_bet_correct[img] ? 1.0 : 0;

        }

        this.averageError = errortotal / idxReader.data.length;

        best_bet_accuracy /= idxReader.data.length;
        this.best_bet_accuracy = best_bet_accuracy;


        return errors;

    }

    void backprop(IdxReader idxReader, double[][] errors, double sigma) throws Exception {

        double[] error = new double[layerDimensions[layerDimensions.length-1]];

        gradient.clear();

        for (int img = 0; img < idxReader.data.length /* caution: -1 for testing! */ ; img++) {

            for(int i = 0 ; i < error.length ; i++){
                error[i] = errors[img][i];
            }

            //only to be save - not really needed
            for(int back = layers.length-1 ; back >= 0 ; back--){
                layers[back].clearError();
            }

            layers[layers.length-1].setError(error);
            for(int back = layers.length-2 ; back >= 0 ; back--){
                layerConnections[back].backprop(gradient);
            }

        }

        GradAdapt(sigma);

    }


    void backprop(IdxReader idxReader, double[][] errors, int batchsize, double sigma) throws Exception {

        double[] error = new double[layerDimensions[layerDimensions.length-1]];

        gradient.clear();

        for (int img = 0; img < idxReader.data.length ; img++) {


            for(int i = 0 ; i < error.length ; i++){
                error[i] = errors[img][i];
            }

            //only to be save - not really needed (?)
            for(int back = layers.length-1 ; back >= 0 ; back--){
                layers[back].clearError();
            }

            layers[layers.length-1].setError(error);
            for(int back = layers.length-2 ; back >= 0 ; back--){
                layerConnections[back].backprop(gradient);
            }

            if(img % batchsize == 0){

                gradient.normalize();
                //gradient.makeJPG();
                //trainer.gd.update();

                GradAdapt(sigma);
                gradient.clear();

                //trainer.nd.update();
                //Thread.sleep(100);

            }

        }

        GradAdapt(sigma);

    }

    void GradAdapt(double sigma) throws Exception {
        Gradient normedGrad = gradient.getNormed_gradient();
        //Gradient normedGrad = gradient;
        Object[] layerconnection_gradients = normedGrad.layerconnection_gradients;
        Object[] node_gradients = normedGrad.node_gradients;

        double[][] weights;
        double[][] newweights;


        for(int layer_c = 0 ; layer_c < layerConnections.length-1 ; layer_c++){
            double[][] current_gradient_layerc = (double[][]) layerconnection_gradients[layer_c];
            weights = layerConnections[layer_c].weightedConnections.getWeightedconnections();
            newweights = new double[weights.length][weights[0].length];
            for(int node_left = 0 ; node_left < layerConnections[layer_c].numLeft ; node_left++){
                for(int node_right = 0 ; node_right < layerConnections[layer_c].numRight ; node_right++){
                    newweights[node_left][node_right] = weights[node_left][node_right] + sigma * (+1) * current_gradient_layerc[node_left][node_right];
                }
            }
            layerConnections[layer_c].weightedConnections.setWeightedConnections(newweights);
        }

        for(int layer = 0 ; layer < layers.length ; layer++){
            double[] current_gradient_node = (double[]) node_gradients[layer];
            for(int node = 0 ; node < layers[layer].numNodes ; node++){
                layers[layer].nodes[node].setBias( layers[layer].nodes[node].bias + sigma * (+1) * current_gradient_node[node] );
            }
        }

    }




    double propagate_get_avgerror(IdxReader idxReader){

        //double[][] labels = idxReader.getLables();
        //double[][] data = idxReader.getData();

        double errortotal = 0;
        double[] out;
        double[] error = new double[layerDimensions[layerDimensions.length-1]];
        for(int img = 0 ; img < idxReader.data.length ; img++){
            clearActivation();
            out = propagate(idxReader.data[img]);
            for(int i = 0 ; i < out.length ; i++){
                error[i] = Function.error(out[i],idxReader.labels[img][i]);
                errortotal+=Math.abs(error[i]);
            }
        }
        averageError = errortotal/idxReader.data.length;
        return averageError;
    }

    double propagate_get_best_bet_accuracy(IdxReader idxReader){
        double[] out;
        //double[] error = new double[layerDimensions[layerDimensions.length-1]];

        //double[][] labels = idxReader.getLables();
        //double[][] data = idxReader.getData();


        boolean[] best_bet_correct = new boolean[idxReader.data.length];
        double[] error = new double[layerDimensions[layerDimensions.length-1]];

        int best_bet_index;
        int label;
        double best_bet_accuracy;
        double errortotal = 0;

        for(int img = 0 ; img < idxReader.data.length ; img++){
            clearActivation();
            out = propagate(idxReader.data[img]);

            best_bet_index = 0;
            label = 0;

            for(int i = 0 ; i < out.length ; i++){

                error[i] = Function.error(out[i],idxReader.labels[img][i]);
                errortotal+=Math.abs(error[i]);

                if(out[i] > out[best_bet_index]){
                    best_bet_index = i;
                }
                if(idxReader.labels[img][i] == 1) {
                    label = i;
                }
            }

            if(best_bet_index == label) best_bet_correct[img] = true;
        }

        best_bet_accuracy = 0;
        for(int img = 0 ; img < idxReader.data.length ; img++){
            best_bet_accuracy += best_bet_correct[img] ? 1.0 : 0;
        }
        best_bet_accuracy /= idxReader.data.length;
        this.best_bet_accuracy = best_bet_accuracy;

        this.averageError = errortotal/idxReader.data.length;

        return best_bet_accuracy;

    }



}
