//import sun.rmi.log.ReliableLog;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Log {

    static FileWriter Log_writer;
    static String name = "Full_Log";
    static final String basepath = System.getProperty("user.dir");
    static File LogFile = new File(basepath + name + ".txt");
    private static boolean blocked;
    //boolean exists = LogFile.exists();
    boolean continue_;

    public Log(boolean continue_) throws IOException, InterruptedException {
        this.continue_ = continue_;
        blocked = false;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        System.out.println(dtf.format(now));
        writeFullLog(dtf.format(now) + " --- new log entry started",continue_);
    }


    public static void writeFullLog(String message,boolean continue_) throws IOException, InterruptedException {
        while(blocked) Thread.sleep(10);
        blocked = true;

        SimpleDateFormat dtf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        Date date = new Date();
        String now = dtf.format(date);
        Log_writer = new FileWriter("log.txt",continue_);
        Log_writer.write(now + " - " + message);
        close();

        blocked = false;
    }

    public static void close() throws IOException {
        Log_writer.close();
    }

}
