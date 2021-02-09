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
        //double[][] current_gradient_layerc = (double[][]) layerconnection_gradients[layerconnection];
        //current_gradient_layerc[left][right] += input;
        ((double[][]) layerconnection_gradients[layerconnection])[left][right] += input;
    }

    void add_bias(int layer, int node, double input){
        //double[] current_gradient_node = (double[])node_gradients[layer];
        //current_gradient_node[node] += input;
        ((double[])node_gradients[layer])[node] += input;
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

        node_gradients = new Object[nn.layers.length];
        for(int layer = 0 ; layer < nn.layers.length ; layer++){
            node_gradients[layer] = new double[nn.layers[layer].nodes.length];
        }

    }

    void makeJPG() throws IOException {
        int dim = 28;
        double[][] cur_image = new double[dim][dim];
        double[][] img_grad = (double[][])(layerconnection_gradients[0]);

        boolean amp = false;
        double max_abs = 0;
        if(amp){
            for(int j = 0 ; j < nn.layerDimensions[1] ; j++){
                for(int i = 0 ; i < dim*dim ; i++){
                    if(max_abs < Math.abs(img_grad[i][j]) ){
                        max_abs = Math.abs(img_grad[i][j]);
                    }
                }
            }
        }

        System.out.println("max_abs of makeJPG in gradient: " + max_abs);

        double eps = 1e-200;
        if(max_abs < eps) max_abs = 1;

        for(int j = 0 ; j < nn.layerDimensions[1] ; j++){

            for(int i = 0 ; i < dim*dim ; i++){

                //[(int) Math.floor(i/dim)][Math.floorMod(i,dim)]
                //[(int)(i/dim)][i%dim]
                cur_image[(int) Math.floor(i/dim)][Math.floorMod(i,dim)] = img_grad[i][j]/max_abs;
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
        if(norm < 1e-3) norm = 1;

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
        if(norm < 0.01) norm = 1;

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