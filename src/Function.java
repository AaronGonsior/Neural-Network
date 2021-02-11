import java.io.IOException;

public class Function {

    public static double squish(double input){
        return 1/(1+Math.exp(-input));
        //return (2/Math.PI)*Math.atan(input);
        //return 1/Math.atan(1)*Math.atan(input);
        //return Math.tanh(input);
        //return Math.exp(-(1/Math.pow(input,2)));
    }

    public static double squish_prime(double input){
        return squish(input)*(1-squish(input));
    }

    public static double avgError(NeuralNetwork nn, double[][] trainingdata, double[][] lables,int outputnodenum,int cores) throws Exception {

        /*
        double[] error = new double[outputnodenum];
        int errortotal = 0;
        int datasize = trainingdata.length;

        double[][] output;
        NeuralNetwork[] thread_nns = new NeuralNetwork[cores];
        for(int i = 0 ; i < cores ; i++){
            thread_nns[i] = new NeuralNetwork(nn.layerDimensions,nn.weightedConnectionsList,nn.biases);
        }
        PropagateThreadScheduler threadScheduler = new PropagateThreadScheduler(cores, thread_nns);
        output = threadScheduler.propagate(trainingdata);
        for (int img = 0 ; img < trainingdata.length ; img++) {
            for (int digit = 0; digit < outputnodenum; digit++) {
                error[digit] = Function.error(output[img][digit], lables[img][digit]);
                errortotal += Math.abs(error[digit]);
            }
            nn.backprop(error, lables[img]);
        }
         */

        double[] out;
        double[] error = new double[outputnodenum];
        int datasize = trainingdata.length;
        int errortotal = 0;
        for (int imgage = 0; imgage < datasize; imgage++) {
            nn.clearActivation();
            //nn.clearError();
            out = nn.propagate(trainingdata[imgage]);
            for (int i = 0; i < out.length; i++) {
                error[i] = Function.error(out[i],lables[imgage][i]);
                errortotal += Math.abs(error[i]);
            }
        }

        return errortotal / (double) datasize;
    }

    /*
    public static NeuralNetwork GradAdaptNN(NeuralNetwork nn, double sigma, Gradient gradient) throws Exception {
        double[] grad;
        int partial;
        double[][] weights;
        double[][] newweights;
        //int rightbias;

        // !
        grad = gradient.getNormedGradient();
        //grad = gradient.getGradient();

        NeuralNetwork testNN = new NeuralNetwork(nn);
        //NeuralNetwork testNN = new NeuralNetwork(nn.layerDimensions,nn.weightedConnectionsList,nn.biases);
        partial = 0;
        for(int layer = nn.layers.length-2 ; layer > 0 ; layer--){
            weights = testNN.layerConnections[layer].weightedConnections.getWeightedconnections();
            newweights = new double[weights.length][weights[0].length];
            //rightbias = testNN.layers[layer+1].biases ? 1 : 0;
            for(int j = 0; j < testNN.layerDimensions[layer+1] ; j++){
                for(int i = 0 ; i < testNN.layerDimensions[layer] ; i++){
                    newweights[i][j] = weights[i][j] + sigma*grad[partial++];
                }
            }
            testNN.layerConnections[layer].weightedConnections.setWeightedConnections(newweights);
            //testNN.updateWeightedConnectionsList();
        }
        return testNN;
    }
     */

