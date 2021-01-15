import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;

public class Client {

    public Client() throws FileNotFoundException {

    }

    public static void main(String[] args) throws Exception {

        String casestr = "digits";

        if(casestr.equals("leftright")){

            String leftrightDir = "G:\\Users\\Aaron\\IdeaProjects\\NeuralNetHelloWorld\\NeuralNets\\leftright";
            String NNdir = "savedNN03";
            String inputdir = "purenoise";

            boolean biases = true;
            double[] network_input;
            int[] layerdims;


            //layerdims = new int[]{100,5,2,2};
            layerdims = new int[]{100,2};


            WeightedConnections[] weightedConnectionsList = new WeightedConnections[layerdims.length-1];
            for(int i = 0 ; i < weightedConnectionsList.length -1 ; i++){
                weightedConnectionsList[i] = new WeightedConnections(layerdims[i],layerdims[i+1]);
            }
            weightedConnectionsList[weightedConnectionsList.length -1] = new WeightedConnections(layerdims[weightedConnectionsList.length -1],layerdims[weightedConnectionsList.length ]);
            NeuralNetwork nn = new NeuralNetwork(null,layerdims,weightedConnectionsList);

            File nnweights = new File("G:\\Users\\Aaron\\IdeaProjects\\NeuralNetHelloWorld\\NeuralNets\\leftright\\"+NNdir+".txt");
            nn.load(nnweights);


            for(int i = 0 ; i < nn.layerConnections.length ; i++){
                nn.layerConnections[i].weightedConnections.makeJPG("G:\\Users\\Aaron\\IdeaProjects\\NeuralNetHelloWorld\\NeuralNets\\leftright\\clientweightprint\\","layerc_+"+i+"_itend");
            }


            // get pixels
            File myimg = new File("G:\\Users\\Aaron\\IdeaProjects\\NeuralNetHelloWorld\\NeuralNets\\leftright\\myinput\\"+inputdir+".jpg");
            BufferedImage img = ImageIO.read(myimg);
            double[][] image = new double[img.getHeight()][img.getWidth()];
            for(int i = 0 ; i < img.getHeight() ; i++){
                for(int j = 0 ; j < img.getWidth() ; j++){
                    image[i][j]=-(double)(img.getRGB(j,i))/(double)(255*65536+255*256+255);
                }
            }

            //translate img into node activations
            double[] img1D = new double[img.getHeight()*img.getWidth()];
            for(int i = 0 ; i < 10 ; i++){
                for(int j = 0 ; j < 10 ; j++){
                    img1D[10*j+i] = image[j][i];
                }
            }

            if(biases){
                network_input = new double[img1D.length+1];
                for (int i = 0; i < img1D.length; i++) {
                    network_input[i] = img1D[i];
                }
                network_input[img1D.length] = 1; //bias node
            }
            else{
                network_input = new double[img1D.length];
                network_input = img1D;
            }

            double[] output = nn.propagate(network_input);
            System.out.println("Left: "+output[0]+" Right: "+output[1]);

        }

        if(casestr.equals("digits")){

            String digitsDir = "G:\\Users\\Aaron\\IdeaProjects\\NeuralNetHelloWorld\\NeuralNets\\digits\\";
            String NNdir = "savedNN";
            String inputdir = "test2";

            boolean biases = true;
            double[] network_input;
            int[] layerdims;

            layerdims = new int[]{784,25,16,10};

            WeightedConnections[] weightedConnectionsList = new WeightedConnections[layerdims.length-1];
            for(int i = 0 ; i < weightedConnectionsList.length -1 ; i++){
                weightedConnectionsList[i] = new WeightedConnections(layerdims[i],layerdims[i+1]);
            }
            weightedConnectionsList[weightedConnectionsList.length - 1] = new WeightedConnections(layerdims[weightedConnectionsList.length - 1],layerdims[weightedConnectionsList.length ]);
            NeuralNetwork nn = new NeuralNetwork(null,layerdims,weightedConnectionsList);

            File nnweights = new File(digitsDir+NNdir+".txt");
            nn.load(nnweights);

            for(int i = 0 ; i < nn.layerConnections.length ; i++){
                nn.layerConnections[i].weightedConnections.makeJPG(digitsDir+"clientweightprint\\","layerc_+"+i);
            }

            // get pixels
            File myimg = new File(digitsDir+"myinput\\"+inputdir+".png");
            BufferedImage img = ImageIO.read(myimg);
            double[][] image = new double[img.getHeight()][img.getWidth()];
            for(int i = 0 ; i < img.getHeight() ; i++){
                for(int j = 0 ; j < img.getWidth() ; j++){
                    image[i][j]=-(double)(img.getRGB(j,i))/(double)(255*65536+255*256+255);
                }
            }

            //translate img into node activations
            double[] img1D = new double[img.getHeight()*img.getWidth()];
            for(int i = 0 ; i < img.getHeight() ; i++){
                for(int j = 0 ; j < img.getWidth() ; j++){
                    img1D[img.getHeight()*j+i] = image[j][i];
                }
            }

            if(biases){
                network_input = new double[img1D.length+1];
                for (int i = 0; i < img1D.length; i++) {
                    network_input[i] = img1D[i];
                }
                network_input[img1D.length] = 1; //bias node
            }
            else{
                network_input = new double[img1D.length];
                network_input = img1D;
            }

            double[] output = nn.propagate(network_input);
            int maximizer = 0;
            double maximum = 0;
            for(int digit = 0 ; digit < 10 ; digit++){
                System.out.println("Digit "+digit+" : "+output[digit]);
                if(output[digit] > maximum){
                    maximum = output[digit];
                    maximizer = digit;
                }
            }
            System.out.println("Best bet: " + maximizer);

        }

    }

}
