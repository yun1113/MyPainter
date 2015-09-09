package com.example.painter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import db_connect.DBConnector;

public class LogInActivity extends ActionBarActivity {
    EditText accountEdt;
    SessionManager session;

    private final mHandler myHandler = new mHandler(this);

    private static class mHandler extends Handler {
        private final WeakReference<LogInActivity> mActivity;

        public mHandler(LogInActivity activity) {
            mActivity = new WeakReference<LogInActivity>(activity);
        }

        public void handleMessage(Message msg) {

            final LogInActivity activity = mActivity.get();
            AlertDialog.Builder dialog = new AlertDialog.Builder(activity);

            switch (msg.what) {
//                case 0x0001:
//                    Intent intent = new Intent();
//                    intent.setClass(activity, Canvas.class);
//                    activity.startActivity(intent);
//                    break;

                case 0x0002:
                    dialog.setMessage(activity.getResources().getString(R.string.wrong_password)).setPositiveButton(activity.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            Intent intent = new Intent();
                            intent.setClass(activity, LogInActivity.class);
                            activity.startActivity(intent);
                        }
                    });
                    dialog.show();
                    break;

                case 0x0003:
                    dialog.setMessage(activity.getResources().getString(R.string.account_doesnt_exist)).setPositiveButton(activity.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            Intent intent = new Intent();
                            intent.setClass(activity, LogInActivity.class);
                            activity.startActivity(intent);
                        }
                    });
                    dialog.show();
                    break;
                case 0x0004:
                    dialog.setMessage("fail").setPositiveButton(activity.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            Intent intent = new Intent();
                            intent.setClass(activity, LogInActivity.class);
                            activity.startActivity(intent);
                        }
                    });
                    dialog.show();
                    break;
            }
        }
    }

    EditText inputemail, inputpassword;
    String result = "";
    private Thread mthread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("CY_Test", "Start");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        Typeface font = Typeface.createFromAsset(LogInActivity.this.getAssets(), "NotoSansCJKtc-Light.otf");

        session = new SessionManager(getApplicationContext());

        inputemail = (EditText) findViewById(R.id.accountEdit);
        inputpassword = (EditText) findViewById(R.id.passwordEdit);
        inputemail.setTypeface(font);
        inputpassword.setTypeface(font);

        Button logInBtn = (Button) findViewById(R.id.logInBtn);
        logInBtn.setTypeface(font);
        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mthread = new Thread(runnable);
                mthread.start();

            }
        });

        TextView signUpBtn = (TextView) findViewById(R.id.signUpBtn);
        signUpBtn.setTypeface(font);
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setClass(LogInActivity.this, Sign_up.class);
                startActivity(intent);
            }
        });

    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            String email = inputemail.getText().toString();
            String password = inputpassword.getText().toString();
            InputStream is = null;

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("email", email));
            nameValuePairs.add(new BasicNameValuePair("password", password));

            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://140.115.87.44/android_connect/login.php");
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity entity = response.getEntity();
                is = entity.getContent();

                BufferedReader bufReader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
                StringBuilder builder = new StringBuilder();
                String line = null;
                while ((line = bufReader.readLine()) != null) {
                    builder.append(line + "\n");
                }
                is.close();
                result = builder.toString();
                Log.d("CY_Test",result);
                try {
                    JSONObject jObj = new JSONObject(result);
                    int LOGIN_RESULT = jObj.getInt("response");
                    Message msg = new Message();
                    if (LOGIN_RESULT == 1) {
                        //登入成功
                        DBConnector dbConnector = new DBConnector("connect1.php");
                        String data = dbConnector.executeQuery(String.format("SELECT * FROM user_list WHERE user_email= '%s' ", email));
                        Log.d("Test", data);

                        String user_name = new JSONArray(data).getJSONObject(0).getString("user_name");
                        String user_galleryPublic = new JSONArray(data).getJSONObject(0).getString("gallery_public");
                        Log.d("Data", user_name);
                        Log.d("Data", email);
                        Log.d("Data", password);;
                        Log.d("Data", user_galleryPublic);
                        session.createLoginSession(email, password, user_name, user_galleryPublic);

                        // canvas
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle(); //建立一個bundle實體，將intent裡的所有資訊放在裡面

                        bundle.putString("account", inputemail.getText().toString());
                        intent.putExtras(bundle); //透過這我們將bundle附在intent上，隨著intent送出而送出

                        bundle.putInt("state", 1); // test

                        intent.setClass(LogInActivity.this, FriendTest.class);
                        startActivity(intent);

                        msg.what = 0x0001;
                        myHandler.sendMessage(msg);
                    } else if (LOGIN_RESULT == 2) {
                        msg.what = 0x0002;
                        myHandler.sendMessage(msg);
                    } else if (LOGIN_RESULT == 3) {
                        msg.what = 0x0003;
                        myHandler.sendMessage(msg);
                    } else {
                        msg.what = 0x0004;
                        myHandler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    Log.e("log_tag", e.toString());
                }

            } catch (ClientProtocolException e) {
                Log.e("ClientProtocol", "Log_tag");
                e.printStackTrace();
            } catch (IOException e) {
                Log.e("Log_tag", "IOException");
                e.printStackTrace();
            }
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.log_in, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
