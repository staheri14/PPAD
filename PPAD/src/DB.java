import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;


public class DB {
    public DB()
    {

    }

    /**
     * Fills in PSP.DBName file with PSP.numUsers profiles of maximum PSP.attribuePerUser random attributes
     * @param numUsers
     * @param numAd
     * @param attribuePerUser
     */
    public DB(int numUsers, int numAd, int attribuePerUser)
    {
        //this.attribuePerUser=attribuePerUser;
        //this.numUsers=numUsers;
        //this.numAd=numAd;
        try {
            File f = new File(PSP.DBName);
            //clear old content
            PrintWriter writer = new PrintWriter(f);
            writer.print("");
            writer.close();
            FileWriter fw = new FileWriter(f);
            Random r = new Random();
            for (int i = 0; i < PSP.numUsers; i++) {
                ArrayList<Integer> att=new ArrayList<>();
                for (int j = 0; j < PSP.attribuePerUser; j++) {
                    int nextAtt=r.nextInt(PSP.numOfAttributes);
                    while(att.contains(nextAtt))
                        nextAtt=r.nextInt(PSP.numOfAttributes);
                    att.add(nextAtt);
                    fw.write(Integer.toString(nextAtt));
                    //System.out.println(r.nextInt(PSP.numOfAttributes));
                    if(j==PSP.attribuePerUser-1)
                        fw.write("\n");
                    else
                        fw.write(",");

                }
            }
            fw.close();
        }catch (Exception e)
        {

        }

    }

    /**
     * Fills in the adDB.txt with the randomly generated advertising requests
     */
    public void generateAdDB()
    {
        try {
            File f = new File(PSP.adDBName);
            //clear old content
            PrintWriter writer = new PrintWriter(f);
            writer.print("");
            writer.close();
            FileWriter fw = new FileWriter(f);
            Random r = new Random();
            for (int i = 0; i < PSP.numAd; i++) {
                ArrayList<Integer> att=new ArrayList<>();
                for (int j = 0; j < PSP.attPerAd; j++) {
                    int nextAtt=r.nextInt(PSP.numOfAttributes);
                    while(att.contains(nextAtt))
                        nextAtt=r.nextInt(PSP.numOfAttributes);
                    att.add(nextAtt);
                    fw.write(Integer.toString(nextAtt));
                    //System.out.println(r.nextInt(PSP.numOfAttributes));
                    if(j==PSP.attPerAd-1)
                        fw.write("\n");
                    else
                        fw.write(",");

                }
            }
            fw.close();
        }catch (Exception e)
        {

        }

    }
    public static  String[] adRandom()
    {
        String []ad=new String[PSP.attPerAd];
        Random r = new Random();
            for (int j = 0; j < PSP.attPerAd; j++) {
                ad[j]=Integer.toString(r.nextInt(PSP.numOfAttributes));
            }

        return ad;
    }


}
