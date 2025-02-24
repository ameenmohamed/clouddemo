package org.hac.codewsp.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class HACTesStr {

    static String MOCK_REQUEST_FILE = "/Users/aminasif/amroot/work/code-projects/code-whisperer/lambdas/uploadfile/src/main/resources/inputnw.txt";

    // Method to read contents of input.txt into String
    public static String readInput() {
        String input = "";
            try {
                input = new String(HACTesStr.class.getResourceAsStream(MOCK_REQUEST_FILE).readAllBytes());
            } catch (IOException e) {           
                e.printStackTrace();
            }
       
        return input;
    }
    
    // Method to read contentd of file into String using java 11
    public static String readInputFile() {
        Path filePath = Path.of(MOCK_REQUEST_FILE);
        String content = "";
        try {
             content = Files.readString(filePath);
        } catch (IOException e) {     
            e.printStackTrace();
        }
        
        return content;
        }
    
}