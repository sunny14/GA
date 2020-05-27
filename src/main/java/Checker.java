import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Checker {

    private static final String CAUSE = "dp";
    private static final String USER = "uid";


    private String uri;
    private String domain;
    private Map<String, String> params = new HashMap<>();

    public Checker(String url)  {
        String [] storage = url.split("\\?");
        if (storage.length>=1)  {
            uri = storage[0];
            domain = ParseUtils.getDomain(this.uri);

        }
        if (storage.length == 2)   {
            String params = storage[1];
            initParams(params);
        }
    }

    public void lookPII(Properties userDetais)    {
        for (Object detail: userDetais.values())    {
            String detailStr = (String)detail;
            lookDetail(detailStr);
        }
    }

    private void lookDetail(String detailStr) {
        for (String key: params.keySet())  {
            String paramVal = params.get(key);
            if (paramVal.contains(detailStr))   {
                System.out.println("found: "+detailStr+" inside "+key+"="+paramVal);
            }
        }
    }

    public void lookBadParams(Properties badParams) {
        for (Object key: badParams.keySet())    {
            if(this.params.keySet().contains(key))  {
                System.out.println("bad param: "+key+"="+params.get(key)+", which exposes "+badParams.get(key));
            }
        }
    }

    private void initParams(String params) {
        String [] pairs = params.split("&");
        for(String pair : pairs)    {
            String [] p = pair.split("=");
            if (p.length <2)    {
                continue;
            }
            try {
                String decoded = URLDecoder.decode(p[1]).toLowerCase();
                if (!(decoded.equals("not available") || decoded.equals("na"))) {
                    this.params.put(p[0], decoded);
                }
            }catch (Throwable th)   {
                System.out.println(th.getMessage());
            }
        }
    }

    public String getCause() {
        return getParam(CAUSE);
    }

    public String getUser() {
        return getParam(USER);
    }

    private String  getParam(String key)  {
        String val = this.params.get(key);
        return val == null? "unknown" : val;
    }

    public String getDomain() {
        return this.domain;
    }

    public void printParams() {
        for (String paramName: this.params.keySet()) {
            System.out.println(paramName+"="+params.get(paramName));
        }
    }

    public String getEventAction() {
        return this.params.get("ea");
    }

    public String getEventCategory() {
        return this.params.get("ec");
    }
}
