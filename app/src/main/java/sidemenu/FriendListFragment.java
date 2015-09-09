package sidemenu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.painter.Canvas;
import com.example.painter.FriendTest;
import com.example.painter.Gallery;
import com.example.painter.LogInActivity;
import com.example.painter.R;
import com.example.painter.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import friendlist.DataSource;
import db_connect.DBConnector;
import textdrawable.TextDrawable;


public class FriendListFragment extends ContentFragment implements AdapterView.OnItemClickListener {

    Bundle bundle;
    public static final String TYPE = "TYPE";
    private static final int HIGHLIGHT_COLOR = 0x999be6ff;
    private DataSource mDataSource;
    private ListView mListView;
    private String deleteUser;
    private String user_id;
    SessionManager session;
    HashMap user;

    // declare the color generator and drawable builder
    private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
    private TextDrawable.IBuilder mDrawableBuilder;

    // list of data items
    private List<ListData> mDataList = new ArrayList();
    private Runnable mutiThread = new Runnable() {
        public void run() {
            // 運行網路連線的程式，用以獲得Friend List
            try {
                DBConnector dbConnector = new DBConnector("connect1.php");
                String result = dbConnector.executeQuery(String.format("SELECT * FROM friend_list where user_id='%s'", user_id));
                Log.d("Query_Result", result);

                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonData = jsonArray.getJSONObject(i);
                    mDataList.add(new ListData(jsonData.getString("friend_id"),jsonData.getString("friend_name")));
                }

                Message msg = messageHandler.obtainMessage();
                msg.what = 1;
                msg.sendToTarget();

            } catch (Exception e) {
                Log.e("log_tag", e.toString());
            }
        }
    };


    SampleAdapter sampleAdapter = new SampleAdapter(mDataList);

    // Update View
    android.os.Handler messageHandler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            sampleAdapter.refresh(mDataList);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d("Change Fragment Start", "Friend list");

        // change xml layout to view
        View v = inflater.inflate(R.layout.contentframe, container, false);
        mListView = (ListView) v.findViewById(R.id.list_content);
        mDataSource = new DataSource(getActivity());
        mDrawableBuilder = TextDrawable.builder().rect();
        bundle = getArguments();
        getListData();

        session = new SessionManager(getActivity().getApplicationContext());
        user = session.getUserDetails();
        user_id = (String) user.get(SessionManager.KEY_EMAIL);

        mListView.setAdapter(sampleAdapter);
        mListView.setOnItemClickListener(this);
        registerForContextMenu(mListView);

        Log.d("Change Fragment End", "Friend list");
        return v;
    }

    private void getListData() {
        Thread thread = new Thread(mutiThread);
        thread.start();
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ListData item = (ListData) mListView.getItemAtPosition(position);
    }

    private static class ListData {

        private String friend_email;
        private String friend_name;

        public ListData(String friend_email,String friend_name) {
            this.friend_email = friend_email;
            this.friend_name = friend_name;
        }
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_friendlist, menu);
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.gallery:
                Log.d("Go Gallery","Start");
                Intent intent = new Intent();
                bundle.putInt("state", 2);
                bundle.putString("friend_account", mDataList.get(info.position).friend_email);
                intent.putExtras(bundle);
                intent.setClass(getActivity(), Gallery.class);
                startActivity(intent);
                return true;
            case R.id.delete:
                deleteUser = mDataList.get(info.position).friend_email;
                Thread thread = new Thread(deleteThread);
                thread.start();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private Runnable deleteThread = new Runnable() {
        public void run() {
            // 運行網路連線的程式，用以獲得Friend List
            try {
                DBConnector deleteConnector = new DBConnector("insert_db.php");
                String result = deleteConnector.executeQuery(String.format("DELETE FROM friend_list WHERE user_id='%s' and friend_id='%s'"
                        , user_id, deleteUser));
                String result2 = deleteConnector.executeQuery(String.format("DELETE FROM friend_list WHERE user_id='%s' and friend_id='%s'"
                        ,deleteUser , user_id));
                Log.d("Test", result);

                Message msg = alertHandler.obtainMessage();
                msg.what = 1;
                msg.sendToTarget();

            } catch (Exception e) {
                Log.e("log_tag", e.toString());
            }
        }
    };

    // Call AlertDialog
    android.os.Handler alertHandler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            AlertDialog.Builder build = new AlertDialog.Builder(getActivity());
            if (msg.what == 1) {
                build.setTitle("Delete success")
                        .setMessage("Delete success")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sampleAdapter.refresh(mDataList);
                            }
                        }).show();
            }
        }
    };


    private static class ViewHolder {

        private View view;
        private ImageView imageView;
        private TextView textView;

        private ViewHolder(View view) {
            this.view = view;
            imageView = (ImageView) view.findViewById(R.id.imageView);
            textView = (TextView) view.findViewById(R.id.textView);
        }
    }

    private class SampleAdapter extends BaseAdapter {

        private List<ListData> mList;

        public SampleAdapter(List<ListData> list) {
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
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.friendlist_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ListData item = getItem(position);
            holder.imageView.setImageDrawable(mDrawableBuilder.build(" ", 0xff616161));
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            holder.textView.setText(item.friend_name);

            return convertView;
        }

        public void refresh(List<ListData> list) {
            mList = list;
            notifyDataSetChanged();
        }
    }
}
