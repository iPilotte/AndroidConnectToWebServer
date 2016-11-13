package kku03.chai.waya.androidconnecttowebserver;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private EditText userEditText, passEditText;
    private String userString, passString;
    private Button loginButton, registerButton;
    private MyConstant myConstant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userEditText = (EditText) findViewById(R.id.editText);
        passEditText = (EditText) findViewById(R.id.editText2);
        loginButton = (Button) findViewById(R.id.button);
        registerButton = (Button) findViewById(R.id.button2);
        myConstant = new MyConstant();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Get Value
                userString = userEditText.getText().toString().trim();
                passString = passEditText.getText().toString().trim();

                if (userString.equals("") || passString.equals("")) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_space), Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    LoginUser loginUser = new LoginUser(MainActivity.this);
                    loginUser.execute(myConstant.getUrlGetJSON());
                }


            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get Value
                userString = userEditText.getText().toString().trim();
                passString = passEditText.getText().toString().trim();

                if (userString.equals("") || passString.equals("")) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_space), Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    RegistUser registUser = new RegistUser(MainActivity.this);
                    registUser.execute(myConstant.getUrlAddUser());
                }

            }
        });

    }   //Method

    private class LoginUser extends AsyncTask<String, Void, String>{
        private Context context;
        private String[] userStrings, passStrings;
        private String truePassword;
        private Boolean aBoolean = true;

        private LoginUser(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                OkHttpClient okHttpClient = new OkHttpClient();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(params[0]).build();
                Response response = okHttpClient.newCall(request).execute();
                return response.body().string();

            } catch (Exception ex) {
                Log.d("Error : ", "LoginUser //doInBack : " + ex.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("null")) {
                Toast.makeText(context, "User False", Toast.LENGTH_SHORT).show();
                Log.d("Error : ", "No User In DB");
            } else {
                try {
                    JSONArray jsonArray = new JSONArray(s);
                    userStrings = new String[jsonArray.length()];
                    passStrings = new String[jsonArray.length()];

                    for(int i=0; i<jsonArray.length();i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        userStrings[i] = jsonObject.getString("Username");
                        passStrings[i] = jsonObject.getString("Password");

                        //Check user
                        if (userString.equals(jsonObject.getString("Username"))) {
                            aBoolean = false;
                            truePassword = jsonObject.getString("Password");
                        }
                    }
                    if (aBoolean) {
                        //User False
                        Toast.makeText(context, "Cannot Find User", Toast.LENGTH_SHORT).show();
                        Log.d("Error : ", "User False");
                    } else if (passString.equals(truePassword)) {
                        //Password true
                        Toast.makeText(context, "Login Complete", Toast.LENGTH_SHORT).show();
                        Log.d("Error : ", "Password true");
                    } else {
                        //PasswordFail
                        Toast.makeText(context, "Wrong Password", Toast.LENGTH_SHORT).show();
                        Log.d("Error : ", "PasswordFail");
                    }   //If



                } catch (Exception ex) {
                    Log.d("Error : ", "LoginUser //onPost : " + ex.toString());
                }
            }
        }
    }

    private class RegistUser extends AsyncTask<String, Void, String> {

        private Context context;
        private String addResString = "False";

        private RegistUser(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = new FormEncodingBuilder().add("isAdd", "true").add("Username", userString)
                        .add("Password", passString).build();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(params[0]).post(requestBody).build();
                Response response = okHttpClient.newCall(request).execute();
                Log.d("Error : ", "RegistUser // doInBack try : " + userString + " : " + passString + " : " + params[0]);

                addResString = response.body().string();
                Log.d("Error : ", "RegistUser // doInBack try : " + addResString);
                return response.body().string();

            } catch (Exception ex) {
                Log.d("Error : ", "RegistUser // doInBack : " + ex.toString());
            }
            return null;
        } // doInBack

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (Boolean.parseBoolean(addResString)) {
                Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                Log.d("Error : ", "Regist / onPost True : " + s);
            } else {
                Toast.makeText(context, "Fail" , Toast.LENGTH_SHORT).show();
                Log.d("Error : ", "Regist / onPost Fail : " + s);
            }
        }   //onPost
    }   //RegistUser

}   //MainActivity class
