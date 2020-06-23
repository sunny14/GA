import org.apache.commons.io.input.BOMInputStream;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.Iterator;

public class JSONUtils {

    private static JSONArray getEntries(JSONObject jsonObject) {
        return (JSONArray)((JSONObject) jsonObject.get("log")).get("entries");
    }

    static Iterator<JSONObject> getEntriesIterator(String requestFileName, JSONParser parser) throws IOException, ParseException {
        InputStream is = skipBom(requestFileName);
        Object obj = parser.parse(new InputStreamReader(is));

        JSONObject jsonObject = (JSONObject) obj;

        JSONArray entries = getEntries(jsonObject);
        return (Iterator<JSONObject>) entries.iterator();
    }

    private static InputStream skipBom (String requestFileName) throws IOException {
        InputStream in = new FileInputStream(requestFileName);
        BOMInputStream bomIn = new BOMInputStream(in);
        if (bomIn.hasBOM()) {

        }

        return bomIn;
    }
}
