package slidingtab;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.painter.GalleryDetail;
import com.example.painter.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import db_connect.DBConnector;


/**
 * Created by hp1 on 21-01-2015.
 */
public class Gallery_Tab extends Fragment {

    private DrawerLayout drawer;

    // list of data items
    List<Bitmap> mDataList = new ArrayList();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.mygallery_tab, container, false);
        //getListData();

        GridView gridView = (GridView) v.findViewById(R.id.gridView);
        gridView.setAdapter(new GridViewAdapter());
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int index = (int)view.getTag();
                GalleryDetail.launch(getActivity(), view.findViewById(R.id.image), index);
            }
        });

        drawer = (DrawerLayout) v.findViewById(R.id.drawer);
        drawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        return v;
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

    private class GridViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            Log.d("CY","here");
            return 10;
        }

        @Override
        public Object getItem(int i) {
            return "Item " + String.valueOf(i + 1);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int index, View view, ViewGroup viewGroup) {

            if (view == null) {
                view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.gallery_item, viewGroup, false);
            }

            ImageView image = (ImageView) view.findViewById(R.id.image);
            new ImageDownloadTask(image,index).execute();
            view.setTag(index);
            return view;
        }
    }
}
