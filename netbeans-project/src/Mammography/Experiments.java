package Mammography;

import csim.misc.ImageAccess;
import csim.statistic.BasicStatistics;
import ij.ImagePlus;
import ij.io.FileSaver;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;

public class Experiments {

    private static int m1 = 1;
    private static int m2 = 6;
    private static double r1 = 0.06;
    private static double r2 = 0.51;
    private static double rStep = 0.02;

    public static void main(String[] args) {
        String exp = args[0];
        if (exp.equals("mix")) {
            expMIX();
        } else if (exp.equals("brodatz")) {
            expBrodatz();
        } else if (exp.equals("white")) {
            String img = args[1];
            expWhiteNoise(img);
        } else if (exp.equals("salt")) {
            String img = args[1];
            expSaltNoise(img);
        } else if (exp.equals("plot")) {
            String img = args[1];
            plot(img);
        } else if (exp.equals("mixImageSize")) {
            expMIXImageSize();
        } else if (exp.equals("valeria")) {
//            Arguments= sural30, sural720
            expEntropy(args[1]);
        } else {
            System.out.println("Please specify <brodatz>, <mix>, <white>, <salt> or <valeria> experiment");
            return;
        }

        //expLineCommand(args);
    }

    // This method is used to plot some examples of simulated images
    private static void plot(String path) {
        double p1 = 0.1;
        double p2 = 0.9;
        double pStep = 0.4;

        // Locale.US: ponto como separador decimal
        NumberFormat nf = NumberFormat.getInstance(Locale.US);
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);

        ImageAccess refImage = new ImageAccess(new ImagePlus(path).getProcessor().convertToByte(true));
        String savePath = "/home/luiz/Dropbox/Projeto/EntropiaImagens/results/examples-simulated/";

