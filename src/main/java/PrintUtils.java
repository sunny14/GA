import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrintUtils {

    static void printDomainsStatistics(List<Checker> filteredUrls, int count) {
        String message = "Processed: "+count+" requests\nFound "+filteredUrls.size()+" requests to 3-party:";
        printBigMessage(message);
        Map<String, Integer> countMap = new HashMap<>();
        for (Checker ch: filteredUrls)  {
            Integer currentCount = countMap.get(ch.getDomain());
            if (currentCount != null) {
                countMap.put(ch.getDomain(), ++currentCount);
            }
            else {
                countMap.put(ch.getDomain(), 1);
            }
        }

        for (String domain: countMap.keySet())  {
            System.out.println(countMap.get(domain)+" times:\n"+domain);
        }

        System.out.println("\n");
    }

    public static void printBigMessage(String message) {
        System.out.println("\n*********************************************************");
        System.out.println(message);
        System.out.println("*********************************************************\n");
    }
}
