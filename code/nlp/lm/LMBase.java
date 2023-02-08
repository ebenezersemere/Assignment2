package nlp.lm;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

abstract class LMBase implements LMModel {
    protected HashMap<String, Integer> unigram;
    protected HashMap<String, HashMap<String, Integer>> bigram;

    public LMBase(String filename) {
        // initialization
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

        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filename);
            return 0.0;
        }
    }

    public abstract double getBigramProb(String first, String second);
}
