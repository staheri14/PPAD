import jdk.nashorn.internal.runtime.linker.InvokeByName;
import paillierp.Paillier;
import paillierp.key.KeyGen;
import paillierp.key.PaillierKey;
import paillierp.key.PaillierPrivateKey;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by stahe on 12/25/2017.
 */
public class PSP {

    static  boolean generateNewProfiles=true;
    static  boolean generateNewAds=true;
    public static int groupSize=5;
    static  int thr=1;//(int)Math.ceil(groupSize*1);
    public static int count=0; //number of registered users

    public static BigInteger[] delimiterSet;

    //bloom filter specifications
    static double falsePositive=0.001;
    static int numOfAttributes=473;
    public static int bloomFilterSize;

    //encryption specifications
    public static PaillierKey publicKey;
    private PaillierPrivateKey privateKey;
    //
    private static BigInteger secretShares[];
    // system parameters
    static int attribuePerUser=403;
    static String DBName="DB.txt";
    static String adDBName="adDB.txt";
    static int numUsers=1000  ;
    static int numAd=100;
    static int attPerAd=1;
    public PSP()
        {
        BloomFilter bF=new BloomFilter(this.falsePositive,this.numOfAttributes);
        this.bloomFilterSize=bF.getBitSet().size();
        createDelimiterSet();
        int encryptionKeyLength=computePKlength();
        //generate key pairs
        SecureRandom rnd = new SecureRandom();
        privateKey = KeyGen.PaillierKey(encryptionKeyLength, rnd.nextLong() );
        publicKey = privateKey.getPublicKey();
        Paillier paillier = new Paillier(publicKey);//make encryptor
        paillier.setDecryptEncrypt(privateKey);
        }

    /**
     *
     * @param cipher
     * @return
     */
    public BigInteger decrypt(BigInteger cipher)
        {
            BigInteger message;
            try {

                Paillier decrypter = new Paillier(privateKey );
                message = decrypter.decrypt(cipher);
                return message;


            }catch (Exception ex) {
                System.out.println(ex.getMessage() );
            }
            return null;
        }


    /**
     *
     * @param
     */
    private void createDelimiterSet()
    {
        BigInteger bFSize=new BigInteger(Integer.toString(bloomFilterSize));
        BigInteger one=new BigInteger(Integer.toString(1));
        BigInteger sum=new BigInteger(Integer.toString(0));
        BigInteger delimiter=one;
        delimiterSet=new BigInteger[groupSize];
        for(int i=0;i<groupSize;i++)
        {
            delimiterSet[i]=delimiter;
            sum=sum.add(delimiter);
            delimiter=delimiter.add((sum.multiply(bFSize)).add(one));
        }
    }

    /**
     *
     * @return
     */
    private static BigInteger[] createSecretShares()
    {
        BigInteger modulus=publicKey.getN();
        BigInteger secretShares[]=new BigInteger[groupSize];
        BigInteger sum=new BigInteger(Integer.toString(0));


        for(int i=0;i<groupSize-1;i++)
        {
            Random rand = new Random();
            secretShares[i]= new BigInteger(delimiterSet[delimiterSet.length-1].bitLength(), rand);
            sum=sum.add(secretShares[i].mod(modulus));
        }
        secretShares[groupSize-1]=new BigInteger(Integer.toString(0)).subtract(sum).mod(modulus);
        return secretShares;
    }

    /**
     *
     * @return
     */
    public static BigInteger[] userRegistration()
    {

        int member=count % groupSize;
        double GID=Math.ceil(((double)count+1)/((double) groupSize));

        if (count % groupSize==0)
        {
            secretShares=createSecretShares();
        }
        BigInteger GroupInfo[]=new BigInteger[3];
        GroupInfo[0]=delimiterSet[member];
        GroupInfo[1]=secretShares[member];
        GroupInfo[2]=new BigInteger(Integer.toString((int)GID));
        count++;
        return  GroupInfo;
    }
    public  int computePKlength()
    {
        BigInteger biggestData=new BigInteger(Integer.toString(0));
        Random rand = new Random();

        for(int i=0;i<groupSize;i++)
        {
            biggestData=biggestData.add(delimiterSet[i].multiply(new BigInteger(Integer.toString(bloomFilterSize))));
        }
        return biggestData.bitLength();

    }
    public int isMatch(BigInteger agg,BigInteger req){
        BigInteger Q;
        int count=0;
        for(int i=groupSize-1;i>=0;i--)
        {
            Q=agg.divide(delimiterSet[i]);
            if(Q.compareTo(req)==0)
            {
                count++;
            }
            agg=agg.mod(delimiterSet[i]);

        }
        if(count==0)

            return count;
        else
        {
           // System.out.println("number of matched: "+count);
            return count;
        }
    }

}
