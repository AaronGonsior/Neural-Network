import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class PropagateDrawer extends Canvas implements Runnable {

    private static int width = 800;
    private static int height = 1000;
    private static int pos_x;
    private static int pos_y;

    boolean update;

    Trainer trainer;
    NeuralNetwork nn;
    JFrame frame;
    //Canvas canvas;
    final String basepath = System.getProperty("user.dir");
    int img_size = 40;
    int cur_img;

    public PropagateDrawer(NeuralNetwork nn,Trainer trainer){

        //defaults
        width = 1000;
        height = 1000;
        pos_x = 0;
        pos_y = 0;

        cur_img = -1;

        update = true;
        this.trainer = trainer;
        this.nn = nn;
        frame = new JFrame("Propagate Drawer");
        //canvas = new Canvas();
        this.setSize(width, height);
        frame.add(this);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE );
        frame.setLocation(pos_x,pos_y);

    }

    void setResolution(int res_x, int res_y){
        width = res_x;
        height = res_y;
        frame.setSize(width, height);
    }

    void setPosition(int pos_x, int pos_y){
        this.pos_x = pos_x;
        this.pos_y = pos_y;
        frame.setLocation(pos_x,pos_y);
    }

    public void update(int cur_img) throws InterruptedException {
        this.cur_img = cur_img;
        int[] dims = Arrays.copyOfRange(nn.layerDimensions,1,nn.layerDimensions.length);
        int max = Arrays.stream(dims).max().getAsInt();
        //System.out.print(max);
        Rectangle r = frame.getBounds();
        height = r.height - (r.height/max);
        width = r.width;
        this.setSize(width, height);

        img_size = (int)( (double)(height/max) * 0.8 );
        frame.remove(this);
        frame.update(frame.getGraphics());
        frame.add(this);
    }





    public void paint(Graphics g) {
        Color background = new Color(76, 79, 76);
        frame.getContentPane().setBackground(background);
        frame.setBackground(background);

        Color frontpaint = new Color(152, 152, 152);
        int nodesize = 10;

        int[] layerdims;
        layerdims = nn.layerDimensions;

        /*
        int spacing = 1;
        for(int i = height/2 - img_size/2 * spacing  ; i <= height/2 + img_size/2 * spacing ; i += spacing){
            for(int j = 0 ; j <= img_size * spacing ; j += spacing){
                g.setColor(frontpaint);
                g.drawLine(j,i,j,i);
            }
        }
         */

        if (cur_img >= 0) {
            try {
                GrayscaleImage cur_img_graysc = new GrayscaleImage(nn.trainer.idxReader.data[cur_img], "1:1");
                cur_img_graysc.makeJPG(basepath, "propagatedrawer");
                File file = new File(basepath + "\\propagatedrawer.jpg");
                Image img = ImageIO.read(file);

                g.drawImage(img,
                        0,
                        height / 2 - img_size / 2,
                        img_size, img_size
                        , null);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        int colourboost = 10;
        for(int layer = 1 ; layer < layerdims.length ; layer++){
            for(int i = 0 ; i < layerdims[layer] ; i++){
                if(layer == 1){

                    try {
                        File file = new File(basepath + "\\single_test\\weights\\weightsmap" + i + "_layerc_0.jpg");

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
                R = 255;
                G = (int)(255*( 1 -  colourboost*  nn.layers[layer].nodes[i].getSignal()   ));
                B = 255;

                R = Math.min(255,Math.max(0,R));
                G = Math.min(255,Math.max(0,G));
                B = Math.min(255,Math.max(0,B));

                g.setColor(new Color(R,G,B));
                g.fillOval(x,y,nodesize,nodesize);

            }

            g.setColor(frontpaint);
            int x_pos = (int)(width * 1) - 150;
            g.drawString("Iteration: " + trainer.iteration , x_pos , 50);
            g.drawString("Accuracy: " + Function.roundoff(trainer.best_bet_accuracy * 100,2)+"%" , x_pos , 70);

        }


        double[][] weights;
        for(int layer = 1 ; layer < layerdims.length-1 ; layer++){
            weights = nn.layerConnections[layer/*-1*/].weightedConnections.getWeightedconnections();
            for(int left = 0 ; left < layerdims[layer] ; left++){
                for(int right = 0 ; right < layerdims[layer+1] ; right++){

                    int R,G,B;

                    if(weights[left][right] * nn.layers[layer].nodes[left].getSignal() > 0){
                        R = (int)(255*( 1 - colourboost*  weights[left][right] * nn.layers[layer].nodes[left].getSignal()     ));
                        G = 255;
                        B = (int)(255*( 1 - colourboost*  weights[left][right] * nn.layers[layer].nodes[left].getSignal()    ));
                    }
                    else {
                        R = 255;
                        G = (int)(255*( 1 + colourboost*  weights[left][right] * nn.layers[layer].nodes[left].getSignal()    ));
                        B = (int)(255*( 1 + colourboost*  weights[left][right] * nn.layers[layer].nodes[left].getSignal()  ));
                    }

                    R = Math.min(255,Math.max(0,R));
                    G = Math.min(255,Math.max(0,G));
                    B = Math.min(255,Math.max(0,B));

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








    @Override
    public void run() {
        while(!update){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
