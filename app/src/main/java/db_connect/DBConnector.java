package db_connect;

import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class DBConnector {

    static String connectPHP;

    public DBConnector(String connectPHP){
        this.connectPHP = connectPHP;
    }

    public static String executeQuery(String query_string) {
        String result = "";
        try {
            // ¨Ï¥Îªº¬OApacheªº¥\¯à¡A­º¥ý«Ø¥ß¤@­ÓHttpClientªº¹êÅé¡A¨Ã¥B¨Ï¥ÎPostªº¤è¦¡
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(String.format("http://140.115.87.44/android_connect/%s",connectPHP));

            //±N¤§«á­nPOSTªº°Ñ¼Æ¦W©M°Ñ¼Æ­È©ñ¤J®e¾¹¤¤
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("query_string", query_string));

            //¹ê»Ú°e¥X½Ð¨D¡A¨Ã¨ú±o¶Ç¦^ª¬ºAµ¥¸ê°T¡C
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpResponse httpResponse = httpClient.execute(httpPost);

            //¨ú±o¦¬¨ìªº¤º®e¡C
            HttpEntity httpEntity = httpResponse.getEntity();
            InputStream inputStream = httpEntity.getContent();

            //©Ò¨ú±oªºContent§Q¥ÎStringBuilderÂà´«¬°¦r¦ê

            // ï¿½Ï¥Îªï¿½ï¿½OApacheï¿½ï¿½ï¿½\ï¿½ï¿½Aï¿½ï¿½ï¿½ï¿½ï¿½Ø¥ß¤@ï¿½ï¿½HttpClientï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Aï¿½Ã¥Bï¿½Ï¥ï¿½Postï¿½ï¿½ï¿½è¦¡
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(String.format("http://140.115.87.44/android_connect/%s",connectPHP));

            //ï¿½Nï¿½ï¿½ï¿½ï¿½nPOSTï¿½ï¿½ï¿½Ñ¼Æ¦Wï¿½Mï¿½Ñ¼Æ­È©ï¿½Jï¿½eï¿½ï¿½ï¿½ï¿½
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("query_string", query_string));

            //ï¿½ï¿½Ú°eï¿½Xï¿½Ð¨Dï¿½Aï¿½Ã¨ï¿½ï¿½oï¿½Ç¦^ï¿½ï¿½ï¿½Aï¿½ï¿½ï¿½ï¿½Tï¿½C
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpResponse httpResponse = httpClient.execute(httpPost);

            //ï¿½ï¿½ï¿½oï¿½ï¿½ï¿½ìªºï¿½ï¿½ï¿½eï¿½C
            HttpEntity httpEntity = httpResponse.getEntity();
            InputStream inputStream = httpEntity.getContent();

            //ï¿½Ò¨ï¿½ï¿½oï¿½ï¿½Contentï¿½Qï¿½ï¿½StringBuilderï¿½à´«ï¿½ï¿½ï¿½rï¿½ï¿½
            BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = bufReader.readLine()) != null) {
                builder.append(line + "\n");
            }
            inputStream.close();
            result = builder.toString();
        } catch (Exception e) {
            Log.e("log_tag1", e.toString());
        }
        return result;
    }
}
