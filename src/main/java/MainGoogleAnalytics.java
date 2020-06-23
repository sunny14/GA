import com.google.gson.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainGoogleAnalytics {

    private static final Logger logger = LoggerFactory.getLogger("MainGoogleAnalytics");

    private static final String CHROME_DETAILS  ="Google Chrome Version 83.0.4103.97 (Official Build) (64-bit)";
    private static final String IE_DETAILS = "Internet Explorer Version 11.900.18362.0, updated to 11.0.195";

    public static void main(String[] args) throws IOException {

        String requestFileName = getArg(args, "input");

        String header = getHeader(requestFileName);
        PrintUtils.printBigMessage(header);

        List<Checker> filteredUrls = printAll(requestFileName);
        analyze(filteredUrls);

    }

    private static String getHeader(String requestFileName) {
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        System.out.println();
        String details = requestFileName.contains("chrome") ? CHROME_DETAILS : IE_DETAILS;
        return formatter.format(date)+"\n"+details;
    }

    private static void analyze( List<Checker> urls ) {
       /* Properties badParams = getBadParams();
        Properties pii = getPII();*/


        if (urls.size() >0) {
            String message = "Analyzing requests to 3-party for userID=" + urls.get(urls.size()-1).getUser();
            PrintUtils.printBigMessage(message);
        }
        for (Checker ch : urls) {

            logger.info("url:\n"+ch.getUrl());

            if (ch.hasParams()) {
                String pageMsg = "Current page: " + ch.getCause();
                String eventMsg = getEventMsg(ch);
                String sentDataMsg = "Following information was sent to " + ch.getDomain() + ":";
                PrintUtils.printBigMessage(pageMsg + "\n" + eventMsg + "\n" + sentDataMsg);
                ch.printParams();

                //test if any dangerous params are used
               /* ch.lookPII(pii);

                ch.lookBadParams(badParams);*/
            }
        }
    }

    private static String getEventMsg(Checker ch) {
        String event;
        if (ch.getEventAction() == null && ch.getEventCategory() == null)    {
            event = "none";
        }
        else {
            event = ch.getEventAction()+" at "+ch.getEventCategory();
        }
        return "Event: "+event;
    }

 /*   private static Properties getGoodDomains() throws IOException {
        return getProps("/home/oem/IdeaProjects/GA/src/main/resources/bl_domains.properties");
    }

    private static Properties getPII() throws IOException {
        return getProps("/home/oem/IdeaProjects/GA/src/main/resources/pii.properties");

    }

    private static Properties getBadParams() throws IOException {
        return getProps("/home/oem/IdeaProjects/GA/src/main/resources/bad_params.properties");
    }*/

    private static Properties getProps(String absPath) throws IOException {
        FileReader reader=new FileReader(absPath);
        Properties dangerousParams = new Properties();
        dangerousParams.load(reader);

        return dangerousParams;
    }

    private static List<Checker> printAll(String requestFileName) {

        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                .setPrettyPrinting()
                .create();
        JSONParser parser = new JSONParser();

        Map<String, Integer> filteredUrls = new HashMap<>();
        List<Checker> checkersList = new ArrayList<>();
        int count = 0;
        try {

            Iterator<JSONObject> iterator = JSONUtils.getEntriesIterator(requestFileName, parser);

            while (iterator.hasNext()) {

                count++;

                  JSONObject request = (JSONObject)iterator.next().get("request");
                  String url = (String) request.get("url");
                  String domain = null;
                try {
                    domain = ParseUtils.getDomain(url);

                    //filter Leumi domains
                    if (domain.contains("leumi.co.il") || domain.contains("bankleumi.co.il")) {
                        continue;
                    }

                    //filter Discount domains
                    /*if (domain.contains("telebank.co.il") || domain.contains("discountbank")) {
                        continue;
                    }*/

                }catch (Throwable th)   {
                    System.out.println("failed to get domain in : \n"+url);
                    continue;
                }

                urlDecode(request);

                String prettyJsonString = gson.toJson(request);
                PrintUtils.printBigMessage(prettyJsonString);
                updateStatistics(filteredUrls, domain);
                checkersList.add(new Checker(url));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        PrintUtils.printDomainsStatistics(filteredUrls, count);

        return checkersList;

    }

    private static void updateStatistics(Map<String, Integer> filteredUrls, String domain) {
        Integer val = filteredUrls.get(domain);
        if ( val == null)  {
            val = 1;
        }
        filteredUrls.put(domain, val);
    }

    private static void urlDecode(JSONObject request) throws UnsupportedEncodingException {
        String url = (String)request.get("url");
        request.put("url", URLDecoder.decode(url));

        JSONArray headers = (JSONArray)request.get("headers");
        for (Object header : headers)  {
            String val = (String) ((JSONObject)header).get("value");
            ((JSONObject)header).put("value", URLDecoder.decode(val, "UTF-16"));
        }

        JSONArray qStrings = (JSONArray)request.get("queryString");
        for (Object param : qStrings)  {
            String val = (String) ((JSONObject)param).get("value");
            ((JSONObject)param).put("value", URLDecoder.decode(val));
        }

    }


    private static String getArg(String[] args, String param) {
        for (String arg : args) {
            if(arg.startsWith(param))   {
                return arg.split("=")[1];
            }
        }

        return null;
    }
}
