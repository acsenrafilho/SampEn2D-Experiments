
import java.io.IOException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author antonio
 */
public class SampEn2DMain {

    public static void main(String[] args) throws IOException {
        String exp = args[0];
        boolean normalizeOption = false;
//        if (args[1].equalsIgnoreCase("y")) {
//            normalizeOption = true;
//        } else if (args[1].equalsIgnoreCase("n")) {
//            normalizeOption = false;
//        } else {
//            System.out.println("(Y)es or (N)o for normalized option.");
//            return;
//        }

        if (exp.equals("preliminar")) {
            Mammography.Experiments.expEntropy(normalizeOption);
        } else if (exp.equals("split")) {
            Mammography.Experiments.splitImageData();
        }else if (exp.equals("noBackground")) {
            Mammography.Experiments.cleanBackgroundImageData();
        } else if (exp.equals("allEntropyData")) {
            Mammography.Experiments.sampEn2DTable();
        }else if (exp.equals("allEntropyDataNoBackground")) {
            Mammography.Experiments.sampEn2DTableNoBackground();
        } else {
            System.out.println("Please specify <preliminar>, <split>, <noBackground>, <allEntropyData> or <allEntropyDataNoBackground> method");
            return;
        }

    }
}
