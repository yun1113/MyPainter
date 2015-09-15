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

import com.example.painter.GalleryDetail;
import com.example.painter.R;
import com.example.painter.SessionManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import db_connect.DBConnector;

/**
 * Created by user on 2015/7/13.
 */
public class GCollection_Tab extends Fragment {

    private DrawerLayout drawer;
    Bundle bundle;
    int state;
    String gallery_id;
    SessionManager session;
    HashMap user;


    // list of data items
    List<Bitmap> mDataList = new ArrayList();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.mygallery_tab, container, false);
        //getListData();
        bundle = getArguments();
        state = bundle.getInt("state");

        session = new SessionManager(getActivity().getApplicationContext());
        user = session.getUserDetails();

        if (state == 2)
            gallery_id = bundle.getString("friend_account");
        else
            gallery_id = (String) user.get(SessionManager.KEY_EMAIL);
        Log.d("Gallery_id", gallery_id);


        GridView gridView = (GridView) v.findViewById(R.id.gridView);
        gridView.setAdapter(new GridViewAdapter());
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int index = (int) view.getTag();
                GalleryDetail.launch(getActivity(), view.findViewById(R.id.image), index,bundle);
            }
        });

        drawer = (DrawerLayout) v.findViewById(R.id.drawer);
        drawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        return v;
    }

    private class ImageDownloadTask extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;
        TextView text;
        int index;

        // Constructor
        public ImageDownloadTask(ImageView imageView, TextView text, int index) {
            this.imageView = imageView;
            this.text = text;
            this.index = index;
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
                String result = dbConnector.executeQuery(String.format("SELECT * FROM gallery_list where gallery_id = '%s'", gallery_id));

                JSONArray jsonArray = new JSONArray(result);
                //for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(index);

//                BitmapFactory.Options options = new BitmapFactory.Options();
//                options.inSampleSize = 2;//?����?�׳�?��?���G�����@�A�Y?���j�p?��?���j�p���|�����@
//                options.inTempStorage = new byte[5 * 1024]; //?�m16MB��??�s?��?�]��?�@��??�ݥX?�A��??�^

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

    private class GridViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 2;
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
            TextView text = (TextView) view.findViewById(R.id.text);
            new ImageDownloadTask(image, text, index).execute();
            view.setTag(index);
            return view;
        }
    }
}
