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

        String filenameTrain = "/Users/ebenezersemere/Workspace/Natural Language Processing/Assignment2/data/sentences_train";
        String filenameDev = "/Users/ebenezersemere/Workspace/Natural Language Processing/Assignment2/data/sentences_development";
        String filenameTest = "/Users/ebenezersemere/Workspace/Natural Language Processing/Assignment2/data/sentences_testing";


        double lambda = .1;

        for (int i = 0; i < 8; i++){
            LambdaLMModel model = new LambdaLMModel(filenameTrain, lambda);
            double perplexity = model.getPerplexity(filenameTest);
            System.out.println(perplexity);
            System.out.println(lambda);
            lambda /= 10;
        }

//        // Development
//        837.5045562172319
//        0.1
//        481.5731773218983
//        0.01
//        425.7744190175921
//        0.001
//        520.4360899049848
//        1.0E-4
//        745.7024733316655
//        1.0E-5
//        1120.0851147061799
//        1.0000000000000002E-6
//        1695.2085289590827
//        1.0000000000000002E-7
//        2567.7680339278627
//        1.0000000000000002E-8

//        // Testing
//        809.2941685685007
//        0.1
//        464.8076416605193
//        0.01
//        410.7734991977494
//        0.001
//        500.77403478931325
//        1.0E-4
//        713.8558210497071
//        1.0E-5
//        1065.475759362419
//        1.0000000000000002E-6
//        1601.989195913179
//        1.0000000000000002E-7
//        2410.5953397969947
//        1.0000000000000002E-8

    }
}
