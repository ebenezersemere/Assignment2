package nlp.lm;

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
        String filenameABC = "/Users/ezraford/Desktop/School/CS 159/Git/Assignment2/data/abc.txt";

        String filenameTrain = "/Users/ebenezersemere/Workspace/Natural Language Processing/Assignment2/data/sentencesTrain";

        double discount = 0.5;

        DiscountLMModel model = new DiscountLMModel(filenameABC, discount);
    }
}

