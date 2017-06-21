/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Duc-Hung LE
 */
public class RestHandler {

    public static enum HttpVerb {

        GET, POST, PUT, DELETE, OTHER;

        public static HttpVerb fromString(String method) {
            try {
                return HttpVerb.valueOf(method.toUpperCase());
            } catch (Exception e) {
                return OTHER;
            }
        }
    }

    public static String callRest(String input_url, HttpVerb method, String data, String type, String accept) {
        return callRest(input_url, method, data, type, accept, false);
    }

    public static String callRest(String input_url, HttpVerb method, String data, String type, String accept, boolean quiet) {
        try {
            URL url = new URL(input_url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method.toString());

            if (accept == null || accept.equals("")) {
                conn.setRequestProperty("Accept", MediaType.APPLICATION_JSON);
            } else {
                conn.setRequestProperty("Accept", accept);
            }

            if (type == null || type.equals("")) {
                conn.setRequestProperty("Content-Type", MediaType.APPLICATION_JSON);
            } else {
                conn.setRequestProperty("Content-Type", type);
            }

            if (data != null) {
                conn.setDoOutput(true);
                try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                    System.out.println("Writing XML to stream\n: " + data);
                    wr.write(data.getBytes());
                }
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            String output;
            String result = "";

            while ((output = br.readLine()) != null) {
                if (!quiet) {
                    System.out.println(output);
                }
                result += output;
            }
            conn.disconnect();

            return result;
        } catch (ConnectException e) {
            System.out.println("Failed in calling SALSA. Please check the IP and Port in the configuration.");
            System.out.println("Error message: " + e.getMessage());
            return null;
        } catch (IOException ex) {
            System.out.println("Failed in reading results. Error: " + ex.getMessage());
            return null;
        }
    }
}
