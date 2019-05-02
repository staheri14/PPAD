import java.io.File;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Created by stahe on 1/3/2018.
 */
public class Performance {
    static  ArrayList<String[]> Users=new ArrayList<>(PSP.numUsers);
    static ArrayList <String[]> Advertismnts=new ArrayList<>(PSP.numAd);
    static double[][] falePos=new double[11][11];
    static double[][] truePos=new double[11][11];
    static double[][] score=new double[11][11];
    static int[] numOfAttPerAd={1,5,10,15};
    public static void main(String[] args)
    {
        //BigInteger nom=new BigInteger("1331337215941872518282496548686853760711629313495951527758632771412447051448982162855");
        //BigInteger denom=new BigInteger("6716329004157198087668934639128455850710553770829302422048861786270521001736743275988");
        //System.out.println(nom.divide(denom));
        DB dataBase;
        if(PSP.generateNewProfiles)
            dataBase=new DB(PSP.numUsers,PSP.numAd,PSP.attribuePerUser);
        else
            dataBase=new DB();
        readDBInPlain();
        if(PSP.generateNewAds)
            dataBase.generateAdDB();
        readAdDBInPlain();
        double maxRatio=0;
        int bestGroupSize=0;
        int bestThr=0;
        int bestNumAd=0;
        for(int numAttPerAd=0;numAttPerAd<numOfAttPerAd.length; numAttPerAd+=1) {
            PSP.attPerAd=numOfAttPerAd[numAttPerAd];
            dataBase.generateAdDB();
            Advertismnts=new ArrayList<>(PSP.numAd);
            readAdDBInPlain();
            falePos=new double[11][11];
            truePos=new double[11][11];
            double[] t = ComputeAdAccuracy();
            System.out.println("numAttPerAd: "+numOfAttPerAd[numAttPerAd]);
            System.out.println("average target user: "+ t[2]);
            for (int groupSize = 2; groupSize <= 10; groupSize++) {
                PSP.groupSize = groupSize;
                for (int thr = 1; thr <= groupSize; thr++) {
                    PSP.thr = thr;
                    double[] res = ComputeAdAccuracy();
                    falePos[groupSize][thr] = 100-res[1];
                    truePos[groupSize][thr] = res[0];
                    score[groupSize][thr] = 0.5*res[0]+0.5*(100-res[1]);
                    // if ((res[1]!=0))
                    //{
                    if(maxRatio<score[groupSize][thr])
                    {
                        maxRatio=score[groupSize][thr];
                        bestGroupSize=groupSize;
                        bestThr=thr;
                        bestNumAd=numAttPerAd;

                    }
                    //}
                }
            }

            display(falePos);
            display(truePos);
            display(score);
        }
        // System.out.println(Arrays.toString(falePos));

        System.out.println("bestGroupSize "+bestGroupSize+"\n bestThr "+bestThr+"\n bestNumAd "+bestNumAd);



    }
    public static void readDBInPlain()
    {
        try {
            Scanner sc = new Scanner(new File(PSP.DBName));

            for (int i = 0; i < PSP.numUsers; i++) {
                String line = sc.nextLine();
                //System.out.println(line);
                String[] att = line.split(",");
                Users.add(att);
            }
            sc.close();
        }catch (Exception e)
        {

        }
    }
    public static void readAdDBInPlain()
    {
        try {
            Scanner sc = new Scanner(new File(PSP.adDBName));

            for (int i = 0; i < PSP.numAd; i++) {
                String line = sc.nextLine();
                //System.out.println(line);
                String[] att = line.split(",");
                Advertismnts.add(att);
            }
            sc.close();
        }catch (Exception e)
        {

        }
    }
    /* public static void ComputeAdAccuracy()
     {
         double adAccuracy=0;
         double falsePsitive=0;
         for(String[] adReq:Server.adRequests) {
             double totalAvgAccuracy=0;
             double numOfTarget=0;
             double numOfTargetShown=0;
             double numOfUnTargetShown=0;
             for (Group g : Server.groups.values()) {
                 if (g.members.size() == PSP.groupSize) {
                     double avgAccuracy = 0;
                     String[][] groupAtt = new String[PSP.groupSize][PSP.attribuePerUser];
                     int k = 0;
                     for (Profile p : g.members.values()) {
                         groupAtt[k++] = p.attSet;
                     }
                     // for(String[] adReq:Server.adRequests) {
                     double matchRes = (double) PSP.plainTextMatch(groupAtt, adReq);
                     avgAccuracy = avgAccuracy + (matchRes / (double) PSP.groupSize);
                     numOfTarget += matchRes;
                     if (matchRes >= PSP.thr) {
                         numOfTargetShown += matchRes;
                         numOfUnTargetShown += PSP.groupSize - matchRes;
                     }
                     //  }
                     avgAccuracy = avgAccuracy / Server.adRequests.length;
                     // System.out.println("Ind Accuracy: " + avgAccuracy);
                     totalAvgAccuracy += avgAccuracy;
                 }
             }
             double numOfGroups = Math.floor(PSP.numUsers / PSP.groupSize);
             double numberOfUnTarget = (PSP.numUsers - numOfTarget);
             totalAvgAccuracy = totalAvgAccuracy / (double) numOfGroups;
             // System.out.println("Accuracy: " + totalAvgAccuracy);
             // System.out.println("numOfTarget: " + numOfTarget);
             // System.out.println("numOfTargetShown: " + numOfTargetShown);
             //System.out.println("Ad accuracy: " + numOfTargetShown * 100 / numOfTarget + "%");
             adAccuracy+=(numOfTargetShown * 100 / numOfTarget);
             //System.out.println("numOfUnTargetShown: " + numOfUnTargetShown);
             // System.out.println("false positive: " + numOfUnTargetShown * 100 / numberOfUnTarget + "%");
             falsePsitive+= (numOfUnTargetShown * 100 / numberOfUnTarget);
             System.out.println("Target= "+numOfTarget );
             System.out.println("numOfUnTargetShown= "+numOfUnTargetShown );
             System.out.println("numOfTargetShown= "+numOfTargetShown );
             System.out.println("---------------------------------" );

         }
         System.out.println("Total ad accuracy: "+adAccuracy/PSP.numAd );
         System.out.println("Total false positibe: "+falsePsitive/PSP.numAd );
     }*/
    public static double[] ComputeAdAccuracy()
    {
        double averageTarget=0;
        int numberOfValidAd=0;
        boolean flag=true;
        double[] resultPerf=new double[3];
        double adAccuracy=0;
        double falsePsitive=0;
        int AdCount=0;
        for(String[] adReq:Advertismnts) { //check for every Ad
            //System.out.println("Ad number= "+AdCount++ +" " +Arrays.toString(adReq));
            double totalAvgAccuracy=0;
            double numOfTarget=0;
            double numOfTargetShown=0;
            double numOfUnTargetShown=0;
            int count=0;
            while (count<=PSP.numUsers-PSP.groupSize) { //checl all the target groups in the OSN
                if (PSP.numUsers>= count+PSP.groupSize) {
                    double avgAccuracy = 0;
                    String[][] groupAtt = new String[PSP.groupSize][PSP.attribuePerUser];

                    for (int k = 0; k<PSP.groupSize;k++) {
                        groupAtt[k] = Users.get(count+k);
                    }
                    double matchRes = (double) plainTextMatch(groupAtt, adReq);
                    //avgAccuracy = avgAccuracy + (matchRes / (double) PSP.groupSize);
                    numOfTarget += matchRes;
                    if (matchRes >= PSP.thr) {
                        numOfTargetShown += matchRes;
                        numOfUnTargetShown += PSP.groupSize - matchRes;
                    }
                    //  }
                    //avgAccuracy = avgAccuracy / Advertismnts.size();
                    // System.out.println("Ind Accuracy: " + avgAccuracy);
                    // totalAvgAccuracy += avgAccuracy;
                    count+=PSP.groupSize;
                }else{
                    break;
                }
            }
            double numOfGroups = Math.floor(PSP.numUsers / PSP.groupSize);
            double numberOfUnTarget = (PSP.numUsers - numOfTarget);
            totalAvgAccuracy = totalAvgAccuracy / (double) numOfGroups;
            // System.out.println("Accuracy: " + totalAvgAccuracy);
            // System.out.println("numOfTarget: " + numOfTarget);
            // System.out.println("numOfTargetShown: " + numOfTargetShown);
            //System.out.println("Ad accuracy: " + numOfTargetShown * 100 / numOfTarget + "%");
            if(numOfTarget!=0)
            {
                numberOfValidAd++;
                adAccuracy+=(numOfTargetShown * 100 / numOfTarget);
            }
            //System.out.println("numOfUnTargetShown: " + numOfUnTargetShown);
            // System.out.println("false positive: " + numOfUnTargetShown * 100 / numberOfUnTarget + "%");
            falsePsitive+= (numOfUnTargetShown * 100 / numberOfUnTarget);
            //  System.out.println("" );

            //System.out.println("#Target in the whole system= "+numOfTarget );
            // System.out.println("numOfTargetShown= "+numOfTargetShown );
            // System.out.println("true positive= "+numOfTargetShown*100/numOfTarget + " %" );
            // System.out.println("" );


            //System.out.println("numOfUnTargetShown= "+numOfUnTargetShown );
            //System.out.println("false positive= "+numOfUnTargetShown*100/numberOfUnTarget +" %");
            // System.out.println("---------------------------------" );
            averageTarget+=numOfTarget/PSP.numUsers;
        }
        // System.out.println("Total ad accuracy averaged over all the Ads: "+adAccuracy/PSP.numAd );
        resultPerf[2]=averageTarget/PSP.numAd;
        if(numberOfValidAd==0){
            resultPerf[0]=-100;
            resultPerf[1]=-100;

        }else{
            resultPerf[0]=adAccuracy/numberOfValidAd;
            resultPerf[1]=falsePsitive/numberOfValidAd;
        }
        //  System.out.println("Total false positibe  averaged over all the Ads: "+falsePsitive/PSP.numAd );

        return resultPerf;
    }

