package pl.parser.dao;


public class Subpage extends Link {

    private int subpage_id;
    private int pageNumber;
    private int parsed;

    public int getSubpage_id() {
        return subpage_id;
    }

    public void setSubpage_id(int subpage_id) {
        this.subpage_id = subpage_id;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getParsed() {
        return parsed;
    }

    public void setParsed(int parsed) {
        this.parsed = parsed;
    }
}
