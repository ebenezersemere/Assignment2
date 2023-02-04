package nlp.lm;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class LambdaLMModel implements LMModel{
    private double lambda;
    private ArrayList<String> tokens;

    public LambdaLMModel(String filename, double lambda){
        this.lambda = lambda;

        File file = new File(filename);
        ArrayList<String> lines = new ArrayList<String>();

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String[] line = ("<s> " + scanner.nextLine() + " </s>").split(" ");
                lines.addAll(Arrays.asList(line));
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + file);
        }

        this.tokens = lines;
    }

    public double logProb(ArrayList<String> sentWords){
        return 0.0;
    }

    public double getPerplexity(String filename){
        return 0.0;
    }

    public double getBigramProb(String first, String second){
        return 0.0;
    }

    public static void main(String[] args) {

        String filename = "/Users/ebenezersemere/Workspace/Natural Language Processing/Assignment2/data/sentences";
        double lambda = 0.5;

        LambdaLMModel model = new LambdaLMModel(filename, lambda);
        System.out.println(model.tokens);

    }

}
