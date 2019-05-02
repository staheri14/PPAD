import paillierp.Paillier;
import paillierp.key.PaillierKey;

import java.math.BigInteger;

/**
 * Created by stahe on 12/25/2017.
 */
public class Profile {
    public BloomFilter bF;
     String[] attSet;
    private BigInteger delimiter;
    private BigInteger secretShare;
    public BigInteger sum;
     BigInteger[] encryptedModifiedBF=new BigInteger[PSP.bloomFilterSize];
    PaillierKey publicKey;

    /**
     *
     * @param attSet
     * @param delimiter
     * @param secretShare
     * @param publicKey
     */
    public Profile(String[] attSet,BigInteger delimiter, BigInteger secretShare,PaillierKey publicKey)
    {
        this.publicKey=publicKey;
        this.delimiter=delimiter;
        this.secretShare=secretShare;
        this.attSet=attSet;
        bF = new BloomFilter(PSP.falsePositive, PSP.numOfAttributes);
        for (String att: attSet)
        {
            bF.add(att);
        }
    }

    /**
     *
     */
    public void encryptProfile()
    {
        Paillier paillier=new Paillier(publicKey);
        sum=new BigInteger(Integer.toString(0));
        for(int i=0;i<bF.getBitSet().size();i++)
        {
            if(bF.getBitSet().get(i))
            {
                 encryptedModifiedBF[i]=paillier.encrypt(delimiter.add(secretShare).mod(publicKey.getN()));
               // sum=sum.add(delimiter.add(secretShare));
                sum=sum.add(new BigInteger(Integer.toString(1)));
             }
            else
            {
                encryptedModifiedBF[i]=paillier.encrypt(secretShare);
                //sum=sum.add(secretShare);
            }
        }
    }



}
