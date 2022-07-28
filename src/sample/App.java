package sample;
import java.io.File;
import java.io.FileReader;

import generation.VIST;

/*************************************\
 *  A sample use of the VIST compiler *  
\*************************************/
public class App {
    /**
     * @param args
     * @throws Exception
      */
    public static void main(String[] args) throws Exception {
        // Create the FileReader
        var fileReader = new FileReader(new File(".\\src\\sample\\GameData.vist").getAbsolutePath());
        
        // Gather the characters
        var fileContent = new StringBuilder();
        while (true) {
            int character = fileReader.read();
            if (character == -1) break;

            fileContent.append((char) character);
        }
        
        // Create the VIST Document
        var gameData = new VIST(fileContent.toString(), true);

        // Retrieve Java types from the document
        float masterVolume = gameData.retrieveFloatFrom("/gameSettings/audioSettings/", "masterVolume");
        System.out.println(masterVolume);

        // Supports not allowing BASE objects to be retrieved
        // System.out.println(gameData.retrieveIntFrom("/item", "value")); // NOT ALLOWED
    }
}