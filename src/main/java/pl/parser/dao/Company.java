package pl.parser.dao;


import org.apache.commons.lang3.StringEscapeUtils;
import pl.parser.utils.Database;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Company {

    private int subpage_id;
    private String name;
    private String company_website;
    private String pf_website;
    private String address;
    private String phone_number;
    private String email;

    public boolean insertIntoDatabase(Database dbConnection) {

        String sql = "SELECT * FROM company WHERE pf_website = \"" + this.pf_website + "\"";
        try {
            ResultSet result = dbConnection.selectStatement(sql);
            if (result.next()) {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        sql = "INSERT INTO company (subpage_id, " +
                "name, " +
                "company_website, " +
                "pf_website, " +
                "phone_number, " +
                "email) VALUES (" +
                subpage_id + ", \"" +
                name + "\", \"" +
                company_website + "\", \"" +
                pf_website + "\", \"" +
                phone_number + "\", \"" +
                email + "\")";
        try {
            dbConnection.createStatement(sql);
        } catch (SQLException e) {
            System.out.println("Problem z wrzucaniem firmy do bazy danych.");
        }

        return true;
    }

    public int getSubpage_id() {
        return subpage_id;
    }

    public void setSubpage_id(int subpage_id) {
        this.subpage_id = subpage_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = StringEscapeUtils.escapeJava(name);
    }

    public String getCompany_website() {
        return company_website;
    }

    public void setCompany_website(String company_website) {
        this.company_website = company_website;
    }

    public String getPf_website() {
        return pf_website;
    }

    public void setPf_website(String pf_website) {
        this.pf_website = pf_website;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = StringEscapeUtils.escapeJava(phone_number);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = StringEscapeUtils.escapeJava(email);
    }
}
