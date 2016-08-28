package pl.parser;


import pl.parser.dao.Subpage;
import pl.parser.utils.CompanyParser;
import pl.parser.utils.Database;
import pl.parser.utils.FileLinksReader;
import pl.parser.utils.LinkParser;

import java.io.File;
import java.util.List;


public class Main {

    public static boolean SHOW_STEPS = true;

    public static void main(String[] args) {

        //Czy wrzucać do bazy linki, które istnieją już w bazie danych?
        boolean repeatSearchLink = false;

        try {
            //Ustawiam dane połączenia z bazą danych
            Database.DatabaseConfiguration(new File("db.properties"));
            //Zamieniam linki z pliku na liste stringów z linkami
            List<String> linkList = FileLinksReader.FileLinksReader(new File("input2"));
            //Parsuje linki i wrzucam je do bazy danych
            LinkParser linkParser = new LinkParser(linkList, repeatSearchLink);
            List<Subpage> subpageList = linkParser.getNotParsedLinks();
            CompanyParser companyParser = new CompanyParser();

            companyParser
                    .parseSubpages(subpageList)
                    .fillAddressFields();


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
