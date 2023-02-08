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
     *
     * @param first
     * @param second
     * @return the probability of the second word given the first word (with lambda smoothing)
     */
    @Override
    public double getBigramProb(String first, String second) {
        // we have first
        if (bigram.containsKey(first)) {
            if (bigram.get(first).containsKey(second)) {
                return (bigram.get(first).get(second) + lambda) / (((unigram.size() - 1) * lambda) + unigram.get(first));
            }
            return lambda / (((unigram.size() - 1) * lambda) + unigram.get(first));
        } else {
            return lambda / ((unigram.size() - 1) * lambda) + bigram.get("<unk>").get(second);
        }
    }

    public static void main(String[] args) {
        String filenameMain = "/Users/ebenezersemere/Workspace/Natural Language Processing/Assignment2/data/sentences";
        String filenameABC = "/Users/ebenezersemere/Workspace/Natural Language Processing/Assignment2/data/abc.txt";

        double lambda = 1.0;

        LambdaLMModel model = new LambdaLMModel(filenameABC, lambda);

        System.out.println(model.getBigramProb("a", "b"));
    }
}
