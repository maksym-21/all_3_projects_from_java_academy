package search_engine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static java.lang.Integer.parseInt;

public class SimpleSearchEngine {
    private static int ROWS_IN_DATA = 0;
    private static final Map<String, Set<Integer>> invertedIndex = new HashMap<>();
    private static final List<String> data = new LinkedList<>();

    private enum SEARCH_STRATEGY {
        ALL, ANY, NONE
    }

    public static void showMenu(boolean errorFlag){
        if (!errorFlag) {
            System.out.println();
            System.out.println("=== Menu ===");
            System.out.println("1. Find a person");
            System.out.println("2. Print all people");
            System.out.println("0. Exit");
        }
        else {
            System.out.println();
            System.out.println("Incorrect option! Try again.");
            showMenu(false);
        }
    }

    public static void showData(){
        System.out.println();
        System.out.println("=== List of people ===");
        for (String i : data) System.out.println(i);

        showMenu(false);
    }

    public static String[] readDataFromFile(String fileName){
        String[] output;

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))){
            String line;

            while ( (line=bufferedReader.readLine()) != null){
                data.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        ROWS_IN_DATA = data.size();

        output = new String[data.size()];

        data.toArray(output);

        return output;
    }

    public static void fillMap(String[] array){
        for (int i = 0; i < array.length; i++) {
            String[] split = array[i].split(" ");

            for (String s : split) {
                if (!invertedIndex.containsKey(s.trim().toLowerCase())) invertedIndex.put(s.trim().toLowerCase(), new HashSet<>(Set.of(i)));
                else {
                    Set<Integer> current = invertedIndex.get(s.trim().toLowerCase());
                    current.add(i);
                }
            }
        }
    }

    public static SEARCH_STRATEGY chooseStrategy(String strategy){
        return switch (strategy) {
            case "ALL" -> SEARCH_STRATEGY.ALL;
            case "ANY" -> SEARCH_STRATEGY.ANY;
            case "NONE" -> SEARCH_STRATEGY.NONE;
            default -> null;
        };
    }

    public static void searchByStrategy(SEARCH_STRATEGY searchStrategy, String prompt){
        String[] split = prompt.trim().toLowerCase().split(" ");
        boolean oneWord = split.length == 1;

        if (searchStrategy == SEARCH_STRATEGY.ALL) {
            if (oneWord) {
                Set<Integer> out = invertedIndex.get(prompt.trim().toLowerCase());

                if (out == null || out.size() == 0) System.out.println("No matching people found.");
                else {
                    System.out.println("Found people:");
                    for (int a : out) {
                        if (a < prompt.length()) System.out.println(data.get(a));
                    }
                }
            }
            else {
                for(String line : data){
                    if (line.toLowerCase().contains(prompt.trim().toLowerCase())) System.out.println(line);
                }
            }
        }
        else if (searchStrategy == SEARCH_STRATEGY.ANY){
            if (oneWord){
                searchByStrategy(SEARCH_STRATEGY.ALL, prompt);
            }
            else{
                HashSet<String> output = new HashSet<>();

                System.out.println("Found people:");
                for (String s : split) {
                    for (String q : data) {
                        if (q.trim().toLowerCase().contains(s)) output.add(q);
                    }
                }

                output.forEach(System.out::println);
            }
        }
        else if (searchStrategy == SEARCH_STRATEGY.NONE){
            HashSet<String> output = new HashSet<>();

            for (String line : data) {
                boolean flag = true;

                for (String q : split) {
                    if(line.trim().toLowerCase().contains(q)) {
                        flag = false;
                        break;
                    }
                }

                if (flag) output.add(line);
            }

            output.forEach(System.out::println);
        }
        else System.out.println("No such strategy, please try again!");
    }


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String[] searchList = null;
        SEARCH_STRATEGY searchStrategy;

        if (args.length != 0){
            if (args[0].equals("--data")){
                searchList = readDataFromFile(args[1]);
            }
        }
        else {
            System.out.println("Enter the number of people:");
            ROWS_IN_DATA = parseInt(scanner.nextLine());
            searchList = new String[ROWS_IN_DATA];
            System.out.println("Enter all people:");
            for (int i = 0; i < searchList.length; i++) {
                String line = scanner.nextLine();

                searchList[i] = line;
                data.add(line);
            }
        }

        assert searchList != null;
        fillMap(searchList);


        showMenu(false);

        while (true) {
            try {
                int option = parseInt(scanner.nextLine());

                if (option == 0) {
                    System.out.println();
                    System.out.println("Bye!");

                    break;
                } else if (option == 1) {
                    // search with strategy pick

                    System.out.println("\nSelect a matching strategy: ALL, ANY, NONE");
                    searchStrategy = chooseStrategy(scanner.nextLine().trim().toUpperCase());

                    System.out.println("\nEnter a name or email to search all suitable people.");
                    searchByStrategy(searchStrategy, scanner.nextLine());

                    showMenu(false);
                } else if (option == 2) {
                    showData();
                }
            }
            catch (Exception e){
                showMenu(true);
            }
        }
    }
}