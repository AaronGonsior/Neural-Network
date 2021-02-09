import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
public class GrayscaleImage {

    double[][] image;
    String scale;
    int size;

    public GrayscaleImage(double[][] image){
        this.image = image;
        size = image[0].length;
    }

    public GrayscaleImage(double[] image, String scale) throws Exception {
        this.scale = scale;
        if( scale.equals("1:1") ){

            int size = (int)Math.sqrt(image.length);
            this.size = size;
            this.image = new double[size][size];
            if(size*size != image.length) throw new Exception();
            for(int pixel = 0 ; pixel < image.length ; pixel++){
                this.image[(int)(pixel/size)][pixel % size] = image[pixel];
            }

        }
    }

    /*

    void makeJPG(String path, String name) throws IOException {
        double maximum = 0;
        double minimum = 0;
        for (double[] row:image) {
            for (double pixel:row) {
                maximum = Math.max(maximum,pixel);
                minimum = Math.min(minimum,pixel);
            }
        }

        //BufferedImage bufferedImage = new BufferedImage(10, 10, BufferedImage.TYPE_BYTE_GRAY);
        BufferedImage bufferedImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        File file = new File(path+"\\"+name+".jpg");
        for(int i = 0; i < image[0].length ; i++){
            for(int j = 0 ; j < image[1].length ; j++){
                //bufferedImage.setRGB(j,i, (int) ((255*65536+255*256+255)*(1- (1/(maximum-minimum))*(this.image[i][j]-minimum))));
                //bufferedImage.setRGB(j,i, (int) ((255*65536+255*256+255)*(1- (this.image[i][j]))));
                bufferedImage.setRGB(j,i,  (1*65536+1*256+1) * (int)(255*( 1 - this.image[i][j] )) );
            }
        }
        ImageIO.write(bufferedImage, "jpg", file);
    }

     */



    void makeJPG(String path, String name) throws IOException {

        boolean inverted = false;
        boolean amp = false;
        double maximum = 1;
        if(amp) {
            for (double[] row : image) {
                for (double pixel : row) {
                    maximum = Math.max(maximum, pixel);
                }
            }
        }

        BufferedImage bufferedImage = new BufferedImage(size,size, BufferedImage.TYPE_INT_RGB);

        String fullpath = "";
        switch (Util.getOS()) {
            case WINDOWS:
                fullpath = path + "\\" + name + ".jpg";
                break;
            case LINUX:
                fullpath = path + "/" + name + ".jpg";
        }

        File file = new File(fullpath);

        //int max_rgb = 255*255*255+255*255+255;
        //int max_rgb = 16777215;
        int max_rgb = (255 << 16) | (255 << 8) | 255; //=16777215


        for(int i = 0; i < image[0].length ; i++){
            for(int j = 0 ; j < image[1].length ; j++){
                if(!inverted){
                    bufferedImage.setRGB(j,i,max_rgb - (int)(image[i][j]) );
                }else {
                    bufferedImage.setRGB(j,i,-max_rgb + (int)(image[i][j]) );
                }
            }
        }
        ImageIO.write(bufferedImage, "jpg", file);
    }


}
