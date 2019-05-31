package com.example.safe.View.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.example.safe.Database.Database;
import com.example.safe.Model.ContactList;
import com.example.safe.View.ListViews.ManageContactsList;
import com.example.safe.Model.Contact;
import com.example.safe.Database.DbSingleton;
import com.example.safe.R;
import com.example.safe.View.ListViews.PhoneContactsList;

import java.util.ArrayList;
import java.util.List;

public class ManageContactsActivity extends Activity {
    private ArrayAdapter<Contact> adapter;
    private CursorAdapter cursorAdapter;


    class LoadDBTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... args) {
            adapter = new ManageContactsList(
                    ManageContactsActivity.this,
                    R.layout.list_item_edit,
                    new ContactList(new DbSingleton(ManageContactsActivity.this).database.contactDao()));
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            ListView list = findViewById(R.id.contactsList);
            list.setAdapter(adapter);
            stopLoadingScreen();
        }
    }

    class LoadContactsTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... args) {
            getContactList();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            ListView list = findViewById(R.id.phoneContacts);
            list.setAdapter(cursorAdapter);
            showPhoneContacts();
            stopLoadingScreen();
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_contacts);

        new LoadDBTask().execute();

        Button addButton = new Button(this);
        addButton.setText(R.string.add_contact);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoadingScreen();
                new LoadContactsTask().execute();
            }
        });

        ListView list = findViewById(R.id.contactsList);
        list.addHeaderView(addButton);


        Button backButton = new Button(this);
        backButton.setText(R.string.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePhoneContacts();
            }
        });

        ListView phoneList = findViewById(R.id.phoneContacts);
        phoneList.addHeaderView(backButton);
    }

    private void stopLoadingScreen() {
        ConstraintLayout layout = findViewById(R.id.loadingLayout);
        layout.setVisibility(View.INVISIBLE);
    }

    private void startLoadingScreen() {
        ConstraintLayout layout = findViewById(R.id.loadingLayout);
        layout.setVisibility(View.VISIBLE);
    }

    private void showPhoneContacts() {
        ConstraintLayout layout = findViewById(R.id.phoneContactsView);
        layout.setVisibility(View.VISIBLE);
    }

    private void hidePhoneContacts() {
        ConstraintLayout layout = findViewById(R.id.phoneContactsView);
        layout.setVisibility(View.INVISIBLE);
    }

    private void getContactList() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    getResources().getInteger(R.integer.REQUEST_ACCESS_CONTACTS));

        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        cursorAdapter = new PhoneContactsList(this, cursor);
    }

    public void addToList(String name, String number, String message) {
        adapter.add(new Contact(number, name));
        adapter.notifyDataSetChanged();
    }
}
