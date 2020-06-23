import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

public class PrintUtils {

    public static Logger logger = LoggerFactory.getLogger("PrintUtils");

    static void printDomainsStatistics(Map<String, Integer> filteredUrls, int totalCount) {
        String message = "Processed: "+totalCount+" requests\nFound "+filteredUrls.size()+" requests to 3-party:";
        printBigMessage(message);

       /* Map<String, Integer> countMap = new HashMap<>();
        for (Checker ch: filteredUrls)  {
            Integer currentCount = countMap.get(ch.getDomain());
            if (currentCount != null) {
                countMap.put(ch.getDomain(), ++currentCount);
            }
            else {
                countMap.put(ch.getDomain(), 1);
            }
        }*/

        for (String domain: filteredUrls.keySet())  {
            logger.info(filteredUrls.get(domain)+" times:\n"+domain);
        }

        logger.info("\n");
    }

    public static void printBigMessage(String message) {
        logger.info("\n*********************************************************");
        logger.info(message);
        logger.info("*********************************************************\n");
    }
}
