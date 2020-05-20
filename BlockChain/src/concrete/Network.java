package concrete;

import interfaces.IAnalyser;
import interfaces.IBlock;
import interfaces.IMessage;
import interfaces.INTW;

import java.io.*;
import java.net.*;
import java.security.PublicKey;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Network implements INTW ,Runnable{
    private ArrayList<String> peers = new ArrayList<>();
    private ArrayList<String> ips = new ArrayList<>();
    private ArrayList<Integer> nodeTypes = new ArrayList<>();
    private ArrayList<String> tableOfNodes = new ArrayList<>();
    private String ExternalIP  ="";
    private Node node;
    private InetAddress sourceIP;
    private final static int PORT =5555;
    private static ObjectInputStream inputStream;
    private ServerSocket ss;

    //private ReentrantLock l;

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }

    @Override
    public int getsizeofPeers() {
        return peers.size();
    }

    public void sendConfigMessage(IMessage m) throws IOException {
        isPrimary = false;
        for (String peer:peers) {
            if (peer.equals(getNextPrimary())){
                m.setisPrimary(true);
                m.setPrimaryPublicKey(getPkfromPairPK(getNextPrimary()));
            }
            if (!getExternalIP().equals(getNextPrimary())) {
                Socket socket = new Socket(InetAddress.getByName(peer), PORT);
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                outputStream.writeObject(m);
                outputStream.flush();
                outputStream.close();
                socket.close();
            }
        }

    }
    public void sendConfigMessageAtFirst(IMessage m) throws IOException {
        for (String peer:peers) {
            if (peer.equals(getExternalIP())){
                m.setisPrimary(true);
                m.setPrimaryPublicKey(getPkfromPairPK(getNextPrimary()));
            }else{

            }
            Socket socket = new Socket(InetAddress.getByName(peer), PORT);
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(m);
            outputStream.flush();
            outputStream.close();
            socket.close();

        }

    }

    @Override
    public void broadcastAnalytics(IAnalyser.Analytics myData) throws IOException {
        for (String p:peers) {
            Socket socket = new Socket(InetAddress.getByName(p), PORT);
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(myData);
            outputStream.flush();
            outputStream.close();
            socket.close();
        }


    }

    public PublicKey getPkfromPairPK(String nextPrimary) {
        for (PairKeyPK pk:node.getPublicKeysIP()) {
            if(pk.getIp().equals(nextPrimary)){
                return pk.getPk();
            }
        }
        return null;
    }


    public void constructTable() throws IOException {
        tableOfNodes.clear();
        tableOfNodes.add(getExternalIP());
        for (String p:peers) {
            tableOfNodes.add(p);
        }
        Collections.sort(tableOfNodes,new AlphanumComparator());
    }

    public String getNextPrimary() {
        return tableOfNodes.get((tableOfNodes.indexOf(sourceIP.getHostAddress())+1)%tableOfNodes.size());
    }

    private boolean isPrimary=false;


    @Override
    public void setNode(Node node) throws IOException {
        this.node  =node;
        this.ExternalIP = getExternalIP();
        this.sourceIP = InetAddress.getByName(ExternalIP);
        System.out.println("Before lock");

        //l = new ReentrantLock();
        System.out.println("afterlock");
    }

    @Override
    public void listenForNewConnections(String IP) throws IOException {
        //TODO handle this to object
        peers.add(IP);
        constructTable();
    }

    @Override
    public ArrayList<String> getConnectedPeers() {
        return peers;
    }

    @Override

    public void listenForTransactions(Transaction t) throws IOException, InterruptedException {
        this.node.addTransaction(t);
    }

    @Override
    public void issueTransaction(Transaction transaction) throws IOException {
        for (String peer:peers) {
            System.out.println("Start"+peer);
            Socket socket = new Socket(InetAddress.getByName(peer), PORT);
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(transaction);
            outputStream.flush();
            outputStream.close();
            socket.close();
            System.out.println("done"+peer);
        }
    }

    @Override
    public void shareBlock(IBlock block, String peer) throws IOException {
        Socket socket = new Socket(InetAddress.getByName(peer), PORT);
        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
        outputStream.writeObject(block);
        outputStream.flush();
        outputStream.close();
        socket.close();
        Analyser.getInstance().reportMessageSent();

    }


    @Override
    public void shareResponse(Response r,String peer) throws IOException {
        Socket socket = new Socket(InetAddress.getByName(peer), PORT);
        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
        outputStream.writeObject(r);
        outputStream.flush();
        outputStream.close();
        socket.close();
        Analyser.getInstance().reportMessageSent();

    }
    public void broadcastResponse(Block block,boolean response) throws IOException {
        Response r = new Response(block,response);
        for (String p:peers) {
            shareResponse(r,p);
        }
        Analyser.getInstance().reportMessageSent();

    }

    @Override
    public void sendPeers(ArrayList<String> ips,ArrayList<Integer> nodeTypes) throws IOException {
        peers.clear();
        this.nodeTypes = nodeTypes;
        int indexOfIssuer = nodeTypes.indexOf(0);
        this.ips = ips;
        if (node.getNodeType() ==0){
            for (String p : ips) {
                if (!p.equals(getExternalIP())) {
                    peers.add(p);
                }
            }
        }else {
            for (String p : ips) {
                if (!p.equals(getExternalIP())) {
                    if (!p.equals(ips.get(indexOfIssuer)))
                        peers.add(p);
                }
            }
        }
        constructTable();
        if (tableOfNodes.get(0).equals(getExternalIP())&&node.getNodeType()==1){
            setPrimary(true);
        }
    }

    @Override
    public void listenForResponses(Response r) {
        //TODO CHECK WHAT THIS METHOD DO BLOCK AND BOOLEAN AND DIFF WITH LISTEN FOR BLOCKS
    }

    @Override
    public void listenForBlocks(Block b) throws IOException {
        this.node.receiveBlock(b);
    }

    @Override
    public String getExternalIP() throws IOException {
        URL whatismyip = new URL("http://checkip.amazonaws.com");
        BufferedReader in = new BufferedReader(new InputStreamReader(
                whatismyip.openStream()));
        return in.readLine();
    }

    @Override
    public void startServer() throws IOException, ClassNotFoundException, InterruptedException {
        ss = new ServerSocket(this.PORT);
        Object t;
        while(true){

            Socket s =ss.accept();
            inputStream = new ObjectInputStream(s.getInputStream());
            while(true) {
                try {
                    t = inputStream.readObject();
                    break;
                } catch (Exception e) {
                    continue;
                }
            }
            if (t instanceof Transaction){
                Object finalT2 = t;
                Thread tmp = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            listenForTransactions((Transaction) finalT2);
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                tmp.start();
            }else if (t instanceof Block){
                Object finalT1 = t;
                Thread tmp = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            listenForBlocks((Block) finalT1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                tmp.start();
            }else if ( t instanceof Response) {
                Object finalT = t;
                Thread tmp = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        listenForResponses((Response) finalT);
                    }
                });
                tmp.start();
            }else if ( t instanceof PairKeyPK) {
                Object finalT3 = t;
                Thread tmp = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        listenforPublicKey((PairKeyPK) finalT3);

                    }
                });
                tmp.start();
            }else if (t  instanceof  IAnalyser.Analytics){
                Object finalT4 = t;
                Thread tmp = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        listenForAnalytics(finalT4);
                    }
                });
                tmp.start();
            }else if (t instanceof HashMap){
                Object finalT5 = t;
                Thread tmp = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        setPublicKeys((HashMap<Integer, PublicKey>) finalT5);
                    }
                });
                tmp.start();

            }else if (t instanceof Message) {
                Object finalT6 = t;
                Thread tmp = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            listenForMessages((IMessage) finalT6);
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                tmp.start();
            }else {
                Object finalT7 = t;
                Thread tmp = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            listenForNewConnections((String) finalT7);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });
                tmp.start();
            }
        }

    }

    public void listenForAnalytics(Object t) {
        Analyser.getInstance().receiveData((IAnalyser.Analytics) t);
        //node.receiveReport(t);
    }

    public void listenforPublicKey(PairKeyPK t) {
        node.receivePK(t);
    }

    public void listenForMessages(IMessage t) throws IOException, InterruptedException {
        node.receiveMessage(t);
    }

    public void setPublicKeys(HashMap<Integer,PublicKey> t) {
        node.setPublicKeys(t);
    }

    @Override
    public void broadcastlock(IBlock block) throws IOException {
        for (String peer:peers) {
            shareBlock(block,peer);
        }
    }

    @Override
    public void broadcastPK(HashMap<Integer, PublicKey> keys) throws IOException {
        for (String peer:peers) {
            sharepublickeys(keys,peer);
        }
    }

    public void sharepublickeys(HashMap<Integer, PublicKey> keys, String peer) throws IOException {
        Socket socket = new Socket(InetAddress.getByName(peer), PORT);
        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
        outputStream.writeObject(keys);
        outputStream.flush();
        outputStream.close();
        socket.close();
    }

    @Override
    public void broadcastPK(PairKeyPK pair) throws IOException {
        for (String p:peers) {
            sharepublickeys(pair,p);
        }
    }

    @Override
    public void sharepublickeys(PairKeyPK pair, String peer) throws IOException {
        Socket socket = new Socket(InetAddress.getByName(peer), PORT);
        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
        outputStream.writeObject(pair);
        outputStream.flush();
        outputStream.close();
        socket.close();
    }

    @Override
    public String getIP() throws IOException {
        return getExternalIP();
    }


    @Override
     public void shareMessage(IMessage message,String peer) throws IOException, InterruptedException {
      //  l.lock();
        try {
            Socket socket = new Socket(InetAddress.getByName(peer), PORT);
            socket.setSendBufferSize(4098*10);
            socket.setReceiveBufferSize(4098*10);
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeUnshared(message);
            //outputStream.reset();
            outputStream.flush();
            outputStream.close();
            socket.close();
            Analyser.getInstance().reportMessageSent();
        }catch (Exception e){
            //l.unlock();
            Thread.sleep(500);
            Socket socket = new Socket(InetAddress.getByName(peer), PORT);
            socket.setSendBufferSize(4098*10);
            socket.setReceiveBufferSize(4098*10);
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(message);
            //outputStream.reset();
            outputStream.flush();
            outputStream.close();
            socket.close();
            Analyser.getInstance().reportMessageSent();
        }
    }

    @Override
    public void broadcastMessage(IMessage message) throws IOException {
        for (String p:peers) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        shareMessage(message,p);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();
        }

        Analyser.getInstance().reportMessageSent();
                //TODO 1N SOLUTION SEND TO ME THE NEW BLOCK MESSAGE

    }

    @Override
    public void run() {
        try {
            startServer();
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}