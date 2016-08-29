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
    public static int SLEEP_TIME = 0; //czas uśpienia pomiędzy pobraniem ze strony kolejnych danych

    public static void main(String[] args) {

        //Czy wrzucać do bazy linki, które istnieją już w bazie danych?
        boolean repeatSearchLink = false;

        if(args.length > 0) {
            int sleep = Integer.parseInt(args[0]);
            SLEEP_TIME = sleep;
        }

        if(Main.SHOW_STEPS) System.out.println("- pobieram dane z opoznieniem " + Main.SLEEP_TIME + " ms");

        try {
            //Ustawiam dane połączenia z bazą danych
            Database.DatabaseConfiguration(new File("db.properties"));
            //Zamieniam linki z pliku na liste stringów z linkami
            List<String> linkList = FileLinksReader.FileLinksReader(new File("input"));
            //Parsuje linki i wrzucam je do bazy danych
            LinkParser linkParser = new LinkParser(linkList, repeatSearchLink);
            List<Subpage> subpageList = linkParser.getNotParsedLinks();
            CompanyParser companyParser = new CompanyParser();

            companyParser
                    .parseSubpages(subpageList)
                    .fillAddressFields();


        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
