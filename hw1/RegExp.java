import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class RegExp {

    public static boolean IsPalindrome(String string) {
        String UPPERstring = string.toUpperCase();
        int lengthOfString = string.length();
        for (int i = 0; i < lengthOfString / 2; i++) {
            if (UPPERstring.charAt(i) != UPPERstring.charAt(lengthOfString - i - 1)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isContainstr1(String string, String str1) {
        str1 = str1.toUpperCase();
        string = string.toUpperCase();

        char[] strArray = string.toCharArray();
        char[] str1Array = str1.toCharArray();

        for (int i = 0; i <= strArray.length - str1Array.length; i++) {
            boolean match = true;
            for (int j = 0; j < str1Array.length; j++) {
                if (strArray[i + j] != str1Array[j]) {
                    match = false;
                    break;
                }
            }
            if (match) {
                return true;
            }
        }
        return false;
    }

    public static boolean isContainstr2(String string, String str2, int n) {
        str2 = str2.toUpperCase();
        string = string.toUpperCase();
        int count = 0;
        char[] strArray = string.toCharArray();
        char[] str2Array = str2.toCharArray();

        for (int i = 0; i <= strArray.length - str2Array.length; i++) {
            boolean match = true;
            for (int j = 0; j < str2Array.length; j++) {
                if (strArray[i + j] != str2Array[j]) {
                    match = false;
                    break;
                }
            }
            if (match) {
                count++;
                if (count >= n) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isabbreapted(String string) {
        String LOWERstring = string.toLowerCase();
        int indexOfa = -1;
        for (int i = 0; i < LOWERstring.length(); i++) {
            if (LOWERstring.charAt(i) == 'a') {
                indexOfa = i;
                break;
            }
        }
        if (indexOfa != -1 && indexOfa < LOWERstring.length() - 1 ){
            String Substring = LOWERstring.substring(indexOfa + 1) ;
            for (int i = 0; i < Substring.length()-1; i++) {
                if (Substring.charAt(i) == 'b' && Substring.charAt(i+1) == 'b') {
                    return true;
                }
            }
        }
            return false;
    }

    public static String booleanToNY(boolean value) {
        return value ? "Y" : "N";
    }

    public static void main(String[] args) {
        String str1 = args[1];
        String str2 = args[2];
        int s2Count = Integer.parseInt(args[3]);

        try {
            BufferedReader reader = new BufferedReader(new FileReader(args[0]));
            String line;
            while ((line = reader.readLine()) != null) {
                boolean result1 = IsPalindrome(line);
                boolean result2 = isContainstr1(line, str1);
                boolean result3 = isContainstr2(line, str2, s2Count);
                boolean result4 = isabbreapted(line);
                String resultStr = booleanToNY(result1) + "," + booleanToNY(result2) + "," + booleanToNY(result3) + "," + booleanToNY(result4);
                System.out.println(resultStr);
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}