    public static void  thrGroupFalsePositiveFalseNegaticve()
    {
        for(int j=1;j<PSP.groupSize;j+=1)
        {
            // System.out.println("Thr= "+ j*0.1);
            PSP.thr=j;
            System.out.println("Thr= "+ PSP.thr);
            ComputeAdAccuracy();
        }

        ComputeAdAccuracy();
    }

    public static int plainTextMatch( String[][] group, String[] adReq)
    {
        int count =0;
        for (int i=0;i<group.length;i++)
        {
            if(Arrays.asList(group[i]).containsAll(Arrays.asList(adReq)))
                count++;
        }
        return count;
    }
    public static void display(double [][]arr)
    {
        NumberFormat formatter = new DecimalFormat("#0.00");

        for(int j=0;j<arr[0].length;j++)
        {
            if(j==arr[0].length-1)
                System.out.print(String.format("%" + 7 + "s", formatter.format(j)) +"\n");
            else
                System.out.print(String.format("%" + 7 + "s", formatter.format(j)) +"\t");
        }
        System.out.println();
        for(int i=0;i<arr.length;i++)
        {
            for(int j=0;j<arr[0].length;j++)
            {
                if(j==arr[0].length-1)
                    System.out.print(String.format("%" + 7 + "s", formatter.format(arr[i][j])) +"\n");
                else
                    System.out.print(String.format("%" + 7 + "s", formatter.format(arr[i][j])) +"\t ");


            }
        }
        System.out.println();

    }
}
