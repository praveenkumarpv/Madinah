package helperclass;

public class classdatahelperclass {
    String mastername;
    String episodenumber;
    String audiofilelink;
    String pdffilelink;

    public classdatahelperclass() {
    }

    public classdatahelperclass(String mastername) {
        this.mastername = mastername;
    }

    public classdatahelperclass(String episodenumber, String audiofilelink, String pdffilelink) {
        this.episodenumber = episodenumber;
        this.audiofilelink = audiofilelink;
        this.pdffilelink = pdffilelink;
    }

    public String getMastername() {
        return mastername;
    }

    public void setMastername(String mastername) {
        this.mastername = mastername;
    }

    public String getEpisodenumber() {
        return episodenumber;
    }

    public void setEpisodenumber(String episodenumber) {
        this.episodenumber = episodenumber;
    }

    public String getAudiofilelink() {
        return audiofilelink;
    }

    public void setAudiofilelink(String audiofilelink) {
        this.audiofilelink = audiofilelink;
    }

    public String getPdffilelink() {
        return pdffilelink;
    }

    public void setPdffilelink(String pdffilelink) {
        this.pdffilelink = pdffilelink;
    }
}