        for (double p = p1; p <= p2; p = p + pStep) {
            String pAux = nf.format(p);
            p = Double.parseDouble(pAux);

            ImageAccess imgMix = mix(256, 256, p);
            ImageAccess imgWN = whiteNoise(refImage, p);
            ImageAccess imgSN = saltNoise(refImage, p);

            //imgMix.show("MIX("+pAux+")");
            //imgWN.show("WhiteNoise("+pAux+")");
            //imgSN.show("SaltPepperNoise("+pAux+")");
            new FileSaver(new ImagePlus("", imgMix.createFloatProcessor())).saveAsPng(savePath + "MIX(" + pAux + ").png");
            new FileSaver(new ImagePlus("", imgWN.createByteProcessor())).saveAsPng(savePath + "WhiteNoise(" + pAux + ").png");
            new FileSaver(new ImagePlus("", imgSN.createByteProcessor())).saveAsPng(savePath + "SaltPepperNoise(" + pAux + ").png");
        }
    }

    private static void expLineCommand(String[] args) {
        String path = args[0];//"/home/luiz/Dropbox/Documentos/RobertoRioPreto/laminas-roberto/R0-gray.jpg";
        //String path = "/home/luiz/Dropbox/Projeto/EntropiaImagens/ImgSampEn/imgs/blobs.gif";
        int m = Integer.parseInt(args[1]);
        double r = Double.parseDouble(args[2]);

        ImageAccess img = new ImageAccess(new ImagePlus(path).getProcessor().convertToByte(true));
        img.show(path);

        double se = fastSampleEn2D(img, m, r);
        System.out.println("SE: " + se);
    }

    private static void expBrodatz() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("results_brodatz_128.cvs"));

            //File[] file = new File("./imgs/Brodatz/").listFiles();
            File[] file = new File("./imgs/Brodatz_128/").listFiles();
            Arrays.sort(file);

            for (int m = m1; m < m2; m++) {
                bw.write(";m=" + m);
                bw.newLine();
                bw.write("r");
                for (double r = r1; r < r2; r = r + rStep) {
                    bw.write(";" + r);
                }

                bw.newLine();
                for (File f : file) {
                    bw.write(f.getName());

                    for (double r = r1; r < r2; r = r + rStep) {
                        ImageAccess img = new ImageAccess(new ImagePlus(f.getAbsolutePath()).getProcessor().convertToByte(true));
                        double se = fastSampleEn2D(img, m, r);

                        bw.write(";" + se);
                        System.out.println(f.getName() + ", m=" + m + ", r=" + r + ",  SE=" + se);
                    }
                    bw.newLine();
                }
                bw.newLine();
            }

            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void expMIX() {
        double p1 = 0.0;
        double p2 = 1.0;
        double pStep = 0.2;

        // Locale.US: ponto como separador decimal
        NumberFormat nf = NumberFormat.getInstance(Locale.US);
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("results_mix.cvs"));

            for (int m = m1; m < m2; m++) {
                bw.write(";m=" + m);
                bw.newLine();
                bw.write("r");
                for (double r = r1; r < r2; r = r + rStep) {
                    bw.write(";" + r);
                }

                bw.newLine();
                for (double p = p1; p < p2; p = p + pStep) {
                    String pAux = nf.format(p);
                    p = Double.parseDouble(pAux);

                    bw.write("MIX(" + pAux + ")");

                    for (double r = r1; r < r2; r = r + rStep) {
                        ImageAccess img = mix(256, 256, p);
                        double se = fastSampleEn2D(img, m, r);

                        bw.write(";" + se);
                        System.out.println("MIX(" + pAux + "), m=" + m + ", r=" + r + ",  SE=" + se);
                    }
                    bw.newLine();
                }
                bw.newLine();
            }

            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void expMIXImageSize() {
//        double p1 = 0.0;
//        double p2 = 1.0;
//        double pStep = 0.2;
        int size1 = 100;
        int size2 = 600;
        int imgSize = 0;
        double p = 0.3;

        // Locale.US: ponto como separador decimal
        NumberFormat nf = NumberFormat.getInstance(Locale.US);
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("results_mix.cvs"));

            for (int m = m1; m < m2; m++) {
                bw.write(";m=" + m);
                bw.newLine();
                bw.write("r");
                for (double r = r1; r < r2; r = r + rStep) {
                    bw.write(";" + r);
                }

                bw.newLine();
                for (int size = size1; size <= size2; size += 100) {
//                    String pAux = nf.format(size);

                    bw.write("MIX(" + imgSize + ")");
                    System.out.println("m=" + m + " imgSize=" + size);

                    for (double r = r1; r < r2; r = r + rStep) {
                        ImageAccess img = mix(size, size, p);
                        long start = System.currentTimeMillis();
                        double se = fastSampleEn2D(img, m, r);
                        long end = System.currentTimeMillis();
                        System.out.println("m=" + m + " - r=" + r + " - time=" + (end - start));

                        bw.write(";" + se);
                        //System.out.println("MIX(" + imgSize + "), m=" + m + ", r=" + r + ",  SE=" + se);
                    }
                    bw.newLine();
                }
                bw.newLine();
            }

            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//================================CODIGOS BIZU===============================
    private static void expEntropy(String args) {
//       Fazer experimento: 
//       Fixar m=2 e r = 0.1 (rever no .tex do artigo)
//       Fazer medida de entropia em cada imagem na pasta Valeria (30 e 720, com d, p)
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("results_valeria_" + args + ".cvs"));

            File[] filesD = new File("./imgs/Valeria/" + args + "/d/").listFiles();
            File[] filesP = new File("./imgs/Valeria/" + args + "/p/").listFiles();
            Arrays.sort(filesD);
            Arrays.sort(filesP);

            bw.newLine();
            bw.write(args + ";Distal");
            System.out.println("Images=" + args + " - D");
            bw.newLine();
            for (File f : filesD) {
                bw.write(f.getName());

                ImageAccess img = new ImageAccess(new ImagePlus(f.getAbsolutePath()).getProcessor().convertToByte(true));
                double se = fastSampleEn2D(img, 2, 0.1);

                bw.write(";" + se);
                System.out.println(f.getName() + ",  SE=" + se);
                bw.newLine();
            }
            
            bw.newLine();
            bw.write(args + ";Proximal");
            System.out.println("Images=" + args + " - P");
            bw.newLine();
            for (File f : filesP) {
                bw.write(f.getName());
                ImageAccess img = new ImageAccess(new ImagePlus(f.getAbsolutePath()).getProcessor().convertToByte(true));
                double se = fastSampleEn2D(img, 2, 0.1);

                bw.write(";" + se);
                System.out.println(f.getName() + ",  SE=" + se);
                bw.newLine();
            }
            bw.newLine();

            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void expWhiteNoise(String args) {
        double p1 = 0.0;
        double p2 = 1.0;
        double pStep = 0.05;
        String path = args;
        ImageAccess image = new ImageAccess(new ImagePlus(path).getProcessor().convertToByte(true));

        // Locale.US: ponto como separador decimal
        NumberFormat nf = NumberFormat.getInstance(Locale.US);
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("results_WhiteNoise.cvs"));

            for (int m = m1; m < m2; m++) {
                bw.write(";m=" + m);
                bw.newLine();
                bw.write("r");
                for (double r = r1; r < r2; r = r + rStep) {
                    bw.write(";" + r);
                }

                bw.newLine();
                for (double p = p1; p < p2; p = p + pStep) {
                    String pAux = nf.format(p);
                    p = Double.parseDouble(pAux);

                    bw.write("WN(" + pAux + ")");

                    for (double r = r1; r < r2; r = r + rStep) {
                        ImageAccess img = whiteNoise(image, p);
                        double se = fastSampleEn2D(img, m, r);

                        bw.write(";" + se);
                        System.out.println("WN(" + pAux + "), m=" + m + ", r=" + r + ",  SE=" + se);
                    }
                    bw.newLine();
                }
                bw.newLine();
            }

            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void expSaltNoise(String args) {
        double p1 = 0.0;
        double p2 = 1.0;
        double pStep = 0.05;
        String path = args;
        ImageAccess image = new ImageAccess(new ImagePlus(path).getProcessor().convertToByte(true));

        // Locale.US: ponto como separador decimal
        NumberFormat nf = NumberFormat.getInstance(Locale.US);
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("results_SaltNoise.cvs"));

            for (int m = m1; m < m2; m++) {
                bw.write(";m=" + m);
                bw.newLine();
                bw.write("r");
                for (double r = r1; r < r2; r = r + rStep) {
                    bw.write(";" + r);
                }

                bw.newLine();
                for (double p = p1; p < p2; p = p + pStep) {
                    String pAux = nf.format(p);
                    p = Double.parseDouble(pAux);

                    bw.write("SN(" + pAux + ")");

                    for (double r = r1; r < r2; r = r + rStep) {
                        ImageAccess img = saltNoise(image, p);
                        double se = fastSampleEn2D(img, m, r);

                        bw.write(";" + se);
                        System.out.println("SN(" + pAux + "), m=" + m + ", r=" + r + ",  SE=" + se);
                    }
                    bw.newLine();
                }
                bw.newLine();
            }

            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    Cria a imagem escolhida com diferentes intensidade de ruido branco uniforme. A intensidade do ruído é definida
