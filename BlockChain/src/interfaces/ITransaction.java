package interfaces;
import concrete.Transaction;

import java.io.Serializable;
import java.security.*;

import java.util.ArrayList;
import java.util.Scanner;
public interface ITransaction {
    /**to represent the output*/
    public class OutputPair implements Serializable {
        public int id;
        public float value;
        public float committedVal;
        public float available ;
        OutputPair(int id, float value){
            this.id=id;this.value=value;this.available=value;this.committedVal=value;
        }
    }
    /**@return true if there are witnesses*/
    public boolean containsWitnesses();

    //setters to modify transaction
    public void setIPs(ArrayList<Integer> ip);
    public void setWitnesses(ArrayList<Integer> ws);
    public void setOPs(ArrayList<OutputPair> op);
    public void setID(int id);
    public void setPrevID(int pID);
    public void setOutIndex(int i);
    public void setBlock(IBlock b);
    public void setPrevTransaction(ITransaction t);
    //getters
    /**@return null in case of no witnesses*/
    public ArrayList<Integer> getWitnesses();
    public ArrayList<Integer> getIPs();
    public ArrayList<OutputPair> getOPs();
    public int getID();
    public int getPrevID();
    public int getOutIndex();
    public IBlock getBlock();
    public byte[] getSignedHash();
    public  PublicKey getPayerPK();
    //to set the data of the transaction ... the concrete class provides a constructor sets that as well
    public void setTransaction(int id , int prevID,ArrayList<Integer>ip,ArrayList<OutputPair>op);
    public void setTransaction(int id , int prevID,ArrayList<Integer>ip,ArrayList<OutputPair>op,ArrayList<Integer>witnesses);

    public String hash();
    public void signTransaction(PrivateKey key, PublicKey publicKey);
    public static ITransaction parseTransaction(String line){
        int id = 0;
        try {
            ITransaction t = new Transaction();
            ArrayList<Integer> ips = new ArrayList<>();
            ArrayList<OutputPair> ops = new ArrayList<>();

            Scanner scanner = new Scanner(line);
            t.setID(scanner.nextInt());                                     //id
            id = t.getID();
            ips.add(Integer.valueOf(scanner.next().split(":")[1]));       //input
            while (scanner.hasNext()) {                                     //prev id, outIx , outvalue pairs
                try {
                    String next = scanner.next();
                    String[] res = next.split(":");
                    if (res[0].equals("previoustx")) {          //may not exist .. set to its default value in the concrete class
                        t.setPrevID(Integer.parseInt(res[1]));
                    } else if (res[0].equals("outputindex")) {  //may not exist .. set to its default value in the concrete class

                        t.setOutIndex(Integer.parseInt(res[1]));
                    } else if (res[0].contains("value")) {
                        float v = Float.parseFloat(res[1]);
                        int op = Integer.parseInt(scanner.next().split(":")[1]);
                        ops.add(new OutputPair(op, v));
                    }
                } catch (Exception e) {
                    System.out.println("error in parsing one of the outputs transaction :");
                    e.printStackTrace();
                }

            }
            t.setIPs(ips);
            t.setOPs(ops);
            return t;
        }catch (Exception e){
            System.out.println("error parsing an essential field");
            e.printStackTrace();
            System.out.println("error in "+id);
            return null;
        }
    }
}

