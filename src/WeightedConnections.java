import java.io.IOException;

public class WeightedConnections {

    private double[][] weightedConnections;

    public WeightedConnections(int dim1, int dim2){
        double[][] weights = new double[dim1][dim2];
        for(int i = 0 ; i < dim1 ; i++){
            for(int j = 0 ; j < dim2 ; j++){
                weights[i][j] = 2*(Math.random()-.5);
                //weights[i][j] = 1;
            }
        }
        this.weightedConnections = weights;
    }

    public double[][] getWeightedconnections(){
        return weightedConnections;
    }

    void setWeightedConnections(double[][] weightedConnections) throws Exception {
        this.weightedConnections = weightedConnections;
    }

    void makeJPG(String path,String name) throws IOException {

        int pixels;
        pixels = weightedConnections.length;

        GreenRedImage[] images = new GreenRedImage[weightedConnections[1].length];
        int pixeldim = (int) Math.ceil(Math.sqrt(pixels));
        double[][] currentimg = new double[pixeldim][pixeldim];
        for(int map = 0 ; map < weightedConnections[1].length ; map++){
            for(int pixel = 0 ; pixel < pixels ; pixel++)
                currentimg[(int) Math.floor(pixel/pixeldim)][Math.floorMod(pixel,pixeldim)] = weightedConnections[pixel][map];
            images[map] = new GreenRedImage(currentimg);
            images[map].makeJPG(path,"weightsmap"+Integer.toString(map)+"_"+name,pixeldim,true);
        }
    }

}