package helperclass;

import com.m24.madinah.Adminpanal;

import java.util.ArrayList;
import java.util.List;

public class adupload {
   public List<Ad> adList;

    private adupload() {
    }

    public adupload(List<Ad> adList) {
        this.adList = adList;
    }


    public List<Ad> getAdList() {
        return adList;
    }

    public void setAdList(List<Ad> adList) {
        this.adList = adList;
    }
}
