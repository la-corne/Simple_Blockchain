package concrete;
import interfaces.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

public class Utils implements IUtils{
    private static String dataDir="txdataset_v2.txt";
    private static Utils u = null;
    private HashMap<Integer, PublicKey> PKs = new HashMap<>();

    public static IUtils getInstance(){
        if (u == null ){
            u= new Utils();
        }
        return u;
    }

    @Override
    public String TransactionsDatasetDir() {
        return dataDir;
    }

    @Override
    public HashMap<Integer, PublicKey> getPKs() {
        return PKs;
    }

    public  PublicKey getPublicKeyFromID(int id) {
        return PKs.get(id);

    }
    @Override

    public void setID2PK(HashMap<Integer,PublicKey> id2keys) {
        this.PKs.putAll( id2keys);
    }











    //Applies Sha256 to a string and returns the result.
    public static String applySha256(String input){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            //Applies sha256 to our input,

            /* converting the input to byte array */
            byte[] hash = digest.digest(input.getBytes("UTF-8"));

            StringBuffer hexString = new StringBuffer(); // This will contain hash as hexidecimal

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);

                if(hex.length() == 1) hexString.append('0');

                hexString.append(hex);
            }
            return hexString.toString();
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }


    //Applies ECDSA Signature and returns the result ( as bytes ).
    //This method takes in the senders private key and string input, signs it and returns an array of bytes.
    public static byte[] applyECDSASig(PrivateKey privateKey, String input) {
        Signature dsa;
        byte[] output = new byte[0];
        try {
            dsa = Signature.getInstance("ECDSA", "BC");
            dsa.initSign(privateKey);
            byte[] strByte = input.getBytes();
            dsa.update(strByte);
            byte[] realSig = dsa.sign();
            output = realSig;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return output;
    }


    //Tacks in array of transactions and returns a merkle root.
    public static String getMerkleRoot(ArrayList<ITransaction> transactions) {
        int count = transactions.size();

        List<String> previousTreeLayer = new ArrayList<String>();
        for(ITransaction transaction : transactions) {
            previousTreeLayer.add(transaction.hash());
        }
        List<String> treeLayer = previousTreeLayer;

        while(count > 1) {
            treeLayer = new ArrayList<String>();
            for(int i=1; i < previousTreeLayer.size(); i+=2) {
                treeLayer.add(applySha256(previousTreeLayer.get(i-1) + previousTreeLayer.get(i)));
            }
            count = treeLayer.size();
            previousTreeLayer = treeLayer;
        }

        String merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
        return merkleRoot;
    }




    public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
        try {
            Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(data.getBytes());
            return ecdsaVerify.verify(signature);
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    //Returns difficulty string target, to compare to hash. eg difficulty of 5 will return "00000"
    public static String getDificultyString(int difficulty) {
        return new String(new char[difficulty]).replace('\0', '0');
    }

    // This method returns encoded string from any key.
    public static String getStringFromKey(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

}