    /*
    public static double PowellWolfe(NeuralNetwork nn, double[][] trainingdata, double[][] lables, int outputnodenum,Gradient gradient,int cores) throws Exception {

        boolean print_details = true;
        boolean log_continue = true;

        if(print_details){
            System.out.println("Powell-Wolfe:");
            Log.writeFullLog("Powell-Wolfe:\n",log_continue);
        }


        double sigma = .1;
        double beta = .1;
        double gamma = .5;
        double eta = .75;

        double cutoff = 3E-16;

        boolean improvement = false;
        double sigma_last_improvement = 0;
        double last_improvement = 0;

        boolean changed = false;
        double sigma_minus;
        double sigma_plus;
        sigma_minus = sigma;
        if(print_details) {
            System.out.print("Sigma: " + sigma + " ");
            Log.writeFullLog("Sigma: " + sigma + " ", log_continue);
        }
        double[] out;
        double[] error = new double[outputnodenum];
        int datasize = trainingdata.length;
        double errortotal;
        //nn.updateWeightedConnectionsList();
        //NeuralNetwork testNN = new NeuralNetwork(nn.layerDimensions,nn.weightedConnectionsList,nn.biases);
        NeuralNetwork testNN = new NeuralNetwork(nn);
        double avg_error_before;
        double avg_error_after;
        //int bias = nn.biases ? 1 : 0;

        avg_error_before = Function.avgError(testNN,trainingdata,lables,outputnodenum,cores);
        testNN = Function.GradAdaptNN(nn,sigma_minus,gradient);
        avg_error_after = Function.avgError(testNN,trainingdata,lables,outputnodenum,cores);
        if(print_details) {
            System.out.println("avg_error_after: " + avg_error_after);
            Log.writeFullLog("avg_error_after: " + avg_error_after + "\n", log_continue);
        }
        // ------------------------------------------------------- new -----------------------------------------------------------------------
        if(avg_error_before - avg_error_after > last_improvement){
            improvement = true;
            sigma_last_improvement = sigma_minus;
            last_improvement = avg_error_before - avg_error_after;
            if(print_details) {
                System.out.println("new improvement found: sigma_minus: " + sigma_minus + " with improvement " + last_improvement);
                Log.writeFullLog("new improvement found: sigma_minus: " + sigma_minus + " with improvement " + last_improvement + "\n", log_continue);
            }
        }

        while( avg_error_after - avg_error_before > - sigma_minus * gamma * gradient.getLength()){ //sigma_minus not Armijo

            changed = true;
            if(print_details) {
                System.out.println("Sigma_minus: " + sigma_minus + " erfüllt nicht Armijo");
                Log.writeFullLog("Sigma_minus: " + sigma_minus + " erfüllt nicht Armijo\n", log_continue);
            }

            if(sigma_minus <= cutoff){
                if(improvement){
                    if(print_details) {
                        System.out.println("Final(cutoff): sigma_last_improvement: " + sigma_last_improvement);
                        Log.writeFullLog("Final(cutoff): sigma_last_improvement: " + sigma_last_improvement + "\n", log_continue);
                    }
                    return sigma_last_improvement;
                }
                else{
                    if(print_details) {
                        System.out.println("No improvement found");
                        Log.writeFullLog("No improvement found\n", log_continue);
                    }
                    return 0;
                    // important for parellelization: check in Trainer if sigma==0 to see if improved, if not, then abandon this thread
                    //btw boolean improvement is not nec. bc it is equivalent to double improvement != 0 !! may delete it
                }
            }

            sigma_minus *= beta;
            testNN = Function.GradAdaptNN(nn,sigma_minus,gradient);
            avg_error_after = Function.avgError(testNN,trainingdata,lables,outputnodenum,cores);
            if(print_details) {
                System.out.println("avg_error_after: " + avg_error_after);
                Log.writeFullLog("avg_error_after: " + avg_error_after + "\n", log_continue);
            }
            // ------------------------------------------------------- new -----------------------------------------------------------------------
            if(avg_error_before - avg_error_after > last_improvement){
                improvement = true;
                sigma_last_improvement = sigma_minus;
                last_improvement = avg_error_before - avg_error_after;
                if(print_details) {
                    System.out.println("new improvement found: sigma_minus: " + sigma_minus + " with improvement " + last_improvement);
                    Log.writeFullLog("new improvement found: sigma_minus: " + sigma_minus + " with improvement " + last_improvement + "\n", log_continue);
                }
            }

        }
        if(print_details) {
            System.out.println("Sigma_minus: " + sigma_minus + " erfüllt Armijo");
            Log.writeFullLog("Sigma_minus: " + sigma_minus + " erfüllt Armijo\n", log_continue);
        }
        if(!changed){

            if(print_details) {
                System.out.println("Armijo war von Anfang an erfüllt.");
                Log.writeFullLog("Armijo war von Anfang an erfüllt.\n", log_continue);
            }

            //pw new grad
            testNN = Function.GradAdaptNN(nn,sigma,gradient);
            for(int img = 0 ; img < trainingdata.length ; img++) {
                testNN.clearActivation();
                out = testNN.propagate(trainingdata[img]);
                for(int i = 0 ; i < lables[0].length ; i++){
                    error[i] = Function.error(out[i],lables[img][i]);
                }
                testNN.backprop(error);
            }
            //newgrad_normed = testNN.gradient.getNormedGradient();

            if( Function.innerProd(testNN.gradient_weights.getGradient(),gradient.getGradient()) <= eta * gradient.getLength()){ //sigma PW
                if(print_details) {
                    System.out.println("PW war von Anfang an erfüllt.");
                    Log.writeFullLog("PW war von Anfang an erfüllt.\n", log_continue);
                    System.out.println("Final(1) Sigma: " + sigma);
                    Log.writeFullLog("Final(1) Sigma: " + sigma + "\n", log_continue);
                }
                return sigma;
            }
            else{
                if(print_details) {
                    System.out.println("Sigma: " + sigma + " erfüllt PW nicht.");
                    Log.writeFullLog("Sigma: " + sigma + " erfüllt PW nicht.\n", log_continue);
                }
                sigma_plus=sigma;
                if(print_details) {
                    System.out.print("Sigma_plus(1): " + sigma_plus + " ");
                    Log.writeFullLog("Sigma_plus(1): " + sigma_plus + " ", log_continue);
                }

                testNN = Function.GradAdaptNN(nn,sigma_plus,gradient);
                avg_error_after = Function.avgError(testNN,trainingdata,lables,outputnodenum,cores);
                if(print_details) {
                    System.out.println("avg_error_after: " + avg_error_after);
                    Log.writeFullLog("avg_error_after: " + avg_error_after + "\n", log_continue);
                }
                // ------------------------------------------------------- new -----------------------------------------------------------------------
                if(avg_error_before - avg_error_after > last_improvement){
                    improvement = true;
                    sigma_last_improvement = sigma_plus;
                    last_improvement = avg_error_before - avg_error_after;
                    if(print_details) {
                        System.out.println("new improvement found: sigma_plus: " + sigma_plus + " with improvement " + last_improvement);
                        Log.writeFullLog("new improvement found: sigma_plus: " + sigma_plus + " with improvement " + last_improvement + "\n", log_continue);
                    }
                }

                while ( avg_error_after - avg_error_before <= - sigma * gamma * gradient.getLength()){ //sigma_plus Armijo
                    if(print_details) {
                        System.out.println("Sigma_plus: " + sigma_plus + " erfüllt Armijo");
                        Log.writeFullLog("Sigma_plus: " + sigma_plus + " erfüllt Armijo\n", log_continue);
                    }
                    sigma_plus /= beta;
                    if(print_details) {
                        System.out.print("Sigma_plus(1): " + sigma_plus + " ");
                        Log.writeFullLog("Sigma_plus(1): " + sigma_plus + " ", log_continue);
                    }
                }
                if(print_details) {
                    System.out.println("Sigma_plus: " + sigma_plus + " erfüllt Armijo zum ersten Mal nicht mehr");
                    Log.writeFullLog("Sigma_plus: " + sigma_plus + " erfüllt Armijo zum ersten Mal nicht mehr\n", log_continue);
                }
                sigma_minus = beta * sigma_plus;
                if(print_details) {
                    System.out.print("Sigma_minus(1): " + sigma_minus + " ");
                    Log.writeFullLog("Sigma_minus(1): " + sigma_minus + " ", log_continue);
                }
            }
        }
        else{
            sigma_plus = sigma_minus / beta;
            if(print_details) {
                System.out.print("Sigma_plus: " + sigma_plus + " ");
                Log.writeFullLog("Sigma_plus: " + sigma_plus + " ", log_continue);
            }
        }

        //sigma = .5*(sigma_minus + sigma_plus);
        //System.out.print("Sigma_avg: "+sigma+" ");

        //pw new grad
        testNN = Function.GradAdaptNN(nn,sigma_minus,gradient);
        for(int img = 0 ; img < trainingdata.length ; img++) {
            testNN.clearActivation();
            out = testNN.propagate(trainingdata[img]);
            for(int i = 0 ; i < lables[0].length ; i++){
                error[i] = Function.error(out[i],lables[img][i]);
            }
            testNN.backprop(error);
        }


        //newgrad_normed = testNN.gradient.getNormedGradient();

        while(Function.innerProd(testNN.gradient_weights.getGradient(),gradient.getGradient()) > eta * gradient.getLength() ){ //sigma_minus not PW

            if(print_details){
                System.out.println(Function.innerProd(testNN.gradient_weights.getGradient(),gradient.getGradient()) + " > eta " + gradient.getLength());
                Log.writeFullLog(Function.innerProd(testNN.gradient_weights.getGradient(),gradient.getGradient()) + " > eta " + gradient.getLength() + "\n",log_continue);
                System.out.println("Sigma_minus: " + sigma_minus + " erfüllt nicht PW");
                Log.writeFullLog("Sigma_minus: " + sigma_minus + " erfüllt nicht PW\n",log_continue);
            }

            sigma = .5*(sigma_minus + sigma_plus);
            if(print_details) {
                System.out.print("Sigma_avg: " + sigma + " ");
                Log.writeFullLog("Sigma_avg: " + sigma + " ", log_continue);
            }

            testNN = Function.GradAdaptNN(nn,sigma,gradient);
            avg_error_after = Function.avgError(testNN,trainingdata,lables,outputnodenum,cores);
            if(print_details) {
                System.out.println("avg_error_after: " + avg_error_after);
                Log.writeFullLog("avg_error_after: " + avg_error_after + "\n", log_continue);
            }
            // ------------------------------------------------------- new -----------------------------------------------------------------------
            if(avg_error_before - avg_error_after > last_improvement){
                improvement = true;
                sigma_last_improvement = sigma_minus;
                last_improvement = avg_error_before - avg_error_after;
                if(print_details) {
                    System.out.println("new improvement found: sigma_minus: " + sigma_minus + " with improvement " + last_improvement);
                    Log.writeFullLog("new improvement found: sigma_minus: " + sigma_minus + " with improvement " + last_improvement + "\n", log_continue);
                }
            }

            if( avg_error_after - avg_error_before <= - sigma * gamma * gradient.getLength()){ //sigma Armijo

                if(print_details){
                    System.out.println("Sigma_avg: " + sigma + " erfüllt Armijo");
                    Log.writeFullLog("Sigma_avg: " + sigma + " erfüllt Armijo\n",log_continue);
                    System.out.println("Sigma_minus: " + sigma_minus+" ");
                    Log.writeFullLog("Sigma_minus: " + sigma_minus+" \n",log_continue);
                }
                sigma_minus = sigma;

                //changed sigma_minus, new point of gradient for pw condition
                testNN = Function.GradAdaptNN(nn,sigma_minus,gradient);
                for(int img = 0 ; img < trainingdata.length ; img++) {
                    out = testNN.propagate(trainingdata[img]);
                    for(int i = 0 ; i < lables[0].length ; i++){
                        error[i] = Function.error(out[i],lables[img][i]);
                    }
                    testNN.backprop(error);
                }

            }
            else{
                if(print_details){
                    System.out.println("Sigma_minus: " + sigma_minus + " erfüllt nicht Armijo");
                    Log.writeFullLog("Sigma_minus: " + sigma_minus + " erfüllt nicht Armijo\n",log_continue);
                    System.out.print("Sigma_plus: " + sigma_plus + " ");
                    Log.writeFullLog("Sigma_plus: " + sigma_plus + " ",log_continue);
                }

                sigma_plus = sigma;
            }

        }

        if(print_details) {
            System.out.println("Sigma_minus: " + sigma_minus + " erfüllt PW");
            Log.writeFullLog("Sigma_minus: " + sigma_minus + " erfüllt PW\n", log_continue);
        }

        sigma = sigma_minus;
        if(sigma <= cutoff){
            if(improvement){
                if(print_details) {
                    System.out.println("Final(cutoff): sigma_last_improvement: " + sigma_last_improvement);
                    Log.writeFullLog("Final(cutoff): sigma_last_improvement: " + sigma_last_improvement + "\n", log_continue);
                }

                return sigma_last_improvement;
            }
            else{
                if(print_details) {
                    System.out.println("No improvement found");
                    Log.writeFullLog("No improvement found\n", log_continue);
                }
                return 0;
                // important for parellelization: check in Trainer if sigma==0 to see if improved, if not, then abandon this thread
                //btw boolean improvement is not nec. bc it is equivalent to double improvement != 0 !! may delete it
            }
        }

        if(print_details) {
            System.out.println("Final(2) Sigma: " + sigma);
            Log.writeFullLog("Final(2) Sigma: " + sigma + "\n", log_continue);
        }
        return sigma;
    }
    */

