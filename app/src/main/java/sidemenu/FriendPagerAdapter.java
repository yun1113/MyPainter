package sidemenu;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by user on 2015/7/18.
 */
public class FriendPagerAdapter extends FragmentStatePagerAdapter {

    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created


    // Build a Constructor and assign the passed Values to appropriate values in the class
    public FriendPagerAdapter(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);

        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;

    }

    public static ContentFragment newInstance(int position) {
        // Create a new fragment and specify the planet to show based on position
        FriendListFragment contentFragment = new FriendListFragment();
        Bundle bundle = new Bundle(); // use to transfer data
        bundle.putInt(Integer.class.getName(), position);
        contentFragment.setArguments(bundle);
        return contentFragment;
    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {

        if (position == 0) // if the position is 0 we are returning the First tab
        {
            FriendListFragment tab = new FriendListFragment();
            return tab;
        } else if (position == 1)            // As we are having 2 tabs if the position is now 0 it must be 1 so we are returning second tab
        {
            MultiConnectionFragment tab = new MultiConnectionFragment();
            return tab;
        } else if (position == 2)            // As we are having 2 tabs if the position is now 0 it must be 1 so we are returning second tab
        {
            FriendsInvitationFragment tab = new FriendsInvitationFragment();
            return tab;
        } else if (position == 3)            // As we are having 2 tabs if the position is now 0 it must be 1 so we are returning second tab
        {
            AddFriendFragment tab = new AddFriendFragment();
            return tab;
        } else {
            FriendListFragment tab = new FriendListFragment();
            return tab;
        }
    }

    // This method return the titles for the Tabs in the Tab Strip

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount() {
        return NumbOfTabs;
    }
}
