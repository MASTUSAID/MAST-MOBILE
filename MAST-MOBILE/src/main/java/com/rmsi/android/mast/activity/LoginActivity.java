package com.rmsi.android.mast.activity;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rmsi.android.mast.activity.R.string;
import com.rmsi.android.mast.db.DbController;
import com.rmsi.android.mast.domain.User;
import com.rmsi.android.mast.util.CommonFunctions;

import static pub.devrel.easypermissions.EasyPermissions.hasPermissions;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends ActionBarActivity {
    Context context = this;
    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    private EditText txtServerAddress;
    private TextView cmdShowServerAddress;
    private TextView cmdHideServerAddress;
    User user = null;
    ContentValues values = new ContentValues();
    List<ContentValues> valueList = new ArrayList<ContentValues>();
    CommonFunctions cf = CommonFunctions.getInstance();
    String error_msg, error_tag;
    ProgressDialog ringProgressDialog;
    String userName, password, roleName;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


 // Creating Shared Prefs and Required Folders
        cf.Initialize(getApplicationContext());
        cf.createLogfolder();
        cf.loadLocale(getApplicationContext());

        setContentView(R.layout.activity_login);



//        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 111);
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.ACCESS_COARSE_LOCATION}, 111);




        // Set up the login form.
        mUsernameView = (EditText) findViewById(R.id.username);
        txtServerAddress = (EditText) findViewById(R.id.txtServerAddress);
        cmdShowServerAddress = (TextView) findViewById(R.id.cmdShowServerAddress);
        cmdHideServerAddress = (TextView) findViewById(R.id.cmdHideServerAddress);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id,
                                          KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });


        Button signInButton = (Button) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        cmdShowServerAddress.setMovementMethod(LinkMovementMethod.getInstance());
        cmdHideServerAddress.setMovementMethod(LinkMovementMethod.getInstance());
        txtServerAddress.setText(cf.getServerAddress());

        if(user != null){
            cmdHideServerAddress.setVisibility(View.GONE);
            cmdShowServerAddress.setVisibility(View.VISIBLE);
            txtServerAddress.setVisibility(View.GONE);
            cmdHideServerAddress.setVisibility(View.GONE);
            cmdShowServerAddress.setVisibility(View.GONE);
            txtServerAddress.setVisibility(View.GONE);
        } else {
            cmdHideServerAddress.setVisibility(View.GONE);
            cmdShowServerAddress.setVisibility(View.VISIBLE);
            txtServerAddress.setVisibility(View.GONE);
        }

        cmdShowServerAddress.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                cmdHideServerAddress.setVisibility(View.VISIBLE);
                cmdShowServerAddress.setVisibility(View.GONE);
                txtServerAddress.setVisibility(View.VISIBLE);
            }
        });

        cmdHideServerAddress.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                cmdHideServerAddress.setVisibility(View.GONE);
                cmdShowServerAddress.setVisibility(View.VISIBLE);
                txtServerAddress.setVisibility(View.GONE);
            }
        });
    }

//    @Override
//    public void onPermissionsGranted(int requestCode) {
//
//    }


    private void attemptLogin() {
        if (user == null) {
            if (TextUtils.isEmpty(txtServerAddress.getText().toString())) {
                cf.showMessage(context, "Warning", "Please enter server address");
                return;
            }

            userName = mUsernameView.getText().toString();
            password = mPasswordView.getText().toString();

            if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) {
                cf.showMessage(context, "Warning", "Please Enter Your Username and Password");
            }

            if (!cf.getServerAddress().equalsIgnoreCase(txtServerAddress.getText().toString())) {
                cf.saveServerAddress(txtServerAddress.getText().toString());
            }

            user = new User();
            user.setUserName(userName);
            user.setPassword(password);
            validateUserOnline(user);
        } else {
            loginAction(true, "");
        }
    }

