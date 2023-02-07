package nlp.lm;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class LambdaLMModel implements LMModel{
    private double lambda;
    private HashMap<String, Integer> unigram;
    private HashMap<String, HashMap<String, Integer>> bigram;

    public LambdaLMModel(String filename, double lambda){
        // initialization
        this.lambda = lambda;
        unigram = new HashMap<String, Integer>();
        bigram = new HashMap<String, HashMap<String, Integer>>();

        // process file
        File file = new File(filename);
        ArrayList<String> main = new ArrayList<String>();

        // add <unk>, <s> and </s> to unigram, as they will not be found naturally from corpus
        unigram.put("<unk>", 0);
        unigram.put("<s>", 0);
        unigram.put("</s>", 1);

        // read file
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                // split line into words
                String[] line = scanner.nextLine().split("\\s");

                // add <s> to start of line & increment unigram count
                unigram.put("<s>", unigram.get("<s>") + 1);
                main.add("<s>");

                for (String word: line) {
                    if (!unigram.containsKey(word)) {
                        unigram.put("<unk>", unigram.get("<unk>") + 1);
                        unigram.put(word, 0);
                        main.add("<unk>");
                    } else {
                        unigram.put(word, unigram.get(word) + 1);
                        main.add(word);
                    }
                }

                main.add("</s>");

            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + file);
        }

        // create unigram & bigram
        for (int i = 0; i < main.size() - 1; i++) {
            String curToken = main.get(i);
            String nextToken = main.get(i + 1);

            if (curToken.equals("</s>")){
                unigram.put("</s>", unigram.get("</s>") + 1);
                continue;
            }

            if (!bigram.containsKey(curToken))
                bigram.put(curToken, new HashMap<String, Integer>());

            if (!bigram.get(curToken).containsKey(nextToken))
                bigram.get(curToken).put(nextToken, 0);

            Integer count = bigram.get(curToken).get(nextToken) + 1;
            bigram.get(curToken).put(nextToken, count);
        }

        unigram.entrySet().removeIf(entry -> entry.getValue().equals(0));
    }


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
     * getBigramProb(hello, world) -> return p(world|hello)
     * lambda / (unigram.counts.get(first).getValue() + unigramsCounts.size() * lambda)
     */
    public double getBigramProb(String first, String second){
        // we have first
        if (bigram.containsKey(first)){
            if (bigram.get(first).containsKey(second)){
                return (bigram.get(first).get(second) + lambda) / (((unigram.size() - 1) * lambda) + unigram.get(first));
            }
            return lambda / (((unigram.size() - 1) * lambda) + unigram.get(first));
        }
        // we don't have first
        // decrement 1 from unigram size, as we don't want to consider p(<s>|first)
        return lambda / ((unigram.size() - 1) * lambda);
    }

    public static void main(String[] args) {

//        String filename = "/Users/ebenezersemere/Workspace/Natural Language Processing/Assignment2/data/sentences";
        String filename = "/Users/ebenezersemere/Workspace/Natural Language Processing/Assignment2/data/abc.txt";
        double lambda = 1.0;

        LambdaLMModel model = new LambdaLMModel(filename, lambda);
        System.out.println(model.bigram);
        System.out.println(model.unigram);
    }
}
