package com.example.safe.View.Activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.safe.Database.ContactDao;
import com.example.safe.Database.Database;
import com.example.safe.Model.ContactBasic;
import com.example.safe.Model.ContactList;
import com.example.safe.View.Activities.AsyncTasks.LoadDb;
import com.example.safe.View.ListViews.ManageContactsList;
import com.example.safe.Model.Contact;
import com.example.safe.Database.DbSingleton;
import com.example.safe.R;
import com.example.safe.View.ListViews.PhoneContactsList;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.ConstantCallSite;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//TODO zrobić commit po pewnym czasie (np dodać przycisk "save") albo jakoś ogarnąć dodawanie do bazy po każdym dodaniu do listy (np. odpalać loading screen)
public class ManageContactsActivity extends Activity {
    private final String phoneVisibility = "PHONE_VISIBILITY";
    private final String addVisibility = "ADD_VISIBLE";
    private final String contactInfoVisibility = "INFO_VISIBILITY";
    private final String nameCode = "TO_ADD_NAME";
    private final String toAddNumberCode = "TO_ADD_NUMBER";
    private final String messageCode = "TO_ADD_MESSAGE";
    private final String addOrEdit = "ADD_EDIT";
    private final String editIndex = "EDIT_INDEX";
    private final String deleteIndex = "DELETE_INDEX";
    private final String toAddImage = "TO_ADD_IMAGE";
    private final int PICK_CONTACT = 123;


    private ArrayAdapter<Contact> adapter;
    private EditText messageText;
    private ContactDao dao;
    private AlertDialog deleteDialog;

    //to save instance state
    private String toAddName = "";
    private String toAddNumber = "";
    private boolean add_edit = false; //true jeżeli add, false jeżeli edit
    private int toRemoveInDialog;
    private int editing;

    class LoadContactsTask extends AsyncTask<Void, Void, ArrayList<Contact>> {
        @Override
        protected ArrayList<Contact> doInBackground(Void... args) {
            return getContactList();
        }

