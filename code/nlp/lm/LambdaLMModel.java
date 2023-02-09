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
     *      * getBigramProb computes the probability of the second word given the first word with lambda smoothing
     * @param first
     * @param second
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


    public static void main(String[] args) {
//        String filenameMain = "/Users/ebenezersemere/Workspace/Natural Language Processing/Assignment2/data/sentences";
//        String filenameABC = "/Users/ebenezersemere/Workspace/Natural Language Processing/Assignment2/data/abc.txt";
        String filenameABC = "/Users/ezraford/Desktop/School/CS 159/Git/Assignment2/data/abc.txt";

        String filenameTrain = "/Users/ebenezersemere/Workspace/Natural Language Processing/Assignment2/data/sentencesTrain";

        double lambda = 1.0;

        LambdaLMModel model = new LambdaLMModel(filenameTrain, lambda);
    }
}