    /*
    public static double Sigma_try(NeuralNetwork nn, IdxReader idxReader, Gradient gradient) throws Exception {

        double[] grad = gradient.getGradient();
        NeuralNetwork testNN;
        double sigma = 1;

        testNN = GradAdaptNN(nn,sigma,gradient);
        testNN.propagate_get_avgerror(idxReader);


        return 0;
    }
     */








    public static void print_n_log(String message, boolean log_continue) throws IOException, InterruptedException {
        System.out.println(message);
        Log.writeFullLog(message + "\n", log_continue);
    }




    public static void test_gradient(NeuralNetwork nn,IdxReader idxReader, double sigma) throws Exception {
        int n = 6;
        int graphhight = 25;
        double stepsize = sigma / n;
        double[] sigmas = new double[2*n+1];
        //-4E-16 -2E-16 -1E-16 0 1E-16 2E-16 4E-16
        for(int k = -n ; k <= n ; k++){
            sigmas[k+n] = Math.signum(k) * Math.pow(2,Math.abs(k)) * stepsize;
        }

        double[] errors = new double[2*n+1];
        double[] accus = new double[2*n+1];
        NeuralNetwork testNN;
        for(int k = -n ; k <= n ; k++){
            testNN = new NeuralNetwork(nn);
            testNN.GradAdapt(sigma);
            //testNN = Function.GradAdaptNN(nn,sigmas[k+n],nn.gradient_weights);
            errors[k+n] = testNN.propagate_get_avgerror(idxReader);
            accus[k+n] = testNN.propagate_get_best_bet_accuracy(idxReader);
        }

        for(int k = -n ; k <= n ; k++){
            System.out.println("sigma: " + roundoff(sigmas[k+n],5) + "- avg_error: " + roundoff(errors[k+n],5) + " - accuracy: " + roundoff(accus[k+n],5));
            //System.out.format("%.5f",errors[k+n]);
        }

        double max = 0;
        double min = 20;
        for(int k = -n ; k <= n ; k++){
            if(errors[k+n] < min) min = errors[k+n];
            if(errors[k+n] > max) max = errors[k+n];
        }
        System.out.println("errors: max = " + max + " - min = " + min);

        String lengthsync = "";
        for(int k = -n ; k <= n ; k++){
            lengthsync = "";
            if(sigmas[k+n]==0.0) lengthsync = "0000-";
            if(sigmas[k+n]>0.0) lengthsync = "-";
            System.out.print("sigma: " + roundoff(sigmas[k+n],5) + lengthsync + " - errors - ");
            for(int l = 0 ; l < graphhight * (errors[k+n]-min)/(max-min) ; l++) System.out.print("#");
            System.out.println("");
        }

        max = 0;
        min = 1;
        for(int k = -n ; k <= n ; k++){
            if(accus[k+n] < min) min = accus[k+n];
            if(accus[k+n] > max) max = accus[k+n];
        }
        System.out.println("accuracies: max = " + max + " - min = " + min);

        for(int k = -n ; k <= n ; k++){
            lengthsync = "";
            if(sigmas[k+n]==0.0) lengthsync = "0000-";
            if(sigmas[k+n]>0.0) lengthsync = "-";
            System.out.print("sigma: " + roundoff(sigmas[k+n],5) + lengthsync + " - accuracies - ");
            for(int l = 0 ; l < graphhight * (accus[k+n]-min)/(max-min) ; l++) System.out.print("#");
            System.out.println("");
        }


    }

