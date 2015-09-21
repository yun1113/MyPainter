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
            // �ϥΪ��OApache���\��A�����إߤ@��HttpClient������A�åB�ϥ�Post���覡
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(String.format("http://140.115.87.44/android_connect/%s",connectPHP));

            //�N����nPOST���ѼƦW�M�Ѽƭȩ�J�e����
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("query_string", query_string));

            //��ڰe�X�ШD�A�è��o�Ǧ^���A����T�C
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpResponse httpResponse = httpClient.execute(httpPost);

            //���o���쪺���e�C
            HttpEntity httpEntity = httpResponse.getEntity();
            InputStream inputStream = httpEntity.getContent();

            //�Ҩ��o��Content�Q��StringBuilder�ഫ���r��
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
