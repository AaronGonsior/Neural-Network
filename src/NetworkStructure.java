public class NetworkStructure {

    Util.NetworkStructure networkStructure;
    int[] layerdimensions;

    public NetworkStructure(Util.NetworkStructure structure , int[] layerdimensions){
        switch (structure){
            case Basic_16_16_Bias:
                this.layerdimensions = layerdimensions;
        }
    }

    public int[] getLayerdimensions(){
        return layerdimensions;
    }

}