//   a partir do valor de probabilidade p.
    private static ImageAccess whiteNoise(ImageAccess image, double p) {
        ImageAccess out = image.duplicate();
        out.normalizeContrast();
        int nx = image.getWidth();
        int ny = image.getHeight();
        double num = 255.0 * p;

        for (int i = 0; i < nx; i++) {
            for (int j = 0; j < ny; j++) {
                out.putPixel(i, j, out.getPixel(i, j) + (Math.random() - 0.5) * num);
            }
        }
        out.normalizeContrast();
        return out;
    }

    //    Cria a imagem escolhida com diferentes intensidades de ruido salt and pepper. A intensidade do ruído é definida
//   a partir da quantidade de pixeis sorteados com probabilidade p.
    private static ImageAccess saltNoise(ImageAccess image, double p) {
        ImageAccess out = image.duplicate();
        out.normalizeContrast();
        int nx = image.getWidth();
        int ny = image.getHeight();
        //int pixels = (nx * ny) * (int) p;
        int pixels = (int) ((nx * ny) * p);
        //System.out.println("pixels = "+pixels);

        for (int i = 0; i < pixels; i++) {
            out.putPixel((int) (nx * Math.random()), (int) (ny * Math.random()), Math.random() > 0.5 ? 255 : 0);
        }

        return out;
    }

