package pl.parser.dao;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
Podstawowa klasa reprezentująca link
 */
public class Link {

    private String link;
    private String k;
    private String l;
    private int subpagesCount = 0;

    public Link(String link)  {
        this.link = link;
        //Pobieram liczbę stron
        this.getPageCunt();
    }

    public Link() {
    }

    private void getPageCunt() {
        try {
            Document doc = Jsoup.connect(this.link).get();
            Elements lastPage = doc.select(".addax-cs_hl_lastpage");

            if(lastPage.size() == 1) {
                Element a = lastPage.get(0);
                String aLink = a.attr("href");
                Pattern pattern = Pattern.compile(",(\\d+).html");
                Matcher matcher = pattern.matcher(aLink);

                if(matcher.find()) {
                    this.subpagesCount = Integer.parseInt(matcher.group(1));
                }
            }

        } catch (IOException e) {
            System.out.println("Nie mogę pobrać liczby stron dla linku: " + this.link + "\n ustawiam 1");
        }
    }

    public String getLinkParsed(int pageNumber) {
        String url = "http://panoramafirm.pl/" + this.k + "/" + this.l + "/firmy";
        if(this.subpagesCount > 0 && pageNumber <= this.subpagesCount && pageNumber > 0) {
            url += "," + pageNumber;
        }
        url += ".html";
        return url;
    }

    public boolean commit() {
        return k != null && l != null && link != null;
    }

    public String getK() {
        return k;
    }

    public void setK(String k) {
        this.k = k;
    }

    public String getL() {
        return l;
    }

    public void setL(String l) {
        this.l = l;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getSubpagesCount() {
        return subpagesCount;
    }

    public void setSubpagesCount(int subpagesCount) {
        this.subpagesCount = subpagesCount;
    }
}
