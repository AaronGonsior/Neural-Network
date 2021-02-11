import java.util.ArrayList;
import java.util.function.DoublePredicate;

public class Trainer {

    NeuralNetwork nn;
    //public int inputnodenum;
    //int outputnodenum;
    double avg_error;
    String name;
    IdxReader idxReader;
    double best_bet_accuracy;
    boolean running;
    boolean final_;
    static final String basepath = System.getProperty("user.dir");
    NetworkDrawer nd;
    GradientDrawer gd;
    ChartDrawer cd;
    PropagateDrawer pd;
    int iteration;
    Problem digitclassification;

    ArrayList avgErrors = new ArrayList<Double>();
    ArrayList bestBetAccus = new ArrayList<Double>();


    public Trainer(){

    }



    void setNN(int[] layerdims,String name){

        final_ = false;
        this.name = name;
        iteration = 0;
        avg_error = 20;
        best_bet_accuracy = 0;

        WeightedConnections[] weightedConnectionsList = new WeightedConnections[layerdims.length-1];
        for(int i = 0 ; i < weightedConnectionsList.length ; i++){
            weightedConnectionsList[i] = new WeightedConnections(layerdims[i],layerdims[i+1]);
        }
        this.nn = new NeuralNetwork(this,layerdims,weightedConnectionsList);
        nn.gradient = new Gradient(nn);
        nn.setName("NN");
    }

    void train(IdxReader idxReader, int iterations) throws Exception {

        String message;

        running = true;
        this.idxReader = idxReader;
        boolean log_continue = true;
        //int outputnodenum  = nn.layerDimensions[nn.layerDimensions.length-1];;
        //double[] error = new double[outputnodenum];
        //double[] out;
        double sigma;
        //double errortotal;
        double[][] errors;

        boolean testing = true;

        for(int iteration = 1 ; iteration <= iterations ; iteration++){

            this.iteration++;

            message = name + ": " + "Iteration " + iteration;
            Function.print_n_log(message,log_continue);


            int sigma_case = 0;

            sigma = 0;
            switch (sigma_case) {
                case 0:
                    sigma = 1;
                    Function.print_n_log("Caution: sigma is constant! (" + sigma + ")",log_continue);
                    break;
                case 1:
                    double frac = 1;
                    sigma = avg_error*frac;
                    Function.print_n_log("Caution: sigma is a fraction (" + frac + ") of avg_error (" + sigma + ")",log_continue);
                    break;
                case 2:
                    System.exit(1);
                    //sigma = Function.armijo(nn,idxReader.data,idxReader.labels,10,nn.gradient_weights,1);
                    Function.print_n_log("Caution: sigma is determined by armijo (beta) ",log_continue);
                    break;
                case 3:
                    /*
                    sigma = Function.PowellWolfe(nn,idxReader.data,idxReader.lables,outputnodenum,nn.gradient_weights,1);
                    if(sigma == 0){

                        message = name + " reached local minimum. optimizing stops";
                        Function.print_n_log(message,log_continue);

                        final_ = true;
                        running = false;
                        break;
                    }
                     */
                     break;
            }
            if(sigma==0){
                System.out.println("sigma is 0! - break");
                System.exit(1);
            }



            boolean grad_testing = false;
            // ----- testing -----
            if(grad_testing){
                System.out.print("grad_test");
                Function.test_gradient(nn,idxReader,sigma);
                System.out.print("start gradadapt");
            }
            // ----- end testing -----


            errors = nn.propagate(idxReader);

            //nn.gradient.normalize();
            nn.backprop(idxReader,errors,sigma);
            int batchsize = 30;
            //nn.backprop(idxReader,errors,batchsize,sigma);

            //nn.gradient.makeJPG(); -- already in gd.update()
            gd.update();

            this.avg_error = nn.averageError;
            this.best_bet_accuracy = nn.best_bet_accuracy;


            //nn.GradAdapt(sigma); new: in nn.backprop()

        }



        // --------- testing ----------
        if(testing){
            switch (Util.getOS()) {
                case WINDOWS:

                    int num_print = 0;

                    System.out.println("testpic");
                    //best_bet_accuracy = nn.propagate_get_best_bet_accuracy(idxReader);
                    //double[][] labels = idxReader.getLables();
                    //double[][] data = idxReader.getData();

                    for (int pic = 0; pic < num_print; pic++) {
                        GrayscaleImage testimg = new GrayscaleImage(idxReader.data[pic], "1:1");
                        double[] test_out;
                        test_out = nn.propagate(idxReader.data[pic]);
                        String name = "(" + Function.roundoff(avg_error,2) + " - " + Function.roundoff(best_bet_accuracy * 100, 2) + "%) ";

                        int label = 0;
                        for (int i = 0; i < idxReader.labels[pic].length; i++) {
                            if (idxReader.labels[pic][i] == 1) label = i;
                        }
                        name += "label (" + label + ") ";

                        double max = 0;
                        int best_bet = 0;
                        for (int i = 0; i < test_out.length; i++) {
                            if (test_out[i] > max) {
                                max = test_out[i];
                                best_bet = i;
                            }
                        }

                        name += "best_bet (" + best_bet + ") ";

                        name += "out - ";
                        for (int i = 0; i < test_out.length; i++) {
                            name += "#" + i + "(" + Function.roundoff(test_out[i],2) + ") ";
                        }

                        double[] test_error = new double[test_out.length];
                        for (int i = 0; i < test_out.length; i++) {
                            test_error[i] = Function.error(test_out[i], idxReader.labels[pic][i]);
                        }

                        name += "errors - ";
                        for (int i = 0; i < test_out.length; i++) {
                            name += "#" + i + "(" + Double.valueOf((int) (test_error[i] * 100)) / 100 + ") ";
                        }

                        testimg.makeJPG(basepath, name);
                        //testimg.makeJPG(basepath,"test");
                    }

                    Function.print_n_log("(" + Function.roundoff(avg_error,2) + " - " + Function.roundoff(best_bet_accuracy * 100, 2) + "%) ",log_continue);

                case LINUX:
                    Function.print_n_log("(" + Function.roundoff(avg_error,2) + " - " + Function.roundoff(best_bet_accuracy * 100, 2) + "%) ",log_continue);
            }
        }
        // --------- end testing ----------







        //this.avg_error = nn.propagate_get_avgerror(idxReader);
        //this.best_bet_accuracy = nn.propagate_get_best_bet_accuracy(idxReader);

        running = false;
    }


