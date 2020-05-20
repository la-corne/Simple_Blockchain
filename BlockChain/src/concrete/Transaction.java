package concrete;
import interfaces.*;
import java.nio.charset.StandardCharsets;

import java.security.*;
import java.util.ArrayList;
import java.io.Serializable;
import java.security.PublicKey;
import java.util.ArrayList;





public class Transaction implements ITransaction ,Serializable{


    public static void testParsing(String [] args){
        ITransaction t = ITransaction.parseTransaction("49\tintput:0\tvalue:79.121956\toutput:49");
        try {
            //System.out.println(t.hash());
            KeyPair pair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
            t.signTransaction(pair.getPrivate(),pair.getPublic());
            //System.out  .println(new String (t.getSignedHash()));
            Signature s = Signature.getInstance("SHA1WithRSA");
            s.initVerify(pair.getPublic());
            s.update(t.hash().getBytes());
            boolean b =s.verify(t.getSignedHash());

            System.out.println("sig ,, "+b);

        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println();
        System.out.println(t.getIPs());
        System.out.println(t.getOPs().get(0).value);

        System.out.println(t.getID());
        System.out.println(t.getPrevID());
        System.out.println(t.getOutIndex());

    }
    protected boolean containsWitnesses = false ;
    private ArrayList<Integer> ips;
    private ArrayList<OutputPair> ops;
    private ArrayList<Integer> witnesses;
    private int prevTrasactionID=-1;
    private int id=-1;
    private int ipCount;
    private int opCount;
    private int outputIndex=-1;
    private IBlock block = null;
    private String hash = null ;
    private byte[] signedHash ;
    private PublicKey payerKey = null;
    private float[] available;
    private ITransaction prev;
    public Transaction(){
    }

    @Override
    public PublicKey getPayerPK() {
        return this.payerKey;
    }

    @Override
    public byte[] getSignedHash() {
        return this.signedHash;
    }

    public Transaction(int id, int prevID , ArrayList<Integer>ip, ArrayList<OutputPair>op ){
        this.setTransaction( id, prevID ,ip,op);
        this.id =id;
        this.prevTrasactionID = prevID;
    }
    public Transaction(int id ,int prevID, ArrayList<Integer>ip, ArrayList<OutputPair>op,ArrayList<Integer>witnesses ){
        this.setTransaction( id, prevID ,ip,op,witnesses);
        this.id =id;
        this.prevTrasactionID = prevID;
    }

    @Override
    public boolean containsWitnesses() {
        return containsWitnesses;
    }


    @Override
    public void setIPs(ArrayList<Integer> ip) {
        this.ips = ip;
        this.ipCount = ip.size();
    }

    @Override
    public void setWitnesses(ArrayList<Integer> ws) {
        this.containsWitnesses = true;
        this.witnesses = ws;
    }

    @Override
    public ArrayList<OutputPair> getOPs() {
        return this.ops;
    }

    @Override
    public void setOPs(ArrayList<OutputPair> op) {
        this.ops = op;
        this.opCount = op.size();
    }

    @Override
    public ArrayList<Integer> getWitnesses() {
        return this.witnesses;
    }

    @Override
    public ArrayList<Integer> getIPs() {
        return this.ips;
    }

    @Override
    public void setID(int id) {
        this.id = id;
    }

    @Override
    public void setPrevID(int pID) {
        this.prevTrasactionID = pID;
    }

    @Override
    public void setOutIndex(int i) {
        this.outputIndex = i;
    }

    @Override
    public int getID() {
        return this.id;
    }

    @Override
    public int getPrevID() {
        return this.prevTrasactionID;
    }

    @Override
    public int getOutIndex() {
        return this.outputIndex;
    }

    @Override
    public void setTransaction(int id , int prevID,ArrayList<Integer> ip, ArrayList<OutputPair> op) {
        this.id = id;
        this.prevTrasactionID = prevID;
        this.ipCount = ip.size();
        this.opCount = op.size();
        this.ips = ip;
        this.ops = op;
        this.containsWitnesses = false;
        this.witnesses = null;

    }

    @Override
    public void setTransaction(int id , int prevID,ArrayList<Integer>ip, ArrayList<OutputPair> op, ArrayList<Integer> witnesses) {
        this.id = id;
        this.prevTrasactionID = prevID;
        this.ipCount = ip.size();
        this.opCount =op.size();
        this.ips = ip;
        this.ops = op;
        this.containsWitnesses = true;
        this.witnesses = witnesses;

    }

    @Override
    public void setBlock(IBlock b) {
        this.block=b;
    }

    @Override
    public IBlock getBlock() {
        return this.block;
    }

    @Override
    public void setPrevTransaction(ITransaction t) {
        this.prev =t;
    }

    @Override
    public String hash() {
        if(this.hash !=null){
            return this.hash;
        }
        StringBuilder sb = new StringBuilder();
        if( prevTrasactionID != -1) {
            if (prev == null) {
                sb.append(this.block.getTransactionByID(this.prevTrasactionID).hash());
            } else {
                sb.append(prev.hash());
            }
        }
        for (Integer i:this.ips) {
            sb.append("-");
            sb.append(this.payerKey);
        }
        for (OutputPair i:this.ops) {
            sb.append("-");
            while(Utils.getInstance().getPublicKeyFromID(i.id) == null)System.out.println("waiting for other ips to broadcast my pk");
            sb.append(Utils.getInstance().getPublicKeyFromID(i.id));
            sb.append("-");
            sb.append(String.valueOf(i.value));
        }
        this.hash =Utils.applySha256(sb.toString());
        System.out.println("transaction hashed ..");
        return this.hash;
    }


    @Override
    public void signTransaction(PrivateKey key ,PublicKey pub) {
        this.payerKey = pub;
        try {
            if (this.hash == null){
                hash();
            }
            Signature s = Signature.getInstance("SHA1WithRSA");
            s.initSign(key);
            s.update(this.hash.getBytes());
            this.signedHash = s.sign();
            System.out.println("transaction signed ..");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
