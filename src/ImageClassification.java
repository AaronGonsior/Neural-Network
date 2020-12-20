public class ImageClassification extends Problem {
    final int pixel_x;
    final int pixel_y;
    final int outputdim;
    double[][] images;
    double[][] labels;
    public ImageClassification(int pixel_x, int pixel_y, int outputdim, double[][] images, double[][] labels){
        this.pixel_x = pixel_x;
        this.pixel_y = pixel_y;
        this.outputdim = outputdim;
        this.images = images;
        this.labels = labels;
    }

    public ImageClassification(int pixel_x, int pixel_y, int outputdim, IdxReader idxReader){
        this.pixel_x = pixel_x;
        this.pixel_y = pixel_y;
        this.outputdim = outputdim;
        this.images = idxReader.data;
        this.labels = idxReader.labels;
    }
}
