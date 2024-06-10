import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class HtmlParser {
    public static void main(String[] args) {
        
        Map<String, List<Double>> existingPrices = new HashMap<>();
        
        int mode = Integer.parseInt(args[0]);
        if (args.length >1){
            int task = Integer.parseInt(args[1]);
        }

        switch (mode) {

            case 0:
                ScrapeStockData.Function();
                break;

            case 1:
                if (Integer.parseInt(args[1]) == 0){
                    mode1task0();
                } else {
                    int task = Integer.parseInt(args[1]);
                    String stock = args[2];
                    int startDate = Integer.parseInt(args[3]);
                    int endDate = Integer.parseInt(args[4]);
                    switch (task) {
                        
                        case 1:
                        mode1task1(stock, startDate, endDate);
                        break;

                        case 2:
                        mode1task2(stock, startDate, endDate);
                        break;

                        case 3:
                        mode1task3(startDate, endDate);
                        break;

                        case 4:
                        mode1task4(stock, startDate, endDate);
                        break;

                        default:
                        break;
                    }
                }

            default:
                break;
        }

    }

    public static void mode1task0() {
        String inputFile = "data.csv"; // 原始CSV文件的路徑
        String outputFile = "output.csv"; // 輸出CSV文件的路徑
        Map<String, List<Double>> existingPrices =  ScrapeStockData.readCSV(inputFile);
        ScrapeStockData.writeToData(outputFile, existingPrices);
    }

    public static void mode1task1(String stock, int startDate, int endDate) {
        Map<String, List<Double>> existingPrices = ScrapeStockData.readCSV("data.csv");
        List<Double> priceOfStock = existingPrices.get(stock);
    
        String output = "";
        for (int start = startDate; start <= endDate - 4; start++) {
            List<Double> targetDoubles = new ArrayList<>();
            for (int i = start; i < start + 5; i++) {
                targetDoubles.add(priceOfStock.get(i - 1));
            }
    
            double sum = 0;
            for (Double price : targetDoubles) {
                sum += price;
            }
            double movingAverage = sum / 5; 
            output += "," + Mathmethod.isInt(Mathmethod.round(movingAverage));
        }
        output = output.substring(1);
        WriteToOutput.writeToOutput(stock + "," + startDate + "," + endDate + "\n" + output);
    }
    

    public static void mode1task2( String stock , int startDate , int endDate ){

        Map<String, List<Double>> existingPrices = ScrapeStockData.readCSV("data.csv");
        List <Double> priceOfStock = existingPrices.get(stock);
        List <Double> targetDoubles = new ArrayList<>();
        for( int  i = startDate; i <= endDate; i++ ){
            targetDoubles.add(priceOfStock.get(i-1));
        }
        double standardDeviation = Mathmethod.SD(targetDoubles);
        String output = stock + "," + startDate + "," + endDate + "\n" + Mathmethod.isInt(Mathmethod.round(standardDeviation));
        WriteToOutput.writeToOutput(output);
    }

    public static void mode1task3(int startDate , int endDate) {

        Map<String, List<Double>> existingPrices = ScrapeStockData.readCSV("data.csv");
        Map< String, Double> standardDeviationMap = new HashMap<>();
        for (Map.Entry<String, List<Double>> entry : existingPrices.entrySet()){
            List<Double> priceOfStock = entry.getValue();
            List<Double> targetDoubles = new ArrayList<>();
            for( int  i = startDate; i <= endDate; i++ ){
                targetDoubles.add(priceOfStock.get(i-1));
            }
            double standardDeviation = Mathmethod.SD(targetDoubles);
            standardDeviationMap.put( entry.getKey(), standardDeviation);
        }
        List<Map.Entry<String, Double>> entryList = new ArrayList<>(standardDeviationMap.entrySet());
        entryList.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        int count = 0;
        String top3Name = "";
        String top3Std = "";
        for (Map.Entry<String, Double> entry : entryList) {
            top3Name += entry.getKey() + "," ;
            top3Std += "," + Mathmethod.isInt(Mathmethod.round(entry.getValue()));
            count++;
            if (count == 3) {
                break;
            }
        }
        top3Name = top3Name.substring(0, top3Name.length() - 1);
        top3Std = top3Std.substring(1); 
        String output = top3Name + "," + startDate + "," + endDate + "\n" + top3Std;
        WriteToOutput.writeToOutput(output);
    }

    public static void mode1task4(String stock, int startDate, int endDate) {
        List<Integer> xList = new ArrayList();
        for ( int i = startDate ; i <= endDate ; i++ ){
            xList.add(i);
        }
        Map<String, List<Double>> existingPrices = ScrapeStockData.readCSV("data.csv");
        List <Double> priceOfStock = existingPrices.get(stock);
        List <Double> targetDoubles = new ArrayList<>();
        for( int  i = startDate; i <= endDate; i++ ){
            targetDoubles.add(priceOfStock.get(i-1));
        }
        String output = stock + "," + startDate + "," +endDate + "\n" ;
        output += (Mathmethod.RegressionLine(xList, targetDoubles));
        WriteToOutput.writeToOutput(output);
    }

}
 
class ScrapeStockData {

    // 定義一個靜態成員變數來保存股票資料
    private static Map<String, List<Double>> stockPrices = new HashMap<>();

    public static void Function() {
        // 呼叫爬取並寫入 CSV 的函式
        crawlStockData();
    }
    

