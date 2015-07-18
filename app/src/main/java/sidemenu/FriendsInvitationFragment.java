package sidemenu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.painter.R;

/**
 * Created by user on 2015/7/18.
 */
public class FriendsInvitationFragment extends ContentFragment {

    private DrawerLayout drawer;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.invitation_tab, container, false);
        return v;
    }
}
