package com.example.painter;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import db_connect.DBConnector;


/**
 * Created by hp1 on 21-01-2015.
 */
public class frame2 extends Activity {


    Bundle bundle;
    GridView gridView;
    int count;

    // list of data items
    List<ListData> mDataList = new ArrayList();
    GridViewAdapter gridViewAdapter = new GridViewAdapter(mDataList);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame);

        gridView = (GridView) findViewById(R.id.mygridview);
        //getListData();

        getGCount();

        gridView.setAdapter(gridViewAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                int index = position;
            }
        });
    }

    void getGCount() {
        Thread thread = new Thread(mutiThread);
        thread.start();
    }

    // list of data items
    private Runnable mutiThread = new Runnable() {
        public void run() {
            try {
                DBConnector dbConnector = new DBConnector("connect1.php");
                String result = dbConnector.executeQuery("SELECT * FROM coloring_sheet_list ");

                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonData = jsonArray.getJSONObject(i);
                    mDataList.add(new ListData(jsonData.getString("id"), jsonData.getString("image")));
                }
                count = jsonArray.length();
                Message msg = messageHandler.obtainMessage();
                msg.what = 1;
                msg.sendToTarget();

            } catch (Exception e) {
                Log.e("log_tag", e.toString());
            }
        }
    };

    // Update View
    android.os.Handler messageHandler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            gridViewAdapter.refresh(mDataList);
        }
    };


    private static class ListData {

        private String id;
        private String image;

        public ListData(String id, String image) {
            this.id = id;
            this.image = image;
        }
    }

    private static class ViewHolder {

        private View view;
        private ImageView imageView;

        private ViewHolder(View view) {
            this.view = view;
            imageView = (ImageView) view.findViewById(R.id.grid_item_image);

        }
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
                String result = dbConnector.executeQuery("SELECT * FROM coloring_sheet_list ");

                JSONArray jsonArray = new JSONArray(result);
                JSONObject jsonData = jsonArray.getJSONObject(index);

                byte[] decodedString = Base64.decode(jsonData.getString("image"), Base64.DEFAULT);
                bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

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

        private List<ListData> mList;

        public GridViewAdapter(List<ListData> list) {
            mList = list;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public ListData getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        byte[] decodedString;
        Bitmap bitmap;

        @Override
        public View getView(final int index, View view, ViewGroup viewGroup) {
            final ViewHolder holder;

            if (view == null) {
                view = View.inflate(getApplication(), R.layout.frame_item_layout, null);
                holder = new ViewHolder(view);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            final ListData item = getItem(index);

            decodedString = Base64.decode(item.image, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.imageView.setImageBitmap(bitmap);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // when the image is clicked, update the selected state

                    Log.d("HOLDER", "CLICKED" + getItem(index));
                    decodedString = Base64.decode(item.image, Base64.DEFAULT);
                    Intent intnet = new Intent(frame2.this, Canvas.class);
                    intnet.putExtra("Image", decodedString);
                    startActivity(intnet);

                }
            });

            return view;
        }

        public void refresh(List<ListData> list) {
            mList = list;
            notifyDataSetChanged();
        }
    }
}

