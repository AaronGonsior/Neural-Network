public class ParallelTrainer implements Runnable {

    Trainer trainer;
    boolean running;
    IdxReader idxReader;
    int iterations;
    int NN_counter;

    String message;

    public ParallelTrainer(IdxReader idxReader,int iterations,int NN_counter){
        this.NN_counter = NN_counter;
        trainer = new Trainer();
        this.idxReader = idxReader;
        this.iterations = iterations;
    }

    void setNN(int[] layerdims , boolean biases){
        trainer.setNN(layerdims,"Trainer " + NN_counter);
    }

    void setNN(int[] layerdims , boolean biases,String name){
        trainer.setNN(layerdims,name);
    }

    public void run() {
        running = true;
        try {

            if(!trainer.final_){

                message = trainer.name + " starts training for " + iterations + " iterations";
                System.out.println(message);
                Log.writeFullLog(message+"\n",true);

                Thread.sleep(100);

                message = trainer.name + ": avg_error before training for " + iterations + " iterations: " + trainer.avg_error;
                message += " and the accuracy is " + trainer.best_bet_accuracy*100 + "%";
                System.out.println(message);
                Log.writeFullLog(message+"\n",true);

                trainer.train(idxReader,iterations);

                message = trainer.name + ": avg_error after training for " + iterations + " iterations: " + trainer.avg_error;
                message += " and the accuracy is " + trainer.best_bet_accuracy*100 + "%";
                System.out.println(message);
                Log.writeFullLog(message+"\n",true);

            }
            else{
                message = trainer.name + " is already final";
                System.out.println(message);
                Log.writeFullLog(message+"\n",true);
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        running = false;
    }

}
