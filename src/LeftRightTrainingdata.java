import java.io.IOException;

public class LeftRightTrainingdata {

    public double[][] images;
    public double[][] lables;
    public int dimension = 10;
    public String savepath;

    public LeftRightTrainingdata(int imgnum, double maxstrengthdiv, double noise, double avgstrengthdiv, String savepath,double purenoise){
        double imgstrength;
        double[][] image = new double[dimension][dimension];
        images = new double[imgnum][dimension*dimension];
        lables = new double[imgnum][2];
        for (int img = 0; img < imgnum; img++) {
            imgstrength = Math.random()*avgstrengthdiv + (1-avgstrengthdiv);
            int randpixel =  (int)Math.floor(Math.random()*(dimension*dimension+1)) ;
            for (int i = 0; i < dimension; i++) {
                for (int j = 0; j < dimension; j++) {
                    if ( (int)((double)randpixel / (double)dimension) == i && randpixel % dimension == j && img >= purenoise*imgnum ) {
                        //image[i][j] = 1;
                        image[i][j] = imgstrength * ( 1 - Math.random() * maxstrengthdiv );
                        //if (Math.floorMod(randpixel, dimension) < dimension/2) {
                        if ( randpixel % dimension < dimension/2 ) {
                            lables[img][0] = 1;
                            lables[img][1] = -1;
                        } else {
                            lables[img][0] = -1;
                            lables[img][1] = 1;
                        }
                    }
                    else{
                        image[i][j] = imgstrength * ( Math.random() * noise );
                        //image[i][j] = 0;
                    }
                }
                for (int k = 0; k < dimension; k++) {
                    for (int j = 0; j < dimension; j++) {
                        images[img][dimension * j + k] = image[j][k];
                    }
                }
            }

            this.savepath=savepath;
        }
    }
    public double[][] getData(){
        return images;
    }

    public double[][] getLables(){
        return lables;
    }


    public void makeImgaes() throws IOException {
        GrayscaleImage currentimg;
        double[][] currentimage = new double[dimension][dimension];
        for(int img = 0 ; img < images.length ; img++){
            for(int pixel = 0 ; pixel < dimension*dimension ; pixel++){
                    currentimage[(int)Math.floor(pixel / dimension)][Math.floorMod(pixel, dimension)] = images[img][pixel];
            }
            currentimg = new GrayscaleImage(currentimage);
            currentimg.makeJPG(savepath,"im_"+img);
        }
    }

    public void makeImgaes(int limit) throws IOException {
        GrayscaleImage currentimg;
        double[][] currentimage = new double[dimension][dimension];
        for(int img = 0 ; img < limit ; img++){
            for(int pixel = 0 ; pixel < dimension*dimension ; pixel++){
                currentimage[(int)Math.floor(pixel / dimension)][Math.floorMod(pixel, dimension)] = images[img][pixel];
            }
            currentimg = new GrayscaleImage(currentimage);
            currentimg.makeJPG(savepath,"im_"+img);
        }
    }

}
