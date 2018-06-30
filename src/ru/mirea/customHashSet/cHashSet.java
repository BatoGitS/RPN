package ru.mirea.customHashSet;

import java.util.ArrayList;

public class cHashSet {

    private ArrayList<cLinkedList> hashSetTable = new ArrayList<>();

    public cHashSet() {
        hashSetTable.add(new cLinkedList());
    }

    private int cHash(String t) {
        return t.hashCode() % hashSetTable.size();
    }

    public void add(String t) {
        if (!hashSetTable.get(cHash(t)).contains(t, cHash(t), hashSetTable))
            hashSetTable.get(cHash(t)).add(t);
    }

    public void remove(String t) {
        hashSetTable.get(cHash(t)).remove(t, cHash(t), hashSetTable);
    }

    public boolean contains(String t) {
        return hashSetTable.get(cHash(t)).contains(t, cHash(t), hashSetTable);
    }

    @Override
    public String toString() {
        return hashSetTable.toString();
    }
}