    public static double innerProd(double[] a, double[] b){
        if(a.length != b.length) throw new IllegalArgumentException("Must be same length.");
        double result = 0;
        for(int i = 0 ; i < a.length ; i++){
            result += a[i]*b[i];
        }
        return result;
    }

    public static double error(double out, double lable){
        double output = lable-out;
        //double output = Math.signum(lable-out)*0.5*Math.pow(lable-out,2);

        //if(Double.isNaN(output)) throw new IllegalStateException();
        //if(output > 0) output *= 9;
        return output;
    }

    static double roundoff(double input, int dec){
        return Double.valueOf((int) (input * Math.pow(10,dec))) / Math.pow(10,dec);
    }

    static double atan1(double input){
        return 2/Math.PI * Math.atan(input);
    }

    static void NaN_Test(double input){
        if(Double.isNaN(input)) throw new IllegalStateException();
    }

    static void NaN_Test(double[] input){
        for(double num : input){
            if(Double.isNaN(num)) throw new IllegalStateException();
        }
    }

    static void NaN_Test(double[][] input){
        for(double[] list : input){
            for(double weight : list){
                if(Double.isNaN(weight)){
                    throw new IllegalStateException();
                }
            }
        }
    }



    /*
    public static double asquish(double input){
        return Math.tan((Math.PI/2)*(input));
        //return Math.tan(Math.atan(1)*input);
        //return 1/Math.pow(Math.cosh(input),2);
        //return Math.sqrt(-1/Math.log(input));
    }
     */