    public static void main(String[] args) throws Exception {


        int iterations = 1;
        int rounds = 1000;

        String continue_path = "";
        String inputImagePath = "";
        String inputLabelPath = "";
        switch (Util.getOS()) {
            case WINDOWS:
                continue_path = basepath + "\\digits\\savedNN.txt";
                inputImagePath = basepath + "\\digits\\Trainingdata\\train-images.idx3-ubyte";
                inputLabelPath = basepath + "\\digits\\Trainingdata\\train-labels.idx1-ubyte";
                break;
            case LINUX:
                continue_path = basepath + "/digits/savedNN.txt";
                inputImagePath = basepath + "/digits/Trainingdata/train-images.idx3-ubyte";
                inputLabelPath = basepath + "/digits/Trainingdata/train-labels.idx1-ubyte";
        }


        Trainer trainer = new Trainer();
        //set up neural network structure
        int inputnodenum = 28*28;
        int outputnodenum = 10;
        int[] layerdims = new int[]{inputnodenum,16,10,outputnodenum};

        trainer.setNN(layerdims,null);
        trainer.nn.setName("savedNN_single");
        trainer.name = "single_trainer";
        //trainer.nn.load(new File(continue_path));


        String savepath_basic = "";
        String savepath_detailed = "";
        String GreenRedSavePath = "";
        switch (Util.getOS()) {
            case WINDOWS:
                savepath_basic = basepath + "\\single_test\\" + trainer.nn.getName() + ".txt";
                savepath_detailed = basepath + "\\single_test\\" + trainer.nn.getName() + "#" + Function.roundoff(trainer.nn.getAverageError(),2) + ".txt";
                GreenRedSavePath = basepath + "\\single_test\\weights";
                break;
            case LINUX:
                savepath_basic = basepath + "/single_test/" + trainer.nn.getName() + ".txt";
                savepath_detailed = basepath + "/single_test/savedNN" + trainer.nn.getName() + "#" + trainer.nn.getName() + ".txt";
                GreenRedSavePath = basepath + "/single_test/weights";
        }

        boolean multimonitor = true;
        char monitor_num = 't';

        switch (Util.getOS()) {
            case WINDOWS:

                trainer.gd = new GradientDrawer(trainer.nn.gradient);
                trainer.cd = new ChartDrawer(null,new String[]{"avgError","BestBetAccu"});
                trainer.pd = new PropagateDrawer(trainer.nn,trainer);
                trainer.nd = new NetworkDrawer(trainer.nn, trainer);


                if(multimonitor){
                    switch (monitor_num){
                        case 'r':
                            trainer.gd.setResolution(1450,710);
                            trainer.gd.setPosition(2400,-100);

                            trainer.cd.setResolution(950,550);
                            trainer.cd.setPosition(2400,-600);

                            trainer.nd.setResolution(550,550);
                            trainer.nd.setPosition(3300,-630);
                            break;

                        case 't':
                            trainer.gd.setResolution(950,600);
                            trainer.gd.setPosition(475,-1080+550-90);

                            trainer.cd.setResolution(950,550);
                            trainer.cd.setPosition(475,-1080-40);

                            trainer.pd.setResolution(550,600);
                            trainer.pd.setPosition(475+900,-1080+550-90);

                            trainer.nd.setResolution(550,550);
                            trainer.nd.setPosition(475+900,-1080-40);


                            break;
                    }

                }

        }

        switch (Util.getOS()){
            case WINDOWS:
                for(int i = 0 ; i < trainer.nn.layerConnections.length ; i++){
                    trainer.nn.layerConnections[i].weightedConnections.makeJPG(GreenRedSavePath,"layerc_"+i);
                }
                trainer.nd.update();

                trainer.nn.gradient.makeJPG();
                trainer.gd.update();

        }


        boolean shuffle = false;
        boolean round_shuffle = false;
        IdxReader idxReader = new IdxReader(inputImagePath,inputLabelPath,shuffle);



        //testimg
        Function.createimagecheck(10,idxReader);
        //idxReader.original(inputLabelPath,inputImagePath);
        System.out.println("images created");

        //needed for avgerror variable in first train?
        //trainer.nn.propagate(idxReader);


        Problem digitclassification = new ImageClassification(28,28,10,idxReader);



        for(int round = 0 ; round < rounds ; round++){

            if(round_shuffle) idxReader.shuffle();

            trainer.train(idxReader,iterations);

            switch (Util.getOS()){
                case WINDOWS:
                    trainer.nn.propagate(idxReader);

                    System.out.println("round " + round + " training is over");
                    for(int i = 0 ; i < trainer.nn.layerConnections.length ; i++){
                        trainer.nn.layerConnections[i].weightedConnections.makeJPG(GreenRedSavePath,"layerc_"+i);
                    }
                    trainer.nd.update();
                    trainer.pd.update(0);

            }


            trainer.avgErrors.add(Function.roundoff(trainer.avg_error,3));
            trainer.bestBetAccus.add(Function.roundoff(trainer.best_bet_accuracy,5));

            ArrayList[] charts = new ArrayList[2];
            charts[0] = trainer.avgErrors;
            charts[1] = trainer.bestBetAccus;
            trainer.cd.update(charts);

        }


        trainer.nn.save(savepath_basic);
        trainer.nn.save(savepath_detailed);
        trainer.nn.layerConnections[trainer.nn.layerConnections.length-1].weightedConnections.makeJPG(GreenRedSavePath,"layerc_0");

    }


}
