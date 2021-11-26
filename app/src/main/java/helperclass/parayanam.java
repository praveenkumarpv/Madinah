package helperclass;

public class parayanam {
    String name;
    String filelink;
    String date;
    String imageindicator;

    public parayanam() {
    }

    public parayanam(String name, String filelink, String date, String imageindicator) {
        this.name = name;
        this.filelink = filelink;
        this.date = date;
        this.imageindicator = imageindicator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilelink() {
        return filelink;
    }

    public void setFilelink(String filelink) {
        this.filelink = filelink;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImageindicator() {
        return imageindicator;
    }

    public void setImageindicator(String imageindicator) {
        this.imageindicator = imageindicator;
    }
}
