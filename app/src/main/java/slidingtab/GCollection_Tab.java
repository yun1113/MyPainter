package slidingtab;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
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
import android.widget.ImageButton;
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
public class GCollection_Tab extends Fragment {


    private DrawerLayout drawer;
    Bundle bundle;
    int state;
    String gallery_id;
    SessionManager session;
    HashMap user;
    GridView gridView;
    int count;

    // list of data items
    List<ListData> mDataList = new ArrayList();
    GridViewAdapter gridViewAdapter = new GridViewAdapter(mDataList);

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.mygallery_tab, container, false);
        gridView = (GridView) v.findViewById(R.id.gridView);
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
        bundle.putString("gallery_id", gallery_id);

        getGCount();

        gridView.setAdapter(gridViewAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                int index = position;
                GalleryDetail.launch(getActivity(), view.findViewById(R.id.image), index, bundle,1);
            }
        });

        drawer = (DrawerLayout) v.findViewById(R.id.drawer);
        drawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        return v;
    }

    void getGCount() {
        String user_id = (String) user.get(SessionManager.KEY_EMAIL);

        if (user_id.equals(gallery_id)) {
            Thread thread = new Thread(userThread);
            thread.start();
        } else {
            Thread thread = new Thread(friendThread);
            thread.start();
        }
    }

    // list of data items
    private Runnable userThread = new Runnable() {
        public void run() {
            try {
                DBConnector dbConnector = new DBConnector("connect1.php");
                String result = dbConnector.executeQuery(String.format("SELECT * FROM gallery_c_list where user_id = '%s'", gallery_id));

                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonData = jsonArray.getJSONObject(i);
                    mDataList.add(new ListData(jsonData.getString("name"), jsonData.getString("image"), false));
                }

                Message msg = messageHandler.obtainMessage();
                msg.what = 1;
                msg.sendToTarget();

            } catch (Exception e) {
                Log.e("log_tag", e.toString());
            }
        }
    };

    private Runnable friendThread = new Runnable() {
        public void run() {
            try {
                DBConnector checkConnector = new DBConnector("connect.php");
                String userCollectCount = checkConnector.executeQuery(String.format("SELECT * FROM gallery_c_list where user_id = '%s' and friend_id = '%s'", (String) user.get(SessionManager.KEY_EMAIL), gallery_id));

                DBConnector dbConnector = new DBConnector("connect1.php");
                String friendGallery = dbConnector.executeQuery(String.format("SELECT * FROM gallerylist where gallery_id = '%s'", gallery_id));
                String userCollect = dbConnector.executeQuery(String.format("SELECT * FROM gallery_c_list where user_id = '%s' and friend_id = '%s'", (String) user.get(SessionManager.KEY_EMAIL), gallery_id));

                Log.d("CY_friendGallery", friendGallery);
                Log.d("CY_userCollect", userCollect);

                if (new JSONObject(userCollectCount).getInt("response") == 0) {
                    JSONArray jsonFriendArray = new JSONArray(friendGallery);
                    for (int i = 0; i < jsonFriendArray.length(); i++) {
                        JSONObject jsonFriendData = jsonFriendArray.getJSONObject(i);
                        boolean collect = false;
                        mDataList.add(new ListData(jsonFriendData.getString("name"), jsonFriendData.getString("image"), collect));
                    }
                } else {
                    JSONArray jsonFriendArray = new JSONArray(friendGallery);
                    JSONArray jsonUserArray = new JSONArray(userCollect);

                    for (int i = 0; i < jsonFriendArray.length(); i++) {
                        JSONObject jsonFriendData = jsonFriendArray.getJSONObject(i);
                        boolean collect = false;
                        for (int j = 0; j < jsonUserArray.length(); j++) {
                            JSONObject jsonUserData = jsonFriendArray.getJSONObject(i);
                            if (jsonFriendData.getString("name").equals(jsonUserData.getString("name"))) {
                                collect = true;
                                break;
                            }
                        }
                        mDataList.add(new ListData(jsonFriendData.getString("name"), jsonFriendData.getString("image"), collect));
                    }
                }


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

        private String name;
        private String image;
        private boolean isCollect;


        public ListData(String name, String image, boolean isCollect) {
            this.name = name;
            this.image = image;
            this.isCollect = isCollect;
        }

        public void setChecked(boolean isCollect) {
            this.isCollect = isCollect;
        }
    }

    private static class ViewHolder {

        private View view;
        private ImageView imageView;
        private TextView textView;
        private ImageView notcollectIcon;
        private ImageView collectIcon;

        private ViewHolder(View view) {
            this.view = view;
            imageView = (ImageView) view.findViewById(R.id.image);
            notcollectIcon = (ImageView) view.findViewById(R.id.notCollect);
            collectIcon = (ImageView) view.findViewById(R.id.collect);
            textView = (TextView) view.findViewById(R.id.text);
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
                String result = dbConnector.executeQuery(String.format("SELECT * FROM gallerylist where gallery_id = '%s'", gallery_id));

                JSONArray jsonArray = new JSONArray(result);
                JSONObject jsonData = jsonArray.getJSONObject(index);

                byte[] decodedString = Base64.decode(jsonData.getString("image"), Base64.DEFAULT);
                bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                text.setText(jsonData.getString("name"));

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

        @Override
        public View getView(final int index, View view, ViewGroup viewGroup) {
            final ViewHolder holder;

            if (view == null) {
                view = View.inflate(getActivity(), R.layout.gallery_item, null);
                holder = new ViewHolder(view);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            final ListData item = getItem(index);

            byte[] decodedString = Base64.decode(item.image, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.imageView.setImageBitmap(bitmap);

            updateCheckedState(holder, item);
            holder.notcollectIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // when the image is clicked, update the selected state
                    ListData data = getItem(index);
                    data.setChecked(!data.isCollect);
                    updateCheckedState(holder, data);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                String user_id = (String) user.get(SessionManager.KEY_EMAIL);
                                if (!user_id.equals(gallery_id)) {
                                    DBConnector dbConnector = new DBConnector("insert_db.php");
                                    String result = dbConnector.executeQuery(String.format("INSERT INTO `gallery_c_list`(`user_id`,`friend_id`, `name`, `image`) " +
                                            "VALUES ('%s','%s','%s','%s')", user_id, gallery_id, item.name, item.image));
                                    Log.d("Query_Result", result);
                                } else {
                                    Log.d("cy","insert out");
                                }
                            } catch (Exception e) {
                                Log.e("log_tag", e.toString());
                            }
                        }
                    }).start();
                }
            });
            holder.collectIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // when the image is clicked, update the selected state
                    ListData data = getItem(index);
                    data.setChecked(!data.isCollect);
                    updateCheckedState(holder, data);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String user_id = (String) user.get(SessionManager.KEY_EMAIL);
                                if (!user_id.equals(gallery_id)) {
                                    DBConnector dbConnector = new DBConnector("insert_db.php");
                                    String result = dbConnector.executeQuery(String.format("DELETE FROM `gallery_c_list` WHERE name='%s' and friend_id='%s'", item.name, gallery_id));
                                    Log.d("Query_Result", result);
                                } else {
                                }
                            } catch (Exception e) {
                                Log.e("log_tag", e.toString());
                            }
                        }
                    }).start();
                }
            });
            holder.textView.setText(item.name);

