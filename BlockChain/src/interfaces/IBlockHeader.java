package interfaces;

import java.util.ArrayList;

public interface IBlockHeader {

    public void resetTimeStamp();
    public void createPrevBlockHash(IBlock prevBlock);
    public  void createTransactionsHash(ArrayList<ITransaction> ts ); // Merkle root hash
    public  void setNonce( int nonce); // -1 by default

    public String calculateHash();
    public String getHash();
    public void setHash(String hash);
    public void setTransactionsHash(String transactionsHash);
    public boolean isSet();//returns true if the prevBlock and transactions' hash are set



    public  long getTimeStamp();
    public String getTransactionsHash();
    public String getPrevBlockHash();
    public int getNonce();


}

//        hashPrevBlock: 256-bit hash of the previous block header.
//        hashMerkleRoot: 256-bit hash based on all of the transactions in block.
//        Time: Current block timestamp as seconds since 1970-01-01T00:00 UTC.
//        Nonce: 32-bit number (starts at 0) (used when PoW mining is used -1 otherwise).

