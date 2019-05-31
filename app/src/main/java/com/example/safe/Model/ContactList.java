package com.example.safe.Model;

import com.example.safe.Database.ContactDao;
import com.example.safe.Database.DbSingleton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ContactList {
    private List<Contact> contacts;
    private List<String> numbers;
    private ContactDao dao;
    private volatile boolean ready = false;

    public ContactList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                dao = DbSingleton.getInstance().db.contactDao();
                contacts = dao.getAll();

                numbers = new ArrayList<>();
                for(Contact contact : contacts)
                    numbers.add(contact.getName());
                ready = true;
            }
        }).start();
    }

    public boolean delete(String number) {
        if(!ready)
            return false; //TODO
        Contact toDelete = null;
        for(Contact c : contacts)
            if(c.getNumber().equals(number)) {
                toDelete = c;
                break;
            }
        if(toDelete == null)
            return false;
        dao.delete(toDelete);
        contacts.remove(toDelete);
        numbers.remove(toDelete.getNumber());
        return true;
    }

    public boolean add(Contact newContact) {
        if(!ready)
            return false;
        for(Contact c : contacts)
            if(c.getNumber().equals(newContact.getNumber()))
                return false;
        dao.insert(newContact);
        contacts.add(newContact);
        numbers.add(newContact.getNumber());
        return true;
    }

    public List<String> getNumbers() {
        return Collections.unmodifiableList(numbers);
    }
}
