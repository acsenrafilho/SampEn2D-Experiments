package Mammography;

import classes.ImageAccess;
import ij.ImagePlus;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import classes.SampEn2D;
import ij.io.FileSaver;
import ij.process.ImageProcessor;

public class Experiments {

    private static String dataPath = "/home/antonio/Dropbox/Artigos_Conferencias/Papers/Cancer-mama-SampEn2D/Data/";
    static classes.SampEn2D se = new SampEn2D();

    private static int m1 = 1;
    private static int m2 = 2;
    private static double r1 = 0.06;
    private static double r2 = 0.52;
    private static double rStep = 0.02;

//================================CODIGOS BIZU===============================
    public static void expEntropy(boolean normOption) {
//       Fazer experimento: 
//       Fazer medida de entropia em cada imagem nas pastas Benigno e Maligno
        if (normOption) {
            System.out.println("SampEn2D-Experiment Set: <preliminar> experiment - Normalize Images: " + normOption);
            expEntropyWithNormalizedImages();
        } else {
            System.out.println("SampEn2D-Experiment Set: <preliminar> experiment - Normalize Images: " + normOption);
            expEntropyWithNoNormalizedImages();
        }

    }

    public static void splitImageData() {
        String allDataPath = dataPath + "ROIS_mama/";
        String BFolder = dataPath + "Benigno/";
        String MFolder = dataPath + "Maligno/";
        File[] allFiles = new File(allDataPath).listFiles();

        for (int img = 0; img < allFiles.length; img++) {
            if (allFiles[img].toString().endsWith("B.bmp")) {
                allFiles[img].renameTo(new File(BFolder + allFiles[img].getName()));
            } else if (allFiles[img].toString().endsWith("M.bmp")) {
                allFiles[img].renameTo(new File(MFolder + allFiles[img].getName()));
            }
        }

    }

    public static void cleanBackgroundImageData() {
//        String allDataPath = dataPath;
        String BFolder = dataPath + "Benigno/";
        String MFolder = dataPath + "Maligno/";
        File[] BFiles = new File(BFolder).listFiles();
        File[] MFiles = new File(MFolder).listFiles();
        ImageProcessor ip;
        ImagePlus imgAux;

        for (int img = 0; img < BFiles.length; img++) {
            ip = new ImagePlus(BFiles[img].getAbsolutePath()).getProcessor();
            imgAux = doThreshold(ip.convertToFloat());
            new FileSaver(imgAux).saveAsPng(dataPath + "/no-background/Benigno/" + BFiles[img].getName().replace("bmp", "png"));
        }

        for (int img = 0; img < MFiles.length; img++) {
            ip = new ImagePlus(MFiles[img].getAbsolutePath()).getProcessor();
            imgAux = doThreshold(ip.convertToFloat());
            new FileSaver(imgAux).saveAsPng(dataPath + "/no-background/Maligno/" + MFiles[img].getName().replace("bmp", "png"));
        }

    }

    private static ImagePlus doThreshold(ImageProcessor ip) {
        ImagePlus read = new ImagePlus("", ip);
        ImageAccess img = new ImageAccess(read.getProcessor());
        int nx = ip.getWidth();
        int ny = ip.getHeight();
        img = doMaksOnImage(img);

        return new ImagePlus("", img.createFloatProcessor());
    }

    private static ImageAccess doMaksOnImage(ImageAccess input) {
        int nx = input.getWidth();
        int ny = input.getHeight();
        int limiar = 0;
        ImageAccess output = input.duplicate();

        limiar = threshold_otsu(input.createByteProcessor().getHistogram());

        for (int i = 0; i < nx; i++) {
            for (int j = 0; j < ny; j++) {
                if (input.getPixel(i, j) < limiar) {
                    output.putPixel(i, j, 0);
                } else {
                    output.putPixel(i, j, input.getPixel(i, j));
                }
            }
        }

        return output;
    }

