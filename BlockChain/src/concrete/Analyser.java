package concrete;

import interfaces.IAnalyser;
import interfaces.INTW;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Analyser implements IAnalyser {
    private boolean broadcasted = false;
    private static IAnalyser ins = null;
    public static IAnalyser getInstance(){
        if(ins == null){
            ins = new Analyser();
        }
        return ins;
    }
    public Analyser(){
        this.allData=new ArrayList<>();
        this.allData.add(this.myData);
    }
    private int type = 0;
    private int blockSize=100;
    private int difficulty = 3;
    private int numberOfParticipants= 3;
    private ArrayList<Analytics> allData = new ArrayList<>();

    private Analytics myData = new Analytics();
    private String report;

    @Override
    public void setType(int i) {
        this.type = i;
    }

    //Exchanging data between nodes


    @Override
    public void broadcastData(INTW ntw) throws IOException {
        if(!this.broadcasted ) {
            System.out.println("Analytics sent ... ");
            ntw.broadcastAnalytics(this.myData);
            this.broadcasted = true;
        }
    }

    @Override
    public void receiveData(Analytics data) {
        System.out.println("Analytics received ... ");
        allData.add(data);
        if(this.isDoneExchanging()){
            this.saveReport();
        }
    }

    @Override
    public boolean isDoneExchanging() {
        //System.out.println(this.allData.size() +"---"+this.numberOfParticipants );
        return this.allData.size() == this.numberOfParticipants ;
    }

    //POW ===============================================================
    @Override
    public void setBlockSize(int size) {
        this.blockSize = size;
    }

    @Override
    public void setDifficulty(int diff) {
        this.difficulty = diff;
    }

    @Override
    public double getAvgMessageComplexity() {
        double numExch =0;
        for(Analytics a : this.allData){
            numExch += a.avgNumOfMessExch4Block;
        }

        return ( (double) numExch /(double) this.numOfBlocks /(double) this.numberOfParticipants);
    }

    @Override
    public double getNumberOfStaleBlocks() {
        int n =0;
        for(Analytics a : this.allData){
            n += a.stales;
        }
        return n;
    }

    @Override
    public double getAvgTimeToMine() {
        double tttm =0;
        int nosm=0;
        for(Analytics a : this.allData){
            tttm +=a.totalTTM;
            nosm+= a.numberOfSuccessfulMines;
        }
        return  (tttm/(double)nosm);
    }
    //BFT =======================================================================

    @Override
    public void setNumOfParticipants(int num) {
        this.numberOfParticipants = num;
    }

    @Override
    public double getMessageComplexity() {
        double numExch =0;
        for(Analytics a : this.allData){
            numExch += a.avgNumOfMessExch4Block;
        }

        return numExch / (double) this.numOfBlocks ;
    }

    @Override
    public double getAvgTimeToAgreeOnBlock() {
        double totT =0;
        int numAg = 0;
        for(Analytics a : this.allData){
            totT += a.totalAgreeOnBlockTime;
            numAg += a.numberOfAgreedOnBlocks;
        }
        return ( totT / (double) numAg);
    }
    //reporting

    @Override
    public String saveReport() {
        this.report = this.getReport();
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter("report_"+(this.type == 0 ? "pow":"bft")+"_"+this.blockSize+"_"+this.difficulty+"_"+this.numberOfParticipants+".txt"));

            writer.write(this.report);

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this.report;
    }

    @Override
    public String getReport() {
        System.out.println("repoort =============");
        System.out.println(this.blockSize);
        System.out.println(this.numberOfParticipants);
        System.out.println(this.difficulty);

        System.out.println(this.getAvgMessageComplexity());
        System.out.println(this.getNumberOfStaleBlocks());
        System.out.println(this.getAvgTimeToMine());
        System.out.println(this.getAvgTimeToAgreeOnBlock());
        System.out.println("repoort +==============");
        StringBuilder sb = new StringBuilder();
        sb.append("FILE\n");

        try {
            if (this.type == 0) {//pow
                sb.append("Type:pow\n");
                sb.append("BlockSize:").append(this.blockSize).append("\n");
                sb.append("Difficulty:").append(this.difficulty).append("\n");
                sb.append("AMC:").append(this.getAvgMessageComplexity()).append("\n");
                sb.append("NSB:").append(this.getNumberOfStaleBlocks()).append("\n");
                sb.append("ATM:").append(this.getAvgTimeToMine()).append("\n");

            } else {//bft
                sb.append("Type:bft\n");
                sb.append("NumOfNodes:").append(this.numberOfParticipants).append("\n");
                sb.append("MC:").append(this.getMessageComplexity()).append("\n");
                sb.append("ATA:").append(this.getAvgTimeToAgreeOnBlock()).append("\n");

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("report : "+ sb.toString());
        return sb.toString();
    }
    //reporting ====================================================================
    private int numOfMessages =0;
    private int numOfBlocks =0;
    private double miningStart =0;
    private double bftStart =0;


    @Override
    public void reportMessageSent() {
        //this.numOfMessages ++;
        this.myData.avgNumOfMessExch4Block++;
    }

    @Override
    public void reportBlockDone() {
        //this.myData.avgNumOfMessExch4Block = ((this.myData.avgNumOfMessExch4Block *this.numOfBlocks) +(double) this.numOfMessages) /(double) (this.numOfBlocks+1);
        this.numOfBlocks ++;
        //this.numOfMessages=0;

    }

    @Override
    public void reportStartingMining() {
        this.miningStart = System.currentTimeMillis();
    }

    @Override
    public void reportEndingMiningSuccessfully() {
        this.myData.totalTTM += System.currentTimeMillis() - this.miningStart;
        this.myData.numberOfSuccessfulMines ++;
    }

    @Override
    public void reportEndingMiningUnsuccessfully() {
        //nothing here , it's just to make sure you call the above func only on success
    }

    @Override
    public void reportStale() {
        this.myData.stales++;
    }

    @Override
    public void reportStartingBFTVoting() {
        this.bftStart = System.currentTimeMillis();
    }

    @Override
    public void reportEndingBFTVoting() {
        this.myData.totalAgreeOnBlockTime += System.currentTimeMillis() - this.bftStart;
        this.myData.numberOfAgreedOnBlocks++;
    }
}
