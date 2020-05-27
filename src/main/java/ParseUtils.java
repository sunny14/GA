public class ParseUtils {
    public static String getDomain(String url) {
        return url.split("://")[1].split("/")[0];
    }
}
