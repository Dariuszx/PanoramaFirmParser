package pl.parser.utils;


import com.sun.org.apache.bcel.internal.generic.RET;
import pl.parser.Main;
import pl.parser.dao.Link;
import pl.parser.dao.Subpage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
W tej klasie zamieniam podany link z pliku na taki,
który przedstawia również wszystkie podstrony i mogę w ten sposób
iterować po wszystkich podstronach wydobywając z nich poszczególne firmy
 */
public class LinkParser {

    private List<Link> parsedLinkList;

    public LinkParser(List<String> linkList, boolean repeatSearchLink) throws Exception {

        //Parsuje wszystkie linki podane w pliku
        this.parsedLinkList = this.parseLinks(linkList);
        //Wrzucam sparsowane już linki do bazy danych
        this.insertLinksIntoDatabase(repeatSearchLink);
    }

    //Rozbijam linki tak abym mógł je później swobodnie przeglądać,
    //chodzi tu głównie o przeglądaniu wszystkich podstron
    private List<Link> parseLinks(List<String> links) throws Exception {
        if (Main.SHOW_STEPS) System.out.println("- rozbijam linki z pliku na czesci.");
        List<Link> parsedLinks = new ArrayList<Link>();
        Pattern pattern = Pattern.compile("([^?&=#]+)=([^&#]*)");

        for (String link : links) {
            Matcher m = pattern.matcher(link);
            Link parsedLink = new Link(link);

            while (m.find()) {
                String key = m.group(1);
                String value = m.group(2);

                if (key.equals("k")) {
                    parsedLink.setK(value);
                } else if (key.equals("l")) {
                    parsedLink.setL(value);
                }
            }

            if (parsedLink.commit()) {
                parsedLinks.add(parsedLink);
            }
        }
        return parsedLinks;
    }

    //Wrzucam sparsowane linki do bazy danych
    private boolean insertLinksIntoDatabase(boolean repeatSearchLink) throws SQLException {

        Database database = new Database();
        int notParsedLinks = 0;
        if (Main.SHOW_STEPS) {
            System.out.println("- sprawdzam czy podane linki byly już parsowane.");
            System.out.println("- czy powtorzyc parsowanie istniejacych linkow: " + repeatSearchLink);
        }

        for (Link link : parsedLinkList) {
            String sql = "SELECT * FROM page WHERE link = '" + link.getLink() + "'";
            ResultSet result = database.selectStatement(sql);
            sql = "INSERT INTO page(link,k,l,subpages_count) VALUES ('" + link.getLink() + "'," +
                    "'" + link.getK() + "', '" + link.getL() + "', " + link.getSubpagesCount() + " )";

            if (!result.next() && !repeatSearchLink) {

                database.createStatement(sql);
                sql = "SELECT * FROM page WHERE link = '" + link.getLink() + "'";
                ResultSet rs = database.selectStatement(sql);
                if (rs.next()) {
                    int page_id = rs.getInt("page_id");
                    for (int i = 0; i <= link.getSubpagesCount(); i++) {
                        sql = "INSERT INTO subpage (page_id, page_number, parsed) VALUES (" + page_id + ", " +
                                i + ", false)";
                        database.createStatement(sql);
                        notParsedLinks++;
                    }
                }
            } else if (result.next() && repeatSearchLink) {
//                notParsedLinks++;
                //TODO dopisać jak się będą powtarzały linki
            }
        }

        if (Main.SHOW_STEPS) {
            System.out.println("- ilość podstron z podanych linków do sparsowania: " + notParsedLinks);
        }

        database.close();
        return true;
    }

    public List<Subpage> getNotParsedLinks() throws SQLException {
        if (Main.SHOW_STEPS) System.out.println("- pobieram z bazy danych niesparsowane strony (niekoniecznie podstrony z pliku z linkami).");
        Database database = new Database();
        List<Subpage> subpagesList = new ArrayList<Subpage>();

        String sql = "SELECT * FROM page LEFT JOIN subpage ON(page.page_id=subpage.page_id) WHERE subpage.parsed = 0";
        ResultSet result = database.selectStatement(sql);

        while (result.next()) {
            String link = result.getString("link");
            String k = result.getString("k");
            String l = result.getString("l");
            int subpages_count = result.getInt("subpages_count");
            int page_number = result.getInt("page_number");
            int parsed = result.getInt("parsed");
            int subpage_id = result.getInt("subpage_id");

            Subpage subpage = new Subpage();
            subpage.setLink(link);
            subpage.setK(k);
            subpage.setL(l);
            subpage.setPageNumber(page_number);
            subpage.setParsed(parsed);
            subpage.setSubpagesCount(subpages_count);
            subpage.setSubpage_id(subpage_id);
            subpagesList.add(subpage);
        }
        return subpagesList;
    }
}
