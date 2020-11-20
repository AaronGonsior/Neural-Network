public class Gradient {

    double[] gradient;
    int current;
    int length;
    int[] entries;

    public Gradient(int length){
        gradient = new double[length];
        entries = new int[length];
        current = 0;
        this.length = length;
    }

    void add(double partial){
        gradient[current] = gradient[current] + partial;
        entries[current] = entries[current] + 1;
        current++;
    }

    /*
    double getNext(){
        return gradient[current--%length];
    }

    void resetPointer(){
        current = 0;
    }
     */

    void reset(){
        current = 0;
    }

    void clear(){
        current = 0;
        gradient = new double[length];
        entries = new int[length];
    }

    double[] getGradient(){
        return gradient;
    }

    double[] getNormedGradient(){
        double[] output = new double[gradient.length];
        double length = Math.sqrt(getSquareGradientLengthEuclid());
        if(length != 0){
            for(int i = 0 ; i < output.length ; i++){
                output[i] = gradient[i]/length;
            }
        }
        return output;
    }

    double[] getAvgGradient(){
        double[] avgGradient = new double[length];
        for(int part = 0 ; part < length ; part++){
            avgGradient[part] = gradient[part] / entries[part];
        }
        return avgGradient;
    }

    double getSquareGradientLengthEuclid(){
        double gradlength = 0;
        for (double part:gradient) {
            gradlength += Math.pow(part,2);
        }
        //gradlength = Math.sqrt(gradlength);
        return gradlength;
    }

    double getLength(){
        double gradlength = 0;
        for (double part:gradient) {
            gradlength += Math.pow(part,2);
        }
        gradlength = Math.sqrt(gradlength);
        return gradlength;
    }

    void print(){
        double[] grad = getNormedGradient();
        for(int partial = 0 ; partial < length ; partial++){
            System.out.println(grad[partial]);
        }
    }
}