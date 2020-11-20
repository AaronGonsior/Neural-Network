import java.util.ArrayList;
import java.util.Collections;

public class RandomPartitionSequence {

    public static int[] RandomPartition(int length){
        int[] ordered = new int[length];
        ArrayList<Integer> list = new ArrayList<Integer>();
        for(int i = 0 ; i < length ; i++){
            list.add(i);
        }
        Collections.shuffle(list);
        int[] shuffled = new int[length];
        for(int i = 0 ; i < shuffled.length ; i++){
            shuffled[i] = list.get(i);
        }
        return shuffled;
    }

    public static void main(String[] args){
        int[] shuffled = RandomPartition(100);
        for(int i : shuffled){
            System.out.println(i);
        }
    }
}
