import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class BuildIndex {

    public static void main(String[] args) throws IOException {
        Indexer idx = new Indexer();
        String filePath = args[0];

        String content = Files.readString(Paths.get(filePath));
        idx.trMap = Indexer.transcriptParser(content, idx.dfMap, idx.docSizeMap);  // 傳遞 dfMap 給 transcriptParser 方法
        String outputFile = Indexer.getCorpusName(filePath);

        try {
            FileOutputStream fos = new FileOutputStream(outputFile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(idx);
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}