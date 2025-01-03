package org.example;

import org.example.request.Request;
import org.example.request.RequestType;
import org.example.response.Response;
import org.example.response.ResponseForCountry;
import org.example.response.ResponseType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    //intervalul de timp delta x la care se trimit mesaje

    private static final int dx = 1;
    //cate trebuie sa trimita deodata un client
    private static final int sendUnit = 7;
    private static final List<String> files = new ArrayList<>();


    public static void main(String[] args) throws InterruptedException, IOException {
        // Configurarea handler-ului pentru a vizualiza logurile în consolă
//        ConsoleHandler consoleHandler = new ConsoleHandler();
//        consoleHandler.setLevel(Level.ALL);  // Setăm nivelul de logare la ALL pentru a captura toate mesajele
//        logger.addHandler(consoleHandler);

        // Setarea nivelului de logare pentru logger
        configureLogger();
        logger.info("Starting server...");


        if (args.length < 1) {
            logger.severe("You have to provide the country code");
            System.exit(1);
        }
        var countryCode = Integer.parseInt(args[0]);
        logger.info("Country code provided: " + countryCode);

        for (int j = 1; j <= 10; j++) {
            files.add("C:\\Users\\bianc\\IdeaProjects\\ppd\\consumer-producer\\Client\\src\\main\\resources\\InputFile\\" + "C" + countryCode + "_P" + j + ".txt");
        }
        logger.info("Files to process: " + files);

        List<Result> buffer = new ArrayList<>();
        for (String file : files) {
            logger.info("Reading file: " + file);
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(" ");
                    int firstNumber = Integer.parseInt(parts[0].trim());
                    int secondNumber = Integer.parseInt(parts[1].trim());
                    buffer.add(new Result(firstNumber, secondNumber, "C" + countryCode));
                    logger.info("Added result to buffer: " + firstNumber + ", " + secondNumber);

                    if (buffer.size() == sendUnit) {
                        logger.info("Sending request with buffer size: " + buffer.size());
                        Request request = new Request(RequestType.SCORE_UPDATE, buffer, file);
                        sendRequestToServer(request);
                        buffer.clear();
                        logger.info("Buffer cleared after sending request");
                        Thread.sleep(dx * 1000);
                    }
                }

                Request request = new Request(RequestType.PARTIAL_RESULT, null, file);
                Response response = sendRequestToServer(request);
                assert response != null;
                var data = response.getData();
                System.out.println("Partial Ranking:");
                for(var r : data) {
                    System.out.println(r.getCountry() + ", " + r.getScore());
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        logger.info("Requesting final ranking for country code: " + countryCode);
        Response finalRankingResponse;
        int maxRetries = 7;
        int retries = 0;
        do {
            finalRankingResponse = sendRequestToServer(new Request(RequestType.FINAL_RESULT, null, "C" + countryCode));
            logger.info("Attempt " + (retries + 1) + ": Received response for final ranking");

            if (finalRankingResponse != null && finalRankingResponse.getResponseType() == ResponseType.SUCCESS) {
                break;
            }
            retries++;
            Thread.sleep(10000); // Wait for 10 seconds before retrying
        } while (retries < maxRetries);

        if (finalRankingResponse != null && finalRankingResponse.getResponseType() == ResponseType.SUCCESS) {
            var data = finalRankingResponse.getData();
            var data_result_participanti = finalRankingResponse.getData_result_participanti();
            System.out.println("Final Ranking:");
            for(var r : data) {
                System.out.println(r.getCountry() + ", " + r.getScore());
            }
            System.out.println("Final Ranking for participants:");
            for(var r : data_result_participanti) {
                System.out.println(r.getId() + ", " + r.getScore());
            }
        } else {
            System.out.println("Max retries reached for final ranking");
        }
    }

    public static Response sendRequestToServer(Request request) {
        System.out.println("Sending request to server...");
        try (Socket socket = new Socket("127.0.0.1", 50000);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
            out.writeObject(request);
            out.flush();
            logger.info("Request sent, waiting for response...");

            Response response = (Response) in.readObject();
            logger.info("Received response from server: " + response.getResponseType());
            return response;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    private static void configureLogger() throws IOException {
        FileHandler fileHandler = new FileHandler("logs/server.log", true);
        fileHandler.setFormatter(new SimpleFormatter());
        logger.addHandler(fileHandler);
        logger.setLevel(Level.INFO);
    }
}
