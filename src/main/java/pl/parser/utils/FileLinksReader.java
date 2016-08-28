package pl.parser.utils;

import pl.parser.Main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/*
W tej klasie na wejsciu przekazuje plik z linkami do stron wyszukiwania
 */
public class FileLinksReader {

    private File fileLinks;

    public static List<String> FileLinksReader(File file) throws Exception {

        if (Main.SHOW_STEPS) System.out.println("- wczytuje linki z pliku do pamięci.");
        List<String> linkList = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null) {
            linkList.add(line);
        }
        return linkList;
    }
}
