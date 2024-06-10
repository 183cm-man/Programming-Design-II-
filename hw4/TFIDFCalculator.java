import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;


public class TFIDFCalculator {
    static HashMap<Integer, List<String>> trMap = new HashMap<>();
    static HashMap<String, Integer>  idfMap = new HashMap<>();
    public static void main(String[] args) {
        String fileName = args[0];
        String tcName = args[1];

        String content = FileReader.readfile(fileName);
        String[] tc = FileReader.readfile(tcName).split("\n");

        trMap = TranscriptParser.transcriptParser(content);

        String [] terms = tc[0].split("\\s+");
        String []  docStrings = tc[1].split("\\s+");

        StringBuilder output = new StringBuilder();
        for(int i = 0; i < terms.length; i++ ) {
            int sampleNo = Integer.parseInt(docStrings[i]); 
            output.append(String.format("%.5f",tfIdfCalculate(trMap.get(sampleNo), trMap, terms[i]))).append(" ");
        } 
        FileWrite.writeOutput(output.toString());
    }
    
    public static double tfIdfCalculate(List<String> doc, HashMap<Integer, List<String>> docs, String term) {
        return tf(doc, term) * idf(docs, term);
    }
    
    public static double tf(List<String> doc, String term) {
        int number_term_in_doc = 0;
        for (String word : doc) {
            if (word.equals(term)) {
                number_term_in_doc++;
            }
        }
        return (double) number_term_in_doc / doc.size();
    }
    
    public static double idf(HashMap<Integer, List<String>> docs, String term) {

        if (idfMap.containsKey(term)){
            return Math.log((double) docs.size() / idfMap.get(term));
        }else{
            int number_doc_contain_term = 0;
            for (List<String> doc : docs.values()) {
                if (doc.contains(term)) {
                    number_doc_contain_term++;
                }
            }
            idfMap.put(term, number_doc_contain_term);
            return Math.log((double) docs.size() /number_doc_contain_term);
        }
        
    }
    
}


class TranscriptParser {

    public static HashMap<Integer, List<String>> transcriptParser(String content){
        HashMap<Integer, List<String>> trMap = new HashMap<>();
        String[] lines = content.toLowerCase().replaceAll("[^a-z \n]", " ").split("\n");
        
        for (int i = 0; i < lines.length; i++) {
            int index = i / 5;
            trMap.putIfAbsent(index, new ArrayList<>());
            trMap.get(index).addAll(Arrays.asList(lines[i].trim().split("\\s+")));
        }

        return trMap;
    }

}


class FileReader{

    public static String readfile(String fileName) {
        try{
            return Files.readString(Paths.get(fileName));
        }
        catch (IOException e) {
            System.err.println("無法讀取文本 " + fileName);
            e.printStackTrace();
            return "無法讀取文件" ;
        }   
    }

}

class FileWrite {
    public static void writeOutput(String output) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"))) {
            writer.write(output);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("無法寫入文件");
            e.printStackTrace();
        }
    }
}