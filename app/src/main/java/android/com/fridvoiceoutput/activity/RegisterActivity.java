package android.com.fridvoiceoutput.activity;

import android.app.ProgressDialog;
import android.com.fridvoiceoutput.R;
import android.com.fridvoiceoutput.retro.RegisterResponse;
import android.com.fridvoiceoutput.retro.Retro;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RegisterActivity extends AppCompatActivity {

    private Context context = RegisterActivity.this;
    private String TAG = RegisterActivity.class.getSimpleName();
     EditText et_name, et_email, et_password, et_mobile, et_address;
    Button register_btn;
    String str_name, str_email, str_password, str_mobile, str_address,str_user_type = "";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        register_btn = (Button) findViewById(R.id.register_btn);
        et_name = (EditText) findViewById(R.id.et_name);
        et_email = (EditText) findViewById(R.id.et_email);
        et_password = (EditText) findViewById(R.id.et_password);
        et_mobile = (EditText) findViewById(R.id.et_mobile);
        et_address = (EditText) findViewById(R.id.et_address);

        et_name.addTextChangedListener(new MyTextWatcher(et_name));
        et_password.addTextChangedListener(new MyTextWatcher(et_password));
        et_email.addTextChangedListener(new MyTextWatcher(et_email));
        et_mobile.addTextChangedListener(new MyTextWatcher(et_password));

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateEmail() && validatePhone() && validateName() && validatePassword()) {

                    str_name = et_name.getText().toString();
                    str_email = et_email.getText().toString();
                    str_password = et_password.getText().toString();
                    str_mobile = et_mobile.getText().toString();
                    str_address = et_address.getText().toString();
//                    str_user_type = user_location.getSelectedItem().toString();
                    Log.e(TAG," Selected user type values is as "+str_user_type);

                    registerOnServer();
                }
            }
        });


    }

    private void registerOnServer(){
        progressDialog.show();
        /*Retro.getInterface(context).register(str_name, str_email, str_mobile, str_password, str_address, new Callback<RegisterResponse>() {
            @Override
            public void success(RegisterResponse registerResponse, Response response) {
                progressDialog.dismiss();
                if (registerResponse.getSuccess().equals("success"))
                    finish();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG," Error as "+error.getMessage());
                progressDialog.dismiss();
            }
        });*/
    }

    private boolean validateEmail() {
        String email = et_email.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            et_email.setError(getString(R.string.err_msg_email));
            requestFocus(et_email);
            return false;
        } else {
            et_email.setError(null);
        }

        return true;

    }

    private boolean validateName() {

        if (et_name.getText().toString().trim().length() == 0) {
            et_name.setError(getString(R.string.err_msg_name));
            requestFocus(et_name);
            return false;
        } else {
            et_name.setError(null);
        }

        return true;
    }

    private boolean validatePhone() {

        String email = et_mobile.getText().toString().trim();

        if (email.isEmpty()) {
            et_mobile.setError(getString(R.string.err_msg_mobile));
            requestFocus(et_mobile);
            return false;
        } else {
            et_mobile.setError(null);
        }

        return true;
    }

    private boolean validatePassword() {
        if (et_password.getText().toString().trim().isEmpty()) {
            et_password.setError(getString(R.string.err_msg_password));
            requestFocus(et_password);
            return false;
        } else {
            et_password.setError(null);
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.et_email:
                    validateEmail();
                    break;
                case R.id.et_password:
                    validatePassword();
                    break;
                case R.id.et_name:
                    validateName();
                    break;
                case R.id.et_mobile:
                    validatePhone();
                    break;
            }
        }
    }

}
