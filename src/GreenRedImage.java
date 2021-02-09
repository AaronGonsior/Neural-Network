import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GreenRedImage {

    double[][] image;

    public GreenRedImage(double[][] image){
        this.image = image;
    }

    public GreenRedImage(double[] image){
        int size = (int)Math.sqrt(image.length);
        this.image = new double[size][size];
        if(size*size != image.length){
            System.out.println("length is not square - abort");
            System.exit(1);
        }
        for(int pixel = 0 ; pixel < image.length ; pixel++){
            this.image[(int)(pixel/size)][pixel % size] = image[pixel];
        }
    }

    void makeJPG(String path, String name,int dim,boolean amp) throws IOException {

        //boolean amp = true;
        /*
        double maximum = 0;
        if(amp) {
            for (double[] row : image) {
                for (double pixel : row) {
                    maximum = Math.max(maximum, pixel);
                }
            }
        }
        double eps = 1e-100;
        if(maximum < eps){
            maximum = 1;
        }
         */

        double max_abs = 0;
        if(amp){
            for(double[] row : image){
                for(double pixel : row){
                    if(max_abs < Math.abs(pixel) ){
                        max_abs = Math.abs(pixel);
                    }
                }
            }
        }

        double eps = 1e-100;
        if(max_abs < eps) max_abs = 1;


        BufferedImage bufferedImage = new BufferedImage(dim, dim, BufferedImage.TYPE_INT_RGB);

        String fullpath = "";
        switch (Util.getOS()) {
            case WINDOWS:
                fullpath = path + "\\" + name + ".jpg";
                break;
            case LINUX:
                fullpath = path + "/" + name + ".jpg";
        }

        File file = new File(fullpath);

        double R=0,G=0,B=0;
        for(int i = 0; i < image[0].length ; i++){
            for(int j = 0 ; j < image[1].length ; j++){
                if(image[i][j]>=0){
                    //double R=1-(1/(maximum))*(image[i][j]);
                    R = Math.min(255,Math.max(0,    (int)(255*(  1 - /*(1/(max_abs)) * */ this.image[i][j] ))    ));
                    //R = 0;

                    //double G = 255 - (int)(255*( 1 - (1/(maximum)) * this.image[i][j] ));
                    G = 255;

                    //double B=1-(1/(maximum))*(image[i][j]);
                    B = Math.min(255,Math.max(0,      (int)(255*( 1 - /*(1/(max_abs)) * */this.image[i][j] ))     ));
                    //B = 0;

                }
                else if(image[i][j]<0) {
                    //double R = 255 - (int)(255*( 1 - (1/(maximum)) * this.image[i][j] ));
                    R = 255;

                    //double G=1+(1/(maximum))*(image[i][j]);
                    G = Math.min(255,Math.max(0,    (int)(255*( 1 +  /*(1/(max_abs)) * */ this.image[i][j] ))    ));
                    //G = 0;

                    //double B=1+(1/(maximum))*(image[i][j]);
                    B = Math.min(255,Math.max(0,    (int)(255*( 1 +  /*(1/(max_abs)) * */ this.image[i][j] ))    ));
                    //B = 0;

                }
                else{
                    R=G=B=0;
                }


                int rgb = 0;
                rgb = rgb | ((int)R << 16);
                rgb = rgb | ((int)G << 8);
                rgb = rgb | (int)B;
                bufferedImage.setRGB(j,i,rgb);

                //bufferedImage.setRGB(j,i, (int)(R*65536 + G*256 + B));
                //(1*65536+1*256+1) * (int)(255*( 1 - this.image[i][j] ))


            }
        }
        ImageIO.write(bufferedImage, "jpg", file);
    }

}
