package interfaces;

import concrete.*;

import java.io.IOException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;

public interface INTW {
    public void setNode(Node node) throws IOException, ClassNotFoundException;
    public void listenForNewConnections(String ip) throws IOException; //listens for any new node request and add it to peers List
    public ArrayList<String> getConnectedPeers();
    public void listenForTransactions(Transaction t) throws IOException, InterruptedException;
    public void issueTransaction(Transaction transaction) throws IOException;//to all ips / primary
    public void shareBlock(IBlock block, String peer) throws IOException; //share the block using the agreement method
    public void listenForResponses(Response r);
    public void listenForBlocks(Block b) throws IOException; //listen for any shared blocks and calls agreeOnBlock (only if node type is 1 in pow)
    public String getExternalIP() throws IOException;
    public void startServer() throws IOException, ClassNotFoundException, InterruptedException;
    public void setPublicKeys(HashMap<Integer,PublicKey> t);
    public void broadcastlock(IBlock block) throws IOException;
    public void broadcastPK(HashMap<Integer,PublicKey> keys) throws IOException;
    public void sharepublickeys(HashMap<Integer, PublicKey> keys, String peer) throws IOException;

    public void broadcastPK(PairKeyPK pair) throws IOException;
    public void sharepublickeys(PairKeyPK pair,String peer) throws IOException;
    public String getIP() throws IOException;


    public void shareMessage(IMessage message,String peer) throws IOException, InterruptedException; //share message to all nodes
    public void broadcastMessage(IMessage message) throws IOException;
    public void listenForMessages(IMessage t) throws IOException, InterruptedException;
    public boolean isPrimary();

    public void setPrimary(boolean primary);
    public int getsizeofPeers();

    public void sendConfigMessage(IMessage m) throws IOException;
    public String getNextPrimary();
    public void constructTable() throws IOException;
    public void shareResponse(Response r,String peer) throws IOException;
    public void broadcastResponse(Block block,boolean response) throws IOException;

    void sendPeers(ArrayList<String> ips,ArrayList<Integer> types) throws IOException;
    public void listenforPublicKey(PairKeyPK t);
    public PublicKey getPkfromPairPK(String nextPrimary);
    public void sendConfigMessageAtFirst(IMessage m) throws IOException;
    public void listenForAnalytics(Object t) ;
    void broadcastAnalytics(IAnalyser.Analytics myData) throws IOException;
}