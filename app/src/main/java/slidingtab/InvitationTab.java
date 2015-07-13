package slidingtab;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.painter.GalleryDetail;
import com.example.painter.R;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

import friendlist.DataSource;
import textdrawable.TextDrawable;

/**
 * Created by user on 2015/7/13.
 */
public class InvitationTab extends Fragment {

    private DrawerLayout drawer;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.invitation_tab, container, false);

        return v;
    }

}
