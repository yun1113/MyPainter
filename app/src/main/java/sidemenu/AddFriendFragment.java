package sidemenu;

import android.content.DialogInterface;
import android.os.Bundle;
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

import org.json.JSONArray;
import org.json.JSONObject;

import db_connect.DBConnector;


public class AddFriendFragment extends ContentFragment {

    EditText friendNameEdt;
    TextView friendTxv;
    ImageButton searchBtn;
    ImageButton addBtn;
    Boolean friendExist = false;
    String friendName = "";
    Bundle bundle;

    private Runnable mutiThread = new Runnable() {
        public void run() {
            // 運行網路連線的程式，用來驗證有無該user
            try {
                DBConnector dbConnector = new DBConnector("connect1.php");
                String result = dbConnector.executeQuery(String.format("SELECT * FROM test"));
                Log.d("Test", result);

                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonData = jsonArray.getJSONObject(i);
                    if (friendNameEdt.getText().toString().equals(jsonData.getString("name"))) {
                        friendName = jsonData.getString("name");
                        friendExist = true;
                        break;
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

    private Runnable sendRequestThread = new Runnable() {
        public void run() {
            // 運行網路連線的程式
            try {
                DBConnector dbConnector = new DBConnector("insert_db.php");
                Log.d("CY_Query_String", String.format("INSERT INTO addfriendlist (user_id, friend_id)" +
                        "VALUES ('%s','%s')", bundle.getString("account"), friendName));

                String result = dbConnector.executeQuery(String.format("INSERT INTO addfriendlist (user_id,friend_id )" +
                        "VALUES ('%s','%s')", bundle.getString("account"), friendName));
                Log.d("CY", result);
                if (result.equals("\"success\"")) {
                    Log.d("CY", "Insert success");

                    AlertDialog.Builder build = new AlertDialog.Builder(getActivity());
                    build.setTitle("加入好友");
                    build.setMessage("add success");
                    build.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    build.show();
                }
                Log.d("CY2", "\"success\"");
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

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.add_friend_tab, container, false);
        friendNameEdt = (EditText) view.findViewById(R.id.friendEdt);
        friendTxv = (TextView) view.findViewById(R.id.friendTxv);

        bundle = getArguments();

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
