
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.io.*;

public class Indexer implements Serializable {
    private static final long serialVersionUID = 1L;
    public HashMap<Integer, Map<String, Integer>> trMap;
    public HashMap<String, Integer> dfMap;  // 添加 dfMap 用於存儲文檔頻率
    public HashMap<Integer, Integer> docSizeMap;

    public Indexer() {
        this.trMap = new HashMap<>();
        this.dfMap = new HashMap<>();
        this.docSizeMap = new HashMap<>();
    }

    public static HashMap<Integer, Map<String, Integer>> transcriptParser(String content, HashMap<String, Integer> dfMap, HashMap<Integer, Integer> docSizeMap) {
        HashMap<Integer, Map<String, Integer>> trMap = new HashMap<>();

        String[] lines = content.toLowerCase().replaceAll("[^a-z \n]", " ").split("\n");
        Set<String> seenWords = new HashSet<>();

        for (int i = 0; i < lines.length; i++) {
            int index = i / 5;
            trMap.putIfAbsent(index, new HashMap<>());
            String[] words = lines[i].trim().split("\\s+");
            docSizeMap.put(index, docSizeMap.getOrDefault(index, 0) + words.length);
            for (String word : words) {
                trMap.get(index).put(word, trMap.get(index).getOrDefault(word, 0) + 1);
                if (!seenWords.contains(word)) {
                    dfMap.put(word, dfMap.getOrDefault(word, 0) + 1);
                    seenWords.add(word);
                }
            }

            if (i % 5 == 4) {
                seenWords.clear();  // 每五行清空一次
            }
        }

        return trMap;
    }

    public static String getCorpusName(String filePath) {
        int dotIdx = filePath.indexOf('.');
        int corpusIdx = 26;
        int corpusNum = Integer.parseInt(filePath.substring(corpusIdx, dotIdx));
        return "corpus" + corpusNum + ".ser";
    }
}