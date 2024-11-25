package org.codefest2024.nghenhan;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ReformatMap {
    public static void main(String[] args) {
        String inputFilePath = "rawMap.txt";
        String outputFilePath = "reformatedMap.txt";

        try {
            String content = Files.readString(Paths.get(inputFilePath));

            String reformatted = content.replaceAll("\\s*(\\[)\\s*", "$1")
                    .replaceAll("\\s*(\\])\\s*", "$1")
                    .replaceAll(",\\s*", ", ")
                    .replaceAll("\\],\\s*\\[", "],\n  [");

            Files.writeString(Paths.get(outputFilePath), reformatted);

            System.out.println("Reformatted content has been written to: " + outputFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
