package nlp.lm;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class DiscountLMModel implements LMModel{
    private double discount;
    private HashMap<String, Integer> unigram;
    private HashMap<String, HashMap<String, Integer>> bigram;

    public DiscountLMModel(String filename, double discount) {
        // initialization
        this.discount = discount;
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

                for (String word : line) {
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
            if ((i + 1) % 4 == 0){
                continue;
            }

            String curToken = main.get(i);
            String nextToken = main.get(i + 1);

            if (curToken.equals("</s>")) {
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


    // preprocess sentwords
    public double logProb(ArrayList<String> sentWords) {
        sentWords.add(0, "<s>");
        sentWords.add("</s>");

        double log = 0.0;

        for (int i = 0; i < sentWords.size() - 1; i++) {
            log += Math.log10(getBigramProb(sentWords.get(i), sentWords.get(i + 1)));
        }

        return log;
    }

    public double getPerplexity(String filename) {
        File file = new File(filename);

        double probSum = 0.0;
        int bigramCount = 0;

        try (Scanner scanner = new Scanner(file)) {

            while (scanner.hasNextLine()) {
                String[] line = scanner.nextLine().split("\\s");
                ArrayList<String> sentence = new ArrayList<>(Arrays.asList(line));

                bigramCount += sentence.size() - 1;
                probSum += logProb(sentence);

            }

            double avg = probSum / bigramCount;
            return Math.pow(10, -1 * avg);

        } catch (FileNotFoundException e){
            System.out.println("File not found: " + filename);
            return 0.0;
        }
    }

    /**
     * We utilize a hashtable of hashtables, where the keys of the master hashtable are all the words of the corpus,
     * and the values are all hashtables, whose keys are all words that follow the master key, and whose values are
     * the bigram value of the two words.
     * getBigramProb(hello, world) -> return p(world|hello) p(hi| askjdhakjdhaskldhkas)
     * discount / (unigram.counts.get(first).getValue() + unigramsCounts.size() * discount)
     */
    @Override
    public double getBigramProb(String first, String second){
        double reservedMass = (bigram.get(first).size() * discount) / (unigram.get(first));

        // <3 I love streams :)
        int uniSum = (unigram.keySet().stream()
                .mapToInt(key -> unigram.get(key))
                .sum()) - unigram.get("<s>");

        double denominator = 1 - (bigram.get(first).keySet().stream()
                .mapToDouble(key -> (unigram.get(key) / (double) uniSum))
                .sum());

        double alpha = reservedMass / denominator;

        if (bigram.get(first).containsKey(second))
            return bigram.get(first).get(second) - discount;
        else
            return alpha * (unigram.get(second) / (double) uniSum);
    }
    public static void main(String[] args) {
//        String filename = "/Users/ebenezersemere/Workspace/Natural Language Processing/Assignment2/data/sentences";
        String filename = "/Users/ebenezersemere/Workspace/Natural Language Processing/Assignment2/data/abc.txt";
        double discount = 0.5;

        DiscountLMModel model = new DiscountLMModel(filename, discount);
    }
}

