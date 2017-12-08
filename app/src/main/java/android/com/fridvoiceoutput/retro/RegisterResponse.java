package android.com.fridvoiceoutput.retro;

import android.com.fridvoiceoutput.Constant;

import com.google.gson.annotations.SerializedName;

public class RegisterResponse {

    @SerializedName(Constant.MOBILE)
    private String mobile;

    @SerializedName(Constant.ID)
    private int id;

    @SerializedName(Constant.EMAIL)
    private String email;

    @SerializedName(Constant.DESIGENATION)
    private String designation;

    @SerializedName(Constant.USERNAME)
    private String name;

    @SerializedName(Constant.RESULT)
    private String success;

    public String getSuccess() {
        return success;
    }

    public String getMobile() {
        return mobile;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getDesignation() {
        return designation;
    }

    public String getName() {
        return name;
    }
}
