import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class ChartDrawer extends Canvas implements Runnable{

    private static int width = 800;
    private static int height = 1000;
    private static int pos_x;
    private static int pos_y;
    Trainer trainer;
    ArrayList<Double>[] chart_datas;
    String[] labels;

    boolean update;
    JFrame frame;
    final String basepath = System.getProperty("user.dir");

    public ChartDrawer(ArrayList[] chart_datas, String[] labels){

        //defaults
        width = 1000;
        height = 1000;
        pos_x = 0;
        pos_y = 0;

        this.chart_datas = chart_datas;
        this.labels = labels;

        update = true;

        //this.trainer = trainer;
        //this.nn = nn;
        frame = new JFrame("Chart Drawer");

        Color background = new Color(76, 79, 76);
        frame.getContentPane().setBackground(background);
        frame.setBackground(background);

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
        frame.update(frame.getGraphics());
    }

    void setPosition(int pos_x, int pos_y){
        this.pos_x = pos_x;
        this.pos_y = pos_y;
        frame.setLocation(pos_x,pos_y);
        frame.update(frame.getGraphics());
    }

    public void update(ArrayList[] chart_datas) throws InterruptedException {
        this.chart_datas = chart_datas;
        Rectangle r = frame.getBounds();
        height = r.height;
        width = r.width;
        this.setSize(width, height);

        frame.remove(this);
        frame.update(frame.getGraphics());
        frame.add(this);
    }

    public void paint(Graphics g) {

        if(chart_datas == null ) return;

        Color background = new Color(76, 79, 76);
        frame.getContentPane().setBackground(background);
        frame.setBackground(background);

        Color frontpaint = new Color(152, 152, 152);

        int num_charts = chart_datas.length;

        double min,max;
        int num_datapoints;

        for(int chart = 0 ; chart < num_charts ; chart++){

            num_datapoints = chart_datas[chart].size();

            min = chart_datas[chart].get(0);
            max = chart_datas[chart].get(0);
            for(int i = 1 ; i < chart_datas[chart].size() ; i++){
                if(min > chart_datas[chart].get(i)) min = chart_datas[chart].get(i);
                if(max < chart_datas[chart].get(i)) max = chart_datas[chart].get(i);
            }
            if(min == max) min = 0;

            int bound_l,bound_r,bound_u,bound_d;
            bound_u = (int)((height/num_charts)*(10.0/100.0));
            bound_d = (int)((height/num_charts)*(90.0/100.0));
            bound_r = (int)((width)*(90.0/100.0));
            bound_l = (int)((width)*(5.0/100.0));
            int bar_offset = 50;

            for(int i = 0 ; i < chart_datas[chart].size() - 1 ; i++){
                double datapoint_new = chart_datas[chart].get(i);
                g.setColor(frontpaint);
                g.drawLine(                                 bound_l      + i*( width / num_datapoints),
                        (chart+1) * ((height-bar_offset)/num_charts) -   bound_u         - (int)( ( (chart_datas[chart].get(i) - min ) / (max - min) ) * (bound_d-bound_u) /*0.8*height/num_charts*/ ),
                                                            bound_l     + (i+1)*( width / num_datapoints),
                        (chart+1) * ((height-bar_offset)/num_charts)  - bound_u - (int)( ( (chart_datas[chart].get(i+1) - min ) / (max - min) ) * (bound_d-bound_u) /* 0.8*height/num_charts*/ )
                        );

                g.drawString(labels[chart] + ": " + chart_datas[chart].get(chart_datas[chart].size()-1),0 ,(chart)*((height-bar_offset)/num_charts) + 10 );
                g.drawString("max: " + String.valueOf(max),0 ,(chart)*((height-bar_offset)/num_charts) + 20 );
                g.drawString("min: " + String.valueOf(min),0 ,(chart+1)*(height/num_charts) - 40 );

            }
            g.drawLine(0,chart*((height-bar_offset)/num_charts),width,chart*((height-bar_offset)/num_charts));
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