        @Override
        protected void onPostExecute(ArrayList<Contact> result) {
            ListView list = findViewById(R.id.phoneContacts);
            list.setAdapter(new PhoneContactsList(
                    ManageContactsActivity.this,
                    R.layout.list_phone_contact,
                    result));
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
            for (DbOperation arg : args) {
                switch (arg.type) {
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
                if (savedInstanceState != null) {
                    int deleteInd = savedInstanceState.getInt(deleteIndex);
                    if (deleteInd >= 0)
                        showDeleteDialog(deleteInd);
                }
            }
        }.execute(this);

        FloatingActionButton addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPhoneContacts();
            }
        });

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

        if (savedInstanceState != null) {
            //load instance state
            int addPhoneVisibility = savedInstanceState.getInt(phoneVisibility);

            if (addPhoneVisibility == View.VISIBLE)
                openPhoneContacts();

            if (savedInstanceState.getInt(addVisibility) == View.VISIBLE) {
                String message = savedInstanceState.getString(messageCode);
                if (savedInstanceState.getBoolean(addOrEdit)) {
                    String name = savedInstanceState.getString(nameCode);
                    String number = savedInstanceState.getString(toAddNumberCode);
                    byte[] img = savedInstanceState.getByteArray(toAddImage);
                    addToList(name, number, message, img);
                } else {
                    int position = savedInstanceState.getInt(editIndex);
                    editListElement(position, message);
                }
            }

            if (savedInstanceState.getInt(contactInfoVisibility) == View.VISIBLE) {
                String message = savedInstanceState.getString(messageCode);
                String name = savedInstanceState.getString(nameCode);
                openContactInfo(name, message);
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

        findViewById(R.id.phoneContactsView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePhoneContacts();
            }
        });

        final ConstraintLayout contactsInfo = findViewById(R.id.contactInfoView);
        contactsInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactsInfo.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void openPhoneContacts() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //phone list
        outState.putInt(phoneVisibility, findViewById(R.id.phoneContactsView).getVisibility());

        //add new/ edit
        int visibility = findViewById(R.id.addMessageView).getVisibility();
        outState.putInt(addVisibility, visibility);
        if (visibility == View.VISIBLE) {
            outState.putString(nameCode, toAddName);
            outState.putString(toAddNumberCode, toAddNumber);
            outState.putBoolean(addOrEdit, add_edit);
            outState.putString(messageCode, messageText.getText().toString());
            outState.putInt(editIndex, editing);
        }

        //delete
        if (deleteDialog != null && deleteDialog.isShowing()) {
            outState.putInt(deleteIndex, toRemoveInDialog);
            deleteDialog.dismiss();
        } else
            outState.putInt(deleteIndex, -1);

        //contact info
        visibility = findViewById(R.id.contactInfoView).getVisibility();
        outState.putInt(contactInfoVisibility, visibility);
        if (visibility == View.VISIBLE) {
            outState.putString(nameCode, ((TextView) findViewById(R.id.contactName)).getText().toString());
            outState.putString(messageCode, ((TextView) findViewById(R.id.contactMessage)).getText().toString());
        }

    }


    private void stopLoadingScreen() {
        final ConstraintLayout layout = findViewById(R.id.loadingLayout);
        layout.animate().alpha(0).setDuration(100).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                layout.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void startLoadingScreen() {
        ConstraintLayout layout = findViewById(R.id.loadingLayout);
        layout.setAlpha(0);
        layout.setVisibility(View.VISIBLE);
        layout.animate().alpha(1f).setDuration(100).setListener(null);
    }

    private void showPhoneContacts() {
        ConstraintLayout layout = findViewById(R.id.phoneContactsView);
        layout.setVisibility(View.VISIBLE);
        layout.animate().alpha(1f).setDuration(300).setListener(null);
    }

    private void hidePhoneContacts() {
        final ConstraintLayout layout = findViewById(R.id.phoneContactsView);
        layout.animate().alpha(0f).setDuration(300).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                layout.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void showAddMessage(String initMessage) {
        EditText view = findViewById(R.id.messageText);
        view.setText(initMessage);
        ConstraintLayout messageView = findViewById(R.id.addMessageView);
        messageView.setAlpha(0f);
        messageView.setVisibility(View.VISIBLE);
        messageView.animate().alpha(1f).setDuration(300).setListener(null);
    }

    private void hideAddMessage() {
        final ConstraintLayout messageView = findViewById(R.id.addMessageView);
        final EditText view = findViewById(R.id.messageText);
        hideKeyboard();
        messageView.animate().alpha(0f).setDuration(300).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                messageView.setVisibility(View.INVISIBLE);
                view.setText("");
            }
        });
    }

    private ArrayList<Contact> getContactList() {
        ArrayList<Contact> result = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    getResources().getInteger(R.integer.REQUEST_ACCESS_CONTACTS));
        else {
            ContentResolver cr = getContentResolver();
            try (Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null)) {
                if (cursor == null)
                    return result;

                String name;
                String phoneNo;
                while (cursor.moveToNext()) {
                    if (cursor.getInt(cursor.getColumnIndex(
                            ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {

                        String id = cursor.getString(
                                cursor.getColumnIndex(ContactsContract.Contacts._ID));


                        Bitmap photo = null;


                        try {
                            InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(),
                                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.valueOf(id)));

                            if (inputStream != null) {
                                photo = BitmapFactory.decodeStream(inputStream);
                                inputStream.close();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        try (Cursor pCur = cr.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id}, null)) {
                            while (pCur.moveToNext()) {
                                phoneNo = pCur.getString(pCur.getColumnIndex(
                                        ContactsContract.CommonDataKinds.Phone.NUMBER));
                                name = cursor.getString(cursor.getColumnIndex(
                                        ContactsContract.Contacts.DISPLAY_NAME));

                                result.add(new Contact(name, phoneNo, "", Contact.encodeImage(photo)));
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    public void addToList(final String name,
                          final String number,
                          final String initMessage,
                          final byte[] image) {
        add_edit = true;
        toAddNumber = number;
        toAddName = name;

        showAddMessage(initMessage);

        findViewById(R.id.acceptMessage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Contact toAdd = new Contact(number, name, messageText.getText().toString(), image);
                int pos = adapter.getPosition(toAdd);
                if (pos != -1)
                    Toast.makeText(ManageContactsActivity.this,
                            "Contact already added!",
                            Toast.LENGTH_SHORT).show();
                else {
                    startLoadingScreen();
                    new CommitDbTask().execute(new DbOperation(toAdd, DbOperationType.ADD));
                    adapter.add(toAdd);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(ManageContactsActivity.this,
                            "Contact successfully added",
                            Toast.LENGTH_SHORT).show();
                    hideAddMessage();
                }
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResult) {
        if (requestCode == getResources().getInteger(R.integer.REQUEST_ACCESS_CONTACTS)) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.READ_CONTACTS)) {
                    if (grantResult[i] == PackageManager.PERMISSION_GRANTED)
                        openPhoneContacts();
                    else
                        hidePhoneContacts();
                }
            }
        }
    }

    public void openContactInfo(String name, String message) {
        findViewById(R.id.contactInfoView).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.contactName)).setText(name);
        ((TextView) findViewById(R.id.contactMessage)).setText(message);
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (reqCode == PICK_CONTACT) {
            if (resultCode == Activity.RESULT_OK) {
                Uri contactData = data.getData();
                ContentResolver cr = getContentResolver();
                try (Cursor cursor = cr.query(
                        contactData,
                        null,
                        null,
                        null,
                        null)) {
                    if (cursor == null)
                        return;

                    if(!cursor.moveToNext())
                        return;

                    String name;
                    String phoneNo;

                    if (cursor.getInt(cursor.getColumnIndex(
                            ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {

                        String id = cursor.getString(
                                cursor.getColumnIndex(ContactsContract.Contacts._ID));

                        name = cursor.getString(cursor.getColumnIndex(
                                ContactsContract.Contacts.DISPLAY_NAME));


                        Bitmap photo = null;


                        try {
                            InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(),
                                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.valueOf(id)));

                            if (inputStream != null) {
                                photo = BitmapFactory.decodeStream(inputStream);
                                inputStream.close();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        try (Cursor pCur = cr.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id}, null)) {
                            if (pCur.moveToNext()) {
                                phoneNo = pCur.getString(pCur.getColumnIndex(
                                        ContactsContract.CommonDataKinds.Phone.NUMBER));

                                addToList(name, phoneNo, "", Contact.encodeImage(photo));
                            }
                        }

                    }


                }
            }
        }
    }

}
