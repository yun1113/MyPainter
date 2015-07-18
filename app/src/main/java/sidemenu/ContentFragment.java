package sidemenu;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.painter.R;

import java.util.Arrays;
import java.util.List;

import friendlist.DataSource;
import textdrawable.TextDrawable;
import yalantis.com.sidemenu.interfaces.ScreenShotable;

/**
 * Created by Konstantin on 22.12.2014.
 */
public class ContentFragment extends Fragment implements ScreenShotable {

    public static final String CLOSE = "Close";
    public static final String BUILDING = "Building";
    public static final String BOOK = "Book";
    public static final String PAINT = "Paint";
    public static final String CASE = "Case";
    public static final String SHOP = "Shop";
    public static final String PARTY = "Party";
    public static final String MOVIE = "Movie";
    protected int res;
    private View containerView;
    private Bitmap bitmap;


    public static ContentFragment newInstance(int position) {
        // Create a new fragment and specify the planet to show based on position
        ContentFragment contentFragment = new ContentFragment();
        Bundle bundle = new Bundle(); // use to transfer data
        bundle.putInt(Integer.class.getName(), position);
        contentFragment.setArguments(bundle);
        return contentFragment;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.containerView = view.findViewById(R.id.container);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // change xml layout to view
        View v = inflater.inflate(R.layout.contentframe, container, false);
        return v;
    }


    /**
     * Implement Interface: ScreenShotable
     * method: void takeScreenShot()
     * method: Bitmap getBitmap()
     */

    @Override
    public void takeScreenShot() {
    }

    @Override
    public Bitmap getBitmap() {
        return bitmap;
    }

}
