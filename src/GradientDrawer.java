import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class GradientDrawer extends Canvas implements Runnable {

    private static int width = 800;
    private static int height = 1000;
    private static int pos_x;
    private static int pos_y;

    JFrame frame;
    final String basepath = System.getProperty("user.dir");
    int img_size = 40;

    Gradient gradient;

    public GradientDrawer(Gradient gradient){

        //defaults
        width = 1000;
        height = 1000;
        pos_x = 0;
        pos_y = 0;

        this.gradient = gradient;


        frame = new JFrame("Gradient Drawer");
        //canvas = new Canvas();
        this.setSize(width, height);
        frame.add(this);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE );
        frame.setLocation(pos_x,pos_y);

    }


    public void update() throws InterruptedException {
        int[] dims = Arrays.copyOfRange(gradient.nn.layerDimensions,1,gradient.nn.layerDimensions.length);
        int max = Arrays.stream(dims).max().getAsInt();
        //System.out.print(max);
        Rectangle r = frame.getBounds();
        height = r.height - (r.height/max);
        width = r.width;
        this.setSize(width, height);

        img_size = (int)( (double)(height/max) * 0.8 );
        frame.remove(this);
        frame.add(this);
        frame.update(frame.getGraphics());
    }



    public void paint(Graphics g) {
        Color background = new Color(76, 79, 76);
        frame.getContentPane().setBackground(background);
        frame.setBackground(background);

        Color frontpaint = new Color(152, 152, 152);
        int nodesize = 10;

        int[] layerdims;
        layerdims = gradient.nn.layerDimensions;

        int spacing = 1;
        for(int i = height/2 - img_size/2 * spacing  ; i <= height/2 + img_size/2 * spacing ; i += spacing){
            for(int j = 0 ; j <= img_size * spacing ; j += spacing){
                g.setColor(frontpaint);
                g.drawLine(j,i,j,i);
            }
        }
        for(int layer = 1 ; layer < layerdims.length ; layer++){
            for(int i = 0 ; i < layerdims[layer] ; i++){
                if(layer == 1){

                    try {
                        File file = new File(basepath + "\\single_test\\gradientimages\\gradient_img_" + i + ".jpg");

                        Image img = ImageIO.read(file);
                        g.drawImage(img,
                                width/5-width/13-img_size/2,
                                (i+1) * height/(layerdims[layer]+1) - img_size/2,
                                img_size,img_size, null);
                    } catch(IIOException e){
                        System.out.println("Error in drawing");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                int x = width/5 + (width-width/5-width/10)/(layerdims.length-1-1) * (layer-1);
                int y = (i+1) * height/(layerdims[layer]+1);
                g.setColor(frontpaint);
                g.drawOval(x,y,nodesize,nodesize);

                int R,G,B;
                //double bias = gradient.nn.layers[layer].nodes[i].bias;
                double bias = ((double[])(gradient.node_gradients[layer]))[i];
                if(bias > 0){
                    R = (int)(255*( 1 - Function.atan1( bias )));
                    G = 255;
                    B = (int)(255*( 1 - Function.atan1( bias )));
                }
                else {
                    R = 255;
                    G = (int)(255*( 1 + Function.atan1( bias )));
                    B = (int)(255*( 1 + Function.atan1( bias )));
                }

                g.setColor(new Color(R,G,B));
                g.fillOval(x,y,nodesize,nodesize);

            }

            g.setColor(frontpaint);
            int x_pos = (int)(width * 1) - 150;
            g.drawString("Iteration: " + gradient.nn.trainer.iteration , x_pos , 50);

        }


        double[][] weights;
        for(int layer = 1 ; layer < layerdims.length-1 ; layer++){
            //weights = gradient.nn.layerConnections[layer-1].weightedConnections.getWeightedconnections();
            weights = ((double[][])(gradient.layerconnection_gradients[layer]));
            for(int left = 0 ; left < layerdims[layer] ; left++){
                for(int right = 0 ; right < layerdims[layer+1] ; right++){

                    int R,G,B;

                    if(weights[left][right] > 0){
                        R = (int)(255*( 1 - Math.min(1,weights[left][right] )));
                        G = 255;
                        B = (int)(255*( 1 - Math.min(1,weights[left][right] )));
                    }
                    else {
                        R = 255;
                        G = (int)(255*( 1 + Math.max(-1,weights[left][right] )));
                        B = (int)(255*( 1 + Math.max(-1,weights[left][right] )));
                    }

                    Color line = new Color(R, G, B);
                    g.setColor(line);
                    g.drawLine(  width/5 + (width-width/5-width/10)/(layerdims.length-1-1) * (layer-1) + nodesize,
                            (left+1) * height/(layerdims[layer]+1) + nodesize/2,
                            width/5 + (width-width/5-width/10)/(layerdims.length-1-1) * (layer-1+1) - 0*nodesize,
                            (right+1) * height/(layerdims[layer+1]+1) + nodesize/2
                    );
                }
            }
        }

    }






    void setResolution(int res_x, int res_y){
        width = res_x;
        height = res_y;
    }

    void setPosition(int pos_x, int pos_y){
        this.pos_x = pos_x;
        this.pos_y = pos_y;
    }


    @Override
    public void run() {

    }
}