//
//            ImageView image = (ImageView) view.findViewById(R.id.image);
//            final TextView text = (TextView) view.findViewById(R.id.text);
//            ImageView notCollect = (ImageView) view.findViewById(R.id.notCollect);
//
//            notCollect.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Log.d("CY_Click", "here");
////                            DBConnector dbConnector = new DBConnector("insert_db.php");
////                            String result = dbConnector.executeQuery(String.format("INSERT INTO `gallery_collect_list`(`gallery_id`, `name`) " +
////                                    "VALUES ('%s','%s')", (String) user.get(SessionManager.KEY_EMAIL), text.getText()));
////                            Log.d("Query_Result", result);
//                        }
//                    }).start();
//                }
//            });
//
//            new ImageDownloadTask(image, text, index).execute();
//            view.setTag(index);
            return view;
        }

        private void updateCheckedState(ViewHolder holder, ListData item) {
            if (item.isCollect) {
                holder.view.setBackgroundColor(0x999be6ff);
                holder.notcollectIcon.setVisibility(View.GONE);
                holder.collectIcon.setVisibility(View.VISIBLE);
            } else {
                holder.view.setBackgroundColor(Color.TRANSPARENT);
                holder.notcollectIcon.setVisibility(View.VISIBLE);
                holder.collectIcon.setVisibility(View.GONE);
            }
        }

        public void refresh(List<ListData> list) {
            mList = list;
            notifyDataSetChanged();
        }
    }
}