    /*
    public static double squishprime(double input){
        return (2/Math.PI)*(1/(Math.pow(input,2)+1));
        //return (1/Math.atan(1))*(1/(Math.pow(input,2)+1));
        //return 1 - Math.pow(squish(input),2);
        //return 2/Math.pow(input,3)*squish(input);
    }
     */

    public static double armijo(NeuralNetwork nn, double[][] trainingdata, double[][] lables, int outputnodenum,Gradient gradient,int cores) throws Exception {

        double gamma = .1;
        double beta = .5;
        double sigma = .5;
        double[] out;
        double[] error = new double[outputnodenum];
        int datasize = trainingdata.length;
        double errortotal = 0;
        //nn.updateWeightedConnectionsList();
        //NeuralNetwork testNN = new NeuralNetwork(nn.layerDimensions,nn.weightedConnectionsList,nn.biases);
        NeuralNetwork testNN = new NeuralNetwork(nn);
        double avg_error_before = 0;
        double avg_error_after = 0;
        //int bias = nn.biases ? 1 : 0;

        avg_error_before = Function.avgError(testNN,trainingdata,lables,outputnodenum,cores);

        double[] grad;
        int partial;
        double[][] weights;
        double[][] newweights;
        int rightbias;

        System.out.print(sigma + " ");

        //change sigma until armijo is satisfied
        do {
            testNN = new NeuralNetwork(nn);
            testNN.GradAdapt(sigma);
            //testNN = Function.GradAdaptNN(nn,sigma,gradient);
            avg_error_after = Function.avgError(testNN,trainingdata,lables,outputnodenum,cores);

            if( avg_error_after - avg_error_before <= - sigma * gamma ){ //Armijo condition
                System.out.println(sigma);
                return sigma;
            }

            //nn.updateWeightedConnectionsList();
            sigma *= beta;

            //testNN = new NeuralNetwork(nn.layerDimensions,nn.weightedConnectionsList,nn.biases);
            testNN = new NeuralNetwork(nn);
            System.out.print(sigma + " ");

        }while (sigma >= .01);
        return sigma;
    }

    static void createimagecheck(int num_pics, IdxReader idxReader) throws Exception {
        System.out.println("data check pic creation");
        for(int pic=1;pic<=num_pics;pic++){
            String name = "";
            int index = (int)(Math.random() * idxReader.data.length);
            GrayscaleImage testimg = new GrayscaleImage(idxReader.data[index], "1:1");
            int label = 0;
            for(int i=0;i<idxReader.labels[index].length;i++){
                label = idxReader.labels[index][i]==1?i:label;
            }
            name += label;
            testimg.makeJPG(Trainer.basepath, name);
        }
        System.out.println("data check pic creation end");
    }


}
