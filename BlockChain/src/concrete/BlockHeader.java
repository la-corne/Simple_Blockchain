package concrete;
import interfaces.*;

import java.io.Serializable;
import java.util.ArrayList;

public class BlockHeader implements IBlockHeader, Serializable {
    private long timeStamp = 0;
    private int nonce = -1;
    private String hash = null;
    private String prevBlockHash = null;
    private String transactionsHash = null;


    public BlockHeader(){
        this.timeStamp = System.currentTimeMillis() / 1000l;
    }

    public BlockHeader(String prevBlockHash, String transactionsHash) {
        this.hash = calculateHash();
        this.timeStamp = System.currentTimeMillis() / 1000l;
        this.prevBlockHash = prevBlockHash;
        this.transactionsHash = transactionsHash;
    }



    @Override
    public void resetTimeStamp() {
        this.timeStamp = System.currentTimeMillis() / 1000l;
    }

    @Override
    public void createPrevBlockHash(IBlock prevBlock) {
        if(prevBlock == null){
            this.prevBlockHash = "null";
        }else {
            this.prevBlockHash = prevBlock.getHeader().getHash();
        }
    }


    @Override
    public void createTransactionsHash(ArrayList<ITransaction> ts) {
        this.transactionsHash = Utils.getMerkleRoot(ts);
    }

    @Override
    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

    @Override
    //Calculate new hash based on blocks contents
    public String calculateHash() {
        String calculatedHash = Utils.applySha256(
                prevBlockHash +
                        Long.toString(timeStamp) +
                        Integer.toString(nonce) +
                        transactionsHash
        );
        return calculatedHash;
    }


    @Override
    public boolean isSet() {
        return (this.prevBlockHash != null) && (this.transactionsHash !=null);
    }

    @Override
    public long getTimeStamp() {
        return this.timeStamp;
    }


    @Override
    public String getHash(){
        return this.hash;
    }

    @Override
    public void setHash(String hash){
        this.hash = hash;
    }


    @Override
    public String getTransactionsHash() {
        return this.transactionsHash;
    }

    @Override
    public void setTransactionsHash(String transactionsHash){
        this.transactionsHash = transactionsHash;
    }

    @Override
    public String getPrevBlockHash() {
        return this.prevBlockHash;
    }

    @Override
    public int getNonce() {
        return this.nonce;
    }
}
