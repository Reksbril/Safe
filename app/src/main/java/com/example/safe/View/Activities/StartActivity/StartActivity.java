package com.example.safe.View.Activities.StartActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.safe.Model.Contact;
import com.example.safe.Model.Message;
import com.example.safe.View.Activities.OngoingActivity;
import com.example.safe.View.Background.Sms;
import com.example.safe.View.ListViews.ChooseContactsList;
import com.example.safe.View.ListViews.ContactsList;
import com.example.safe.View.Background.CurrentActivity;
import com.example.safe.R;

import java.util.ArrayList;


public class StartActivity extends AppCompatActivity {
    private final String locationCode = "DEST_LOCATION";
    private final String addressCode = "DEST_ADDRESS";
    private final String timeCode = "TIME";
    private final String toAddCode = "TO_ADD_ARRAY";
    private final String addViewOpenedCode = "ADD_VIEW_OPENED";
    private final String contactsToAdd = "CONTACTS_TO_ADD";

    private volatile boolean phoneContactsOpened = false;

    private final static int DEST_SELECT_CODE = 15523;
    private Button accept;
    private Location destination;
    private TextView addressView;
    private EditText time;
    private ArrayAdapter<Contact> adapter;
    private ArrayList<Contact> contacts = new ArrayList<>();
    private ContactsList contactsAdapter;
    private int timemillis;

    //to load state
    private ArrayList<Integer> toAddIndices = new ArrayList<>();


   public void setDestination(Location destination) {
       this.destination = destination;
   }

   public void setTime(int timemillis) {
       this.timemillis = timemillis;
   }

    public void startNewActivity() {
        Intent intent = new Intent(getApplicationContext(), CurrentActivity.class);
        intent.putExtra(getString(R.string.location_data), destination);
        //duration in milliseconds;
        intent.putExtra(getString(R.string.duration), timemillis);

        ArrayList<Message> messages = new ArrayList<>();
        for(Contact contact : contacts)
            messages.add(new Sms(contact.getMessage(), contact.getNumber()));
        intent.putExtra(getString(R.string.messages), messages);

        startService(intent);

        Intent newActivity = new Intent(getApplicationContext(), OngoingActivity.class);
        newActivity.putExtra(getString(R.string.destination), destination);
        startActivity(newActivity);

        finish();
    }


    public void checkBox(int position) {
        contacts.add(adapter.getItem(position));
        toAddIndices.add(position);
    }

    public void uncheckBox(int position) {
        contacts.remove(adapter.getItem(position));
        toAddIndices.remove((Integer)position);
    }

    public void setAdapter(final ArrayAdapter<Contact> adapter) {
        this.adapter = adapter;
        for(int i : toAddIndices) {
            contacts.add(adapter.getItem(i));
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((ChooseContactsList)adapter).checkBoxes(toAddIndices);
            }
        });
    }



    public void deleteContact(Contact contact) {
        Integer pos = adapter.getPosition(contact);
        contactsAdapter.remove(contact);
    }

    void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


   private static final int NUM_PAGES = 3;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = findViewById(R.id.viewpager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(pagerAdapter);

        if(savedInstanceState != null) {
            toAddIndices = (ArrayList<Integer>) savedInstanceState.get(toAddCode);
        }
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        private Fragment[] fragments = new Fragment[3];


        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
            fragments[0] = new SelectDestination();
            fragments[1] = new SelectTime();
            fragments[2] = new SelectContacts();
        }

        @Override
        public Fragment getItem(int position) {
            if(position > 2 || position < 0)
                return null;
            return fragments[position];
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    public void goToNext() {
        int current = mPager.getCurrentItem();
        if(current < NUM_PAGES - 1)
            mPager.setCurrentItem(current + 1, true);
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putParcelable(locationCode, destination);
        bundle.putInt(timeCode, timemillis);
        bundle.putIntegerArrayList(toAddCode, toAddIndices);
    }
}
