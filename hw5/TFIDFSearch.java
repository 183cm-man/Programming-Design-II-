import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class TFIDFSearch {

    public static void main(String[] args) {
        String testFilePath = args[0] + ".ser";
        String tcPath = args[1];

        Indexer deserializedIdx = null;

        // 反序列化
        try {
            FileInputStream fis = new FileInputStream(testFilePath);
            ObjectInputStream ois = new ObjectInputStream(fis);
            deserializedIdx = (Indexer) ois.readObject();
            ois.close();
            fis.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }


        HashMap<Integer, Map<String, Integer>> trMap = deserializedIdx.trMap;
        HashMap<String, Integer> dfMap = deserializedIdx.dfMap;
        HashMap<Integer, Integer> docSizeMap = deserializedIdx.docSizeMap;
        

        try {
            // 讀取測資
            String tcContent = Files.readString(Path.of(tcPath));
            List<String> tcList = Arrays.asList(tcContent.split("\n"));

            int outputNum = Integer.parseInt(tcList.get(0));

            List<List<Integer>> results = new ArrayList<>();
            for (int i = 1; i < tcList.size(); i++) {
                HashMap<Integer, Double> tfidfMap = new HashMap<>();
                Set<Integer> documentIds = queryDocumentIds(trMap, tcList.get(i));
                for (int id : documentIds) {
                    int docSize = docSizeMap.get(id);
                    double tfidfSum = TFIDFCalculator.tfIdfCalculate(trMap.get(id), trMap.size(), dfMap, tcList.get(i), docSize,id);
                    tfidfMap.put(id, tfidfSum);
                }

                List<Integer> sortedResults = getTopResults(tfidfMap, outputNum);
                results.add(sortedResults);
            }

            writeResultsToFile(results);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Set<Integer> queryDocumentIds(HashMap<Integer, Map<String, Integer>> trMap, String queryLine) {
        String[] tokens = queryLine.trim().split("\\s+");

        Set<String> set = new HashSet<>(Arrays.asList(tokens));
        
        // 將結果轉換回 String[]
        tokens = set.toArray(new String[0]);

        List<Set<Integer>> wordGroups = new ArrayList<>();
        String operator = "OR";  // 默认操作符为 OR

        for (String token : tokens) {
            if (token.equals("AND") || token.equals("OR")) {
                operator = token;
            } else {
                Set<Integer> wordGroup = new HashSet<>();
                for (Map.Entry<Integer, Map<String, Integer>> entry : trMap.entrySet()) {
                    if (entry.getValue().containsKey(token)) {
                        wordGroup.add(entry.getKey());
                    }
                }
                wordGroups.add(wordGroup);
            }
        }

        // 根据操作符合并结果
        Set<Integer> resultSet = new HashSet<>(wordGroups.get(0));
        for (int i = 1; i < wordGroups.size(); i++) {
            if (operator.equals("AND")) {
                resultSet.retainAll(wordGroups.get(i));  // 保留交集
            } else if (operator.equals("OR")) {
                resultSet.addAll(wordGroups.get(i));  // 取并集
            }
        }

        return resultSet;
    }

    public static List<Integer> getTopResults(HashMap<Integer, Double> tfidfMap, int outputNum) {
        List<Map.Entry<Integer, Double>> sortedEntries = new ArrayList<>(tfidfMap.entrySet());
        sortedEntries.sort((entry1, entry2) -> {
            int compare = entry2.getValue().compareTo(entry1.getValue()); // 先按 TF-IDF 值排序
            if (compare != 0) {
                return compare;
            } else {
                // 如果 TF-IDF 值相同，按照文檔 ID 從小到大排序
                return entry1.getKey().compareTo(entry2.getKey());
            }
        });
    
        List<Integer> topResults = new ArrayList<>();
        for (int i = 0; i < outputNum; i++) {
            if (i < sortedEntries.size()) {
                topResults.add(sortedEntries.get(i).getKey());
            } else {
                topResults.add(-1);  // 用 -1 取代缺少的文檔
            }
        }
        return topResults;
    }
    

    public static void writeResultsToFile(List<List<Integer>> results) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"))) {
            for (List<Integer> result : results) {
                String resultLine = result.toString().replace("[", "").replace("]", "").replace(",", "");
                writer.write(resultLine);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("無法寫入文件");
            e.printStackTrace();
        }
    }

    private static class TFIDFCalculator {

        public static double tfIdfCalculate(Map<String, Integer> doc, int docCount, HashMap<String, Integer> dfMap, String termLine, Integer docSize, int id) {
            // 計算TF
            String[] terms = termLine.trim().replaceAll("AND|OR", " ").split("\\s+");

            HashMap<String, Double> tfidfMap = new HashMap<>();
            //HashMap<Integer, Double> id_idfMap = new HashMap<>();

           
            double tfidfSum = 0.0;
            for (String term : terms) {
                if (tfidfMap.containsKey(term)){
                    tfidfSum += tfidfMap.get(term);
                }else{

                    double tf = doc.getOrDefault(term, 0) / (double) docSize;//(double) docSize ;
                    // 計算IDF，使用 IDF 緩存
                     
                    double termCount = dfMap.getOrDefault(term, 0);
                    double idf = (termCount > 0) ? Math.log((double) docCount / termCount) : 0;

                    double tfidf = tf * idf;
                    tfidfSum += tfidf;
                    
                    tfidfMap.put(term, tfidf);
                }
                
                
            }
            return tfidfSum;
        }


    }
}
