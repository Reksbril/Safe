package com.example.safe.View.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContextWrapper;
import android.content.DialogInterface;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.safe.Database.ContactDao;
import com.example.safe.Database.Database;
import com.example.safe.Model.ContactList;
import com.example.safe.View.Activities.AsyncTasks.LoadDb;
import com.example.safe.View.ListViews.ManageContactsList;
import com.example.safe.Model.Contact;
import com.example.safe.Database.DbSingleton;
import com.example.safe.R;
import com.example.safe.View.ListViews.PhoneContactsList;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//TODO zrobić commit po pewnym czasie (np dodać przycisk "save") albo jakoś ogarnąć dodawanie do bazy po każdym dodaniu do listy (np. odpalać loading screen)
public class ManageContactsActivity extends Activity {
    private final String phoneVisibility = "PHONE_VISIBILITY";
    private final String addVisibility = "ADD_VISIBLE";
    private final String toAddNameCode = "TO_ADD_NAME";
    private final String toAddNumberCode = "TO_ADD_NUMBER";
    private final String toAddMessage = "TO_ADD_MESSAGE";
    private final String addOrEdit = "ADD_EDIT";
    private final String editIndex = "EDIT_INDEX";
    private final String deleteIndex = "DELETE_INDEX";


    private ArrayAdapter<Contact> adapter;
    private CursorAdapter cursorAdapter;
    private EditText messageText;
    private ContactDao dao;
    private AlertDialog deleteDialog;

    //to save instance state
    private String toAddName = "";
    private String toAddNumber = "";
    private boolean add_edit = false; //true jeżeli add, false jeżeli edit
    private int toRemoveInDialog;
    private int editing;

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

    enum DbOperationType {ADD, DELETE, EDIT}

    private class DbOperation {
        Contact contact;
        DbOperationType type;

        DbOperation(Contact contact, DbOperationType type) {
            this.contact = contact;
            this.type = type;
        }
    }

    class CommitDbTask extends AsyncTask<DbOperation, Void, Void> {
        @Override
        protected Void doInBackground(DbOperation... args) {
            for(DbOperation arg : args) {
                switch(arg.type) {
                    case ADD:
                        dao.insert(arg.contact);
                        break;
                    case DELETE:
                        dao.delete(arg.contact);
                        break;
                    case EDIT:
                        dao.update(arg.contact);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            stopLoadingScreen();
        }
    }


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_contacts);

        dao = new DbSingleton(this).database.contactDao();

        messageText = findViewById(R.id.messageText);

        new LoadDb(dao, false) {
            @Override
            protected void onPostExecute(ArrayAdapter<Contact> result) {
                adapter = result;
                ListView list = findViewById(R.id.contactsList);
                list.setAdapter(adapter);
                stopLoadingScreen();

                //load instance state
                if(savedInstanceState != null) {
                    int deleteInd = savedInstanceState.getInt(deleteIndex);
                    if (deleteInd >= 0)
                        showDeleteDialog(deleteInd);
                }
            }
        }.execute(this);

        Button addButton = new Button(this);
        addButton.setText(R.string.add_contact);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPhoneContacts();
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
                hideAddMessage();
            }
        });

        ListView phoneList = findViewById(R.id.phoneContacts);
        phoneList.addHeaderView(backButton);

        if(savedInstanceState != null) {
            //load instance state
            int addPhoneVisibility = savedInstanceState.getInt(phoneVisibility);

            if (addPhoneVisibility == View.VISIBLE)
                openPhoneContacts();

            if (savedInstanceState.getInt(addVisibility) == View.VISIBLE) {
                String message = savedInstanceState.getString(toAddMessage);
                if (savedInstanceState.getBoolean(addOrEdit)) {
                    String name = savedInstanceState.getString(toAddNameCode);
                    String number = savedInstanceState.getString(toAddNumberCode);
                    addToList(name, number, message);
                } else {
                    int position = savedInstanceState.getInt(editIndex);
                    editListElement(position, message);
                }
            }
        }

        findViewById(R.id.decline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideAddMessage();
            }
        });

        findViewById(R.id.addMessageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
            }
        });

    }

    private void openPhoneContacts() {
        startLoadingScreen();
        new LoadContactsTask().execute();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //phone list
        outState.putInt(phoneVisibility, findViewById(R.id.phoneContactsView).getVisibility());

        //add new/ edit
        outState.putInt(addVisibility, findViewById(R.id.addMessageView).getVisibility());
        outState.putString(toAddNameCode, toAddName);
        outState.putString(toAddNumberCode, toAddNumber);
        outState.putBoolean(addOrEdit, add_edit);
        outState.putString(toAddMessage, messageText.getText().toString());
        outState.putInt(editIndex, editing);

        //delete
        if(deleteDialog != null && deleteDialog.isShowing()) {
            outState.putInt(deleteIndex, toRemoveInDialog);
            deleteDialog.dismiss();
        }
        else
            outState.putInt(deleteIndex, -1);
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

    private void showAddMessage(String initMessage) {
        EditText view = findViewById(R.id.messageText);
        view.setText(initMessage);
        findViewById(R.id.addMessageView).setVisibility(View.VISIBLE);
    }

    private void hideAddMessage() {
        findViewById(R.id.addMessageView).setVisibility(View.INVISIBLE);
        messageText.setText("");
        hideKeyboard();
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


    public void addToList(final String name, final String number, final String initMessage) {
        add_edit = true;
        toAddNumber = number;
        toAddName = name;

        showAddMessage(initMessage);

        findViewById(R.id.acceptMessage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoadingScreen();
                Contact toAdd = new Contact(number, name, messageText.getText().toString());
                new CommitDbTask().execute(new DbOperation(toAdd, DbOperationType.ADD));
                adapter.add(toAdd);
                adapter.notifyDataSetChanged();
                hideAddMessage();
            }
        });
    }

    public void editListElement(final int position, String initMessage) {
        add_edit = false;
        editing = position;
        showAddMessage(initMessage);

        findViewById(R.id.acceptMessage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoadingScreen();
                Contact toEdit = adapter.getItem(position);
                toEdit.setMessage(messageText.getText().toString());
                new CommitDbTask().execute(new DbOperation(toEdit, DbOperationType.EDIT));
                adapter.notifyDataSetChanged();
                hideAddMessage();
            }
        });
    }

    public void remove(Contact contact) {
        startLoadingScreen();
        new CommitDbTask().execute(new DbOperation(contact, DbOperationType.DELETE));
        adapter.remove(contact);
        adapter.notifyDataSetChanged();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void showDeleteDialog(int position) {
        toRemoveInDialog = position;
        final Contact toRemove = adapter.getItem(position);
        deleteDialog = new AlertDialog.Builder(this)
                .setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        remove(toRemove);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert).show();
    }
}