    private static int threshold_otsu(int[] histogram) {
        // Otsu's threshold algorithm
        // C++ code by Jordan Bevik <Jordan.Bevic@qtiworld.com>
        // ported to ImageJ plugin by G.Landini
        int k, kStar;  // k = the current threshold; kStar = optimal threshold
        double N1, N;    // N1 = # points with intensity <=k; N = total number of points
        double BCV, BCVmax; // The current Between Class Variance and maximum BCV
        double num, denom;  // temporary bookeeping
        double Sk;  // The total intensity for all histogram points <=k
        double S, L = 256; // The total intensity of the image

        // Initialize values:
        S = N = 0;
        for (k = 0; k < L; k++) {
            S += (double) k * histogram[k];   // Total histogram intensity
            N += histogram[k];       // Total number of data points
        }

        Sk = 0;
        N1 = histogram[0]; // The entry for zero intensity
        BCV = 0;
        BCVmax = 0;
        kStar = 0;

        // Look at each possible threshold value,
        // calculate the between-class variance, and decide if it's a max
        for (k = 1; k < L - 1; k++) { // No need to check endpoints k = 0 or k = L-1
            Sk += (double) k * histogram[k];
            N1 += histogram[k];

            // The float casting here is to avoid compiler warning about loss of precision and
            // will prevent overflow in the case of large saturated images
            denom = (double) (N1) * (N - N1); // Maximum value of denom is (N^2)/4 =  approx. 3E10

            if (denom != 0) {
                // Float here is to avoid loss of precision when dividing
                num = ((double) N1 / N) * S - Sk;  // Maximum value of num =  255*N = approx 8E7
                BCV = (num * num) / denom;
            } else {
                BCV = 0;
            }

            if (BCV >= BCVmax) { // Assign the best threshold found so far
                BCVmax = BCV;
                kStar = k;
            }
        }
        // kStar += 1;  // Use QTI convention that intensity -> 1 if intensity >= k
        // (the algorithm was developed for I-> 1 if I <= k.)
        return kStar;
    }

//    TODO: Terminar experimento. Rever todo o codigo deste metodo
    public static void sampEn2DTable() throws IOException {
        ImageAccess imageAux;
        BufferedWriter bwB = new BufferedWriter(new FileWriter("sampEn2D_Mamography_Data.csv"));

        File[] imgB = new File(dataPath + "Benigno/").listFiles();
        File[] imgM = new File(dataPath + "Maligno/").listFiles();
        Arrays.sort(imgB);
        Arrays.sort(imgM);

        bwB.write("image");
        for (int m = m1; m <= m2; m++) {
            for (double r = r1; r <= r2; r = r + rStep) {
                bwB.write(";m" + m + "r" + r);
            }
        }
        bwB.newLine();

        for (int imgBSelected = 0; imgBSelected < imgB.length; imgBSelected++) {
            System.out.println("Image B: #" + (imgBSelected + 1));
//            System.out.println("Image B: #" + (0 + 1));
            bwB.write(imgB[imgBSelected].getName());
//            bwB.write(imgB[0].getName());
            imageAux = new ImageAccess(new ImagePlus(imgB[imgBSelected].getAbsolutePath()).getProcessor().convertToByte(true));
//            imageAux = new ImageAccess(new ImagePlus(imgB[0].getAbsolutePath()).getProcessor().convertToByte(true));
            for (int m = m1; m <= m2; m++) {
                for (double r = r1; r <= r2; r += rStep) {
                    bwB.write(";" + se.fastSampleEn2D(imageAux, m, r));
                }
            }
            bwB.newLine();
        }

        for (int imgMSelected = 0; imgMSelected < imgM.length; imgMSelected++) {
            System.out.println("Image M: #" + (imgMSelected + 1));
//            System.out.println("Image M: #" + (0 + 1));
            bwB.write(imgM[imgMSelected].getName());
//            bwB.write(imgM[0].getName());
            imageAux = new ImageAccess(new ImagePlus(imgM[imgMSelected].getAbsolutePath()).getProcessor());
//            imageAux = new ImageAccess(new ImagePlus(imgM[0].getAbsolutePath()).getProcessor().convertToByte(true));
            for (int m = m1; m <= m2; m++) {
                for (double r = r1; r <= r2; r += rStep) {
                    bwB.write(";" + se.fastSampleEn2D(imageAux, m, r));
                }
            }
            bwB.newLine();
        }

        bwB.close();
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

    public static void sampEn2DTableNoBackground() throws IOException {
        ImageAccess imageAux;
        BufferedWriter bwB = new BufferedWriter(new FileWriter("sampEn2D_Mamography_Data_NoBackground.csv"));

        File[] imgB = new File(dataPath + "/no-background/Benigno/").listFiles();
        File[] imgM = new File(dataPath + "/no-background/Maligno/").listFiles();
        Arrays.sort(imgB);
        Arrays.sort(imgM);

        bwB.write("image");
        for (int m = m1; m <= m2; m++) {
            for (double r = r1; r <= r2; r = r + rStep) {
                bwB.write(";m" + m + "r" + r);
            }
        }
        bwB.newLine();

        for (int imgBSelected = 0; imgBSelected < imgB.length; imgBSelected++) {
            System.out.println("Image B: #" + (imgBSelected + 1));
//            System.out.println("Image B: #" + (0 + 1));
            bwB.write(imgB[imgBSelected].getName());
//            bwB.write(imgB[0].getName());
            imageAux = new ImageAccess(new ImagePlus(imgB[imgBSelected].getAbsolutePath()).getProcessor().convertToByte(true));
//            imageAux = new ImageAccess(new ImagePlus(imgB[0].getAbsolutePath()).getProcessor().convertToByte(true));
            for (int m = m1; m <= m2; m++) {
                for (double r = r1; r <= r2; r += rStep) {
                    bwB.write(";" + se.fastSampleEn2DNoBackground(imageAux, m, r));
                }
            }
            bwB.newLine();
        }

        for (int imgMSelected = 0; imgMSelected < imgM.length; imgMSelected++) {
            System.out.println("Image M: #" + (imgMSelected + 1));
//            System.out.println("Image M: #" + (0 + 1));
            bwB.write(imgM[imgMSelected].getName());
//            bwB.write(imgM[0].getName());
            imageAux = new ImageAccess(new ImagePlus(imgM[imgMSelected].getAbsolutePath()).getProcessor().convertToByte(true));
//            imageAux = new ImageAccess(new ImagePlus(imgM[0].getAbsolutePath()).getProcessor().convertToByte(true));
            for (int m = m1; m <= m2; m++) {
                for (double r = r1; r <= r2; r += rStep) {
                    bwB.write(";" + se.fastSampleEn2DNoBackground(imageAux, m, r));
                }
            }
            bwB.newLine();
        }

        bwB.close();
    }

    public static void sampEn2DWaveletsTable(String waveletType) throws IOException {
        ImageAccess imageAux;
        BufferedWriter bwB;
        switch (waveletType) {
            case "H":
                bwB = new BufferedWriter(new FileWriter("sampEn2D_Mamography_Haar.csv"));
                break;
            case "D":
                bwB = new BufferedWriter(new FileWriter("sampEn2D_Mamography_Dalbechies.csv"));
                break;
        }

        File[] imgB = new File(dataPath + "wavelets_coef_matricesBenigno/").listFiles();
        File[] imgM = new File(dataPath + "wavelets_coef_matricesBenigno/").listFiles();
        Arrays.sort(imgB);
        Arrays.sort(imgM);

        bwB.write("image");
        for (int m = m1; m <= m2; m++) {
            for (double r = r1; r <= r2; r = r + rStep) {
                bwB.write(";m" + m + "r" + r);
            }
        }
        bwB.newLine();

        for (int imgBSelected = 0; imgBSelected < imgB.length; imgBSelected++) {
            System.out.println("Image B: #" + (imgBSelected + 1));
//            System.out.println("Image B: #" + (0 + 1));
            bwB.write(imgB[imgBSelected].getName());
//            bwB.write(imgB[0].getName());
            imageAux = new ImageAccess(new ImagePlus(imgB[imgBSelected].getAbsolutePath()).getProcessor().convertToByte(true));
//            imageAux = new ImageAccess(new ImagePlus(imgB[0].getAbsolutePath()).getProcessor().convertToByte(true));
            for (int m = m1; m <= m2; m++) {
                for (double r = r1; r <= r2; r += rStep) {
                    bwB.write(";" + se.fastSampleEn2D(imageAux, m, r));
                }
            }
            bwB.newLine();
        }

        for (int imgMSelected = 0; imgMSelected < imgM.length; imgMSelected++) {
            System.out.println("Image M: #" + (imgMSelected + 1));
//            System.out.println("Image M: #" + (0 + 1));
            bwB.write(imgM[imgMSelected].getName());
//            bwB.write(imgM[0].getName());
            imageAux = new ImageAccess(new ImagePlus(imgM[imgMSelected].getAbsolutePath()).getProcessor());
//            imageAux = new ImageAccess(new ImagePlus(imgM[0].getAbsolutePath()).getProcessor().convertToByte(true));
            for (int m = m1; m <= m2; m++) {
                for (double r = r1; r <= r2; r += rStep) {
                    bwB.write(";" + se.fastSampleEn2D(imageAux, m, r));
                }
            }
            bwB.newLine();
        }

        bwB.close();
    }

}
