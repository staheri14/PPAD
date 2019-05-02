import paillierp.Paillier;
import paillierp.key.PaillierKey;

import java.math.BigInteger;

/**
 * Created by stahe on 12/27/2017.
 */
public class Advertiser {
    public BloomFilter bF;
    private String[] attSet;
    public BigInteger sum;
    public BigInteger numOfOnes=new BigInteger("0");
    public Advertiser(String[] attSet)
    {
        this.attSet=attSet;
        bF = new BloomFilter(PSP.falsePositive, PSP.numOfAttributes);
        for (String att: attSet)
        {
            bF.add(att);
        }
        numOfOnes=this.countOnes();
    }

    /**
     * Couts the number of set bits in the Bloom filter of advertising request
     * @return
     */
    private BigInteger countOnes()
    {
        sum=new BigInteger(Integer.toString(0));
        for(int i=0;i<bF.getBitSet().size();i++)
        {
            if(bF.getBitSet().get(i))
            {
                sum=sum.add(new BigInteger(Integer.toString(1)));
            }

        }
        return sum;
    }
}
