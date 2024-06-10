/*import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.*;  

public class CodeGenerator {
    public static void main(String[] args) {
        Map<String, List<String>> finalMap = new HashMap<>() ;

        if (args.length == 0) {
            System.err.println("請輸入mermaid檔案名稱");           
        }
        else {
            String fileName = args[0];
            String mermaidCode = "";

            FileReader mermaidCodeReader = new FileReader();
            mermaidCode = mermaidCodeReader.read(fileName);
            finalMap = Parser.splitByClass(mermaidCode);
            Writejava.Write(finalMap);

       }
    }
}

class FileReader {
    public String read(String fileName) {

        String mermaidCode = "";
        try {
            mermaidCode = Files.readString(Paths.get(fileName));
            return mermaidCode;
        }
        catch (IOException e) {
            System.err.println("無法讀取文件 " + fileName);
            e.printStackTrace();
            return "無法讀取文件" ;
        }   
    }   
}

class Parser {

    public static Map<String, List<String>> findClass(String mermaidcode,Map<String, List<String>> classDefinitions){
        mermaidcode = mermaidcode.trim();
        String[] lines = mermaidcode.split("\\n"); 
        for (String line : lines) {
            String[] words = line.trim().split("\\s+"); 
            if (words.length >= 2 && words[0].equals("class")) { 
                String className = words[1]; 
                classDefinitions.put(className, new ArrayList<>());
            }
        }
        return classDefinitions;
    }
    

    public static Map<String, List<String>> splitByClass(String mermaidcode) {
        Map<String, List<String>> classDefinitions = new HashMap<>();

        classDefinitions = findClass(mermaidcode, classDefinitions); //獲得具有class-list的map
        List<String> classLines = Arrays.asList(mermaidcode.split("\n"));; // 將每行分割並存入arraylist

            for (Map.Entry<String, List<String>> entry : classDefinitions.entrySet()) {
                entry.getValue().add("public class " + entry.getKey() + " {") ;

                for(String line : classLines){
                    String output = "";
                    line = line.replaceAll("\\s+", " ");
                    if ( line.contains(entry.getKey() + " :" )) {
                        int indexOfclass = line.indexOf(entry.getKey());
                        int startIndex = line.indexOf(":") + 1; // 取得冒號的索引位置，並加一 //setSize(int size) void
                        if(indexOfclass < startIndex){
                            String subString = line.substring(startIndex).trim(); // 從冒號後的位置開始取得子字串
                            if(subString.contains("+")){
                                output += "    public ";
                                int index = subString.indexOf("+") + 1;
                                subString = subString.substring(index);               
                            }else{
                                output += "    private ";
                                int index = subString.indexOf("-") + 1;
                                subString = subString.substring(index);
                            }
                            if(subString.contains(")")){
                                String returnType = "";
                                int indexgua = subString.indexOf(")") + 1;
                                returnType = subString.substring(indexgua).trim(); 
                                if (returnType == null || returnType == "void"){
                                    output += "void "; 
                                }else{
                                    output += returnType + " ";                   
                                }
                                int index = subString.indexOf(")")+1;
                                int indexOfname = subString.indexOf("(");
                                String functionName = subString.substring(0, index);
                                String Name = subString.substring(0,indexOfname);
                                output += functionName + " {";
                                if(Name.contains("get")){
                                    int indexOfVar = Name.indexOf("get") + 3;
                                    String functionVar = Name.substring(indexOfVar).toLowerCase();
                                    output += "\n" + "        " + "return " + functionVar + ";\n    }";
                                }else if (functionName.contains("set")) {
                                    int indexOfVar = Name.indexOf("set") + 3;
                                    String functionVar = Name.substring(indexOfVar).toLowerCase();
                                    output += "\n" + "        " + "this." + functionVar + " = " + functionVar + ";\n    }"; 
                                }else{                       
                                    if (returnType.contains("int")){
                                        output += "return 0;}";
                                    }else if(returnType.contains("string")){
                                        output += "return \"\";}";
                                    }else if(returnType.contains("boolean")){
                                        output += "return false;}";
                                    }else{
                                        output += ";}";
                                    }                        
                                }  
                            }else{
                                output += subString + ";";      
                            }
                        }   
                        entry.getValue().add(output);                   
                    }
                }
                entry.getValue().add("}");
            }

    return classDefinitions;    
    }

}
           
class Writejava {    
    public static void Write(Map<String, List<String>> finalMap){

        try {
            for(Map.Entry<String, List<String>> entry : finalMap.entrySet()){
                String key = entry.getKey();
                String outputName =  key + ".java";
                List<String> outputList = entry.getValue();

                String contentString = String.join("\n" , outputList);
                File file = new File(outputName);
                if (!file.exists()) {
                    file.createNewFile();
                }
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                    bw.write(contentString);
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}


*/


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.*;  

public class CodeGenerator {
    public static void main(String[] args) {
        Map<String, List<String>> finalMap = new HashMap<>() ;

        if (args.length == 0) {
            System.err.println("請輸入mermaid檔案名稱");           
        }
        else {
            String fileName = args[0];
            String mermaidCode = "";

            FileReader mermaidCodeReader = new FileReader();
            mermaidCode = mermaidCodeReader.read(fileName);
            finalMap = Parser.splitByClass(mermaidCode);
            Writejava.Write(finalMap);

       }
    }
}

