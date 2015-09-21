package sidemenu;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.app.AlertDialog;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.painter.R;
import com.example.painter.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import db_connect.DBConnector;
import friendlist.DataSource;
import textdrawable.TextDrawable;


// 接收好友邀請頁面
public class FriendsInvitationFragment extends ContentFragment implements AdapterView.OnItemClickListener {

    private DrawerLayout drawer;
    private ListView mListView;
    private static final int HIGHLIGHT_COLOR = 0x999be6ff;
    private DataSource mDataSource;
    private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
    private TextDrawable.IBuilder mDrawableBuilder;
    Bundle bundle;
    String friendName;

    private String user_id;
    private String user_name;
    SessionManager session;
    HashMap user;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("Change Fragment", "Friend Invitation");
        View view = inflater.inflate(R.layout.invitation_tab, container, false);
        mListView = (ListView) view.findViewById(R.id.invitation_list);

        mDataSource = new DataSource(getActivity());
        mDrawableBuilder = TextDrawable.builder().rect();

        bundle = getArguments();
        session = new SessionManager(getActivity().getApplicationContext());
        user = session.getUserDetails();
        user_id = (String) user.get(SessionManager.KEY_EMAIL);
        user_name = (String) user.get(SessionManager.KEY_NAME);
        getListData();

        mListView.setAdapter(sampleAdapter);
        mListView.setOnItemClickListener(this);
        return view;
    }

    private void getListData() {
        Thread thread = new Thread(mutiThread);
        thread.start();
    }

    // list of data items
    private List<ListData> mDataList = new ArrayList();
    private Runnable mutiThread = new Runnable() {
        public void run() {
            // 運行網路連線的程式，用來抓取addfriend list
            try {
                DBConnector dbConnector = new DBConnector("connect1.php");
                String result = dbConnector.executeQuery(String.format("SELECT * FROM add_friend_list where friend_id='%s'", user_id));
                Log.d("Query_Invitation_Result", result);

                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonData = jsonArray.getJSONObject(i);
                    mDataList.add(new ListData(jsonData.getString("user_id"), jsonData.getString("user_name")));
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


    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("CY", "" + position);
        ListData item = (ListData) mListView.getItemAtPosition(position);
    }

    private static class ListData {

        private String friend_email;
        private String friend_name;

        public ListData(String friend_email, String friend_name) {
            this.friend_email = friend_email;
            this.friend_name = friend_name;
        }
    }

    private static class ViewHolder {

        private View view;
        private ImageView imageView;
        private TextView textView;
        private Button checkBtn;

        private ViewHolder(View view) {
            this.view = view;
            imageView = (ImageView) view.findViewById(R.id.imageView);
            textView = (TextView) view.findViewById(R.id.textView);
            checkBtn = (Button) view.findViewById(R.id.checkBtn);
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
                convertView = View.inflate(getActivity(), R.layout.invitation_friendlist_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // provide support for selected state
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // when the image is clicked, update the selected state
                    ListData data = getItem(position);
                }
            });
            holder.textView.setText(getItem(position).friend_name);
            holder.checkBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // when the image is clicked, update the selected state
                    AlertDialog.Builder build = new AlertDialog.Builder(getActivity());
                    build.setTitle("Friend Invitation Accept")
                            .setMessage("")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub
                                    Thread thread = new Thread(new Runnable() {
                                        public void run() {
                                            ListData item = getItem(position);
                                            // 運行網路連線的程式
                                            try {
                                                DBConnector dbConnector = new DBConnector("connect1.php");
                                                String result = dbConnector.executeQuery(String.format("SELECT * FROM user_list whert user_email='%s'", item.friend_email));
                                                Log.d("Search_User_Result", result);
                                                JSONObject jsonData = new JSONObject(result);

                                                DBConnector insertConnector = new DBConnector("insert_db.php");
                                                String result1 = insertConnector.executeQuery(String.format("INSERT INTO friend_list (user_id, user_name,friend_id,friend_name,friend_status)" +
                                                        "VALUES ('%s','%s')", user_id, user_name, item.friend_email, item.friend_name, jsonData.getString("friend_status")));
                                                String result2 = insertConnector.executeQuery(String.format("INSERT INTO friend_list (user_id, user_name,friend_id,friend_name,friend_status)" +
                                                        "VALUES ('%s','%s')", item.friend_email, item.friend_name, user_id, user_name, "on"));
                                                Log.d("Insert_Friend_Result1", result1);
                                                Log.d("Insert_Friend_Result2", result2);

                                                DBConnector deleteConnector = new DBConnector("insert_db.php");
                                                String result3 = deleteConnector.executeQuery(String.format("DELETE FROM add_friend_list WHERE friend_id='%s' and user_id='%s'"
                                                        , user_id, item.friend_email));

                                                getListData();

                                                Log.d("Delete_Friend_Result", result3);

                                            } catch (Exception e) {
                                                Log.e("log_tag", e.toString());
                                            }
                                        }
                                    });

                                    thread.start();
                                }
                            }).show();
                }
            });

            return convertView;
        }

        public void refresh(List<ListData> list) {
            mList = list;
            notifyDataSetChanged();
            Log.d("FI", "refresh");
        }
    }
}
