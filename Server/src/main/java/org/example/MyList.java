package org.example;



import java.util.ArrayList;
import java.util.List;

public class MyList {
    public final Node head = new Node(null, null, null);
    public final Node tail = new Node(null, null, null);

    public MyList() {
        head.next = tail;
        tail.previous = head;
    }
    public List<Node> getElements(){
        List<Node> all = new ArrayList<>();
        Node p = this.head;
        while(p!=null){
            all.add(p);
            p = p.next;
        }
        return all;
    }
    public List<Result> showList() {
        List<Result> results = new ArrayList<>();
        Node p= this.head.next;
        while(p!=this.tail){
            results.add(new Result(p.getData().getId(),p.getData().getScore(),p.getData().getCountry()));
            //System.out.println("("+p.getData().getId()+" , "+p.getData().getScore()+")");
            p=p.next;
        }
        return results;
    }
    public List<Participant> getItemsAsList() {
        List<Participant> list = new ArrayList<>();

        Node actual = head.next;

        while (actual.isNotLastNode()) {
            list.add(actual.getData());
            actual = actual.next;
        }

        return list;
    }

    public void sort() {
        boolean sorted;
        do {
            sorted = true;

            Node actual = head.next;

            while (actual.next != tail) {
                if (actual.getData().getScore() < actual.next.getData().getScore()) {
                    Participant temporaryData = actual.getData();
                    actual.setData(actual.next.getData());
                    actual.next.setData(temporaryData);
                    sorted = false;
                }else if (actual.getData().getScore() == actual.next.getData().getScore()) {
                    if (actual.getData().getId() > actual.next.getData().getId()) {
                        // Dacă id-ul este mai mare, schimbă datele
                        Participant temporaryData = actual.getData();
                        actual.setData(actual.next.getData());
                        actual.next.setData(temporaryData);
                        sorted = false;
                    }
                }

                actual = actual.next;
            }

        } while (!sorted);
    }

    public Node update(Participant participant) {
        Node actual = head.next;
        if (head.next != tail) {
            while (actual.isNotLastNode()) {
                actual.lock();
                if (actual.getData().equals(participant)) {
                    actual.getData().setScore(actual.getData().getScore() + participant.getScore());
                    actual.unlock();
                    return actual;
                }
                actual.unlock();
                actual = actual.next;
            }
        }

        return null;
    }

    public void add(Participant participant) {
        head.lock();
        head.next.lock();

        Node right = head.next;

        Node node = new Node(participant, null, null);
        node.lock();

        head.next = node;
        node.previous = head;
        node.next = right;
        right.previous = node;

        right.unlock();
        node.unlock();
        head.unlock();
    }

    public void delete(Participant participant) {
        head.lock();
        head.next.lock();
        if (head.next == tail) {
            head.unlock();
            head.next.unlock();
            return;
        }
        Node actual = head.next;

        while (actual.isNotLastNode()) {
            actual.next.lock();
            if (actual.getData().getId()==participant.getId()) {
                Node left = actual.previous;
                Node right = actual.next;

                left.next = right;
                right.previous = left;
                left.unlock();
                actual.unlock();
                right.unlock();
                return;
            }
            actual.previous.unlock();
            actual = actual.next;
        }

        actual.previous.unlock();
        actual.unlock();
    }
}