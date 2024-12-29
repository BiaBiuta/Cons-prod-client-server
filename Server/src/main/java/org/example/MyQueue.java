package org.example;
import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyQueue<T> {
    private final LinkedList<T> queue = new LinkedList<>();
    private final int MAX_CAPACITY = 100; // Can be set to 50 or 100 as required
    private final Lock queueLock = new ReentrantLock();
    private final Condition notFull = queueLock.newCondition();
    private final Condition notEmpty = queueLock.newCondition();
    private boolean producersFinished = false;
    public void finish() {
        queueLock.lock();
        notEmpty.signalAll();
        notFull.signalAll();
        queueLock.unlock();
    }
    public void enqueue(T element) throws InterruptedException {
        queueLock.lock();
        try {
            while (queue.size() >= MAX_CAPACITY) {
                notFull.await(); // Astept pana cand notFull e pe true adica cand mai am loc in coada
            }
            queue.add(element);
            notEmpty.signal(); // astept pana cand notEmpty e pe true adica pot sa consum din ea
        } finally {
            queueLock.unlock();
        }
    }

    public T dequeue() throws InterruptedException {
        queueLock.lock();
        try {

            while (queue.isEmpty() && !producersFinished) { // Adăugăm verificarea flagului
                notEmpty.await(); // Așteptăm până când nu este goală sau producătorii termină
            }
            if (queue.isEmpty() && producersFinished) {
                return null; // Dacă coada este goală și producătorii au terminat, ies
            }
            T element = queue.remove();
            notFull.signal(); // Notific că există spațiu liber în coadă
            return element;
        } finally {
            queueLock.unlock();
        }
    }


    public int size() {
        return queue.size();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public void setProducersFinished() {
        queueLock.lock();
        try {
            producersFinished = true;
            notEmpty.signalAll(); // Wake up all waiting consumers
        } finally {
            queueLock.unlock();
        }
    }

    // This method allows consumers to check if producers are finished
    public boolean isProducersFinished() {
        return producersFinished;
    }
}