class FileReader {
    public String read(String fileName) {

        String mermaidCode = "";
        try {
            mermaidCode = Files.readString(Paths.get(fileName));
            return mermaidCode;
        }
        catch (IOException e) {
            System.err.println("無法讀取文件 " + fileName);
            e.printStackTrace();
            return "無法讀取文件" ;
        }   
    }   
}

class Parser {

    public static Map<String, List<String>> findClass(String mermaidcode,Map<String, List<String>> classDefinitions){
        mermaidcode = mermaidcode.trim();
        String[] lines = mermaidcode.split("\\n"); 
        for (String line : lines) {
            String[] words = line.trim().split("\\s+"); 
            if (words.length >= 2 && words[0].equals("class")) { 
                String className = words[1]; 
                classDefinitions.put(className, new ArrayList<>());
            }
        }
        return classDefinitions;
    }


    public static Map<String, List<String>> splitByClass(String mermaidcode) {
        Map<String, List<String>> classDefinitions = new HashMap<>();

        classDefinitions = findClass(mermaidcode, classDefinitions); //獲得具有class-list的map
        List<String> classLines = Arrays.asList(mermaidcode.split("\n"));; // 將每行分割並存入arraylist

            for (Map.Entry<String, List<String>> entry : classDefinitions.entrySet()) {
                entry.getValue().add("public class " + entry.getKey() + " {") ;

                for(String line : classLines){
                    String output = "";
                    line = line.replaceAll ("\\s+", " ");
                    if ( line.contains(entry.getKey() + " :" )) {
                        int indexOfclass = line.indexOf(entry.getKey());
                        int startIndex = line.indexOf(":") + 1; // 取得冒號的索引位置，並加一 //setSize(int size) void
                        if(indexOfclass < startIndex){
                            String subString = line.substring(startIndex).trim(); // 從冒號後的位置開始取得子字串
                            output = isPublic.isPublic(subString);
                            
                            if(subString.contains(")")){
                                output = isFunction.isfunction(subString, output);
                            }else{
                                output = isAttribute.isattribute(subString, output);     
                            }
                        }   
                        entry.getValue().add(output);                   
                    }
                }

                entry.getValue().add("}");
            }
    return classDefinitions;    
    }
}

class isPublic {
    public static String isPublic(String suString){
        if(suString.contains("+")){
            return "    public ";
        }else{
            return "    private ";
        }
    }
}

class isFunction {

    public static String isfunction(String subString ,String output ){
        output = writeReturntype(subString, output);
        String returnType = getReturntype(subString, output);
        int index = subString.indexOf(")")+1;
        int indexOfname = subString.indexOf("(");
        String functionName = subString.substring(1, index);
        //System.out.println(functionName);
        String Name = subString.substring(1,indexOfname);
        output += functionName + " {";
        
        if(Name.contains("get")){
            int indexOfVar = Name.indexOf("get") + 3;
            String functionVar = Name.substring(indexOfVar).toLowerCase();
            output += "\n" + "        " + "return " + functionVar + ";\n    }";
        }else if (functionName.contains("set")) {
            int indexOfVar = Name.indexOf("set") + 3;
            String functionVar = Name.substring(indexOfVar).toLowerCase();
            output += "\n" + "        " + "this." + functionVar + " = " + functionVar + ";\n    }"; 
        }else{                       
            if (returnType.contains("int")){
                output += "return 0;}";
            }else if(returnType.contains("string")){
                output += "return \"\";}";
            }else if(returnType.contains("boolean")){
                output += "return false;}";
            }else{
                output += ";}";
            }                        
        }  
        return output;
    }
    
    public static String getReturntype (String subString ,String output) {
        String returnType = "";
        int indexOfreturn = subString.indexOf(")") + 1;
        returnType = subString.substring(indexOfreturn).trim(); 
        if (returnType == null || returnType == "void"){
            output += "void "; 
        }else{
            output += returnType + " ";                   
        }
        return returnType;
    }
    public static String writeReturntype(String subString, String output) {
        String returnType = "";
        int indexOfreturn = subString.indexOf(")") + 1;
        returnType = subString.substring(indexOfreturn).trim(); 
        if (returnType == null || returnType == "void"){
            output += "void "; 
        }else{
            output += returnType + " ";                   
        }
        return output;
    }
}

class isAttribute {
    public static String isattribute(String subString, String output){
        subString = subString.substring(1);                               
        output += subString + ";"; 
        return output;     
    }
}

class Writejava {    
    public static void Write(Map<String, List<String>> finalMap){

        try {
            for(Map.Entry<String, List<String>> entry : finalMap.entrySet()){
                String key = entry.getKey();
                String outputName =  key + ".java";
                List<String> outputList = entry.getValue();

                String contentString = String.join("\n" , outputList);
                File file = new File(outputName);
                if (!file.exists()) {
                    file.createNewFile();
                }
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                    bw.write(contentString);
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}