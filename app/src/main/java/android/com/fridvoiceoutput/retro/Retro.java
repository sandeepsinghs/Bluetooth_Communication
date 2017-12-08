package android.com.fridvoiceoutput.retro;

import android.com.fridvoiceoutput.Constant;
import android.com.fridvoiceoutput.preferance.Shareprefrance;
import android.content.Context;
import android.util.Log;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

public class Retro {

    public static String BASE_URL = "http://localhost:8080/RTOandFine/rest/AppService/Login";

    public static RestAdapter getClient(Context context) {

        Shareprefrance shareprefrance = new Shareprefrance();
        BASE_URL = "http://" + shareprefrance.getServerURL(context) + "/RTOandFine/rest/AppService/";

        Log.e("Retro", " BASE URL AS " + BASE_URL);

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(BASE_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        return adapter;
    }

    private static final String Loginuser = "Login";
    private static final String Registration = "Registration";
    private static final String authenticateUser = "authenticateUser";
    private static final String getPoliceReport = "getPoliceReport";
    private static final String forgotPassword = "forgotPassword";
    private static final String getFinger = "getFinger";
    private static final String updatePassword = "updatePassword";
    private static final String getFine = "getFine";
    private static final String getVehicleLicense = "getVehicleLicense";
    private static final String getRule = "getRule";

    public static Retrointerface getInterface(Context context) {
        return getClient(context).create(Retrointerface.class);
    }

    public interface Retrointerface {

        @FormUrlEncoded
        @POST("/" + Loginuser)
        public void loginUser(
                @Field(Constant.EMAILID) String emailId,
                @Field(Constant.PASSWORD) String password,
                Callback<RegisterResponse> response);


    }
}
