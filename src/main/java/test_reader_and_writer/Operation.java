package main.java.test_reader_and_writer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Operation {
    public static void main(String[] args) throws IOException {

        List<String> list = new ArrayList<>();
        List<String> listInt = new ArrayList<>();
        List<String> listDouble = new ArrayList<>();
        ArrayList<String> listString = new ArrayList<>();
        ArrayList<Integer> intsForStats = new ArrayList<>();
        ArrayList<Double> doublesForStats = new ArrayList<>();

        String resultFolder = null;
        String prefixForResultFiles = "";
        boolean appendParam = false;
        String statsType = null;
        List<String> txtFiles = new ArrayList<>();

        for (int i = 0; i < args.length; i++) {

            if (args[i].equals("-o")) {
                i++;
                resultFolder = args[i];
            } else if (args[i].equals("-p")) {
                i++;
                prefixForResultFiles = args[i];
            } else if (args[i].equals("-a")) {
                appendParam = true;
            } else if (args[i].equals("-s")) {
                statsType = "short";
            } else if (args[i].equals("-f")) {
                statsType = "full";
            } else if (args[i].matches(".*\\.txt$")) {
                txtFiles.add(args[i]);
            } else {
                throw new IllegalStateException
                        ("Введена недопустимая опция или путь. " +
                        "Пожалуйста, обратитесь к инструкции.");
            }

        }


            if (resultFolder == null) {
                resultFolder = Paths.get(txtFiles.get(0)).getParent().toString() + "\\";
            } else if (!Files.isDirectory(Paths.get(resultFolder))) {
                resultFolder = Paths.get(txtFiles.get(0)).getParent().toString() + "\\";
                System.out.println("WARN: Указанный каталог для выходных файлов не существует. " +
                        "Программа будет использовать каталог по умолчанию для выходных файлов.");

            }

        System.out.println();

        int cntFiles = txtFiles.size();
        int cntRows = 1000;
        String[][] twoDimArray = new String[cntFiles][cntRows];
        System.out.println("Количество файлов на вход: " + cntFiles);
        for (int i = 0; i < cntFiles; i++) {

            try {
                System.out.println(txtFiles.get(i));
                Scanner scanner = new Scanner(new File(txtFiles.get(i)));
                list.clear();
                while (scanner.hasNext()) {
                    list.add(scanner.next());
                }
                scanner.close();

                int lSize = list.size();

                for (int j = 0; j < lSize; j++) {
                    String iStr = list.get(j);
                    twoDimArray[i][j] = iStr;
                }

                System.out.println();

            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
        }

        for (int j = 0; j < cntRows; j++) {
            for (int i = 0; i < cntFiles; i++) {
                if (twoDimArray[i][j] != null) {

                    if (twoDimArray[i][j].matches("\\d+")) {
                        int intRow = Integer.parseInt(twoDimArray[i][j]);  //целый тип
                        intsForStats.add(intRow);
                        listInt.add(twoDimArray[i][j]);

                    } else if (twoDimArray[i][j].matches("^-?\\d+\\.\\d+((E|e)-\\d+)?")) {
                        double doubleRow = Double.parseDouble(twoDimArray[i][j]);
                        doublesForStats.add(doubleRow);
                        listDouble.add(twoDimArray[i][j]);

                    } else {
                        listString.add(twoDimArray[i][j]);
                    }
                }
            }
        }

        writeArrayInOutputFile(listInt, prefixForResultFiles + "integers.txt", resultFolder, appendParam);
        writeArrayInOutputFile(listDouble, prefixForResultFiles + "floats.txt", resultFolder, appendParam);
        writeArrayInOutputFile(listString, prefixForResultFiles + "strings.txt", resultFolder, appendParam);

        if (Objects.equals(statsType, "full")) {
            System.out.println("Статистика по целым числам:");
            getFullStatsOnIntegers(intsForStats);
            System.out.println("Статистика по вещественным числам:");
            getFullStatsOnDoubles(doublesForStats);
            System.out.println("Статистика по объектам типа \"строка\":");
            getFullStatsOnStrings(listString);

        } else if (Objects.equals(statsType, "short")) {
            System.out.println("Количество целых чисел: " + listInt.stream().count());
            System.out.println("Количество вещественных чисел: " + listDouble.stream().count());
            System.out.println("Количество объектов типа \"строка\": " + listString.stream().count());

        }
    }

    private static void writeArrayInOutputFile
            (List<String> arrayList, String postfix, String resultPath, boolean appendPrm) throws IOException {

        if (!arrayList.isEmpty()) {
            File outputFile = new File(resultPath + postfix);

            outputFile.createNewFile();

            FileWriter writer = new FileWriter(
                    outputFile, appendPrm);

            for (String s : arrayList) {
                writer.write(s);
                writer.append(System.lineSeparator());
            }
            writer.close();
        }
    }

    private static void getFullStatsOnIntegers(ArrayList<Integer> intList) {

        System.out.println("Количество: " + (long) intList.size());
        System.out.println("Минимальное значение: " + Collections.min(intList));
        System.out.println("Максимальное значение: " + Collections.max(intList));
        int sum = intList.stream().mapToInt(e -> (int) e).sum();
        System.out.println("Сумма элементов: " + sum);
        System.out.println("Среднее значение: " + sum / intList.size());
        System.out.println();
    }

    private static void getFullStatsOnDoubles(ArrayList<Double> doubleList) {

        System.out.println("Количество: " + (long) doubleList.size());
        System.out.println("Минимальное значение: " + Collections.min(doubleList));
        System.out.println("Максимальное значение: " + Collections.max(doubleList));
        double sum = doubleList.stream().mapToDouble(e -> e).sum();
        System.out.println("Сумма элементов: " + sum);
        System.out.println("Среднее значение: " + sum / doubleList.size());
        System.out.println();
    }

    private static void getFullStatsOnStrings(ArrayList<String> strList) {

        System.out.println("Количество: " + (long) strList.size());
        System.out.println("Размер самой короткой строки: "
                + Collections.min(strList, Comparator.comparing(String::length)).length());
        System.out.println("Размер самой длинной строки: "
                + Collections.max(strList, Comparator.comparing(String::length)).length());
    }
}

