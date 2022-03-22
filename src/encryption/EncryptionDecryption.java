package encryption;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class EncryptionDecryption {

    public static String readFromFile(boolean operation ,String fileName, int shift){
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
            StringBuilder lines = new StringBuilder();
            String line;

            while ( (line = bufferedReader.readLine()) != null ){
                lines.append(line);
            }

            return operation ? encrypt(lines.toString().toCharArray(),shift) : decrypt(lines.toString().toCharArray(),shift);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Error";
    }

    public static void writeToFile(String fileName, String data){
        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName))) {
            bufferedWriter.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String encrypt(char[] c, int shift){
        if (c.length == 0) return "";

        for (int i = 0; i < c.length; i++) {
            char tmp = c[i];

            tmp += shift;

            c[i] = tmp;
        }

        return new String(c);
    }

    public static String decrypt(char[] c,int shift){
        if (c.length == 0) return "";

        for (int i = 0; i < c.length; i++) {
            char tmp = c[i];

            tmp -= shift;

            c[i] = tmp;
        }

        return new String(c);
    }

    public static void main(String[] args) {
        Map<String,String> map = new HashMap<>();

        map.put("-mode", "enc");
        map.put("-key", "0");
        map.put("-data", "");
        map.put("-out", null);
        map.put("-in", null);

        for (int i = 0; i < args.length; i+=2) {
            map.put(args[i], args[i + 1]);
        }

        int shift = Integer.parseInt(map.get("-key"));


        if (map.get("-in") == null){
            if (map.get("-out") == null){
                if (map.get("-mode").equals("enc")) System.out.println(encrypt(map.get("-data").toCharArray(), shift));
                else System.out.println(decrypt(map.get("-data").toCharArray(), shift));
            }
            else {
                if (map.get("-mode").equals("enc")) {
                    writeToFile(map.get("-out"), encrypt(map.get("-data").toCharArray(), shift));
                }
                else writeToFile(map.get("-out"), decrypt(map.get("-data").toCharArray(), shift));
            }
        }
        else {
            String data;

            if(!map.get("-data").equals("")){
                if (map.get("-mode").equals("enc")) System.out.println(encrypt(map.get("-data").toCharArray(), shift));
                else System.out.println(decrypt(map.get("-data").toCharArray(), shift));
            }else {
                if (map.get("-mode").equals("enc")) data = readFromFile(true, map.get("-in"), shift);
                else data = readFromFile(false, map.get("-in"),  shift);

                if (map.get("-out") == null){
                    System.out.println(data);
                }
                else writeToFile(map.get("-out"), data);
            }
        }
    }
}
