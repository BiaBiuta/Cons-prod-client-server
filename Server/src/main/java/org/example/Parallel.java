package org.example;

import org.example.*;
import org.example.request.Request;
import org.example.request.RequestType;
import org.example.response.Response;
import org.example.response.ResponseResult;
import org.example.response.ResponseType;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Pair {
    public int id;
    public String country;

    Pair(int id, String country) {
        this.id = id;
        this.country = country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair)) return false;
        Pair pair = (Pair) o; // Cast explicit
        return Objects.equals(id, pair.id) && Objects.equals(country, pair.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, country);
    }
}

class MyListBlack {
    List<Pair> list = new ArrayList<>();
    Lock lock = new ReentrantLock();

    public boolean contains(Pair pair) {
        lock.lock();
        try {
            return list.contains(pair);
        } finally {
            lock.unlock();
        }
    }

    public void add(Pair pair) {
        lock.lock();
        try {
            list.add(pair);
        } finally {
            lock.unlock();
        }
    }

    public List<Pair> getList() {
        return list;
    }
}

public class Parallel {
    private static final int PORT = 50000;
    static int p_r = 2;
    static int p_w = 4;
    private static final ExecutorService producer = Executors.newFixedThreadPool(p_r);
    private static final int totalClients = 5;
    private static final CountDownLatch allScoresSubmitted = new CountDownLatch(totalClients);
    ExecutorService producerPool = Executors.newFixedThreadPool(p_r);
    private static final Map<Integer, ReentrantLock> access = new ConcurrentHashMap<>();
    private static final MyList resultList = new MyList();
    private static final MyListBlack blackList = new MyListBlack();
    private static final MyQueue<Node> queue = new MyQueue<>();
    private static final Map<String, Boolean> finishedCountries = new HashMap<>(){
        @Override
        public Boolean get(Object key) {
            return super.getOrDefault(key, false); // Default false
        }
    };
    private static final AtomicInteger countriesFinalResultLeft = new AtomicInteger(totalClients); // Countries that processed all
    private static final AtomicInteger countriesLeft = new AtomicInteger(totalClients); // Countries left to process