//    private void getLoggedUserFromDB() {
//        DbController sqllite = DbController.getInstance(context);
//        user = sqllite.getLoggedUser();
//        //########## Setting logged USER
//        if (user != null) {
//            mUsernameView.setText(user.getUserName());
//            mPasswordView.setText(user.getPassword());
//            roleName = user.getRoleName();
//
//            mUsernameView.setEnabled(false);
//            mPasswordView.setEnabled(false);
//        } else {
//            mUsernameView.setText("");
//            mPasswordView.setText("");
//        }
//    }

    private void getLoggedUserFromDB() {
        Log.d("LoggedIn","LoggedIn");
        DbController sqllite = DbController.getInstance(context);

        user = sqllite.getLoggedUser();
        //########## Setting logged USER
        boolean checkUser=sqllite.checkUserExist();
        Log.d("LoggedUser",String.valueOf(checkUser));
        if(checkUser) {
            if (user != null &&
                    user.getUserName() != null) {
                mUsernameView.setText(user.getUserName());
                mPasswordView.setText(user.getPassword());
                roleName = user.getRoleName();

                mUsernameView.setEnabled(false);
                mPasswordView.setEnabled(false);
            }
        }else{

            mUsernameView.setText("");
            mPasswordView.setText("");

        }
    }
    private void validateUserOnline(User newUser) {
        if (cf.getConnectivityStatus()) {
            ringProgressDialog = ProgressDialog.show(new ContextThemeWrapper(context, android.R.style.Theme_Holo),
                    null, getResources().getString(R.string.server_logging_msg), true);
            ringProgressDialog.setCancelable(false);
            ringProgressDialog.setCanceledOnTouchOutside(false);
            new validateUser().execute(newUser);

//        if (userName.equalsIgnoreCase("Live_TI4")&&password.equalsIgnoreCase("testme")){
//            values.put("USER_NAME",newUser.getUserName() );
//            values.put("ROLE_NAME", "ROLE_TRUSTED_INTERMEDIARY");
//            values.put("PASSWORD", newUser.getPassword());
//            values.put("USER_ID", "235");
//            values.put("ROLE_ID", "4");
//            roleName="ROLE_TRUSTED_INTERMEDIARY";
//        valueList.add(values);
//        String tableName = "USER";
//        DbController.getInstance(getApplicationContext()).insertValues(valueList, tableName);
//        loginAction(true, "Success");
        } else {
            cf.showIntenetSettingsAlert(context);
            user = null;
//            error_msg = getResources().getString(string.login_error);
//            loginAction(false, error_msg);
//            user = null;
        }

    }


    private class validateUser extends AsyncTask<User, Integer, String> {
        protected String doInBackground(User... user) {
            String json_string = null;
            InputStream is = null;
            try {
                String requestUrl = cf.getServerAddress() + "/mast/sync/mobile/user/auth";

                HttpURLConnection conn = (HttpURLConnection) new URL(requestUrl).openConnection();
                conn.setReadTimeout(100000 /* milliseconds */);
                conn.setConnectTimeout(150000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                // Starts the query
                conn.connect();

                //Setting parameters
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                String urlParameters = "email=" + userName + "&password=" + password;
                writer.write(urlParameters);
                writer.flush();

                int response = conn.getResponseCode();

                if (response > 1) {
                    is = conn.getInputStream();
                    // Convert the InputStream into a string
                    json_string = CommonFunctions.getStringFromInputStream(is);
                }
            } catch (SocketTimeoutException e1) {

            } catch (Exception e) {
                user = null;
                e.printStackTrace();
            } finally {
                ringProgressDialog.dismiss();
                if (is != null) {
                    try {
                        is.close();
                    } catch (Exception e) {
                    }
                }
            }
            return json_string;
        }

        protected void onPostExecute(String response) {
            JSONObject Obj = null;
            JSONObject childObj = null;
            values = new ContentValues();
            valueList.clear();
            try {
                if (!TextUtils.isEmpty(response)) {
                    Obj = new JSONObject(response);
                    if (Obj.equals(null)) {
                        //cf.addErrorMessage("Json is null", Obj.toString());
                        error_msg = getResources().getString(R.string.login_error_msg);
                        loginAction(false, error_msg);
                    } else if (Obj.has("Error")) {
                        error_msg = getResources().getString(string.login_error_msg);
                        loginAction(false, error_msg);
                    } else {
                        if (Obj.has("id")) {
                            values.put("USER_ID", Obj.get("id").toString());
                        }
                        if (Obj.has("username")) {
                            values.put("USER_NAME", Obj.get("username").toString());
                        }
                        if (Obj.has("password")) {
                            values.put("PASSWORD", Obj.get("password").toString());
                        }


//roles
                        if (Obj.has("userRole")) {
                            JSONArray jsonarrayForRoles = Obj.getJSONArray("userRole");

                            for (int i = 0; i < jsonarrayForRoles.length(); i++) {
                                JSONObject childObj1 = new JSONObject(jsonarrayForRoles.get(0).toString());
                                JSONObject jsonObject=childObj1.getJSONObject("roleBean");
                                roleName = jsonObject.get("name").toString();
                                values.put("ROLE_ID", jsonObject.get("roleid").toString());
                                values.put("ROLE_NAME", jsonObject.get("name").toString());
                            }
                        }
                        valueList.add(values);
                        String tableName = "USER";
                        DbController.getInstance(getApplicationContext()).insertValues(valueList, tableName);
                        loginAction(true, "Success");
                    }
                } else {
                    error_msg = getResources().getString(string.login_error);
                    loginAction(false, error_msg);
                    user = null;
                }
            } catch (JSONException e) {
                user = null;
                ringProgressDialog.dismiss();
                error_msg = getResources().getString(string.login_error);
                loginAction(false, error_msg);
                e.printStackTrace();
            }
        }
    }

    private void loginAction(boolean loginSuccess, String msg) {
        if (ringProgressDialog != null)
            ringProgressDialog.dismiss();

        if (loginSuccess) {
            if (roleName.equals("ROLE_TRUSTED_INTERMEDIARY")) {
                CommonFunctions.setRoleID(User.ROLE_TRUSTED_INTERMEDIARY);
            } else if (roleName.equals("ROLE_ADJUDICATOR")) {
                CommonFunctions.setRoleID(User.ROLE_ADJUDICATOR);
            }else if (roleName.equals("ROLE_ADMIN")) {
                CommonFunctions.setRoleID(User.ROLE_TRUSTED_INTERMEDIARY);
            }

            Intent intentlogin = new Intent(getApplicationContext(), LandingPageActivity.class);
            startActivity(intentlogin);
        } else {
            cf.showMessage(context, "Login Failed", msg);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
        //resume tasks needing this permission
            //Fetching Logged In USER
            getLoggedUserFromDB();
        }
    }
}