    // 爬取股票資料並更新股票資料的函式
    public static void crawlStockData() {
        try {
            // 從網站上獲取 HTML 文檔
            Document doc = Jsoup.connect("https://pd2-hw3.netdb.csie.ncku.edu.tw").get();
            // 獲取文檔內容
            String htmlContent = doc.text();
            //System.out.println(doc.title());
            htmlContent = htmlContent.trim().replaceAll("\\s+", " ");
            int day = htmlContent.charAt(4);
            htmlContent = htmlContent.substring(5);
            //System.out.println(htmlContent);
            // 將 HTML 內容分割為行
            List<String> stockNames = new ArrayList<>();
            List<Double> prices = new ArrayList<>();

                String[] tokens = htmlContent.split(" ");
                for (String token : tokens) {
                    if (token.matches(".*[A-Z].*")) { // 判斷是否為股票代號（全大寫字母）
                        stockNames.add(token);
                    } else if (token.matches("[0-9.]+")) { // 判斷是否為價格（數字或小數點）  
                        prices.add(Double.parseDouble(token));
                    }
                }
            
            //System.out.println(stockNames);
            //System.out.println(prices);
            // 將股票名稱和價格放入到映射中
            Map<String, List<Double>> existingPrices = readCSV("data.csv");
            //System.out.println(stockNames.size());
            //System.out.println(prices.size());
            // 將新的股價資料插入到原始映射中
            for (int i = 0; i < stockNames.size(); i++) {
                String stockName = stockNames.get(i);
                double latestPrice = prices.get(i);
                if (existingPrices.containsKey(stockName)) {
                    existingPrices.get(stockName).add(latestPrice);
                } else {
                    List<Double> priceList = new ArrayList<>();
                    priceList.add(latestPrice);
                    existingPrices.put(stockName, priceList);
                }
            }
            /*for (Map.Entry<String, List<Double>> entry : existingPrices.entrySet()){
                List<Double> prList = entry.getValue();
                for (Double value : prList){
                    System.out.print(value + " ");
                }

            }*/
        // 將更新後的股票資料寫入回CSV文件
        writeToData("data.csv", existingPrices);
     
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, List<Double>> readCSV(String filePath) {
        Map<String, List<Double>> existingPrices = new LinkedHashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            if ((line = br.readLine()) != null) {
                String[] stockNamesArray = line.split(",");
                // 跳過第一行，因為它是股票名稱
                while ((line = br.readLine()) != null) {
                    String[] tokens = line.split(",");
                    for (int i = 0; i < stockNamesArray.length; i++) {
                        String stockName = stockNamesArray[i];
                        double price = Double.parseDouble(tokens[i]);
                        existingPrices.computeIfAbsent(stockName, k -> new ArrayList<>()).add(price);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return existingPrices;
    }
    

    // 寫入股票資料到 CSV 檔案的函式
    public static void writeToData(String filePath, Map<String, List<Double>> stockPrices) {
        try (FileWriter writer = new FileWriter(filePath)) {
            StringBuilder nameBuilder = new StringBuilder();
            for (String stockName : stockPrices.keySet()) {
                nameBuilder.append(",").append(stockName);
            }
            writer.write(nameBuilder.substring(1) + "\n"); // 刪除開頭的逗號並寫入文件
            
            int numPrices = stockPrices.values().stream().findFirst().orElseThrow(() -> new RuntimeException("No prices found")).size();
            
            for (int i = 0; i < numPrices; i++) {
                StringBuilder priceBuilder = new StringBuilder();
            
                for (List<Double> prices : stockPrices.values()) {
                        priceBuilder.append(",").append(String.format("%.2f",prices.get(i)));
                    }
                
                String price = priceBuilder.toString() + "\n" ;
                price = price.substring(1);
                writer.write(price);
                //System.out.println("資料已寫入到 " + filePath);
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}



class Mathmethod {

    public static double Average(List <Double> targetDoubles){
        double sum = 0;
        for (Double prices : targetDoubles ){
            sum += prices; 
        }
        return round((double) sum / targetDoubles.size());
    }
 
    public static double Sqrt(double var){
        double guess = var / 2.0;
        double epsilon = 0.0001;
        while (true) {
            double newGuess = (guess + var / guess) / 2.0;
            if ((guess - newGuess) < epsilon && (newGuess - guess) < epsilon) {
                break;
            }
            guess = newGuess;
        }
        return round(guess);
    }
    
    public static double SD(List <Double> targetDoubles){
        double average = Average(targetDoubles);
        double var = 0;
        for (Double prices : targetDoubles ) {
            prices = prices - average;
            prices = prices * prices;
            prices  = prices / (targetDoubles.size() - 1);
            var += prices;
        }
        return round(Sqrt(var));
    }

    public static String RegressionLine(List<Integer> x, List<Double> y) {
        int n = x.size();
        int sumX = 0;
        double sumY = 0;
        double sumXY = 0;
        double sumX2 = 0;
        
        // 計算 Σx, Σy, Σxy 和 Σ(x^2)
        for (int i = 0; i < n; i++) {
            sumX += x.get(i);
            sumY += y.get(i);
            sumXY += x.get(i) * y.get(i);
            sumX2 += x.get(i) * x.get(i); // 直接計算 x 的平方
        }
        
        double slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
        
        double intercept = (sumY - slope * sumX) / n;
        
        return  Mathmethod.isInt(round(slope)) + "," + Mathmethod.isInt(round(intercept));

    }
    
    public static double round(double value) {
        double scaledValue = value * 100;
        double roundedValue = 0.0;
        if (value >= 0) {
            roundedValue = (long) (scaledValue + 0.5); // 四捨五入
        } else {
            roundedValue = (long) (scaledValue - 0.5); // 四捨五入
        }
        return (double) roundedValue / 100;
    }
    
    public static String isInt(double num) {
        if ( (int) num == num){
            return String.valueOf((int) num);
        } else {
            return String.valueOf(num);
        }
    }
}

class WriteToOutput {
    public static void writeToOutput( String output ){
        try (FileWriter writer = new FileWriter("output.csv" , true)) {
            writer.write(output + "\n" );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
