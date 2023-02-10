package nlp.lm;

import java.util.Arrays;
import java.util.List;

/**
 * A class for bigram language models with interpolation smoothing
 */
public class InterpolationLMModel extends LMBase {
    // declare lambda
    private final double lambda;

    public InterpolationLMModel(String filename, double lambda) {
        super(filename);
        this.lambda = lambda;
    }

    /**
     * getBigramProb computes the probability of the second word given the first word with interpolated smoothing
     * @param first the first word in the bigram
     * @param second the second word in the bigram
     * @return p(second | first)
     */
    @Override
    public double getBigramProb(String first, String second){

        // check whether first and second are in vocabulary
        if (!bigram.containsKey(first))
            first = "<unk>";

        if (!unigram.containsKey(second))
            second = "<unk>";

        // Get unigram probability
        double unigramProb = (double) unigram.get(second) / unigram.size();

        // Set default value for bigramProb to zero
        double bigramProb = 0.0;

        // Calculates bigram probability
        if (bigram.get(first).containsKey(second)) {
            bigramProb = (double) bigram.get(first).get(second) / bigram.get(first).size();
        }

        // return interpolated smoothing probability
        return lambda * unigramProb + (1 - lambda) * bigramProb;
    }

//        public static void main(String[] args) {
//        String train = "/Users/ezraford/Desktop/School/CS 159/Git/Assignment2/data/sentences_train";
//        String development = "/Users/ezraford/Desktop/School/CS 159/Git/Assignment2/data/sentences_development";
//        String test= "data/sentences_testing";
//
//        List<Double> lambdaList = Arrays.asList(1.0, 0.99, 0.9, 0.75, 0.5, 0.25, 0.1, .01, .001, .0001);
//
//        for (double lambda : lambdaList){
//            InterpolationLMModel model = new InterpolationLMModel(train, lambda);
//            double perplexity = model.getPerplexity(test);
//            System.out.println(perplexity);
//            System.out.println(lambda);
//        }
//    }
}
