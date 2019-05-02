import paillierp.Paillier;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main {

    public static Server server;
    static DB dataBase;
    public static void main(String[] args) {

        double totalMatch=0;
        double totalDisplayed=0;

        long avgRunTime=0;
        long startTime=0;
        long endTime=0;
        PSP pSP=new PSP();
        server=new Server();

    //===============Read DB and Make profiles=========================
        if(PSP.generateNewProfiles)
            dataBase=new DB(PSP.numUsers,PSP.numAd,PSP.attribuePerUser);
        else
            dataBase=new DB();

        server.readAndMakeProfiles();

    //===============Read advertising DB and Make Ads=========================
        if(PSP.generateNewAds)
             dataBase.generateAdDB();
        server.readAndMakeAds();

        //String[]tAud=Server.adRequests[0];
        //System.out.println(Arrays.toString(tAud));


      // Advertiser ad=server.advertisers[0];
       // BigInteger req=ad.countOnes();

     //   System.out.println(Server.avgRunTimeProfileCreation / PSP.numUsers);
       // ProfileCrreationRnTime();
       // AdCreationRunTime();
        System.out.println("matching is begun");
        for(Advertiser ad:server.advertisers) {
            for (Group g : server.groups.values()) {
                if (g.members.size() == PSP.groupSize) {
                    //System.out.println("Group: "+g.gID);
                    BigInteger[][] groupAggP = new BigInteger[PSP.groupSize][PSP.bloomFilterSize];
                    int count = 0;
                    for (Profile p : g.members.values()) {
                        //groupAggP[count++]=Server.aggregate(p.encryptedModifiedBF,ad.bF);
                        groupAggP[count++] = p.encryptedModifiedBF;
                        //System.out.println(Arrays.toString(p.attSet));
                    }
                   // startTime = System.nanoTime();
                    BigInteger EAggGPf = Server.groupAggregate(groupAggP, ad.bF);
                    //endTime = System.nanoTime();
                    //avgRunTime = avgRunTime + endTime - startTime;
                    startTime = System.nanoTime();
                    BigInteger result = pSP.decrypt(EAggGPf);
                    int matchResult = pSP.isMatch(result, ad.numOfOnes);
                    endTime = System.nanoTime();
                    avgRunTime = avgRunTime + endTime - startTime;

                    if (pSP.isMatch(result, ad.numOfOnes) > 0)
                        System.out.println("match");
                    else
                        System.out.println("NO match");


                    totalMatch += matchResult;
                    if (matchResult >= PSP.thr) {
                        totalDisplayed += PSP.groupSize;
                    }
                }
            }
        }


        System.out.println(avgRunTime/server.groups.size());

      //  System.out.println("Ad  accuracy: " + totalMatch+" "+totalDisplayed);
/*

        BigInteger[] groupAggP=new BigInteger[PSP.groupSize];

        BigInteger[] gInfo=PSP.userRegistration();
        Paillier paillier=new Paillier(PSP.publicKey);
        String[]a={"football","science","art"};
        Profile p=new Profile(a,gInfo[0],gInfo[1],PSP.publicKey);
        p.encryptProfile();

        groupAggP[0]=Server.aggregate(p.encryptedModifiedBF,ad.bF);

        String[]a2={"science","art"};
        BigInteger[] gInfo2=PSP.userRegistration();
        Profile p2=new Profile(a2,gInfo2[0],gInfo2[1],PSP.publicKey);
        p2.encryptProfile();
        groupAggP[1]=Server.aggregate(p2.encryptedModifiedBF,ad.bF);

        BigInteger[][] arr= new BigInteger[PSP.groupSize][PSP.bloomFilterSize];
        arr[0]=p.encryptedModifiedBF;
        arr[1]=p2.encryptedModifiedBF;
        //pSP.decrypt(Server.groupAggregate(arr));*/



      //  BigInteger req=p2.sum;




    }

public static void ProfileCrreationRnTime()
{
    int []AttPerProfile={400,300,200,100,50,25};
    for(int i=0;i<AttPerProfile.length;i++) {
        System.out.println("AttPerProfile=  "+AttPerProfile[i]);
        PSP.attribuePerUser=AttPerProfile[i];
        dataBase=new DB(PSP.numUsers,PSP.numAd,PSP.attribuePerUser);
        server.readAndMakeProfiles(); //read from databaseand generate profiles
    }
}

    public static void AdCreationRunTime()
    {
        int []AttPerAd={400,300,200,100,50,25};
        for(int i=0;i<AttPerAd.length;i++) {
            System.out.println("AttPerProfile=  "+AttPerAd[i]);
            PSP.attPerAd=AttPerAd[i];
            dataBase.generateAdDB();
            server.readAndMakeAds();
        }
    }

}
