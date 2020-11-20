import java.io.File;

public class ParallelTrainingScheduler {


    public static void main(String[] args) throws Exception {

        String message;

        message = "New Run started";
        System.out.println(message);
        Log.writeFullLog(message+"\n",true);

        //set up scenario
        Util.TrainingScenario scenario = Util.TrainingScenario.DIGITS28x28;
        Util.NetworkStructure structure = Util.NetworkStructure.Basic_16_16_Bias;
        final String basepath = System.getProperty("user.dir");
        String continue_path = "";
        String inputImagePath = "";
        String inputLabelPath = "";
        switch (Util.getOS()) {
            case WINDOWS:
                continue_path = basepath + "\\digits\\NN.txt";
                inputImagePath = basepath + "\\digits\\Trainingdata\\train-images.idx3-ubyte";
                inputLabelPath = basepath + "\\digits\\Trainingdata\\train-labels.idx1-ubyte";
                break;
            case LINUX:
                continue_path = basepath + "/digits/NN.txt";
                inputImagePath = basepath + "/digits/Trainingdata/train-images.idx3-ubyte";
                inputLabelPath = basepath + "/digits/Trainingdata/train-labels.idx1-ubyte";
        }
        boolean continue_ = true;
        boolean biases = true;
        int[] layerdimensions = new int[]{};
        switch (scenario){
            case DIGITS28x28:
                switch (structure){
                    case Basic_16_16_Bias:
                        biases = true;
                        layerdimensions = new int[]{28*28,16+1,16+1,10};
                }
        }

        //read training data
        IdxReader idxReader = new IdxReader(inputImagePath,inputLabelPath,true);


        // set up parallel trainers
        int NN_counter = 1;
        int num_parallel = 2;
        int iterations = 10;
        int num_trainer;
        double[] avg_errors;
        //int index_highest = 0;
        int index_lowest = 0;
        double[] new_avg_error;

        message = "Initial NN gets created";
        System.out.println(message);
        Log.writeFullLog(message+"\n",true);

        ParallelTrainer current_best_parallelTrainer = new ParallelTrainer(idxReader,iterations,NN_counter);
        Thread current_best_parallelThread = new Thread(current_best_parallelTrainer);
        current_best_parallelTrainer.setNN(layerdimensions,biases,"CurrentBest");
        if(continue_){
            File savedNN = new File(continue_path);
            current_best_parallelTrainer.trainer.nn.load(savedNN);

            double loaded_avgerror;
            loaded_avgerror = current_best_parallelTrainer.trainer.nn.propagate_get_avgerror(idxReader);
            current_best_parallelTrainer.trainer.avg_error = loaded_avgerror;

            double loaded_best_bet_accuracy;
            loaded_best_bet_accuracy = current_best_parallelTrainer.trainer.nn.propagate_get_best_bet_accuracy(idxReader);
            current_best_parallelTrainer.trainer.best_bet_accuracy = loaded_best_bet_accuracy;

            message = "A saved NN (" + continue_path + ") got loaded into the current best NN. It has an avg_error of " + loaded_avgerror + " and an accuracy of " + loaded_best_bet_accuracy*100 + "%";
            System.out.println(message);
            Log.writeFullLog(message+"\n",true);

        }


        current_best_parallelThread.start();

        Thread.sleep(10);
        while(current_best_parallelTrainer.running){
            Thread.sleep(1);
        }
        message = "\n";
        System.out.println(message);
        Log.writeFullLog(message,true);
        //current_best_parallelThread.join();

        int generation = 0;
        boolean any_running;
        String groupnums;

        do{
            message = "Generation " + generation;
            System.out.println(message);
            Log.writeFullLog(message+"\n",true);

            num_trainer = (int) Math.pow(num_parallel,generation++);

            ParallelTrainer[] parallelTrainers = new ParallelTrainer[num_trainer];
            Thread[] parallelThreads = new Thread[num_trainer];

            message = num_trainer + " new NNs got created to find a new competitor";
            System.out.println(message);
            Log.writeFullLog(message+"\n",true);

            message = "All " + num_trainer + " are getting set up";
            System.out.println(message);
            Log.writeFullLog(message+"\n",true);

            for(int trainer_num = 0 ; trainer_num < num_trainer ; trainer_num++){
                parallelTrainers[trainer_num] = new ParallelTrainer(idxReader,iterations,NN_counter++);
                parallelTrainers[trainer_num].setNN(layerdimensions,biases);

                parallelTrainers[trainer_num].trainer.avg_error = parallelTrainers[trainer_num].trainer.nn.propagate_get_avgerror(idxReader);

            }

            avg_errors = new double[num_trainer];
            new_avg_error = new double[num_trainer / 2];
            ParallelTrainer[] new_parallelTrainers = new ParallelTrainer[num_trainer/2];


            while(num_trainer > 1){

                message = "Round elimination starts between " + num_trainer + " Trainers: ";
                //System.out.print(message);
                //Log.writeFullLog(message,true);

                groupnums = parallelTrainers[0].NN_counter + "";
                for(int trainer_num = 1 ; trainer_num < num_trainer ; trainer_num++){
                    groupnums += "," + parallelTrainers[trainer_num].NN_counter;
                }
                message += groupnums;
                System.out.println(message);
                Log.writeFullLog(message+"\n",true);

                for(int trainer_num = 0 ; trainer_num < num_trainer ; trainer_num++){
                    for(int thread_num = 0 ; thread_num < num_trainer ; thread_num++){
                        parallelThreads[thread_num] = new Thread(parallelTrainers[thread_num]);
                    }
                    parallelThreads[trainer_num].start();

                    parallelThreads[trainer_num].setPriority(1);
                    System.out.println("prio set");
                }

                any_running = true;
                while (any_running) {
                    any_running = false;
                    for (int trainer_num = 0; trainer_num < num_trainer; trainer_num++) {
                        if (parallelTrainers[trainer_num].running) any_running = true;
                    }
                    Thread.sleep(1);
                }
                for(int trainer_num = 0 ; trainer_num < num_trainer ; trainer_num++){
                    parallelThreads[trainer_num].join();
                }

                //round elimination
                //only for num_parallel == 2 -- for general num_parallel I need to find a good way to sort avg_errors and find the order of indices
                for(int trainer_num = 0 ; trainer_num < num_trainer ; trainer_num += 2){
                    //index_highest = trainer_num;
                    index_lowest = trainer_num;
                    groupnums = "";
                    for(int batch_index = trainer_num ; batch_index < trainer_num + 2 ; batch_index++){
                        groupnums += parallelTrainers[batch_index].NN_counter + ",";
                        if (avg_errors[batch_index] < avg_errors[index_lowest]) index_lowest = batch_index;
                    }
                    groupnums = groupnums.substring(0,groupnums.length()-1);

                    message = "Trainer " + parallelTrainers[index_lowest].NN_counter + " is the winner of its group (" + groupnums + ") with avg. error: " + parallelTrainers[index_lowest].trainer.avg_error;
                    System.out.println(message);
                    Log.writeFullLog(message+"\n",true);

                    new_avg_error[trainer_num/2] = avg_errors[index_lowest];
                    new_parallelTrainers[trainer_num/2] = parallelTrainers[index_lowest];
                }

                avg_errors = new_avg_error;
                parallelTrainers = new_parallelTrainers;

                num_trainer /= 2;

                for(int trainer_num = 0 ; trainer_num < num_trainer ; trainer_num++){
                    parallelThreads[trainer_num] = new Thread(parallelTrainers[trainer_num]);
                }

                new_avg_error = new double[num_trainer];
                new_parallelTrainers = new ParallelTrainer[num_trainer];

                message = "Round over - Now there are " + num_trainer + " Trainers in the tournament";
                System.out.println(message);
                Log.writeFullLog(message+"\n",true);

                for(int trainer_num = 0 ; trainer_num < num_trainer ; trainer_num++){

                    //parallelTrainers[trainer_num].run();
                    for(int thread_num = 0 ; thread_num < num_trainer ; thread_num++){
                        parallelThreads[thread_num] = new Thread(parallelTrainers[thread_num]);
                    }
                    if(!parallelTrainers[trainer_num].trainer.final_){
                        parallelThreads[trainer_num].start();
                    }
                    else{
                        message = parallelTrainers[trainer_num].trainer.name + " is already final / reached its local minimum";
                        System.out.println(message);
                        Log.writeFullLog(message+"\n",true);
                    }

                }

                Thread.sleep(10);
                any_running = true;
                while (any_running) {
                    any_running = false;
                    for (int trainer_num = 0; trainer_num < num_trainer; trainer_num++) {
                        if (parallelTrainers[trainer_num].running) any_running = true;
                    }
                    Thread.sleep(1);
                }
                for(int trainer_num = 0 ; trainer_num < num_trainer ; trainer_num++){
                    parallelThreads[trainer_num].join();
                }
                //all stopped

                message = "All trainers finished this round";
                System.out.println(message);
                Log.writeFullLog(message+"\n",true);

                /*
                for (int trainer_num = 0; trainer_num < num_trainer; trainer_num++) {
                    message = "Trainer " + parallelTrainers[trainer_num].NN_counter + ": avg. error: " + parallelTrainers[trainer_num].trainer.avg_error;
                    System.out.println(message);
                    Log.writeFullLog(message+"\n",true);
                }
                 */

            }

            current_best_parallelThread = new Thread(current_best_parallelTrainer);
            current_best_parallelThread.start();

            Thread.sleep(10);
            current_best_parallelThread.join();
            while(current_best_parallelTrainer.running){
                Thread.sleep(1);
            }


            message = "Current best NN trained for " + iterations + " iterations - New avg. error: " + current_best_parallelTrainer.trainer.avg_error;
            System.out.println(message);
            Log.writeFullLog(message+"\n",true);

            if(parallelTrainers[0].trainer.avg_error < current_best_parallelTrainer.trainer.avg_error){
                current_best_parallelTrainer = parallelTrainers[0];
                current_best_parallelThread = new Thread(current_best_parallelTrainer);

                message = "The current best NN got defeated by " + parallelTrainers[0].trainer.name;
                System.out.println(message);
                Log.writeFullLog(message+"\n",true);
            }
            else{
                message = "Current best NN defended its position against " + parallelTrainers[0].trainer.name;
                System.out.println(message);
                Log.writeFullLog(message+"\n",true);
            }


            /*
            index_highest = 0;
            index_lowest = 0;
            for (int index = 0; index < num_trainer; index++) {
                if (avg_errors[index] > avg_errors[index_highest]) index_highest = index;
                if (avg_errors[index] < avg_errors[index_lowest]) index_lowest = index;
            }
             */

            /*
            System.out.println("Trainer " + parallelTrainers[index_highest].NN_counter + " performed the worst after " + iterations + "iterations and gets replaced.");
            parallelTrainers[index_highest] = new ParallelTrainer(idxReader, iterations, NN_counter++);
            parallelTrainers[index_highest].setNN(layerdimensions, biases);
            parallelTrainers[index_highest].run();
             */


            //saving
            String savepath_basic = "";
            String savepath_detailed = "";
            String GreenRedSavePath = "";
            switch (Util.getOS()) {
                case WINDOWS:
                    savepath_basic = basepath + "\\digits\\" + current_best_parallelTrainer.trainer.nn.getName() + ".txt";
                    savepath_detailed = basepath + "\\digits\\" + current_best_parallelTrainer.trainer.nn.getName() + "#" + current_best_parallelTrainer.trainer.nn.getAverageError() + ".txt";
                    GreenRedSavePath = basepath + "\\digits\\weights";
                    break;
                case LINUX:
                    savepath_basic = basepath + "/digits/" + current_best_parallelTrainer.trainer.nn.getName() + ".txt";
                    savepath_detailed = basepath + "/digits/" + current_best_parallelTrainer.trainer.nn.getName() + "#" + current_best_parallelTrainer.trainer.nn.getAverageError() + ".txt";
                    GreenRedSavePath = basepath + "/digits/weights";
            }

            current_best_parallelTrainer.trainer.nn.save(savepath_basic);
            current_best_parallelTrainer.trainer.nn.save(savepath_detailed);

            //make JPGs of weights per layerconnection
            for(int layer = 0 ; layer < current_best_parallelTrainer.trainer.nn.layerConnections.length ; layer++){
                current_best_parallelTrainer.trainer.nn.layerConnections[layer].weightedConnections.makeJPG(GreenRedSavePath,"layerc_"+layer);
            }

        }while(true);

    }

}

