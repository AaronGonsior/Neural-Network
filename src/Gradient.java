import java.io.IOException;

public class Gradient {

    /*
    double[] gradient;
    int current;
    int length;
    int[] entries;
     */
    NeuralNetwork nn;

    Object[] layerconnection_gradients;
    Object[] node_gradients;

    public Gradient(){

    }

    public Gradient(NeuralNetwork nn){
        this.nn = nn;

        layerconnection_gradients = new Object[nn.layerConnections.length];
        for(int layer_c = 0 ; layer_c < nn.layerConnections.length ; layer_c++){
            layerconnection_gradients[layer_c] = new double[ nn.layerConnections[layer_c].layerLeft.nodes.length ][ nn.layerConnections[layer_c].layerRight.nodes.length ];
        }

        node_gradients = new Object[nn.layers.length];
        for(int layer = 0 ; layer < nn.layers.length ; layer++){
            node_gradients[layer] = new double[nn.layers[layer].nodes.length];
        }

        //double[] test_gradient_node = (double[]) node_gradients[];
        //double[][] test_gradient_layerc = (double[][]) layerconnection_gradients[];
    }

    public Gradient(Object[] layerconnection_gradients, Object[] node_gradient){
        this.layerconnection_gradients = layerconnection_gradients;
        this.node_gradients = node_gradient;
    }

    void add_weight(int layerconnection, int left, int right, double input){
        double[][] current_gradient_layerc = (double[][]) layerconnection_gradients[layerconnection];
        current_gradient_layerc[left][right] += input;
    }

    void add_bias(int layer, int node, double input){
        double[] current_gradient_node = (double[])node_gradients[layer];
        current_gradient_node[node] += input;
    }

    /*
    void add(double partial){
        gradient[current] = gradient[current] + partial;
        entries[current] = entries[current] + 1;
        current++;
    }
     */

    /*
    void reset(){
        current = 0;
    }


    void clear(){
        current = 0;
        gradient = new double[length];
        entries = new int[length];
    }
     */

    void clear(){
        layerconnection_gradients = new Object[nn.layerConnections.length];
        for(int layer_c = 0 ; layer_c < nn.layerConnections.length ; layer_c++){
            layerconnection_gradients[layer_c] = new double[ nn.layerConnections[layer_c].layerLeft.nodes.length ][ nn.layerConnections[layer_c].layerRight.nodes.length ];
        }

        //biases? add?? ----------------

    }

    void makeJPG() throws IOException {
        int dim = 28;
        double[][] cur_image = new double[dim][dim];
        for(int j = 0 ; j < nn.layerDimensions[1] ; j++){


            for(int i = 0 ; i < dim*dim ; i++){
                double[][] temp = (double[][])(layerconnection_gradients[0]);
                cur_image[(int)(i/dim)][i%dim] = temp[i][j];
            }
            GreenRedImage gradientimg = new GreenRedImage(cur_image);
            gradientimg.makeJPG(System.getProperty("user.dir")+ "\\single_test\\gradientimages\\","gradient_img_"+j,28,false);


        }

    }

    Gradient getNormed_gradient(){

        double squaresum = 0;
        double norm;

        for(int layer_c = 0 ; layer_c < nn.layerConnections.length ; layer_c++){
            double[][] current_gradient_layerc = (double[][]) layerconnection_gradients[layer_c];
            for(int node_left = 0 ; node_left < nn.layerConnections[layer_c].numLeft ; node_left++){
                for(int node_right = 0 ; node_right < nn.layerConnections[layer_c].numRight ; node_right++){
                    squaresum += Math.pow(current_gradient_layerc[node_left][node_right],2);
                }
            }
        }

        norm = Math.sqrt(squaresum);


        Object[] new_layerconnection_gradients;
        new_layerconnection_gradients = new Object[nn.layerConnections.length];
        for(int layer_c = 0 ; layer_c < nn.layerConnections.length ; layer_c++){
            new_layerconnection_gradients[layer_c] = new double[ nn.layerConnections[layer_c].layerLeft.nodes.length ][ nn.layerConnections[layer_c].layerRight.nodes.length ];
        }
        for(int layer_c = 0 ; layer_c < nn.layerConnections.length ; layer_c++){
            double[][] current_gradient_layerc = (double[][]) layerconnection_gradients[layer_c];
            double[][] new_current_gradient_layerc = (double[][]) new_layerconnection_gradients[layer_c];
            for(int node_left = 0 ; node_left < nn.layerConnections[layer_c].numLeft ; node_left++){
                for(int node_right = 0 ; node_right < nn.layerConnections[layer_c].numRight ; node_right++){
                    new_current_gradient_layerc[node_left][node_right] = current_gradient_layerc[node_left][node_right] / norm;
                }
            }
        }


        squaresum = 0;

        for(int layer = 0 ; layer < nn.layers.length ; layer++){
            double[] current_gradient_node = (double[])node_gradients[layer];
            for(int node = 0 ; node < nn.layers.length ; node++){
                squaresum += Math.pow(current_gradient_node[node],2);
            }
        }

        norm = Math.sqrt(squaresum);

        Object[] new_node_gradient;
        new_node_gradient = new Object[nn.layers.length];
        for(int layer = 0 ; layer < nn.layers.length ; layer++){
            new_node_gradient[layer] = new double[nn.layers[layer].nodes.length];
        }
        for(int layer = 0 ; layer < nn.layers.length ; layer++){
            double[] current_gradient_node = (double[])node_gradients[layer];
            double[] new_current_gradient_node = (double[])new_node_gradient[layer];
            for(int node = 0 ; node < nn.layers.length ; node++){
                new_current_gradient_node[node] = current_gradient_node[node] / norm;
            }
        }

        Gradient normed_gradient = new Gradient(new_layerconnection_gradients,new_node_gradient);
        return normed_gradient;
    }

    void normalize(){
        Gradient normed_gradient = getNormed_gradient();
        this.layerconnection_gradients = normed_gradient.layerconnection_gradients;
        this.node_gradients = normed_gradient.node_gradients;
    }

    /*
    double[] getGradient(){
        return gradient;
    }

    double[] getNormedGradient(){
        double[] output = new double[gradient.length];
        double length = Math.sqrt(getSquareGradientLengthEuclid());
        if(length != 0){
            for(int i = 0 ; i < output.length ; i++){
                output[i] = gradient[i]/length;
            }
        }
        return output;
    }

    double[] getAvgGradient(){
        double[] avgGradient = new double[length];
        for(int part = 0 ; part < length ; part++){
            avgGradient[part] = gradient[part] / entries[part];
        }
        return avgGradient;
    }

    double getSquareGradientLengthEuclid(){
        double gradlength = 0;
        for (double part:gradient) {
            gradlength += Math.pow(part,2);
        }
        //gradlength = Math.sqrt(gradlength);
        return gradlength;
    }

    double getLength(){
        double gradlength = 0;
        for (double part:gradient) {
            gradlength += Math.pow(part,2);
        }
        gradlength = Math.sqrt(gradlength);
        return gradlength;
    }

    void print(){
        double[] grad = getNormedGradient();
        for(int partial = 0 ; partial < length ; partial++){
            System.out.println(grad[partial]);
        }
    }
     */
}