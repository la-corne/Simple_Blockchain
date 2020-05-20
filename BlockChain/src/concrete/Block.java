package concrete;
import interfaces.*;

import java.io.Serializable;
import java.util.ArrayList;

public class Block implements IBlock, Serializable {
    private IBlockHeader header;
    private ArrayList<ITransaction> ts;
    private IBlock prevBlock=null;

    public int getSeqNum() {
        return seqNum;
    }

    public void setSeqNum(int seqNum) {
        this.seqNum = seqNum;
    }

    private int seqNum = 0;
    @Override
    public void setHash(String hash) {
        this.hash = hash;
    }

    private String hash = null;


    public Block(){
        this.header = new BlockHeader();
    }


    public Block(IBlockHeader header, ArrayList<ITransaction> ts, IBlock prevBlock) {
        this.header = header;
        this.ts = ts;
        this.prevBlock = prevBlock;
    }


    @Override
    public IBlockHeader getHeader() {
        return this.header;
    }

    @Override
    public void setTransactions(ArrayList<ITransaction> ts) {
        this.header.createTransactionsHash(ts);
        this.ts=ts;
    }

    @Override
    public ArrayList<ITransaction> getTransactions() {
        return this.ts;
    }

    @Override
    public void setPrevBlock(IBlock block) {
        this.header.createPrevBlockHash(block);
        this.prevBlock = block;
    }

    @Override
    public IBlock getPrevBlock() {
        return this.prevBlock;
    }


    private String hashBlock(IBlock b ){
        StringBuilder sb = new StringBuilder();
        IBlockHeader h = b.getHeader();
        sb.append(String.valueOf(h.getNonce()));
        sb.append("-");
        sb.append(String.valueOf(h.getPrevBlockHash()));
        sb.append("-");
        sb.append(String.valueOf(h.getTimeStamp()));
        sb.append("-");
        sb.append(String.valueOf(h.getTransactionsHash()));
        sb.append("-");
        sb.append(String.valueOf(b.getSeqNum()));
        return Utils.applySha256(sb.toString());
    }
    @Override
    public String getBlockHash() {

        if(this.hash !=null){
            return this.hash;
        }

        this.hash =hashBlock(this);
        System.out.println("block hashed ..");
        return this.hash;

    }

    @Override
    public boolean verifyBlockHash() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.valueOf(this.header.getNonce()));
        sb.append("-");
        sb.append(String.valueOf(this.header.getPrevBlockHash()));
        sb.append("-");
        sb.append(String.valueOf(this.header.getTimeStamp()));
        sb.append("-");
        sb.append(String.valueOf(this.header.getTransactionsHash()));
        sb.append("-");
        sb.append(String.valueOf(this.getSeqNum()));
        String hash =Utils.applySha256(sb.toString());
        System.out.println("verifying block hash ..");
        return hash.equals( this.hash);
    }

    @Override
    public boolean verifyPrevHash() {
        if(this.prevBlock == null){
            return true;
        }
        return hashBlock(this.prevBlock).equals(this.header.getPrevBlockHash()) ;
    }


    @Override
    public String calculateHash() {
        return getBlockHash();
    }





    @Override
    public ITransaction getTransactionByID(int id) {
        if (this.ts == null){
            System.out.println("error , trying to get transaction without providing any ..");
            return null;
        }
        for (ITransaction t :this.ts){
            if(t.getID() == id){
                return t;
            }
        }
        return null;
    }
}