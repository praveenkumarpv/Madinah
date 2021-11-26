package helperclass;

public class burthadb {
    String name;
    String date;
    String filelink;

    public burthadb() {
    }

    public burthadb(String name, String date, String filelink) {
        this.name = name;
        this.date = date;
        this.filelink = filelink;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFilelink() {
        return filelink;
    }

    public void setFilelink(String filelink) {
        this.filelink = filelink;
    }
}
