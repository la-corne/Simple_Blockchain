package concrete;

import java.io.Serializable;
import java.security.PublicKey;

public class PairKeyPK implements Serializable {
    public PairKeyPK(String ip, PublicKey pk) {
        this.ip = ip;
        this.pk = pk;
    }

    public String getIp() {
        return ip;
    }

    public PublicKey getPk() {
        return pk;
    }

    private String ip;
    private PublicKey pk;
}
