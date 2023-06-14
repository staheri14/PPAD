//import com.google.common.util.concurrent.AtomicLongMap;
import paillierp.Paillier;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


public class Server {
    public  static HashMap<BigInteger,Group> groups=new HashMap<>();
    //public static String[][] adRequestsAttributes=new String[PSP.numAd][PSP.attPerAd];
    public  Advertiser[] advertisers=new Advertiser[PSP.numAd];
    public static Paillier paillier = new Paillier(PSP.publicKey);
    public static BigInteger nSquare = PSP.publicKey.getN().multiply(PSP.publicKey.getN());
    static  int completedThreads=0;

    /**
     *
     * @param encryptedModifiedBF
     * @param reqBF a simple not-encrypted bloom filter,
     * @return a Ciphertext wthat is the aggregate of encryptedModifiedBF based on the one posiotions in reqBF
     */
    public static BigInteger aggregate(BigInteger[] encryptedModifiedBF, BloomFilter reqBF)
    {
        BigInteger aggregate=paillier.encrypt(new BigInteger(Integer.toString(0)));
        for(int i=0;i<encryptedModifiedBF.length;i++)
        {
            if(reqBF.getBitSet().get(i))
            {
                aggregate=aggregate.multiply(encryptedModifiedBF[i]).mod(nSquare);
            }
        }
        return aggregate;
    }

    /**
     *
     * @param encryptedModifiedBF is an array of encrypted elementd using paillier encryption scheme
     * @return aggregate all the encryptedModifiedBF elements and returns the ciphertext
     */
    public static BigInteger aggregate(BigInteger[] encryptedModifiedBF)
    {
        BigInteger aggregate=paillier.encrypt(new BigInteger(Integer.toString(0)));
        for(int i=0;i<encryptedModifiedBF.length;i++)
        {

                aggregate=aggregate.multiply(encryptedModifiedBF[i]).mod(nSquare);

        }
        return aggregate;
    }

    /**
     *
     * @param encryptedModifiedBF
     * @param reqBF
     * @return
     */
    public static BigInteger groupAggregate(BigInteger[][] encryptedModifiedBF, BloomFilter reqBF)
    {
        //initialize the aggregate which will contain the element-wise aggregation group's profiles
        BigInteger[] aggregate= new BigInteger[encryptedModifiedBF.length];
        for (int i=0;i<encryptedModifiedBF.length;i++)
        {
            aggregate[i]=paillier.encrypt(new BigInteger(Integer.toString(0)));
        }

        //aggregate group members' encrypted modified bloom filters in a element-wise manner(profiles) to cancel out the secret shared value
        for(int i=0;i<encryptedModifiedBF[0].length;i++)
        {
            //aggregate the ith bit for all the members
            if(!reqBF.getBitSet().get(i)) continue;
            for(int j=0;j<PSP.groupSize;j++)
            {

                    aggregate[j] = aggregate[j].multiply(encryptedModifiedBF[j][i]).mod(nSquare);
            }
        }

        //then aggregate the elements of the aggregate array
        BigInteger result=paillier.encrypt(new BigInteger(Integer.toString(0)));
        for (int i=0;i<encryptedModifiedBF.length;i++)
        {
            result=result.multiply(aggregate[i]).mod(nSquare);
        }
        return result;
    }
    public void RestartServer()
    {
        groups=new HashMap<>();
        //Server.avgRunTimeProfileCreation=0;
    }
    /**
     *
     * Reads the DB and creates encrypted profiles and groups them
     * Server.groups contains the groups info and the members
     */
    public void readAndMakeProfiles()
    {
        long startTime=0;
        long endTime=0;
        long avgRunTimeProfileCreation=0;

        Paillier paillier=new Paillier(PSP.publicKey);
        try{
            Scanner sc=new Scanner(new File(PSP.DBName));
            for (int i = 0; i < PSP.numUsers; i++) {
                String line=sc.nextLine();
                //System.out.println(line);
                String []att=line.split(",");

                BigInteger[] gInfo=PSP.userRegistration();
                startTime = System.nanoTime();
                Profile p=new Profile(att,gInfo[0],gInfo[1],PSP.publicKey);
                p.encryptProfile();
                endTime = System.nanoTime();
                avgRunTimeProfileCreation=avgRunTimeProfileCreation+endTime-startTime;
                BigInteger gID=gInfo[2];
                Group g;
                if(!Server.groups.containsKey(gID))
                {
                      g=new Group();
                      g.gID=gID;
                      g.members.put(g.size,p);
                      g.size=new AtomicInteger(g.size.getAndAdd(1));
                      Server.groups.put(gID,g);
                }else {
                      //Server.groups.get(gID).members.add(p);
                    g=Server.groups.get(gID);
                    g.members.put(g.size,p);
                    g.size=new AtomicInteger(g.size.getAndAdd(1));
                }
                //MyThread th=new MyThread(p);
                //th.start();





            }
            System.out.println("Average time for profile creation=" +avgRunTimeProfileCreation / PSP.numUsers);

        }catch (Exception e)
        {
            System.out.println("error in reading DB");
            e.printStackTrace();

        }
    }
    public void readAndMakeAds()
    {
        long startTime=0;
        long endTime=0;
        long avgRunTimeAdCreation=0;

        try{
            Scanner sc=new Scanner(new File(PSP.adDBName));
            for (int i = 0; i < PSP.numAd; i++) {
                String line=sc.nextLine();
                String []att=line.split(",");
                endTime = System.nanoTime();
                Advertiser ad=new Advertiser(att);
                endTime = System.nanoTime();
                avgRunTimeAdCreation=avgRunTimeAdCreation+endTime-startTime;
                advertisers[i]=ad;

            }
            System.out.println("Average time for Ad creation=" +avgRunTimeAdCreation / PSP.numAd);

        }catch (Exception e)
        {
            System.out.println("error in reading DB");
            e.printStackTrace();

        }
    }
    public void aggregateAndMatch()
    {

    }
    private class MyThread extends  Thread{

        String att[];
        BigInteger[] gInfo;
        Profile p;
        public MyThread(Profile p)
        {
            this.p=p;
            //this.att=att;
            /*gInfo=PSP.userRegistration();
            p=new Profile(att,gInfo[0],gInfo[1],PSP.publicKey);
            BigInteger gID=gInfo[2];
            if(!Server.groups.containsKey(gID))
            {
                Group g=new Group();
                g.gID=gID;
                g.members.add(p);
                Server.groups.put(gID,g);
            }else {
                Server.groups.get(gID).members.add(p);
            }*/
        }
        public void run(){
            p.encryptProfile();
            Server.completedThreads++;
        }

    }
}
