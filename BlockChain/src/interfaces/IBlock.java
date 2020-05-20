package interfaces;

import java.util.ArrayList;

public interface IBlock {

    public IBlockHeader getHeader();
    public void setTransactions(ArrayList<ITransaction> ts);
    public ArrayList<ITransaction> getTransactions();
    public void setPrevBlock(IBlock block);
    public IBlock getPrevBlock();
    //public String getBlockHash();
    public ITransaction getTransactionByID(int id);

    public boolean verifyBlockHash();

    public boolean verifyPrevHash();
    public String getBlockHash();

    public String calculateHash();
    public void setHash(String hash);

    public int getSeqNum();

    public void setSeqNum(int seqNum);




}
