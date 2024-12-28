package org.example;

import org.example.request.Request;
import org.example.request.RequestType;
import org.example.response.Response;
import org.example.response.ResponseType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Main {
    //intervalul de timp delta x la care se trimit mesaje
    private static final int dx = 1;
    //cate trebuie sa trimita deodata un client
    private static final int sendUnit = 20;
    private static final List<String> files = new ArrayList<>();

    public static void main(String[] args) throws InterruptedException {
        if (args.length < 1) {
            System.out.println("You have to provide the country code");
            System.exit(1);
        }
        var countryCode = Integer.parseInt(args[0]);
        System.out.println("Country code provided: " + countryCode);

        for (int j = 1; j <= 10; j++) {
            files.add("C:\\Users\\bianc\\IdeaProjects\\ppd\\consumer-producer\\Client\\src\\main\\resources\\InputFile\\" + "C" + countryCode + "_P" + j + ".txt");
        }
        System.out.println("Files to process: " + files);

        List<Result> buffer = new ArrayList<>();
        for (String file : files) {
            System.out.println("Reading file: " + file);
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    int firstNumber = Integer.parseInt(parts[0].trim());
                    int secondNumber = Integer.parseInt(parts[1].trim());
                    buffer.add(new Result(firstNumber, secondNumber, ""));
                    System.out.println("Added result to buffer: " + firstNumber + ", " + secondNumber);

                    if (buffer.size() == sendUnit) {
                        System.out.println("Sending request with buffer size: " + buffer.size());
                        Request request = new Request(RequestType.SCORE_UPDATE, buffer, null);
                        sendRequestToServer(request);
                        buffer.clear();
                        System.out.println("Buffer cleared after sending request");
                        Thread.sleep(dx * 1000);
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Requesting final ranking for country code: " + countryCode);
        Response finalRankingResponse;
        int maxRetries = 7;
        int retries = 0;
        do {
            finalRankingResponse = sendRequestToServer(new Request(RequestType.FINAL_RESULT, null, "C" + countryCode));
            System.out.println("Attempt " + (retries + 1) + ": Received response for final ranking");

            if (finalRankingResponse != null && finalRankingResponse.getResponseType() == ResponseType.SUCCESS) {
                break;
            }
            retries++;
            Thread.sleep(10000); // Wait for 10 seconds before retrying
        } while (retries < maxRetries);

        if (finalRankingResponse != null && finalRankingResponse.getResponseType() == ResponseType.SUCCESS) {
            var data = finalRankingResponse.getData();
            System.out.println("Final Ranking:");
            for(Result r : data) {
                System.out.println(r.getId() + ", " + r.getCountryName() + ", " + r.getScore());
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
            System.out.println("Request sent, waiting for response...");

            Response response = (Response) in.readObject();
            System.out.println("Received response from server: " + response.getResponseType());
            return response;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
