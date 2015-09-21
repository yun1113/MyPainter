package sidemenu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
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

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.painter.Canvas;
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


// �h�H�s�u
public class MultiConnectionFragment extends ContentFragment implements AdapterView.OnItemClickListener {

    Bundle bundle;
    public static final String TYPE = "TYPE";
    private static final int HIGHLIGHT_COLOR = 0x999be6ff;
    private DataSource mDataSource;
    private ListView mListView;
    private Button mConnectBtn;
    private List<ListData> mDataList = new ArrayList();
    private List<ListData> connectDataList = new ArrayList();
    private int connectCount = 0;
    final int MAX_CONNECT = 4;

    private String user_id, user_name;
    SessionManager session;
    HashMap user;
    // declare the color generator and drawable builder
    private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
    private TextDrawable.IBuilder mDrawableBuilder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d("Change Fragment Start", "Multiconnection");

        // change xml layout to view
        View v = inflater.inflate(R.layout.friend_connect_frame, container, false);
        mListView = (ListView) v.findViewById(R.id.list_content);
        mConnectBtn = (Button) v.findViewById(R.id.connectBtn);
        mDataSource = new DataSource(getActivity());
        mDrawableBuilder = TextDrawable.builder().rect();
        bundle = getArguments();
        getListData();

        session = new SessionManager(getActivity().getApplicationContext());
        user = session.getUserDetails();
        user_id = (String) user.get(SessionManager.KEY_EMAIL);
        user_name = (String) user.get(SessionManager.KEY_NAME);

        mListView.setAdapter(sampleAdapter);
        mListView.setOnItemClickListener(this);

        mConnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

//                for (int i = 0; i < connectDataList.size(); i++) {
//                    Log.d("CY" + i, connectDataList.get(i).data);
//                }
//
//                for (int i = 0; i < mDataList.size(); i++) {
//                    if (mDataList.get(i).isChecked) {
//                        connectCount++;
//                        connectDataList.add(mDataList.get(i));
//                    }
//                }
                if (connectCount > MAX_CONNECT) {
                    Message msg = alertHandler.obtainMessage();
                    msg.what = 1;
                    msg.sendToTarget();
                } else {
                    Thread thread = new Thread(createConnectThread);
                    thread.start();
                }
            }
        });

        Log.d("Change Fragment End", "Multiconnection");
        return v;
    }

    private void getListData() {
        Thread thread = new Thread(mutiThread);
        thread.start();
    }

    // list of data items
    private Runnable mutiThread = new Runnable() {
        public void run() {
            try {
                DBConnector dbConnector = new DBConnector("connect1.php");
                String result = dbConnector.executeQuery(String.format("SELECT * FROM friend_list where user_id ='%s'", user_id));
                Log.d("Query_Result", result);

                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonData = jsonArray.getJSONObject(i);
                    if (jsonData.getString("friend_status").equals("on")) {
                        mDataList.add(new ListData(jsonData.getString("friend_name"), jsonData.getString("friend_status")));
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

    // �إ߳s�u���
    private Runnable createConnectThread = new Runnable() {
        public void run() {
            try {

                DBConnector dbConnector = new DBConnector("insert_db.php");
                String[] user = new String[MAX_CONNECT];
                for (int i = 0; i < MAX_CONNECT; i++) {
                    if (i < connectCount) {
                        user[i] = connectDataList.get(i).data;
                        dbConnector.executeQuery(String.format("UPDATE friend_list SET friend_status='busy' WHERE friend_id = '%s'", user[i]));
                    } else
                        user[i] = "";
                }
                dbConnector.executeQuery(String.format("UPDATE friend_list SET friend_status='busy' WHERE friend_id = '%s'", user_id));
                String result = dbConnector.executeQuery(String.format("INSERT INTO nowconnect(user_1,user_2,user_3,user_4,user_5)" +
                        " VALUES ('%s','%s','%s','%s','%s')", user_name, user[0], user[1], user[2], user[3]));
                Log.d("Send_Result", result);

                JSONObject jObj = new JSONObject(result);
                int resultInt = jObj.getInt("response");
                if (resultInt == 1) {
                    Message msg = alertHandler.obtainMessage();
                    msg.what = 2;
                    msg.sendToTarget();
                }
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
                build.setTitle("Too more connect")
                        .setMessage("You can connect to most 5 people only.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                connectDataList.clear();
                                connectCount = 0;
                            }
                        }).show();

            } else if (msg.what == 2) {
                build.setTitle("Connect Successful")
                        .setMessage("Connect Successful")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                intent.setClass(getActivity(), Canvas.class);
                                startActivity(intent);
                            }
                        }).show();
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
        ListData item = (ListData) mListView.getItemAtPosition(position);

    }

    private static class ListData {

        private String data;
        private boolean isChecked;
        private String state;

        public ListData(String data, String state) {
            this.data = data;
            this.isChecked = false;
            this.state = state;
        }

        public void setChecked(boolean isChecked) {
            this.isChecked = isChecked;
        }
    }

    private static class ViewHolder {

        private View view;
        private ImageView imageView;
        private TextView textView;
        private ImageView checkIcon;

        private ViewHolder(View view) {
            this.view = view;

            imageView = (ImageView) view.findViewById(R.id.imageView);
            checkIcon = (ImageView) view.findViewById(R.id.check_icon);
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
                convertView = View.inflate(getActivity(), R.layout.multiple_connection_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ListData item = getItem(position);

            // provide support for selected state
            updateCheckedState(holder, item);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // when the image is clicked, update the selected state
                    ListData data = getItem(position);
                    data.setChecked(!data.isChecked);
                    updateCheckedState(holder, data);
                }
            });
            holder.textView.setText(item.data);

            return convertView;
        }

        // �Ŀ窱�A
        private void updateCheckedState(ViewHolder holder, ListData item) {
            holder.imageView.setImageDrawable(mDrawableBuilder.build(" ", 0xff616161));
            if (item.isChecked) {
                holder.view.setBackgroundColor(HIGHLIGHT_COLOR);
                holder.checkIcon.setVisibility(View.VISIBLE);

                connectCount++;
                connectDataList.add(item);
            } else {
                holder.view.setBackgroundColor(Color.TRANSPARENT);
                holder.checkIcon.setVisibility(View.GONE);

                connectCount--;
                connectDataList.remove(item);
            }
        }

        public void refresh(List<ListData> list) {
            mList = list;
            notifyDataSetChanged();
        }
    }
}
