package org.example;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Utils {
    public static boolean areFilesEqual(String filePath1, String filePath2) throws IOException {
        File file1 = new File(filePath1);
        File file2 = new File(filePath2);

        if (!file1.exists() || !file2.exists()) {
            return false;
        }

        try (FileInputStream fileStream1 = new FileInputStream(filePath1);
             FileInputStream fileStream2 = new FileInputStream(filePath2)) {

            byte[] buffer1 = new byte[1024];
            byte[] buffer2 = new byte[1024];

            int bytesRead1, bytesRead2;

            while (true) {
                bytesRead1 = fileStream1.read(buffer1);
                bytesRead2 = fileStream2.read(buffer2);

                if (bytesRead1 != bytesRead2) {
                    return false; // Files have different lengths
                }

                if (bytesRead1 == -1) {
                    // Reached the end of both files without finding any differences
                    return true;
                }

                // Compare the contents of the two buffers
                if (!Arrays.equals(buffer1, buffer2)) {
                    System.out.println("Here");
                    return false; // Found a difference
                }
            }
        }
    }

    public static void writeResult(MyList l, String filePath){
        List<Node> nodes = l.getElements();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Node n: nodes) {
                writer.write(n.getData().getId() + "," + n.getData().getScore());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void writeResultParalell(MyList l, String filePath){
        List<Node> nodes = l.getElements();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (int i=1;i<nodes.size()-1;i++) {
                writer.write(nodes.get(i).getData().getId() + ", " + nodes.get(i).getData().getScore() + ", " + nodes.get(i).getData().getCountry());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeMapToFile(Map<String, Integer> countryResult, String filePath) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));

            for (String key : countryResult.keySet()) {
                bw.write("Country: " + key + " ; Score: " + countryResult.get(key));
                bw.newLine();
            }
            bw.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
