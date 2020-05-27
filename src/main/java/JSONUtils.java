import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

public class JSONUtils {

    private static JSONArray getEntries(JSONObject jsonObject) {
        return (JSONArray)((JSONObject) jsonObject.get("log")).get("entries");
    }

    static Iterator<JSONObject> getEntriesIterator(String requestFileName, JSONParser parser) throws IOException, ParseException {
        Object obj = parser.parse(new FileReader(requestFileName));

        JSONObject jsonObject = (JSONObject) obj;

        JSONArray entries = getEntries(jsonObject);
        return (Iterator<JSONObject>) entries.iterator();
    }
}
