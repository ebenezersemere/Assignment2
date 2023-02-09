package nlp.lm;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * An abstract class for bigram language models
 */
abstract class LMBase implements LMModel {
    // declare unigram & bigram
    protected HashMap<String, Integer> unigram;
    protected HashMap<String, HashMap<String, Integer>> bigram;

    /**
     * Constructor for constructing a language model from a file.
     * Unigram and bigram maps are populated from the file.
     *
     * @param filename the file to read from
     */
    public LMBase(String filename) {
        // initialize unigram & bigram
        unigram = new HashMap<String, Integer>();
        bigram = new HashMap<String, HashMap<String, Integer>>();

        // process file
        File file = new File(filename);
        ArrayList<String> main = new ArrayList<String>();

        // add <unk>, <s> and </s> to unigram, as they will not be found naturally from corpus
        unigram.put("<unk>", 0);
        unigram.put("<s>", 0);
        // set </s> to 1, as it we will later stop processing one word before the end of the file
        unigram.put("</s>", 1);

        // read file
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                // split line into words
                String[] line = scanner.nextLine().split("\\s");

                // add <s> to start of line & increment unigram count
                unigram.put("<s>", unigram.get("<s>") + 1);
                main.add("<s>");

                // if found, add word to unigram & increment unigram count; otherwise, increment <unk>
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

                // add </s> to end of line
                main.add("</s>");

            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + file);
        }

        // create unigram & bigram
        for (int i = 0; i < main.size() - 1; i++) {
            String curToken = main.get(i);
            String nextToken = main.get(i + 1);

            // if </s>, continue
            if (curToken.equals("</s>")) {
                unigram.put("</s>", unigram.get("</s>") + 1);
                continue;
            }

            // if not in bigram, add to bigram
            if (!bigram.containsKey(curToken))
                bigram.put(curToken, new HashMap<String, Integer>());

            if (!bigram.get(curToken).containsKey(nextToken))
                bigram.get(curToken).put(nextToken, 0);

            // increment bigram count
            Integer count = bigram.get(curToken).get(nextToken) + 1;
            bigram.get(curToken).put(nextToken, count);
        }

        // remove unigrams with count 0
        unigram.entrySet().removeIf(entry -> entry.getValue().equals(0));
    }

    /**
     * logProb calculates the sum of the log probabilities of all bigrams in a sentence.
     *
     * @param first
     * @param second
     * @return the log probability of a sentence
     */
    public double logProb(ArrayList<String> sentWords) {
        // add <s> and </s> to start and end of sentence
        sentWords.add(0, "<s>");
        sentWords.add("</s>");

        // initialize log
        double log = 0.0;

        // calculate log probability by adding log of each bigram probability
        for (int i = 0; i < sentWords.size() - 1; i++) {
            log += Math.log10(getBigramProb(sentWords.get(i), sentWords.get(i + 1)));
        }

        // return log
        return log;
    }

    /**
     * getPerplexity calculates the perplexity of a file.
     *
     * @param filename the file to read from
     * @return the perplexity of the file
     */
    public double getPerplexity(String filename) {
        // initialize sum and bigram counter
        double probSum = 0.0;
        int bigramCount = 0;

        // read file
        File file = new File(filename);
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                // split line into words
                String[] line = scanner.nextLine().split("\\s");
                ArrayList<String> sentence = new ArrayList<>(Arrays.asList(line));

                // increment bigram counter & increment sum by log probability
                bigramCount += sentence.size() - 1;
                probSum += logProb(sentence);
            }

            // calculate & return perplexity
            double avg = probSum / bigramCount;
            return Math.pow(10, -1 * avg);

        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filename);
            return 0.0;
        }
    }

    public abstract double getBigramProb(String first, String second);
}
