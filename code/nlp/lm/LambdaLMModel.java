package nlp.lm;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class LambdaLMModel implements LMModel{
    private double lambda;
    private HashMap<String, HashMap<String, Double>> bigram;

    private HashMap<String, HashMap<String, Double>> bigramCounts;

    /**
     * Trains a language model on the given file and lambda value.
     * We want the contents of the file to be stored in a main list, where sentences are separated by <s> and </s>.
     * We also want to replace the first occurrence of each word with <unk>.
     */

    public LambdaLMModel(String filename, double lambda){
        this.lambda = lambda;

        // process file
        File file = new File(filename);
        ArrayList<String> main = new ArrayList<String>();

        HashMap<String, Integer> wordCounts = new HashMap<String, Integer>();
        wordCounts.put("<unk>", 0);
        wordCounts.put("<s>", 0);

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String[] line = scanner.nextLine().split("\\s");

                wordCounts.put("<s>", wordCounts.get("<s>") + 1);
                main.add("<s>");

                for (String word: line) {
                    if (!wordCounts.containsKey(word)) {
                        wordCounts.put("<unk>", wordCounts.get("<unk>") + 1);
                        wordCounts.put(word, 0);
                        main.add("<unk>");
                    } else {
                        wordCounts.put(word, wordCounts.get(word) + 1);
                        main.add(word);
                    }
                }

                main.add("</s>");

            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + file);
        }

        // create bigram
        HashMap<String, HashMap<String, Double>> bigram = new HashMap<String, HashMap<String, Double>>();

        for (int i = 0; i < main.size() - 1; i++) {
            String curToken = main.get(i);
            String nextToken = main.get(i + 1);

            if (curToken.equals("</s>")){
                continue;
            }

            if (!bigram.containsKey(curToken))
                bigram.put(curToken, new HashMap<String, Double>());

            if (!bigram.get(curToken).containsKey(nextToken))
                bigram.get(curToken).put(nextToken, 0.0);

            double count = bigram.get(curToken).get(nextToken) + 1.0;
            bigram.get(curToken).put(nextToken, count);
        }

        // create a deep copy of bigram
        bigramCounts = new HashMap<String, HashMap<String, Double>>();
        for (String outerKey: bigram.keySet()){
            bigramCounts.put(outerKey, new HashMap<String, Double>());
            for (String innerKey: bigram.get(outerKey).keySet()){
                bigramCounts.get(outerKey).put(innerKey, bigram.get(outerKey).get(innerKey));
            }
        }

        // normalize bigram probabilities with lambda smoothing
        for (String outerKey: bigram.keySet()){
            double denominator = wordCounts.get(outerKey);
            for (String innerKey: bigram.get(outerKey).keySet()){
                double numerator = bigram.get(outerKey).get(innerKey);
                bigram.get(outerKey).put(innerKey, (numerator + lambda) / (denominator + (lambda * bigram.get(outerKey).size())));
            }
        }

        this.bigram = bigram;
    }

    /**
     * We utilize the hashtable to find the bigram value of each pair of words in our model. We simply return the
     * product of the probabilities at every bigram.
     */
    public double logProb(ArrayList<String> sentWords){
        return 0.0;
    }

    public double getPerplexity(String filename){
        return 0.0;
    }

    /**
     * We utilize a hashtable of hashtables, where the keys of the master hashtable are all the words of the corpus,
     * and the values are all hashtables, whose keys are all words that follow the master key, and whose values are
     * the bigram value of the two words.
     *
     * For example, {hello: {sunshine: .01}, {neighbor: .1}, {world: .89}}
     *
     * So, given a first and second word, we access the master hashtable at the first word, and return the
     * value at the key of the second word.
     *
     * getBigramProb(hello, world) -> return p(world|hello) -> return words[hello][world]
     */
    public double getBigramProb(String first, String second){
        try {
            return bigram.get(first).get(second);
        } catch (NullPointerException e) {
            /**
             * Add new value to bigram at the first's key
             * first = old key
             * second = new val
             *  1/3 a 2/3 b
             * word counts ++
             *
             * new val
             *
             * old vals
             */

            bigram.put(first, new HashMap<String, Double>());


//            bigram.get(first).put(second, 0.0);
//
//            for (String key : bigram.get(first).keySet()){
//                bigram.get(first).put(key, (double) (wordCounts.get(key)) / (wordCounts.get(first)));
//            }
//            return 0.0;
        }
        return 0.0;
    }

    public static void main(String[] args) {

//        String filename = "/Users/ebenezersemere/Workspace/Natural Language Processing/Assignment2/data/sentences";
        String filename = "/Users/ebenezersemere/Workspace/Natural Language Processing/Assignment2/data/abc.txt";
        double lambda = 0.0;

        LambdaLMModel model = new LambdaLMModel(filename, lambda);
        System.out.println(model.getBigramProb("b", "a"));
        System.out.println(model.bigram);
        System.out.println(model.bigramCounts);
    }

}
