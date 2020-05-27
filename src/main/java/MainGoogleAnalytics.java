import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class MainGoogleAnalytics {

    public static void main(String[] args) throws IOException {

        String requestFileName = getArg(args, "input");
        if (requestFileName == null)    {
            System.out.println("ERROR: input param is missing");
            return;
        }

        List<Checker> filteredUrls = getGoogleAnalyticUrls(requestFileName);
        Properties badParams = getBadParams();
        Properties pii = getPII();


        if (filteredUrls.size() >0) {
            String message = "Analyzing requests to 3-party for userID=" + filteredUrls.get(0).getUser();
            PrintUtils.printBigMessage(message);
        }
        for (Checker ch : filteredUrls) {

            String pageMsg = "Current page: "+ch.getCause();
            String eventMsg = getEventMsg(ch);
            String sentDataMsg = "Following information was sent to "+ch.getDomain()+":";
            PrintUtils.printBigMessage(pageMsg+"\n"+eventMsg+"\n"+sentDataMsg);
            ch.printParams();

           //test if any dangerous params are used
         //   ch.lookPII(pii);

            ch.lookBadParams(badParams);
        }

        //TODO: check cookies

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

    private static Properties getGoodDomains() throws IOException {
        return getProps("/home/oem/IdeaProjects/GA/src/main/resources/bl_domains.properties");
    }

    private static Properties getPII() throws IOException {
        return getProps("/home/oem/IdeaProjects/GA/src/main/resources/pii.properties");

    }

    private static Properties getBadParams() throws IOException {
        return getProps("/home/oem/IdeaProjects/GA/src/main/resources/bad_params.properties");
    }

    private static Properties getProps(String absPath) throws IOException {
        FileReader reader=new FileReader(absPath);
        Properties dangerousParams = new Properties();
        dangerousParams.load(reader);

        return dangerousParams;
    }

    private static List<Checker> getGoogleAnalyticUrls(String requestFileName) {
        JSONParser parser = new JSONParser();
        List<Checker> filteredUrls = new ArrayList<>();
        int count = 0;
        try {
            Iterator<JSONObject> iterator = JSONUtils.getEntriesIterator(requestFileName, parser);


            while (iterator.hasNext()) {

                count++;

                  JSONObject request = (JSONObject)iterator.next().get("request");
                  String url = (String) request.get("url");
                try {
                    String domain = ParseUtils.getDomain(url);
                    if (domain.contains("leumi.co.il") || domain.contains("bankleumi.co.il")) {
                        continue;
                    }

                }catch (Throwable th)   {
                    System.out.println("failed to get domain in : \n"+url);
                }

                filteredUrls.add(new Checker(url));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        PrintUtils.printDomainsStatistics(filteredUrls, count);

        return filteredUrls;
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
