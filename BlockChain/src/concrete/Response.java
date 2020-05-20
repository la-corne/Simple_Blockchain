package concrete;

import java.io.Serializable;

public class Response implements Serializable {
    private Block block;

    public Block getBlock() {
        return block;
    }

    public boolean isFlag() {
        return flag;
    }

    private boolean flag;
    public Response(Block block, boolean flag) {
        this.block = block;
        this.flag = flag;
    }


}
