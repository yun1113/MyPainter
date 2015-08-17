package sidemenu;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.painter.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
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
                String result = dbConnector.executeQuery(String.format("SELECT * FROM friendlist where friendlist_id='%s'",bundle.getString("account")));
                Log.d("Test", result);

                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonData = jsonArray.getJSONObject(i);
                    mDataList.add(new ListData(jsonData.getString("user_id")));
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

        Log.d("CY","Create Friend list start");

        // change xml layout to view
        View v = inflater.inflate(R.layout.contentframe, container, false);
        mListView = (ListView) v.findViewById(R.id.list_content);
        mDataSource = new DataSource(getActivity());
        mDrawableBuilder = TextDrawable.builder().rect();
        bundle = getArguments();
        getListData();

        mListView.setAdapter(sampleAdapter);
        mListView.setOnItemClickListener(this);
        Log.d("CY","Create Friend list end");
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

        private String data;
        private boolean isChecked;

        public ListData(String data) {
            this.data = data;
            this.isChecked = false;
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
            textView = (TextView) view.findViewById(R.id.textView);
            checkIcon = (ImageView) view.findViewById(R.id.check_icon);
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

        private void updateCheckedState(ViewHolder holder, ListData item) {
            holder.imageView.setImageDrawable(mDrawableBuilder.build(" ", 0xff616161));
            if (item.isChecked) {
                holder.view.setBackgroundColor(HIGHLIGHT_COLOR);
                holder.checkIcon.setVisibility(View.VISIBLE);
            } else {
                holder.view.setBackgroundColor(Color.TRANSPARENT);
                holder.checkIcon.setVisibility(View.GONE);
            }
        }

        public void refresh(List<ListData> list) {
            mList = list;
            notifyDataSetChanged();
        }
    }
}