    public static void printAllThreads() {
        Map<Thread, StackTraceElement[]> threads = Thread.getAllStackTraces();
        for (Map.Entry<Thread, StackTraceElement[]> entry : threads.entrySet()) {
            Thread thread = entry.getKey();
            StackTraceElement[] stackTrace = entry.getValue();
            System.out.println("Thread: " + thread.getName() + " (State: " + thread.getState() + ")");
            for (StackTraceElement element : stackTrace) {
                System.out.println("  at " + element);
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        MyQueue<Node> synchronizedQueue = new MyQueue<>();
        for (int i = 1; i < 449; ++i) {
            access.put(i, new ReentrantLock());
        }

        // ExecutorService producerPool = Executors.newFixedThreadPool(p_r);
//        ExecutorService consumerPool = Executors.newFixedThreadPool(p_w);
//
//        List<Future<?>> producerFutures = new ArrayList<>();

        Thread[] writersThreads = new Thread[p_w];

        for (int i = 0; i < p_w; ++i) {
            Thread thread = new Writer();
            writersThreads[i] = thread;
            thread.start();
        }
        long start_t = System.nanoTime();
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started, listening on port " + PORT);

            while (countriesFinalResultLeft.get() != 0) {
                try {
                    final Socket clientSocket = serverSocket.accept();
                    System.out.println(countriesFinalResultLeft.get() + " countries left");
                    System.out.println("New client connected: " + clientSocket);
                    producer.submit(new ClientHandler(clientSocket));
                    System.out.println("queue_size "+queue.size());
                    Thread.sleep(500);
                } catch (IOException | InterruptedException e) {
                    System.err.println("Exception caught when trying to listen on port " + PORT + " or listening for a connection");
                    System.err.println(e.getMessage());
                }

            }
            serverSocket.close();
            System.out.println("am iesit din while de client HAndler ");

        } catch (IOException e) {
            System.err.println("Could not listen on port " + PORT);
            System.err.println(e.getMessage());
        }



//        for (Future<?> future : producerFutures) {
//            try {
//                future.get();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        // Marchează că producătorii au terminat
//        synchronizedQueue.setProducersFinished();
        Arrays.stream(writersThreads).forEach(thread -> {
            try {
                thread.join();
                System.out.println("joined 1 writer thread");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        // Oprește thread pool-urile

        System.out.println("Am ieșit toți");

        printAllThreads();
        resultList.sort();
        resultList.showList();

        Utils.writeResultParalell(resultList, "ResultParallel.txt");
        long end_t = System.nanoTime();

        assert (Utils.areFilesEqual("Result.txt", "ResultParallel.txt"));

        System.out.println("Execution time: " + (double) (end_t - start_t) / 1000000);
        producer.shutdown();
    }

    static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream()); ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {

                Request request = (Request) in.readObject();
                System.out.println("Received request: " + request.getRequestType());

                try {
                    if (request.getRequestType() == RequestType.SCORE_UPDATE) {
                        System.out.println("Processing SCORE_UPDATE request.");
                        var data = request.getData();
                        data.forEach(result -> {
                            try {
                                var id = result.getId();
                                var country = result.getCountryName();
                                var score = result.getScore();
                                var participant = new Participant(id, score, country);
                                Node node = new Node(participant, null, null);
                                queue.enqueue(node);
                                System.out.println("Added participant: " + participant);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        out.writeObject(new Response(ResponseType.SUCCESS, null));
                        out.flush();
                    }  else if (request.getRequestType() == RequestType.FINAL_RESULT) {
                        System.out.println("Processing FINAL_RESULT request.");
                        var country = request.getCountry();
                        System.out.println("Country: " + country);
                        if (!finishedCountries.get(country)) {
                            System.out.println(country + " finished."+ finishedCountries.size()+" "+finishedCountries.get(country));
                            countriesLeft.decrementAndGet();
                            finishedCountries.put(country, true);
                            System.out.println("Country " + country + " finished. Remaining countries: " + countriesLeft.get());
                        }

                        if (countriesLeft.get() == 0) {
                            countriesFinalResultLeft.decrementAndGet();
                            Future<Map<String, Integer>> futureResult = producer.submit(() -> {
                                Map<String, Integer> result = new HashMap<>();
                                //resultList.showList();
                                resultList.sort();
                                resultList.getItemsAsList().forEach(participant -> {
                                    result.merge(participant.getCountry(), participant.getScore(), Integer::sum);
                                });
//
                                return result;
                            });


                            List<Result> resul = resultList.showList();
                            out.writeObject(new Response(ResponseType.SUCCESS, resul));
                            out.flush();
                            queue.finish();
                            System.out.println("Final result sent.");
                            System.out.println("Countries left: " + countriesFinalResultLeft.get());
                            if(countriesLeft.get() == 0) {
                                System.out.println("All countries finished.");
                                if (clientSocket != null && !clientSocket.isClosed()) {
                                    try {
                                        clientSocket.close();
                                        System.out.println("Client socket closed.");
                                    } catch (IOException e) {
                                        System.err.println("Error closing client socket: " + e.getMessage());
                                    }
                                }

                            }
                        } else {
                            out.writeObject(new Response(ResponseType.FAILURE, null));
                            out.flush();
                            System.out.println("Not all countries finished yet.");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    out.writeObject(new Response(ResponseType.FAILURE, null));
                    out.flush();
                }
                clientSocket.close();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    public static class Writer extends Thread {
        @Override
        public void run() {
            try {
                while (!queue.isEmpty() || !queue.isProducersFinished()) {
                    Participant participant = null;
                    Node node = null;
                    try {
                        node = queue.dequeue();
                        if (node == null) {
                            // Dacă node este null (coada este goală și producătorii au terminat)
                            if (queue.isProducersFinished()) {
                                break;
                            }
                        } else {
                            participant = node.getData();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (participant == null) {
                        continue;
                    }

                    System.out.println("Consumer processing participant: " + participant);

                    access.get(participant.getId()).lock();

                    if (!blackList.contains(new Pair(participant.getId(), participant.getCountry()))) {
                        if (participant.getScore() == -1) {
                            resultList.delete(participant);
                            blackList.add(new Pair(participant.getId(), participant.getCountry()));
                        } else {
                            Node actual = resultList.update(participant);

                            if (actual == null) {
                                resultList.add(participant);
                            }
                        }
                    }

                    access.get(participant.getId()).unlock();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class Consumer {
        MyQueue<Node> queue;
        MyList list;

        public Consumer(MyQueue<Node> queue, MyList list) {
            this.queue = queue;
            this.list = list;
        }

        public void run() {
            try {
                while (!queue.isEmpty() || !queue.isProducersFinished()) {
                    Participant participant = null;
                    Node node = null;
                    try {
                        node = queue.dequeue();
                        if (node == null) {
                            // Dacă node este null (coada este goală și producătorii au terminat)
                            if (queue.isProducersFinished()) {
                                break;
                            }
                        } else {
                            participant = node.getData();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (participant == null) {
                        continue;
                    }

                    System.out.println("Consumer processing participant: " + participant);

                    access.get(participant.getId()).lock();

                    if (!blackList.contains(new Pair(participant.getId(), participant.getCountry()))) {
                        if (participant.getScore() == -1) {
                            resultList.delete(participant);
                            blackList.add(new Pair(participant.getId(), participant.getCountry()));
                        } else {
                            Node actual = resultList.update(participant);

                            if (actual == null) {
                                resultList.add(participant);
                            }
                        }
                    }

                    access.get(participant.getId()).unlock();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
