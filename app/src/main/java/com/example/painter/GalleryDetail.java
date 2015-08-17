package com.example.painter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewCompat;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.support.v7.widget.Toolbar;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import db_connect.DBConnector;
import gallerymaterial.BaseActivity;

public class GalleryDetail extends BaseActivity {

    public static final String EXTRA_IMAGE = "DetailActivity:image";

    public static void launch(FragmentActivity activity, View transitionView, int index) {
        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                        activity, transitionView, EXTRA_IMAGE);
        Intent intent = new Intent(activity, GalleryDetail.class);
        intent.putExtra(EXTRA_IMAGE, index);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        //toolbar.setNavigationIcon(R.drawable.ic_launcher);

        ImageView image = (ImageView) findViewById(R.id.image);
        new ImageDownloadTask(image,getIntent().getIntExtra(EXTRA_IMAGE,0)).execute();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_gallery_detail;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.gallery, menu);
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

    private class ImageDownloadTask extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;
        int index ;

        // Constructor
        public ImageDownloadTask(ImageView imageView, int i) {
            this.imageView = imageView;
            index = i;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

        }

        protected Bitmap doInBackground(String... addresses) {
            Bitmap bitmap = null;
            try {
                String result = DBConnector.executeQuery("SELECT * FROM test1");
                Log.d("Test", result);

                JSONArray jsonArray = new JSONArray(result);
                //for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(index);

                byte[] decodedString = Base64.decode(jsonData.getString("image"), Base64.DEFAULT);
                bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                //}

            } catch (Exception e) {
                Log.e("log_tag", e.toString());
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // Set bitmap image for the result
            imageView.setImageBitmap(result);
        }
    }
}
