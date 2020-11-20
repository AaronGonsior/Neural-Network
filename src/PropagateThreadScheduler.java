public class PropagateThreadScheduler {
    NeuralNetwork[] thread_nns;
    int cores;
    PropagateThread[] threads;

    public PropagateThreadScheduler(int cores, NeuralNetwork[] thread_nns){
        this.thread_nns = thread_nns;
        this.cores = cores;
        threads = new PropagateThread[cores];
        for(int core = 0 ; core < cores ; core++){
            threads[core] = new PropagateThread(core,thread_nns[core]);
        }
    }

    double[][] propagate(double[][] trainingdata) throws InterruptedException {
        int outputnodenum = thread_nns[0].layers[thread_nns[0].layers.length-1].nodes.length;
        int done = 0;
        int imgnum = trainingdata.length;
        int nextimg = 0;
        int[] indices = new int[cores];
        boolean[] doneList = new boolean[imgnum];
        double[][] out = new double[imgnum][outputnodenum];
        double[] curout;
        for(int i = 0 ; i < imgnum ; i++){
            doneList[i] = false;
        }
        //double[][] images = new double[size][nn.layers[0].nodes.length];

        for(int thread = 0 ; thread < cores ; thread++){
            indices[threads[thread].number] = nextimg;
            threads[thread].giveInput(trainingdata[nextimg++]);
            //System.out.println("Thread " + threads[thread].number + " got image " + nextimg + " assigned for processing.");
            threads[thread].start();
        }

        while(done < imgnum){
            for(int thread = 0 ; thread < cores ; thread++){
                if(!threads[thread].isAlive()){
                    curout = threads[thread].getOutput();

                    for(int i = 0 ; i < outputnodenum ; i++){
                        out[indices[threads[thread].number]][i] = curout[i];
                    }
                    //System.out.println("image " + indices[threads[thread].number] + " got processed by thread " + threads[thread].number);
                    doneList[indices[threads[thread].number]] = true;
                    done++;

                    if(nextimg < imgnum){
                        indices[threads[thread].number] = nextimg;
                        //System.out.println("Thread " + threads[thread].number + " got image " + nextimg + "assigned for processing.");
                        //thread.giveInput(images[nextimg++]);
                        threads[thread].giveInput(trainingdata[nextimg++]);
                        threads[thread].start();
                    }
                }
            }
            Thread.sleep(1);
        }
        return out;
    }

/*
    public static void main(String[] args) throws InterruptedException {
        int cores = 3;
        PropagateThread[] threads = new PropagateThread[cores];
        for(int core = 0 ; core < cores ; core++){
            //threads[core] = new PropagateThread(core);
        }

        for(PropagateThread thread : threads){
            thread.start();
        }

        for(int thread = 0 ; thread < cores ; thread++){
            System.out.println("Thread " + thread + " isAlive: " + threads[thread].isAlive());
        }

        Thread.sleep(200);

        for(int thread = 0 ; thread < cores ; thread++){
            System.out.println("Thread " + thread + " isAlive: " + threads[thread].isAlive());
        }


        int done = 0;
        int size = 10;
        int nextimg = 0;
        int[] indices = new int[cores];
        boolean[] doneList = new boolean[size];
        double[][] out = new double[size][10];
        for(int i = 0 ; i < size ; i++){
            doneList[i] = false;
        }
        double[][] images = new double[size][748];

        for(PropagateThread thread : threads){
            indices[thread.number] = nextimg;
            System.out.println("Thread " + thread.number + " got image " + nextimg + " assigned for processing.");
            thread.giveInput(images[nextimg++]);
            thread.run();
        }

        while(done < size){
            for(PropagateThread thread : threads){
                if(!thread.isAlive()){

                    out[indices[thread.number]] = thread.getOutput();
                    System.out.println("image " + indices[thread.number] + " got processed.");
                    doneList[indices[thread.number]] = true;
                    done++;

                    if(nextimg < size){
                        indices[thread.number] = nextimg;
                        System.out.println("Thread " + thread.number + " got image " + nextimg + "assigned for processing.");
                        thread.giveInput(images[nextimg++]);
                        thread.run();
                    }
                }
            }
            Thread.sleep(10);
        }
    }

 */
}