//==============================================================================
    private static double fastSampleEn2D(ImageAccess image, int m, double r) {
        long startTime = System.currentTimeMillis();
        double tol = r * BasicStatistics.std(image);
        //System.out.println("Image STDEV: "+BasicStatistics.std(image));

        int nx = image.getWidth();
        int ny = image.getHeight();

        int A = 0; // Matches for (m+1)-length patterns
        int B = 0; // Matches for m-length patterns

        int Cim, Cim1;
        double Cm = 0.0;
        double Cm1 = 0.0;
        // Total number of patterns (for both m and m+1)
        double den = (nx - m) * (ny - m);

        for (int yi = 0; yi < ny - m; yi++) {
            for (int xi = 0; xi < nx - m; xi++) {
//            if(new Double(image.getPixel(xi, yi)).equals(Double.NaN))
//                System.out.println("pixel("+xi+","+yi+") = NaN");
//            System.out.println("("+xi+","+yi+"), B = "+B);

                // Counters of similar patterns for m and m+1
                Cim = Cim1 = 0;

                // Acabar a linha atual
                int yj = yi;
                int xj = xi + 1;
                while (xj < nx - m) {
                    if (similar(image, xi, yi, xj, yj, m, tol)) {  // Similar for M?
                        B++;
                        Cim++;

                        // Are they still similar for the next point?
                        //if(similar(image,xi,yi,xj,yj,m+1,tol))  // Similar for M?
                        if (similarNext(image, xi, yi, xj, yj, m, tol)) { // Similar for M?
                            A++;
                            Cim1++;
                        }
                    }
                    xj++;
                }

                // Proximas linhas
                for (yj = yi + 1; yj < ny - m; yj++) {
                    for (xj = 0; xj < nx - m; xj++) {
                        if (similar(image, xi, yi, xj, yj, m, tol)) {  // Similar for M?
                            B++;
                            Cim++;

                            // Are they still similar for the next point?
                            //if(similar(image,xi,yi,xj,yj,m+1,tol))  // Similar for M?
                            if (similarNext(image, xi, yi, xj, yj, m, tol)) { // Similar for M?
                                A++;
                                Cim1++;
                            }
                        }
                    }
                }

                Cm += Cim / (den - 1);
                Cm1 += Cim1 / (den - 1);
            }
        }
        Cm /= den;
        Cm1 /= den;

        //System.out.println("A="+A+", B="+B);
        //System.out.println("SE: "+(- Math.log( ((double)A)/((double)B) )));
        //System.out.println("\nCm="+Cm+", Cm1="+Cm1);
        //System.out.println("SE: "+(- Math.log( ((double)Cm1)/((double)Cm) )));
        double se = -Math.log(((double) Cm1) / ((double) Cm));

        long finishTime = System.currentTimeMillis();
        //System.out.println("Execution time: "+((finishTime - startTime)/1000.0));

        return se;
    }

    private static double slowSampleEn2D(ImageAccess image, int m, double r) {
        double tol = r * BasicStatistics.std(image);
        int nx = image.getWidth();
        int ny = image.getHeight();

        int A = 0; // Matches for (m+1)-length patterns
        int B = 0; // Matches for m-length patterns

        // Varre a imagem inteira para cada padrao iniciado em (xi,yi)
        for (int yi = 0; yi < ny - m; yi++) {
            for (int xi = 0; xi < nx - m; xi++) {

                for (int yj = 0; yj < ny - m; yj++) {
                    for (int xj = 0; xj < nx - m; xj++) {
                        if ((xi == xj) && (yi == yj)) {
                            continue;  // Avoid self matches
                        }
                        // Comparar tanto para M como M+1
                        if (similar(image, xi, yi, xj, yj, m, tol)) {  // Similar for M?
                            B++;

                            // Are they still similar for the next point?
                            if (similar(image, xi, yi, xj, yj, m + 1, tol)) // Similar for M?
                            //if(similarNext(image,xi,yi,xj,yj,m,tol))  // Similar for M?
                            {
                                A++;
                            }
                        }
                    }
                }
            }
        }

        // There are (nx-m)(ny-m) patterns
        int N = (nx - m) * (ny - m);
        double denom = (N - 1) * N / 2.0;
        //double se = Math.log(B/denom) - Math.log(A/denom);
        double se = -Math.log(((double) A) / ((double) B));

        System.out.println("A=" + A + ", B=");
        return se;
    }

    // Calculates SampEn for m=1 up to mMax
