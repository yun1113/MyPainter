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
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import db_connect.DBConnector;
import gallerymaterial.BaseActivity;

public class GalleryDetail extends BaseActivity {

    public static final String EXTRA_IMAGE = "DetailActivity:image";

    public static void launch(FragmentActivity activity, View transitionView, int index, Bundle bundle) {
        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                        activity, transitionView, EXTRA_IMAGE);

        Intent intent = new Intent(activity, GalleryDetail.class);
        intent.putExtra(EXTRA_IMAGE, index);
        intent.putExtra("bundle", bundle);
        Log.d("CY_GID",bundle.getString("gallery_id"));
        intent.putExtra("G_id", bundle.getString("gallery_id"));
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.undo));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
                Back();
            }
        });


        ImageView image = (ImageView) findViewById(R.id.image);
        TextView text = (TextView) findViewById(R.id.text);
        new ImageDownloadTask(image, text, getIntent().getIntExtra(EXTRA_IMAGE, 0), getIntent().getStringExtra("G_id")).execute();
    }

    void Back(){

        Bundle bundle = getIntent().getBundleExtra("bundle");

        Intent intent = new Intent();
        intent.putExtras(bundle);
        intent.setClass(this, Gallery.class);
        startActivity(intent);
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
        TextView text;
        int index;
        String gallery_id;

        // Constructor
        public ImageDownloadTask(ImageView imageView, TextView text, int index, String gallery_id) {
            this.imageView = imageView;
            this.text = text;
            this.index = index;
            this.gallery_id = gallery_id;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

        }

        protected Bitmap doInBackground(String... addresses) {
            Bitmap bitmap = null;
            try {
                DBConnector dbConnector = new DBConnector("connect1.php");
                Log.d("CY_Query",String.format("SELECT * FROM gallerylist where gallery_id = '%s'", gallery_id));
                String result = dbConnector.executeQuery(String.format("SELECT * FROM gallerylist where gallery_id = '%s'", gallery_id));
                Log.d("CY_Result",result);

                JSONArray jsonArray = new JSONArray(result);
                //for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(index);

                byte[] decodedString = Base64.decode(jsonData.getString("image"), Base64.DEFAULT);
                bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                text.setText(jsonData.getString("name"));
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
