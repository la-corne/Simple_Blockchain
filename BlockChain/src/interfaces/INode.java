package interfaces;

import concrete.PairKeyPK;
import concrete.Transaction;

import java.io.IOException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;

public interface INode {
    public void setConfigs(boolean isPow,int maxNumTransactions, ArrayList<String> IPsOfOtherPeers, int nodeType,int diff, int malicious);//0 for client , 1 for miner
    public void issueTransactions();//for client nodes, issue for ids from .. to ..
    public void setNTW(INTW ntw);

    public int getNodeType();

    public void addTransaction(Transaction t) throws IOException, InterruptedException; //when # of ts == max , call create block

    public void createBlock() throws IOException, InterruptedException; //creates a block using the transactions , prev block and agreement method

    public boolean verifyTransaction(ITransaction t);

    //returns the tr with that id if it still has values !=0 , null otherwise
    public ITransaction getUnspentTransactionByID(int id);
    //called if the block was rejected to reset the available to be equal to the original value
    public void resetUnspent();
    //called if the block was accepted to decrease the values to be equal to availabl
    public void commitUnspent();
    public void shareBlock(IBlock block) throws IOException; //share the block over the network
    public void pow(IBlock block, int difficulty) throws IOException, InterruptedException;
    // agree/disagree on a block coming from the ntw..send the decision to the ntw and add/not to the chain
    // use th agreementmethod (BFT/pow) to agree/not
    public void receiveBlock(IBlock block) throws IOException;//flag : 0 block , 1 response

    public void addToChain(IBlock block) throws IOException;

    public IBlock getLastBlock();

    public PublicKey getPrimaryId();

    public void setPrimaryId(PublicKey primaryId);


    public int getSeqNum();
    public void setSeqNum();
    public void setSeqNum(int seqNum);

    public PublicKey getNodePublicKey();
    public int getViewNum();

    public void setViewNum();
    public void setViewNum(int viewNum);

    public void setIsPrimary();

    public boolean getIsPrimary();

    public void setState(String state);

    public String getState();

    public void setMaxMaliciousNodes(int f);

    public int getMaxMaliciousNodes();

    public IMessagePool getPreparePool();

    public IMessagePool getCommitPool();


    //public IMessagePool getChangeViewPool();


    public void generateNewBlockMessage(IBlock block) throws IOException, InterruptedException;

    //public void generateViewChangeMessage(int newViewNum) throws IOException;

    //public void generateViewChangedMessage() throws IOException;

    public void generatePreprepareMessage() throws IOException, InterruptedException;

    public void generatePrepareMessage() throws IOException;

    public void generateCommitMessage() throws IOException;

    public void broadcastMessage(IMessage message) throws IOException;
    public void addTransaction(ArrayList<ITransaction> ledger);
    public void broadCastPublicKeys(HashMap<Integer,PublicKey> keys) throws IOException;

    void setPublicKeys(HashMap<Integer, PublicKey> t);
    public void readConfiguration() throws IOException;


    public void generateKeyPair() throws IOException;
    public void generateNodeSignature();
    public IBlock getNewBlock();
    public void setNewBlock(IBlock newBlock);
    public void setNewBlock(IMessage newBlockMessage) throws IOException, InterruptedException;
    public void insertPreprepareMessage(IMessage preprepareMessage) throws IOException;
    public void insertPrepareMessageInPool(ArrayList<IMessage> prepareMessages) throws IOException;
    public void insertCommitMessageInPool(ArrayList<IMessage> commitMessages) throws IOException;
    //public void insertChangeViewMessageInPool(ArrayList<IMessage> changeViewMessages) throws IOException;
    //public void checkTruthyOfNewView(IMessage viewChangedMessage);
    //public boolean verifyNewViewPool(IMessagePool messagePool);
    public void receiveMessage(IMessage t) throws IOException, InterruptedException;
    public void sendConfigMessage(IMessage m) throws IOException;
    public int sizeOfNetwork();

    public int getNewViewNum();
    public void setNewViewNum(int newViewNum);
    public IBlock getBlock();
    public void setBlock(IBlock block);
    public boolean verifyBlockTransactions(ArrayList<ITransaction> transactions);
    public byte[] getNodeSignature();
    public void setNodeSignature(byte[] nodeSignature);
    public IValidator getValidator();
    public void setValidator(IValidator validator);
    public INTW getNetwork();
    public void setNetwork(INTW network);
    public IBlock getCurrentBlock();
    public void setCurrentBlock(IBlock currentBlock);
    public boolean verifyTransactionsSignature(ArrayList<ITransaction> transactions);
    public String getPrevState();
    public void setPrevState(String prevState);

    void receivePK(PairKeyPK t);

    void receiveReport(Object t);
}
