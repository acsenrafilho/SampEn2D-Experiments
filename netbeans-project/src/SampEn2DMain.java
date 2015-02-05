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

    public static void main(String[] args) {
        String exp = args[0];
        if (exp.equals("preliminar")) {
//            Arguments= sural30, sural720
            Mammography.Experiments.expEntropy();
        } else {
            System.out.println("Please specify <preliminar> experiment");
            return;
        }

    }
}
