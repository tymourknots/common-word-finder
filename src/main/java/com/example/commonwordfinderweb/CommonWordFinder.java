package com.example.commonwordfinderweb;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Class that instantiates either a BSTMap, AVLTreeMap, or MyHashMap from the MyMap interface
 * to parse an input text file and return an array of the most common words, ordered primarily by frequency (from highest to lowest)
 * and secondarily in alphabetical order.
 *
 * @author Tymour A. Aidabole
 * @uni taa2146
 */
public class CommonWordFinder {

    public static void main(String[] args) {
        if (!validateArguments(args)) return;

        MyMap<String, Integer> map = createMap(args[1]);
        if (map == null) return;

        if (!fillMap(map, args[0])) return;

        int limit = (args.length == 3) ? Integer.parseInt(args[2]) : 10;

        String[][] sortedWords = sortMap(map);

        printResults(sortedWords, limit);
    }

    private static boolean validateArguments(String[] args) {
        if (args.length != 2 && args.length != 3) {
            return false;
        }

        File file = new File(args[0]);
        if (!file.exists()) {
            return false;
        }

        if (!args[1].equals("bst") && !args[1].equals("avl") && !args[1].equals("hash")) {
            return false;
        }

        if (args.length == 3) {
            try {
                int limit = Integer.parseInt(args[2]);
                if (limit <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    private static MyMap<String, Integer> createMap(String structure) {
        switch (structure) {
            case "bst":
                return new BSTMap<>();
            case "avl":
                return new AVLTreeMap<>();
            case "hash":
                return new MyHashMap<>();
            default:
                return null;
        }
    }

    private static boolean fillMap(MyMap<String, Integer> map, String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;

            while ((line = br.readLine()) != null) {
                line = line.replaceAll("[^a-zA-Z0-9\\s'-]+", " ").trim();
                String[] words = line.split("\\s+");

                for (String word : words) {
                    String normalizedWord = word.trim().toLowerCase();
                    if (normalizedWord.isEmpty() || normalizedWord.equals("www") || normalizedWord.equals("ftp")) continue;

                    if (normalizedWord.matches("[a-zA-Z]+(['-][a-zA-Z]+)*(--[a-zA-Z]+)?(--)?'?")) {
                        Integer count = map.get(normalizedWord);
                        map.put(normalizedWord, (count == null) ? 1 : count + 1);
                    }
                }
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    private static String[][] sortMap(MyMap<String, Integer> map) {
        String[][] words = new String[map.size()][2];
        int index = 0;
        Iterator<Entry<String, Integer>> itr = map.iterator();
        while (itr.hasNext()) {
            Entry<String, Integer> pair = itr.next();
            words[index][0] = pair.key;
            words[index++][1] = String.valueOf(pair.value);
        }

        Arrays.sort(words, (o1, o2) -> {
            int countComparison = Integer.compare(Integer.parseInt(o2[1]), Integer.parseInt(o1[1]));
            return (countComparison != 0) ? countComparison : o1[0].compareTo(o2[0]);
        });

        return words;
    }

    private static void printResults(String[][] words, int limit) {
        System.out.println("Total unique words: " + words.length);
        for (int i = 0; i < Math.min(limit, words.length); i++) {
            System.out.printf("%" + (String.valueOf(words.length).length()) + "d. %s %s%n", i + 1, words[i][0], words[i][1]);
        }
    }

    public static String runFromFile(String filename, String structure, int limit) {
        StringBuilder output = new StringBuilder();

        String[] args = {filename, structure, String.valueOf(limit)};
        if (!validateArguments(args)) return "Invalid arguments.";

        MyMap<String, Integer> map = createMap(structure);
        if (map == null) return "Invalid map structure.";

        if (!fillMap(map, filename)) return "Error processing file.";

        String[][] sortedWords = sortMap(map);

        output.append("Total unique words: ").append(sortedWords.length).append("\n");
        for (int i = 0; i < Math.min(limit, sortedWords.length); i++) {
            output.append(String.format("%" + (String.valueOf(sortedWords.length).length()) + "d. %s %s%n", i + 1, sortedWords[i][0], sortedWords[i][1]));
        }

        return output.toString();
    }

    public static Map<String, Object> getResults(String filename, String structure, int limit) {
        Map<String, Object> response = new HashMap<>();

        String[] args = {filename, structure, String.valueOf(limit)};
        if (!validateArguments(args)) {
            response.put("error", "Invalid arguments.");
            return response;
        }

        MyMap<String, Integer> map = createMap(structure);
        if (map == null) {
            response.put("error", "Invalid map structure.");
            return response;
        }

        if (!fillMap(map, filename)) {
            response.put("error", "Error processing file.");
            return response;
        }

        String[][] sortedWords = sortMap(map);

        List<String[]> resultList = new ArrayList<>();
        for (int i = 0; i < Math.min(limit, sortedWords.length); i++) {
            resultList.add(new String[]{sortedWords[i][0], sortedWords[i][1]});
        }

        response.put("result", resultList);
        response.put("uniqueCount", sortedWords.length);
        return response;
    }

    public static Map<String, Object> getResultsFromText(String text, String structure, int limit) {
        Map<String, Object> response = new HashMap<>();

        if (!structure.equals("bst") && !structure.equals("avl") && !structure.equals("hash")) {
            response.put("error", "Invalid data structure.");
            return response;
        }

        if (limit <= 0) {
            response.put("error", "Limit must be a positive number.");
            return response;
        }

        MyMap<String, Integer> map = createMap(structure);
        if (map == null) {
            response.put("error", "Invalid map structure.");
            return response;
        }

        try (BufferedReader br = new BufferedReader(new StringReader(text))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.replaceAll("[^a-zA-Z0-9\\s'-]+", " ").trim();
                String[] words = line.split("\\s+");

                for (String word : words) {
                    String normalizedWord = word.trim().toLowerCase();
                    if (normalizedWord.isEmpty() || normalizedWord.equals("www") || normalizedWord.equals("ftp")) {
                        continue;
                    }
                    if (normalizedWord.matches("[a-zA-Z]+(['-][a-zA-Z]+)*(--[a-zA-Z]+)?(--)?'?")) {
                        Integer count = map.get(normalizedWord);
                        map.put(normalizedWord, (count == null) ? 1 : count + 1);
                    }
                }
            }
        } catch (IOException e) {
            response.put("error", "Error processing text.");
            return response;
        }

        String[][] sortedWords = sortMap(map);
        List<String[]> resultList = new ArrayList<>();
        for (int i = 0; i < Math.min(limit, sortedWords.length); i++) {
            resultList.add(new String[]{sortedWords[i][0], sortedWords[i][1]});
        }

        response.put("result", resultList);
        response.put("uniqueCount", sortedWords.length);
        return response;
    }
}
