package com.example.safe.Model;

import android.content.Context;

import com.example.safe.Database.ContactDao;
import com.example.safe.Database.DbSingleton;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ContactList extends AbstractList<Contact> {
    private List<Contact> list;
    private List<Observer> observers;

    public interface Observer {
        void notifyAdd(Contact contact);
        void notifyDelete(Contact contact);
    }

    public ContactList(ContactDao dao) {
        list = Collections.synchronizedList(new ArrayList<>(dao.getAll()));
        observers = Collections.synchronizedList(new LinkedList<Observer>());
    }

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean add(final Contact newContact) {
        for(Observer o : observers)
            o.notifyAdd(newContact);
        list.add(newContact);
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
            for(Observer o : observers)
                o.notifyDelete(toRemove);
            list.remove(toRemove);
            return true;
        }
        return false;
    }

    @Override
    public Contact remove(final int position) {
        if (position < 0 || position >= list.size())
            return null;
        final Contact toRemove = get(position);
        for(Observer o : observers)
            o.notifyDelete(toRemove);
        list.remove(position);
        return toRemove;
    }
}