//    private static double[] sampleEn2D(ImageAccess image, int mMax, double r) {
//        mMax++;
//                
//        int nx = image.getWidth();
//        int ny = image.getHeight();
//        int N = nx*ny;
//        int[] run = new int[N];
//        int[] lastrun = new int[N];
//        int[] A = new int[mMax];
//        int[] B = new int[mMax];
//
//        
//        /* start running */
//        for (int y=0; y < ny-1; y++)
//        for (int x=0; x < nx-1; x++) {
//            int runLength = N - y*nx+ x - 1;
//            double pixel = image.getPixel(x, y);
//
//            for (int runIndex=0; runIndex < runLength; runIndex++) {
//                int dist=runIndex+1;
//                int x1 = (dist + x) % nx;
//                int y1 = (dist + x - x1)/nx + y;
//                
//                if (( (image.getPixel(x1, y1) - pixel) < r) && ((pixel - image.getPixel(x1, y1)) < r)) {
//                    // Ver o que fazer
//                    
//                    
//                    run[runIndex] = lastrun[runIndex] + 1;
//                    int M1 = mMax < run[runIndex] ? mMax : run[runIndex];  // M1 = min(mMax,run[jj])
//                    //System.out.println("SIM. run["+jj+"]="+run[jj]+", M1="+M1);
//                    
//                    for (int mm = 0; mm < M1; mm++) {
//                        A[mm]++;
//                        //System.out.println("A["+mm+"]="+A[mm]);
//                        if (j < n - 1) {
//                            B[mm]++;
//                            //System.out.println("B["+mm+"]="+B[mm]);
//                        }
//                    }
//                } else {
//                    run[runIndex] = 0;
//                    //System.out.println("NAO. run["+jj+"]="+run[jj]);
//                }
//
//            }
//
//            // Update lastrun
//            for (int j = 0; j < nj; j++)
//                lastrun[j] = run[j];
//        }
//
//
//        for (int mm = 1; mm < mMax; mm++) {
//            // A and B are matches of forward counts.
//            // So, correct values of A^m and B^m must be multiplied by 2 and divided by (N-m)*(N-m-1)
//            double denom = (n-mm-1)*(n-mm)/2.0;
//            corr[0][mm] = (double) B[mm - 1] / denom;
//            corr[1][mm] = (double) A[mm] / denom; 
//        }
//
//        return corr;
//    }
    // Compares if m-length patterns are similar with tolerance r
    // First patter start at (x1,y1) and second at (x2,y2)
    private static boolean similar(ImageAccess image, int x1, int y1, int x2, int y2, int m, double r) {
        for (int y = 0; y < m; y++) {
            for (int x = 0; x < m; x++) {
                double diff = Math.abs(image.getPixel(x1 + x, y1 + y) - image.getPixel(x2 + x, y2 + y));
                if (diff >= r) {
                    return false;
                }
            }
        }
        return true;
    }

    // Compares only if collum and row m+1 are similar with tolerance r
    // First patter start at (x1,y1) and second at (x2,y2)
    private static boolean similarNext(ImageAccess image, int x1, int y1, int x2, int y2, int m, double r) {
        double diff;
        for (int y = 0; y <= m; y++) {  // Compares collumn M
            diff = Math.abs(image.getPixel(x1 + m, y1 + y) - image.getPixel(x2 + m, y2 + y));
            if (diff >= r) {
                return false;
            }
        }

        for (int x = 0; x <= m; x++) {  // Compares row M
            diff = Math.abs(image.getPixel(x1 + x, y1 + m) - image.getPixel(x2 + x, y2 + m));
            if (diff >= r) {
                return false;
            }
        }

        return true;
    }

    //====================================================================
    private static ImageAccess mix(int nx, int ny, double p) {
        ImageAccess image = new ImageAccess(nx, ny);

        double rand;
        double sqrt3 = Math.sqrt(3);
        for (int i = 0; i < nx; i++) {
            for (int j = 0; j < ny; j++) {
                // Raffling a number
                rand = Math.random();

                if (rand < p) // Random
                {
                    image.putPixel(i, j, ((Math.random() - 0.5) * 2.0 * sqrt3));  // [-sqrt3, sqrt3] 
                } else // Deterministic
                {
                    image.putPixel(i, j, (Math.sin(2.0 * Math.PI * i / 12.0) + Math.sin(2.0 * Math.PI * j / 12.0)));
                }
            }
        }

        return image;
    }
}
