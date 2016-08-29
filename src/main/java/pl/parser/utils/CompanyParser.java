package pl.parser.utils;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pl.parser.Main;
import pl.parser.dao.Address;
import pl.parser.dao.Company;
import pl.parser.dao.Subpage;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CompanyParser {

    private List<Company> companyList;

    public CompanyParser() {}

    public CompanyParser parseSubpages(List<Subpage> subpages) throws IOException, InterruptedException, SQLException {
        this.companyList = new ArrayList<Company>();
        if (Main.SHOW_STEPS) {
            if (subpages.size() > 0)
                System.out.println("- parsuje firmy z kolejnych podstron podanych w pliku linków.");
            else
                System.out.println("- brak firm do sparsowania");
        }


        Document document;
        int parsedCompanies = 0;

        for (int j=0; j < subpages.size(); j++) {

            Subpage subpage = subpages.get(j);
            Thread.sleep(Main.SLEEP_TIME);
            String url = subpage.getLinkParsed(subpage.getPageNumber());
            try {
                document = Jsoup.connect(url).get();
            } catch (SocketTimeoutException e) {
                if(Main.SHOW_STEPS) {
                    System.out.println("!- SocketSystemTimeOutException, ponawiam próbe pobrania" +
                            " strony " + url);
                }
                j--;
                continue;
            }
            Elements companyDiv = document.select("#serpContent .card");
            Database database = new Database();

            for (int i = 0; i < companyDiv.size(); i++) {
                Element companyEl = companyDiv.get(i);
                Company company = new Company();

                String name;
                String company_website;
                String pf_website;
                String phone_number;
                String email;

                Element titleEl = this.getFirstElement(companyEl.select(".title h2 a"));
                if (titleEl != null) {
                    name = titleEl.text().trim();
                    pf_website = titleEl.attr("href");
                } else {
                    name = "brak danych";
                    pf_website = "brak danych";
                }

                Element phoneEl = this.getFirstElement(companyEl.select(".contacts a"));
                phone_number = phoneEl != null ? phoneEl.text().trim() : "brak danych";

                Element companyWebsiteEl = this.getFirstElement(companyEl.select(".title .addax-cs_hl_hit_homepagelink_click"));
                company_website = companyWebsiteEl != null ? companyWebsiteEl.attr("href") : "brak danych";

                Element emailEl = this.getFirstElement(companyEl.select(".title .addax-cs_hl_email_submit_click"));
                email = emailEl != null ? emailEl.text().trim() : "brak danych";


                company.setName(name);
                company.setCompany_website(company_website);
                company.setPf_website(pf_website);
                company.setPhone_number(phone_number);
                company.setEmail(email);
                company.setSubpage_id(subpage.getSubpage_id());

                if (company.insertIntoDatabase(database)) parsedCompanies++;

                this.companyList.add(company);
            }


            if (Main.SHOW_STEPS) System.out.println("\t - pobieram firmy ze strony: " + url + " | sparsowano: " + companyDiv.size());

            String sql = "UPDATE subpage SET parsed = 1 WHERE subpage_id = " + subpage.getSubpage_id();
            database.createStatement(sql);
            database.close();
        }

        if (Main.SHOW_STEPS) System.out.println("\tW sumie sparsowano " + parsedCompanies + " firm.");
        return this;
    }

    public void fillAddressFields() throws SQLException, IOException, InterruptedException {

        if (Main.SHOW_STEPS) {
            System.out.println("- pobieram adresy dla sparsowanych firm");
        }

        int index = 0;
        Database database = new Database();
        String sql = "SELECT * FROM company WHERE address_id is NULL";
        ResultSet result = database.selectStatement(sql);

        while (result.next()) {
            Thread.sleep(Main.SLEEP_TIME);
            int company_id = result.getInt("company_id");
            String pf_website = result.getString("pf_website");
            Document document;
            try {
                document = Jsoup.connect(pf_website).get();
            } catch (SocketTimeoutException e) {
                if(Main.SHOW_STEPS) {
                    System.out.println("!- SocketSystemTimeOutException, ponawiam próbe pobrania" +
                            " strony " + pf_website);
                }
                result.previous();
                continue;
            }

            Element element = getFirstElement(this.getFirstElement(document.select(".additionalData div")).select("p:last-child"));

            if(element.childNodes().size() == 5) {

                Address address = new Address();

                String street = element.childNode(0).toString();
                String postalCode_City = element.childNode(2).toString();
                String province = element.childNode(4).toString().replace("woj.", "");
                String postalCode = "";
                String city = "";

                String pattern = "[0-9]{2}-[0-9]{3}";
                Pattern p = Pattern.compile(pattern);

                Matcher matcher = p.matcher(postalCode_City);
                if(matcher.find()) {
                    postalCode = matcher.group(0);
                    city = postalCode_City.replace(postalCode, "");
                }

                address.setProvince(province);
                address.setCity(city);
                address.setPostalCode(postalCode);
                address.setStreet(street);

                address.insertIntoDatabase(database, company_id);

                if (Main.SHOW_STEPS) System.out.println("\t - pobieram adres dla firmy o ID w bazie: " + company_id);
            }
            index++;
        }
        database.close();
        if (Main.SHOW_STEPS) System.out.println("- pobrano " + index + " adresow firm");
    }

    private Element getFirstElement(Elements elements) {
        if (elements != null && elements.size() > 0) {
            return elements.get(0);
        }
        return null;
    }

    private Element getLastElement(Elements elements) {
        if (elements != null) {
            return elements.get(elements.size() - 1);
        }
        return null;
    }
}
