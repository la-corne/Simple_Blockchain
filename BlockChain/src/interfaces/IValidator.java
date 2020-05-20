package interfaces;

import java.util.ArrayList;

public interface IValidator {

    public void initiateNewBlockMessage();

    /*stop adding transactions to the block*/
    public IMessage finalizeBlock();

}
