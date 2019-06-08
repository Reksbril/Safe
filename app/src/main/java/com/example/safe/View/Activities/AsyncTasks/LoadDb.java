package com.example.safe.View.Activities.AsyncTasks;


import android.app.Activity;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.safe.Database.ContactDao;
import com.example.safe.Model.Contact;
import com.example.safe.Model.ContactList;
import com.example.safe.R;
import com.example.safe.View.Activities.ManageContactsActivity;
import com.example.safe.View.ListViews.ChooseContactsList;
import com.example.safe.View.ListViews.ManageContactsList;

public abstract class LoadDb extends AsyncTask<Activity, Void, ArrayAdapter<Contact>> {
    private final ContactDao dao;
    private final boolean choose;

    public LoadDb(ContactDao dao, boolean choose) {
        super();
        this.dao = dao;
        this.choose = choose;
    }


    @Override
    protected ArrayAdapter<Contact> doInBackground(Activity... args) {
        if(args.length != 1)
            throw new RuntimeException("Only one argument will be accepted!");
        if(choose)
            return new ChooseContactsList(args[0],
                    R.layout.list_item_choose,
                    new ContactList(dao.getAll()));
        return new ManageContactsList(
                args[0],
                R.layout.list_item_edit,
                new ContactList(dao.getAll()));
    }

    @Override
    protected abstract void onPostExecute(ArrayAdapter<Contact> result);
}
