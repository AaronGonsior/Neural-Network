import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GreenRedImage {
    double[][] image;
    public GreenRedImage(double[][] image){
        this.image = image;
    }

    void makeJPG(String path, String name,int dim) throws IOException {

        boolean amp = true;
        double maximum = 1;
        if(amp) {
            for (double[] row : image) {
                for (double pixel : row) {
                    maximum = Math.max(maximum, pixel);
                }
            }
        }

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

        for(int i = 0; i < image[0].length ; i++){
            for(int j = 0 ; j < image[1].length ; j++){
                if(image[i][j]>0){
                    //double R=1-(1/(maximum))*(image[i][j]);
                    double R = (int)(255*( 1 - (1/(maximum)) * this.image[i][j] ));
                    //double G = 255 - (int)(255*( 1 - (1/(maximum)) * this.image[i][j] ));
                    double G = 255;
                    //double B=1-(1/(maximum))*(image[i][j]);
                    double B = (int)(255*( 1 - (1/(maximum)) * this.image[i][j] ));
                    //double B = 0;

                    bufferedImage.setRGB(j,i, (int)(R*65536 + G*256 + B));
                    //(1*65536+1*256+1) * (int)(255*( 1 - this.image[i][j] ))
                }
                else {
                    //double R = 255 - (int)(255*( 1 - (1/(maximum)) * this.image[i][j] ));
                    double R = 255;
                    //double G=1+(1/(maximum))*(image[i][j]);
                    double G = (int)(255*( 1 + (1/(maximum)) * this.image[i][j] ));
                    //double B=1+(1/(maximum))*(image[i][j]);
                    double B = (int)(255*( 1 + (1/(maximum)) * this.image[i][j] ));
                    //double B = 0;
                    bufferedImage.setRGB(j,i, (int)(R*65536 + G*256 + B));
                }
            }
        }
        ImageIO.write(bufferedImage, "jpg", file);
    }

}
