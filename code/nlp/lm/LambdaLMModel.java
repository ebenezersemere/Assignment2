package nlp.lm;

/**
 * A class for bigram language models with lambda smoothing
 */
public class LambdaLMModel extends LMBase{
    // declare lambda
    private final double lambda;

    public LambdaLMModel(String filename, double lambda) {
        super(filename);
        this.lambda = lambda;
    }

    /**
     * getBigramProb computes the probability of the second word given the first word with lambda smoothing
     * @param first the first word in the bigram
     * @param second the second word in the bigram
     * @return p(second | first)
     */
    @Override
    public double getBigramProb(String first, String second) {
        // check whether first and second are in vocabulary
        if (!bigram.containsKey(first))
            first = "<unk>";

        if (!unigram.containsKey(second))
            second = "<unk>";

        if (bigram.get(first).containsKey(second)) {
            return (bigram.get(first).get(second) + lambda) / (((unigram.size() - 1) * lambda) + unigram.get(first));
        }

        return lambda / (((unigram.size() - 1) * lambda) + unigram.get(first));
    }


//    public static void main(String[] args) {
//        String train = "data/sentences_shuffle_train";
//        String development = "data/sentences_shuffle_development";
//        String test= "data/sentences_shuffle_test";
//
//        double lambda = .1;
//
//        for (int i = 0; i < 8; i++){
//            LambdaLMModel model = new LambdaLMModel(train, lambda);
//            double perplexity = model.getPerplexity(test);
//            System.out.println(perplexity);
//            System.out.println(lambda);
//            lambda /= 10;
//        }
//    }
}
