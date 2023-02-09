package nlp.lm;

import java.util.Arrays;
import java.util.List;

/**
 * A class for bigram language models with discount smoothing
 */
public class DiscountLMModel extends LMBase{
    // declare discount
    private final double discount;

    public DiscountLMModel(String filename, double discount) {
        super(filename);
        this.discount = discount;
    }

    /**
     * getBigramProb computes the probability of the second word given the first word with discount smoothing
     * @param first
     * @param second
     * @return p(second | first)
     */
    @Override
    public double getBigramProb(String first, String second){
        // calculate reserved mass
        double reservedMass = (bigram.get(first).size() * discount) / (unigram.get(first));

        // <3 I love streams :)

        // sum the values from unigram
        int uniSum = (unigram.keySet().stream()
                .mapToInt(key -> unigram.get(key))
                .sum());

        // 1 - (sum of unigram probabilities for values seen in first's bigram)
        double denominator = 1 - (bigram.get(first).keySet().stream()
                .mapToDouble(key -> (unigram.get(key) / (double) uniSum))
                .sum());

        // calculate alpha
        double alpha = reservedMass / denominator;

        // discount if we have the bigram, otherwise multiply alpha by p(second)
        if (bigram.get(first).containsKey(second))
            return bigram.get(first).get(second) - discount;
        else
            return alpha * (unigram.get(second) / (double) uniSum);
    }


    public static void main(String[] args) {
//        String filenameMain = "/Users/ebenezersemere/Workspace/Natural Language Processing/Assignment2/data/sentences";
//        String filenameABC = "/Users/ebenezersemere/Workspace/Natural Language Processing/Assignment2/data/abc.txt";
//
//        double discount = 0.5;
//
//        DiscountLMModel model = new DiscountLMModel(filenameABC, discount);

        String filenameTrain = "/Users/ebenezersemere/Workspace/Natural Language Processing/Assignment2/data/sentences_train";
        String filenameDev = "/Users/ebenezersemere/Workspace/Natural Language Processing/Assignment2/data/sentences_development";
        String filenameTest = "/Users/ebenezersemere/Workspace/Natural Language Processing/Assignment2/data/sentences_testing";

//        double discount = .1;

        List<Double> discount = Arrays.asList(0.99, 0.9, 0.75, 0.5, 0.25, 0.1);

        for (double disc : discount){
            LambdaLMModel model = new LambdaLMModel(filenameTrain, disc);
            double perplexity = model.getPerplexity(filenameTest);
            System.out.println(perplexity);
            System.out.println(disc);
        }

//        // Dev
//        2210.1003431764807
//        0.99
//        2106.3371327748073
//        0.9
//        1924.609132693029
//        0.75
//        1588.2300828933421
//        0.5
//        1176.5117951541981
//        0.25
//        837.5045562172319
//        0.1

//        // Test
//        2145.079988806118
//        0.99
//        2043.8582581124003
//        0.9
//        1866.658917449232
//        0.75
//        1538.9459009003788
//        0.5
//        1138.4251166815525
//        0.25
//        809.2941685685007
//        0.1

    }
}
