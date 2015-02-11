package Mammography;

import classes.ImageAccess;
import ij.ImagePlus;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import classes.SampEn2D;

public class Experiments {

    private static String dataPath = "/home/antonio/Dropbox/Artigos_Conferencias/Papers/Cancer-mama-SampEn2D/Data/";
    static classes.SampEn2D se = new SampEn2D();

    private static int m1 = 1;
    private static int m2 = 3;
    private static double r1 = 0.06;
    private static double r2 = 0.51;
    private static double rStep = 0.02;


//================================CODIGOS BIZU===============================
    public static void expEntropy(boolean normOption) {
//       Fazer experimento: 
//       Fazer medida de entropia em cada imagem nas pastas Benigno e Maligno
        if(normOption){ 
            System.out.println("SampEn2D-Experiment Set: <preliminar> experiment - Normalize Images: "+normOption);
            expEntropyWithNormalizedImages();
        }else {
            System.out.println("SampEn2D-Experiment Set: <preliminar> experiment - Normalize Images: "+normOption);
            expEntropyWithNoNormalizedImages();
        }
        
    }

    private static void expEntropyWithNormalizedImages() {
        try {
            
            BufferedWriter bwB = new BufferedWriter(new FileWriter("results_preliminar_benigno_ImgNorm.cvs"));

            File[] imgB = new File(dataPath + "Benigno/").listFiles();
            File[] imgM = new File(dataPath + "Maligno/").listFiles();
            Arrays.sort(imgB);
            Arrays.sort(imgM);

            for (int m = m1; m <= m2; m++) {
                bwB.write(";m=" + m);
                bwB.newLine();
                bwB.write("r");
                for (double r = r1; r < r2; r = r + rStep) {
                    bwB.write(";" + r);
                }

                bwB.newLine();
                for (File f : imgB) {
                    bwB.write(f.getName());

                    for (double r = r1; r < r2; r = r + rStep) {
                        ImageAccess img = new ImageAccess(new ImagePlus(f.getAbsolutePath()).getProcessor().convertToByte(true));
                        img.normalizeContrast();
                        double seValue = se.fastSampleEn2D(img, m, r);

                        bwB.write(";" + seValue);
                        System.out.println(f.getName() + ", m=" + m + ", r=" + r + ",  SE=" + seValue);
                    }
                    bwB.newLine();
                }
                bwB.newLine();
            }

            bwB.close();

            BufferedWriter bwM = new BufferedWriter(new FileWriter("results_preliminar_maligno_ImgNorm.cvs"));

            for (int m = m1; m < m2; m++) {
                bwM.write(";m=" + m);
                bwM.newLine();
                bwM.write("r");
                for (double r = r1; r < r2; r = r + rStep) {
                    bwM.write(";" + r);
                }

                bwM.newLine();
                for (File f : imgM) {
                    bwM.write(f.getName());

                    for (double r = r1; r < r2; r = r + rStep) {
                        ImageAccess img = new ImageAccess(new ImagePlus(f.getAbsolutePath()).getProcessor().convertToByte(true));
                        img.normalizeContrast();
                        double seValue = se.fastSampleEn2D(img, m, r);

                        bwM.write(";" + seValue);
                        System.out.println(f.getName() + ", m=" + m + ", r=" + r + ",  SE=" + seValue);
                    }
                    bwM.newLine();
                }
                bwM.newLine();
            }

            bwM.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void expEntropyWithNoNormalizedImages() {
        try {
            BufferedWriter bwB = new BufferedWriter(new FileWriter("results_preliminar_benigno.cvs"));

            File[] imgB = new File(dataPath + "Benigno/").listFiles();
            File[] imgM = new File(dataPath + "Maligno/").listFiles();
            Arrays.sort(imgB);
            Arrays.sort(imgM);

            for (int m = m1; m <= m2; m++) {
                bwB.write(";m=" + m);
                bwB.newLine();
                bwB.write("r");
                for (double r = r1; r < r2; r = r + rStep) {
                    bwB.write(";" + r);
                }

                bwB.newLine();
                for (File f : imgB) {
                    bwB.write(f.getName());

                    for (double r = r1; r < r2; r = r + rStep) {
                        ImageAccess img = new ImageAccess(new ImagePlus(f.getAbsolutePath()).getProcessor().convertToByte(true));
                        double seValue = se.fastSampleEn2D(img, m, r);

                        bwB.write(";" + seValue);
                        System.out.println(f.getName() + ", m=" + m + ", r=" + r + ",  SE=" + seValue);
                    }
                    bwB.newLine();
                }
                bwB.newLine();
            }

            bwB.close();

            BufferedWriter bwM = new BufferedWriter(new FileWriter("results_preliminar_maligno.cvs"));

            for (int m = m1; m < m2; m++) {
                bwM.write(";m=" + m);
                bwM.newLine();
                bwM.write("r");
                for (double r = r1; r < r2; r = r + rStep) {
                    bwM.write(";" + r);
                }

                bwM.newLine();
                for (File f : imgM) {
                    bwM.write(f.getName());

                    for (double r = r1; r < r2; r = r + rStep) {
                        ImageAccess img = new ImageAccess(new ImagePlus(f.getAbsolutePath()).getProcessor().convertToByte(true));
                        double seValue = se.fastSampleEn2D(img, m, r);

                        bwM.write(";" + seValue);
                        System.out.println(f.getName() + ", m=" + m + ", r=" + r + ",  SE=" + seValue);
                    }
                    bwM.newLine();
                }
                bwM.newLine();
            }

            bwM.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
