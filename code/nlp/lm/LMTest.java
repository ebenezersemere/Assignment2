package nlp.lm;

import com.sun.tools.javac.Main;

import java.util.ArrayList;

public class LMTest extends LMBase{
    private double discount;

    // constructor
    public LMTest(String filename, double discount){
        super(filename);
        this.discount = discount;
    }

    @Override
    public double getBigramProb(String first, String second) {
        return 0;
    }

    // main mehtod
    public static void main(String[] args) {
        LMTest lm = new LMTest("/Users/ebenezersemere/Workspace/Natural Language Processing/Assignment2/data/abc.txt", 0.75);

        System.out.println(lm.unigram);
        System.out.println(lm.discount);
    }
}
