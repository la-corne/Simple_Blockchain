package concrete;

import interfaces.IBlock;
import interfaces.IMessage;
import interfaces.IMessagePool;

import java.io.Serializable;
import java.security.PublicKey;

public class Message implements IMessage, Serializable {

    private String type;
    private PublicKey primaryNodePublicKey;
    private int seqNum = 0;
    private int viewNum = 0;
    private int newViewNum = 0;
    private byte[] nodeSignature;
    private IBlock block;
    private PublicKey nodePublicKey;
    private IMessagePool messagePool;
    private int maxMaliciousNodes;

    public boolean isPrimary() {
        return isPrimary;
    }

    @Override
    public void setisPrimary(boolean b) {
        this.isPrimary=b;
    }


    private boolean isPrimary;

    //config
    public Message(String type,boolean isPrimary, PublicKey primaryNodePublicKey) {
        this.primaryNodePublicKey = primaryNodePublicKey;
        this.type = type;
        this.isPrimary = isPrimary;
    }

    //"change view"
    public Message(String type, PublicKey primaryNodePublicKey, int seqNum, int viewNum, byte[] nodeSignature, IBlock block, int newViewNum, PublicKey nodePublicKey) {
        this.type = type;
        this.primaryNodePublicKey = primaryNodePublicKey;
        this.seqNum = seqNum;
        this.viewNum = viewNum;
        this.nodeSignature = nodeSignature;
        this.block = block;
        this.newViewNum = newViewNum;
        this.nodePublicKey = nodePublicKey;
    }

    //"commit", finish, prepare
    public Message(String type, PublicKey primaryNodePublicKey, int seqNum, int viewNum, byte[] nodeSignature, IBlock block, PublicKey nodePublicKey) {
        this.type = type;
        this.primaryNodePublicKey = primaryNodePublicKey;
        this.seqNum = seqNum;
        this.viewNum = viewNum;
        this.nodeSignature = nodeSignature;
        this.block = block;
        this.nodePublicKey = nodePublicKey;
    }

    //"new block"
    public Message(String type, PublicKey primaryNodePublicKey, int seqNum, int viewNum, IBlock block) {
        this.type = type;
        this.primaryNodePublicKey = primaryNodePublicKey;
        this.seqNum = seqNum;
        this.viewNum = viewNum;
        this.block = block;
    }

    //view changed
    public Message(String type, PublicKey primaryNodePublicKey, int seqNum, int viewNum, byte[] nodeSignature, IBlock block, IMessagePool messagePool) {
        this.type = type;
        this.primaryNodePublicKey = primaryNodePublicKey;
        this.seqNum = seqNum;
        this.viewNum = viewNum;
        this.nodeSignature = nodeSignature;
        this.block = block;
        this.messagePool = messagePool;
    }

    //PrePrepare
    public Message(String type, PublicKey primaryNodePublicKey, int seqNum, int viewNum, byte[] nodeSignature, PublicKey nodePublicKey, IBlock block) {
        this.type = type;
        this.primaryNodePublicKey = primaryNodePublicKey;
        this.seqNum = seqNum;
        this.viewNum = viewNum;
        this.nodeSignature = nodeSignature;
        this.block = block;
        this.nodePublicKey = primaryNodePublicKey;

    }


    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public IBlock getBlock() {
        return this.block;
    }

    @Override
    public void setBlock(IBlock block) {
        this.block = block;
    }

    @Override
    public String getMessageType() {
        return this.type;
    }

    @Override
    public PublicKey getPrimaryPublicKey() {
        return this.primaryNodePublicKey;
    }

    @Override
    public void setPrimaryPublicKey(PublicKey primaryNodeKey) {
        this.primaryNodePublicKey = primaryNodeKey;
    }

    @Override
    public int getSeqNum() {
        return this.seqNum;
    }

    @Override
    public void setSeqNum(int seqNum) {
        this.seqNum = seqNum;
    }

    @Override
    public int getViewNum() {
        return this.viewNum;
    }

    @Override
    public void setViewNum(int viewNum) {
        this.viewNum = viewNum;
    }

    @Override
    public boolean verifyPeerSignature() {

        String data = Utils.getStringFromKey(this.nodePublicKey) + String.valueOf(this.seqNum) +
                String.valueOf(this.viewNum) + this.block.getHeader().getTransactionsHash();
        return Utils.verifyECDSASig(this.nodePublicKey, data, this.nodeSignature);

    }

    @Override
    public PublicKey getNodePublicKey() {
        return this.nodePublicKey;
    }

    @Override
    public int getNewViewNum() {
        return this.newViewNum;
    }

    @Override
    public IMessagePool getMessagePool() {
        return this.messagePool;
    }

    public int getMaxMaliciousNodes() {
        return maxMaliciousNodes;
    }

    public void setMaxMaliciousNodes(int maxMaliciousNodes) {
        this.maxMaliciousNodes = maxMaliciousNodes;
    }

    @Override
    public void setNodePublicKey(PublicKey nodePublicKey) {
        this.nodePublicKey = nodePublicKey;
    }

    @Override
    public byte[] getNodeSignature() {
        return nodeSignature;
    }
    @Override
    public void setNodeSignature(byte[] nodeSignature) {
        this.nodeSignature = nodeSignature;
    }
}
