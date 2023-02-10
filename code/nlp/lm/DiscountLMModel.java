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
            return (bigram.get(first).get(second) - discount) / unigram.get(first);
        else
            return alpha * (unigram.get(second) / (double) uniSum);
    }

//    public static void main(String[] args) {
//        String train = "data/sentences_shuffle_train";
//        String development = "data/sentences_shuffle_development";
//        String test= "data/sentences_shuffle_test";
//
//
//        List<Double> discount = Arrays.asList(0.99, 0.9, 0.75, 0.5, 0.25, 0.1, .01, .001, .0001);
//
//        for (double disc : discount){
//            LambdaLMModel model = new LambdaLMModel(train, disc);
//            double perplexity = model.getPerplexity(development);
//            System.out.println(perplexity);
//            System.out.println(disc);
//        }
//    }
}

