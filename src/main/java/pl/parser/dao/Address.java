package pl.parser.dao;


import org.apache.commons.lang3.StringEscapeUtils;
import pl.parser.utils.Database;

import java.sql.SQLException;

public class Address {

    private String city;
    private String province;
    private String street;
    private String postalCode;

    public void insertIntoDatabase(Database database, int companyId) throws SQLException {

        String param = companyId + ", '" + city + "', '" + street + "', '', '" + postalCode + "', '" + province + "'";

        String sql = "CALL insert_address(" + param + ")";
        database.createStatement(sql);
    }


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = StringEscapeUtils.escapeJava(city);
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = StringEscapeUtils.escapeJava(province);
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = StringEscapeUtils.escapeJava(street);
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = StringEscapeUtils.escapeJava(postalCode);
    }
}
