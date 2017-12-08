package android.com.fridvoiceoutput.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.com.fridvoiceoutput.R;
import android.com.fridvoiceoutput.preferance.Shareprefrance;
import android.com.fridvoiceoutput.retro.RegisterResponse;
import android.com.fridvoiceoutput.retro.Retro;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoginActivity extends AppCompatActivity {

    TextView tv_register;
    EditText et_email, et_password;
    Button login_btn;
    String str_email, str_password;
    Shareprefrance shareprefrance;
    Toolbar toolbar;
    ProgressDialog progressDialog;
    Dialog dialog;
    private Context context = LoginActivity.this;
    private String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.login);
        setSupportActionBar(toolbar);

        login_btn = (Button) findViewById(R.id.login_btn);
        et_email = (EditText) findViewById(R.id.et_email);
        et_password = (EditText) findViewById(R.id.et_password);
        tv_register = (TextView) findViewById(R.id.tv_register);
        et_email.addTextChangedListener(new MyTextWatcher(et_email));
        et_password.addTextChangedListener(new MyTextWatcher(et_password));

        shareprefrance = new Shareprefrance();
        progressDialog = new ProgressDialog(context);

        Log.e(TAG, " Is user login status in Login Class " + shareprefrance.isLOgin(context));
        if (shareprefrance.isLOgin(context)) {
            startActivity(new Intent(context, HomeActivity.class));
            finish();
        }

        //TODO Register Page
        String resister_str = context.getResources().getString(R.string.regisetr);
        String register_msg = context.getResources().getString(R.string.regisetr);
        SpannableString text = new SpannableString(register_msg);
        ClickableSpan clickfor = new ClickableSpan() {

            @Override
            public void onClick(View widget) {
                startActivity(new Intent(context, RegisterActivity.class));
            }
        };

        text.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.black)), (register_msg.length() - resister_str.length()), register_msg.length(), 0);
        text.setSpan(clickfor, (register_msg.length() - resister_str.length()), register_msg.length(), 0);
        tv_register.setText(text);
        tv_register.setMovementMethod(LinkMovementMethod.getInstance());
        tv_register.setText(text, TextView.BufferType.SPANNABLE);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                str_email = et_email.getText().toString();
                str_password = et_password.getText().toString();
                startActivity(new Intent(context, ScaneActivity.class));

                /*if (validateEmail() && validatePassword()) {
                    postOnServer();
                }*/

            }
        });

    }

    private void postOnServer() {
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        Log.e(TAG, " FCM device id as " + shareprefrance.getFCMkey(context));

        Retro.getInterface(context).loginUser(str_email, str_password, new Callback<RegisterResponse>() {
            @Override
            public void success(RegisterResponse registerResponse, Response response) {
                progressDialog.dismiss();

                //TODO Set SharedPreferance to login.
                Log.e(TAG, " Results " + registerResponse.getSuccess() + " user name " + registerResponse.getName());
                if (registerResponse.getSuccess().equals("success")) {
                    shareprefrance.loginUser(context, registerResponse.getName(), registerResponse.getEmail(), registerResponse.getMobile(), registerResponse.getId(), true);
                    startActivity(new Intent(context, HomeActivity.class));
                    finish();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                progressDialog.dismiss();
                Log.e(TAG, " Error as " + error.getMessage());
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_ip, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu_update_IP:
                final EditText ip_address;
                Button update_bttn;
                View view = LayoutInflater.from(context).inflate(R.layout.dialog_change_ip, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Update IP Address")
                        .setView(view);

                update_bttn = (Button) view.findViewById(R.id.update_bttn);
                ip_address = (EditText) view.findViewById(R.id.ip_address);

                shareprefrance = new Shareprefrance();

                String IP_address = shareprefrance.getServerURL(context);
                Log.e(TAG, " Server IP address as follow as " + IP_address);
                ip_address.setText(IP_address);

                dialog = builder.create();
                dialog.show();

                update_bttn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (ip_address.getText().toString().trim().length() != 0) {
                            Log.e(TAG, " UPDATED IP values as " + ip_address.getText().toString());
                            shareprefrance.setServerURL(context, ip_address.getText().toString());
                            dialog.dismiss();
                        }
                    }
                });

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean validateEmail() {
        String email = et_email.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            et_email.setError(getString(R.string.err_msg_email));
            requestFocus(et_email);
            return false;
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean validatePassword() {
        if (et_password.getText().toString().trim().length() < 5) {
            et_password.setError(getString(R.string.err_msg_password));
            requestFocus(et_password);
            return false;
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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
            }
        }
    }

}
