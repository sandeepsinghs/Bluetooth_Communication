package android.com.fridvoiceoutput.retro;

import android.com.fridvoiceoutput.Constant;

import com.google.gson.annotations.SerializedName;


public class GenralResponse {

    @SerializedName(Constant.RESULT)
    private String success;


    public String getSuccess() {
        return success;
    }


}
