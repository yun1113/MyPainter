package com.example.painter;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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
import slidingtab.Gallery_Tab;

public class LogInActivity extends ActionBarActivity {
    EditText accountEdt;
    SessionManager session;
    EditText inputemail, inputpassword;
    String result = "";
    private Thread mthread;

    private final mHandler myHandler = new mHandler(this);
    private static class mHandler extends Handler {
        private final WeakReference<LogInActivity> mActivity;
        public mHandler(LogInActivity activity){
            mActivity = new WeakReference<LogInActivity>(activity);
        }

        public void handleMessage(Message msg){

            final LogInActivity activity = mActivity.get();
            AlertDialog.Builder dialog = new AlertDialog.Builder(activity);

            switch(msg.what){
                case 0x0001:
                    Intent intent = new Intent();

                    Bundle bundle = new Bundle();
                    bundle.putInt("state",1);
                    intent.putExtras(bundle);

                    intent.setClass(activity, Canvas.class);
                    activity.startActivity(intent);
                    break;
                case 0x0002:
                    dialog.setMessage(activity.getResources().getString(R.string.wrong_password)).setPositiveButton(activity.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                        }
                    });
                    dialog.show();
                    break;

                case 0x0003:
                    dialog.setMessage(activity.getResources().getString(R.string.account_doesnt_exist)).setPositiveButton(activity.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                        }
                    });
                    dialog.show();
                    break;
                case 0x0004:
                    dialog.setMessage("fail").setPositiveButton(activity.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                        }
                    });
                    dialog.show();
                    break;
                case 0x0005:
                    dialog.setMessage("please enter email and password").setPositiveButton(activity.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                        }
                    });
                    dialog.show();
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        Typeface font = Typeface.createFromAsset(LogInActivity.this.getAssets(), "NotoSansCJKtc-Light.otf");
        inputemail = (EditText) findViewById(R.id.accountEdit);
        inputpassword = (EditText) findViewById(R.id.passwordEdit);
        inputemail.setTypeface(font);
        inputpassword.setTypeface(font);

        session = new SessionManager(getApplicationContext());

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
    String email;

    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            email = inputemail.getText().toString();
            String password = inputpassword.getText().toString();
            String name = "";
            String galleryID = "";
            String galleryPublic = "";
            String friendListID="";
            InputStream is = null;
            Message msg = new Message();

            if(email.trim().length() > 0 && password.trim().length() > 0){
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
                    while((line = bufReader.readLine()) != null) {
                        builder.append(line + "\n");
                    }
                    is.close();
                    result = builder.toString();

                    try{
                        JSONObject jObj = new JSONObject(result);
                        int LOGIN_RESULT = jObj.getInt("response");

                        if(LOGIN_RESULT == 1){
                            //�n�J���\
                            DBConnector dbConnector = new DBConnector("connect1.php");
                            String data = dbConnector.executeQuery(String.format("SELECT * FROM user_list WHERE user_email= '%s' ", email));
                            Log.d("Test", data);

                            String user_name = new JSONArray(data).getJSONObject(0).getString("user_name");
                            String user_galleryPublic = new JSONArray(data).getJSONObject(0).getString("gallery_public");

                            session.createLoginSession(email, password, user_name, user_galleryPublic);
                            dbConnector.executeQuery(String.format("update user_list set user_status = 'on'  where user_email = '%s'", email));
                            dbConnector.executeQuery(String.format("update friend_list set friend_status = 'on'  where friend_id = '%s'",  email));
                            msg.what = 0x0001;
                            myHandler.sendMessage(msg);
                        }else if(LOGIN_RESULT == 2){
                            //�K�X���~
                            msg.what = 0x0002;
                            myHandler.sendMessage(msg);
                        }else if(LOGIN_RESULT == 3){
                            //�b�����s�b
                            msg.what = 0x0003;
                            myHandler.sendMessage(msg);
                        }else{
                            msg.what = 0x0004;
                            myHandler.sendMessage(msg);
                        }
                    }catch (Exception e){
                        Log.e("log_tag", e.toString());
                    }

                } catch (ClientProtocolException e) {
                    Log.e("ClientProtocol", "Log_tag");
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.e("Log_tag", "IOException");
                    e.printStackTrace();
                }


            }else{
                msg.what=0x0005;
                myHandler.sendMessage(msg);
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
