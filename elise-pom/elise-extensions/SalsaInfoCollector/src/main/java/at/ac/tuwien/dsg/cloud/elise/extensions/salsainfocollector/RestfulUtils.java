/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.extensions.salsainfocollector;

import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaCenterConnector;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Duc-Hung LE
 */
public class RestfulUtils {

    static Logger logger = LoggerFactory.getLogger(RestfulUtils.class);

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

    public static String queryDataToCenter(String input_url, HttpVerb method, String data, String type, String accept) {
        try {
            URL url = new URL(input_url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method.toString());

            if (accept.equals("")) {
                conn.setRequestProperty("Accept", MediaType.TEXT_PLAIN);
            } else {
                conn.setRequestProperty("Accept", accept);
            }

            if (type.equals("")) {
                conn.setRequestProperty("Type", MediaType.TEXT_PLAIN);
            } else {
                conn.setRequestProperty("Type", type);
            }
            logger.debug("Execute a query. URL: " + url + ". Method: " + method + ". Data: " + data + ". Sending type:" + type + ". Recieving type: " + accept);

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            String output;
            String result = "";

            while ((output = br.readLine()) != null) {
                System.out.println(output);
                result += output;
            }
            conn.disconnect();

            return result;
        } catch (Exception e) {
            logger.error("Error when executing the query. Error: " + e);
            return null;
        }
    }
}
