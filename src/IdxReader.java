import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class IdxReader {

    public double[][] data;
    public double[][] labels;

    public IdxReader(String inputImagePath, String inputLabelPath,boolean shuffle){
        this.data = readImages(inputImagePath);
        this.labels = readLables(inputLabelPath);
        System.out.println("IDX loaded with date size " + data.length + " and label size " + this.labels.length);
        //System.out.print(data);
        //System.out.print(labels);
        /*
        if(shuffle){
            //- optional improvement via random partition
            int[] partition = RandomPartitionSequence.RandomPartition(data.length);
            double[][] newdata = new double[data.length][28*28];
            double[][] newlables = new double[this.labels.length][10];
            for(int img = 0 ; img < data.length ; img++){
                for(int pixel = 0 ; pixel < 28*28 ; pixel++){
                    newdata[partition[img]][pixel] = data[img][pixel];
                }
            }
            for(int img = 0 ; img < data.length ; img++){
                for(int digit = 0 ; digit < 10 ; digit++){
                    newlables[partition[img]][digit] = labels[img][digit];
                }
            }
            this.data = newdata;
            this.labels = newlables;
        }
         */
    }

    /*
    double[][] getData(){
        return this.data;
    }

    double[][] getLables(){
        return this.labels;
    }
     */

    public static double[][] readImages(String inputImagePath, int firstimg , int lastimg){


        FileInputStream inImage = null;
        FileInputStream inLabel = null;


        try {
            inImage = new FileInputStream(inputImagePath);
            int magicNumberImages = (inImage.read() << 24) | (inImage.read() << 16) | (inImage.read() << 8) | (inImage.read());
            int numberOfImages = (inImage.read() << 24) | (inImage.read() << 16) | (inImage.read() << 8) | (inImage.read());
            int numberOfRows  = (inImage.read() << 24) | (inImage.read() << 16) | (inImage.read() << 8) | (inImage.read());
            int numberOfColumns = (inImage.read() << 24) | (inImage.read() << 16) | (inImage.read() << 8) | (inImage.read());
            int numberOfPixels = numberOfRows * numberOfColumns;

            double[][] images = new double[lastimg-firstimg][numberOfPixels];

            for(int i = 0 ; i < firstimg; i++){
                inImage.read();
            }

            for(int i = firstimg; i < lastimg; i++) {
                for(int p = 0; p < numberOfPixels; p++) {
                    int gray = 255 - inImage.read();
                    images[i-firstimg][p] = 0xFF000000 | (gray<<16) | (gray<<8) | gray;
                }
            }
            return images;
        }
        catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (inImage != null) {
                try {
                    inImage.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (inLabel != null) {
                try {
                    inLabel.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public double[][] readImages(String inputImagePath){


        FileInputStream inImage = null;
        FileInputStream inLabel = null;


        try {
            inImage = new FileInputStream(inputImagePath);


            int magicNumberImages = (inImage.read() << 24) | (inImage.read() << 16) | (inImage.read() << 8) | (inImage.read());
            int numberOfImages = (inImage.read() << 24) | (inImage.read() << 16) | (inImage.read() << 8) | (inImage.read());
            int numberOfRows  = (inImage.read() << 24) | (inImage.read() << 16) | (inImage.read() << 8) | (inImage.read());
            int numberOfColumns = (inImage.read() << 24) | (inImage.read() << 16) | (inImage.read() << 8) | (inImage.read());
            int numberOfPixels = numberOfRows * numberOfColumns;

            double[][] images = new double[numberOfImages][numberOfPixels];

            for(int i = 0; i < numberOfImages; i++) {
                for(int p = 0; p < numberOfPixels; p++) {
                    int gray = 255 - inImage.read();
                    images[i][p] = -(0xFF000000 | (gray<<16) | (gray<<8) | gray);
                }
            }
            this.data = images;
            return images;
        }
        catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (inImage != null) {
                try {
                    inImage.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (inLabel != null) {
                try {
                    inLabel.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static double[][] readLables(String inputLabelPath,String inputImagePath, int firstimg , int lastimg){

        FileInputStream inImage = null;
        FileInputStream inLabel = null;

        try {
            inImage = new FileInputStream(inputImagePath);
            inLabel = new FileInputStream(inputLabelPath);

            int magicNumberImages = (inImage.read() << 24) | (inImage.read() << 16) | (inImage.read() << 8) | (inImage.read());
            int numberOfImages = (inImage.read() << 24) | (inImage.read() << 16) | (inImage.read() << 8) | (inImage.read());
            int numberOfRows  = (inImage.read() << 24) | (inImage.read() << 16) | (inImage.read() << 8) | (inImage.read());
            int numberOfColumns = (inImage.read() << 24) | (inImage.read() << 16) | (inImage.read() << 8) | (inImage.read());
            int magicNumberLabels = (inLabel.read() << 24) | (inLabel.read() << 16) | (inLabel.read() << 8) | (inLabel.read());
            int numberOfLabels = (inLabel.read() << 24) | (inLabel.read() << 16) | (inLabel.read() << 8) | (inLabel.read());
            int numberOfPixels = numberOfRows * numberOfColumns;

            double[][] lables = new double[lastimg-firstimg][10];

            for(int i = 0 ; i < firstimg; i++){
                inLabel.read();
            }

            for(int i = firstimg; i < lastimg; i++) {
                int label = inLabel.read();
                for(int digit = 0 ; digit < 10 ; digit++){
                    lables[i-firstimg][digit] = digit == label ? 1 : 0;
                }
            }
            return lables;

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (inImage != null) {
                try {
                    inImage.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (inLabel != null) {
                try {
                    inLabel.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public double[][] readLables(String inputLabelPath){

        //FileInputStream inImage = null;
        FileInputStream inLabel = null;

        try {
            //inImage = new FileInputStream(inputImagePath);
            inLabel = new FileInputStream(inputLabelPath);

            int magicNumberImages = (inLabel.read() << 24) | (inLabel.read() << 16) | (inLabel.read() << 8) | (inLabel.read());
            int numberOfImages = (inLabel.read() << 24) | (inLabel.read() << 16) | (inLabel.read() << 8) | (inLabel.read());
            //int numberOfRows  = (inLabel.read() << 24) | (inLabel.read() << 16) | (inLabel.read() << 8) | (inLabel.read());
            //int numberOfColumns = (inLabel.read() << 24) | (inLabel.read() << 16) | (inLabel.read() << 8) | (inLabel.read());

            int magicNumberLabels = (inLabel.read() << 24) | (inLabel.read() << 16) | (inLabel.read() << 8) | (inLabel.read());
            int numberOfLabels = (inLabel.read() << 24) | (inLabel.read() << 16) | (inLabel.read() << 8) | (inLabel.read());

            double[][] lables = new double[numberOfImages][10];

            for(int i = 0; i < numberOfImages; i++) {
                int label = inLabel.read();
                for(int digit = 0 ; digit < 10 ; digit++){
                    //lables[i][digit] = digit == label ? 1 : -1;
                    lables[i][digit] = digit == label ? 1 : 0;
                }
            }
            //this.labels = lables;
            return lables;

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e){
            return null;
        } finally {
            if (inLabel != null) {
                try {
                    inLabel.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (inLabel != null) {
                try {
                    inLabel.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}

