package sidemenu;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.app.AlertDialog;
import android.widget.Toast;

import com.example.painter.R;
import com.example.painter.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import db_connect.DBConnector;


public class AddFriendFragment extends ContentFragment {

    EditText friendNameEdt;
    TextView friendTxv;
    ImageButton searchBtn;
    ImageButton addBtn;
    Boolean friendExist = false;
    String friendName = "";
    String friendEmail = "";
    Bundle bundle;

    private String user_id;
    private String user_name;
    SessionManager session;
    HashMap user;


    private Runnable mutiThread = new Runnable() {
        public void run() {
            // �B������s�u���{���A�Ψ����Ҧ��L��user
            try {
                DBConnector dbConnector = new DBConnector("connect1.php");
                String result = dbConnector.executeQuery(String.format("SELECT * FROM user_list WHERE user_name='%s'",friendNameEdt.getText().toString()));
                Log.d("Search_User_Result", result);

                  JSONArray jsonArray = new JSONArray(result);
//                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonData = jsonArray.getJSONObject(0);
//                    if (friendNameEdt.getText().toString().equals(jsonData.getString("user_name"))) {
                        friendName = jsonData.getString("user_name");
                        friendEmail = jsonData.getString("user_email");
                        friendExist = true; //�ӨϥΪ̦s�b
//                        break;
//                    }
//                }

                Message msg = messageHandler.obtainMessage();
                msg.what = 1;
                msg.sendToTarget();

            } catch (Exception e) {
                Log.e("log_tag", e.toString());
            }
        }
    };

    private Runnable sendRequestThread = new Runnable() {
        public void run() {
            // �B������s�u���{��
            try {
                DBConnector dbConnector = new DBConnector("insert_db.php");

                String result = dbConnector.executeQuery(String.format("INSERT INTO add_friend_list (user_id,user_name,friend_id,friend_name )" +
                        "VALUES ('%s','%s','%s','%s')", user_id,user_name, friendEmail,friendName));
                Log.d("Send_Result", result);

                JSONObject jObj = new JSONObject(result);
                int resultInt = jObj.getInt("response");
                if (resultInt == 1) {
                    Message msg = alertHandler.obtainMessage();
                    msg.what = 1;
                    msg.sendToTarget();
                } else {
                    Message msg = alertHandler.obtainMessage();
                    msg.what = 2;
                    msg.sendToTarget();
                }

                Log.d("CY", "\"success\"");
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
            if (friendExist) {
                friendTxv.setText(friendName);
                addBtn.setVisibility(View.VISIBLE);
            } else {
                friendTxv.setText("Can't find this user!");
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
                build.setTitle("Add Friend Success")
                        .setMessage("add success")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                friendNameEdt.setText("");
                                friendTxv.setText("");
                                addBtn.setVisibility(View.INVISIBLE);
                            }
                        }).show();
            } else if (msg.what == 2) {
                build.setTitle("Add Friend Fail")
                        .setMessage("add fail")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                friendNameEdt.setText("");
                                friendTxv.setText("");
                                addBtn.setVisibility(View.INVISIBLE);
                            }
                        }).show();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.add_friend_tab, container, false);
        friendNameEdt = (EditText) view.findViewById(R.id.friendEdt);
        friendTxv = (TextView) view.findViewById(R.id.friendTxv);

        bundle = getArguments();
        session = new SessionManager(getActivity().getApplicationContext());
        user = session.getUserDetails();
        user_id = (String) user.get(SessionManager.KEY_EMAIL);
        user_name = (String) user.get(SessionManager.KEY_NAME);

        searchBtn = (ImageButton) view.findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                getListData();
            }
        });

        addBtn = (ImageButton) view.findViewById(R.id.addBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sendRequest();
            }
        });

        return view;
    }

    private void getListData() {
        Thread thread = new Thread(mutiThread);
        thread.start();
    }

    private void sendRequest() {
        Thread thread = new Thread(sendRequestThread);
        thread.start();
    }
}
