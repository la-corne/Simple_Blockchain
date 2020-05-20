package concrete;

import interfaces.*;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.ArrayList;

public class Validator implements IValidator, Serializable {
    private IBlock newBlock;
    private IMessage newBlockMessage;
    private IMessagePool finishPool;
    private PublicKey primaryPublicKey;
    private boolean isCommited = false; // to stop taking messages after committing the block to the chain
    private int seqNum = 0;
    private int viewNum = 0;
    private int maxMaliciousNodes = 0;

    public Validator(PublicKey primaryPublicKey, int seqNum, int viewNum, int maxMaliciousNodes,IBlock block) {
        this.finishPool = new MessagePool();
        this.primaryPublicKey = primaryPublicKey;
        this.seqNum = seqNum;
        this.viewNum = viewNum;
        this.maxMaliciousNodes = maxMaliciousNodes;
        this.newBlock = block;
    }

    @Override
    public void initiateNewBlockMessage() {

//        IBlockHeader blockHeader = new BlockHeader();
//        blockHeader.createPrevBlockHash(prevBlock);
//        blockHeader.createTransactionsHash(transactions);
//        this.newBlock = new Block(blockHeader, transactions, prevBlock);
        System.out.println("primary public key: "+this.primaryPublicKey);
        this.newBlockMessage = new Message("new block",this.primaryPublicKey, this.seqNum, this.viewNum, this.newBlock);
        this.isCommited = false;

    }

    @Override
    public IMessage finalizeBlock() {
        //System.out.println("finalize primary public key: "+this.newBlockMessage.getNodePublicKey());
        return this.newBlockMessage;
    }

}
