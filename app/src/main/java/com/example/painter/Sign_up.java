package com.example.painter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.StrictMode;
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
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogRecord;


public class Sign_up extends ActionBarActivity {
    private final mHandler myHandler = new mHandler(this);
    private static class mHandler extends Handler{
        private final WeakReference<Sign_up> mActivity;
        public mHandler(Sign_up activity){
            mActivity = new WeakReference<Sign_up>(activity);
        }

        public void handleMessage(Message msg){

            final Sign_up activity = mActivity.get();
            AlertDialog.Builder dialog = new AlertDialog.Builder(activity);

            switch(msg.what){
                case 0x0001:
                   /*String msg_success = "data entered successfully";
                    Toast.makeText(activity.getApplicationContext(),msg_success, Toast.LENGTH_LONG).show();*/
                    dialog.setMessage(activity.getResources().getString(R.string.sign_up_success)).setPositiveButton(activity.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            Intent intent = new Intent();
                            intent.setClass(activity, Canvas.class);
                            activity.startActivity(intent);
                        }
                    });
                    dialog.show();
                    break;

                case 0x0002:
                    /*Toast.makeText(activity.getApplicationContext(), result, Toast.LENGTH_LONG).show();*/
                    dialog.setMessage(activity.getResources().getString(R.string.sign_up_existed)).setPositiveButton(activity.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
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
                  /* Toast.makeText(activity.getApplicationContext(), result, Toast.LENGTH_LONG).show();*/
                    dialog.setMessage(activity.getResources().getString(R.string.sign_up_fail)).setPositiveButton(activity.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            Intent intent = new Intent();
                            intent.setClass(activity, Sign_up.class);
                            activity.startActivity(intent);
                        }
                    });
                    dialog.show();
                    break;
            }
        }
    }

    EditText inputname, inputemail, inputpassword;
    String result = "";
    private Thread mthread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Typeface font = Typeface.createFromAsset(Sign_up.this.getAssets(), "NotoSansCJKtc-Light.otf");
        inputname = (EditText) findViewById(R.id.nickNameEdt);
        inputemail = (EditText) findViewById(R.id.emailEdt);
        inputpassword = (EditText) findViewById(R.id.passwordEdt);
        inputname.setTypeface(font);
        inputemail.setTypeface(font);
        inputpassword.setTypeface(font);

        Button signUpBtn = (Button) findViewById(R.id.signUpBtn);
        signUpBtn.setTypeface(font);
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mthread = new Thread(runnable);
                mthread.start();
            }
        });

        TextView cancelBtn = (TextView) findViewById(R.id.cancelBtn);
        cancelBtn.setTypeface(font);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setClass(Sign_up.this, LogInActivity.class);
                startActivity(intent);
            }
        });
    }


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            String name = inputname.getText().toString();
            String email = inputemail.getText().toString();
            String password = inputpassword.getText().toString();
            InputStream is = null;

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("name", name));
            nameValuePairs.add(new BasicNameValuePair("email", email));
            nameValuePairs.add(new BasicNameValuePair("password", password));

            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://140.115.80.233/android_connect/test123.php");
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
                    int SIGNUP_RESULT = jObj.getInt("response");
                    Message msg = new Message();

                    if(SIGNUP_RESULT == 1){
                        msg.what = 0x0001;
                        myHandler.sendMessage(msg);
                    }else if(SIGNUP_RESULT == 2){
                        msg.what = 0x0002;
                        myHandler.sendMessage(msg);
                    }else{
                        msg.what = 0x0003;
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
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
