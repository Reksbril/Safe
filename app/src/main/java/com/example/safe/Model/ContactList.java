package com.example.safe.Model;

import android.content.Context;

import com.example.safe.Database.ContactDao;
import com.example.safe.Database.DbSingleton;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ContactList extends AbstractList<Contact> {
    private ContactDao dao;
    private ArrayList<Contact> list;

    public ContactList(ContactDao dao) {
        this.dao = dao;
        list = new ArrayList<>(dao.getAll());
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean add(final Contact newContact) {
        if(contains(newContact))
            return false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                dao.insert(newContact);
                list.add(newContact);
            }
        }).start();
        return true;
    }

    @Override
    public Contact get(int index) {
        return list.get(index);
    }

    @Override
    public boolean remove(Object obj) {
        if(!(obj instanceof Contact))
            return false;
        final Contact toRemove = (Contact) obj;
        if(list.contains(toRemove)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    dao.delete(toRemove);
                    list.remove(toRemove);
                }
            }).start();
            return true;
        }

        return false;
    }

    @Override
    public Contact remove(final int position) {
        if(position < 0 || position >= list.size())
            return null;
        final Contact toRemove = get(position);
        new Thread(new Runnable() {
            @Override
            public void run() {
                dao.delete(toRemove);
                list.remove(position);
            }
        }).start();
        return toRemove;
    }
}
