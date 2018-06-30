package ru.mirea.customHashSet;

import java.util.ArrayList;

class cLinkedList {
    private myNode first;
    private myNode last;

    private class myNode {
        String item;
        myNode next;
        myNode prev;

        myNode(myNode prev, String element, myNode next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }

        int cHashCodeList(String t, ArrayList<cLinkedList> hashSetTable) {
            return t.hashCode() % hashSetTable.size();
        }

        @Override
        public String toString(){
            return this.item;
        }
    }

    void add(String e) {
        final myNode l = last;
        final myNode newNode = new myNode(l, e, null);
        last = newNode;
        if (l == null)
            first = newNode;
        else
            l.next = newNode;
    }

    void remove(String t, int hash, ArrayList<cLinkedList> hashSetTable) {
        for (myNode x = first; x != null; x = x.next) {
            if (hash == x.cHashCodeList(x.item, hashSetTable)) {
                if (t.equals(x.item)) {
                    unlink(x);
                }
            }
        }
    }

    boolean contains(String t, int hash, ArrayList<cLinkedList> hashSetTable) {
        return indexOf(t, hash, hashSetTable) != -1;
    }

    private void unlink(myNode x) {
        final myNode next = x.next;
        final myNode prev = x.prev;

        if (prev == null) {
            first = next;
        } else {
            prev.next = next;
            x.prev = null;
        }

        if (next == null) {
            last = prev;
        } else {
            next.prev = prev;
            x.next = null;
        }
        x.item = null;
    }

    private int indexOf(String t, int hash, ArrayList<cLinkedList> hashSetTable) {
        int index = 0;
        for (myNode x = first; x != null; x = x.next) {
            if (hash == x.cHashCodeList(x.item, hashSetTable)) {
                if (t.equals(x.item))
                    return index;
            }
            index++;
        }
        return -1;
    }
    @Override
    public String toString() {
        ArrayList<String> result = new ArrayList<>();
        for (myNode x = first; x != null; x = x.next) {
            result.add(x.item);
        }
        return String.join(", ", result);
    }
}

