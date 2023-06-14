import paillierp.Paillier;
import paillierp.key.PaillierKey;

import java.math.BigInteger;


public class Profile {
    public BloomFilter bF;
     String[] attSet;
    private BigInteger delimiter; //group member identifier
    private BigInteger secretShare;
    public BigInteger sum; //the number of set bit in a bloom filter
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
     * Integrates the secret share and the delimiter into the BloomFilter and then
     * creates the encrypted profile
     */
    public void encryptProfile()
    {
        Paillier paillier=new Paillier(publicKey);
        sum=new BigInteger(Integer.toString(0));
        for(int i=0;i<bF.getBitSet().size();i++)
        {
            //if bit is 1
            if(bF.getBitSet().get(i))
            {
                 encryptedModifiedBF[i]=paillier.encrypt(delimiter.add(secretShare).mod(publicKey.getN()));
               // sum=sum.add(delimiter.add(secretShare));
                sum=sum.add(new BigInteger(Integer.toString(1)));
             } //if bit is zero
            else
            {
                encryptedModifiedBF[i]=paillier.encrypt(secretShare);
                //sum=sum.add(secretShare);
            }
        }
    }



}
