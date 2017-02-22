package net.backtothefront;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// courtesy of: http://backtothefront.net/2011/storing-sets-keyvalue-pairs-single-db-column-hibernate-postgresql-hstore-type/
public class HstoreHelper {

    private static final String K_V_SEPARATOR = "=>";
    private static final Map<String, String> escapeStrings = new HashMap<String, String>();
    private static final Logger LOGGER = LoggerFactory.getLogger(HstoreHelper.class);

    static {
        escapeStrings.put(",", "__comma__");
        escapeStrings.put("=", "__equals__");
        escapeStrings.put("\"", "__quote__");
    }

    private static String escapeValue(String value) {
        String escaped = value;
        for (String str : escapeStrings.keySet()) {
            String strEscaped = escapeStrings.get(str);
            escaped = escaped.replaceAll(str, strEscaped);
        }

        return escaped;
    }

    private static String unescapeValue(String value) {
        String unescaped = value;
        for (String str : escapeStrings.keySet()) {
            String strEscaped = escapeStrings.get(str);
            unescaped = unescaped.replaceAll(strEscaped, str);
        }

        return unescaped;
    }

    public static String toString(Map<String, String> m) {
        if (m.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        int n = m.size();
        for (String key : m.keySet()) {
            String escapedValue = escapeValue(m.get(key));
            sb.append("\"" + key + "\"" + K_V_SEPARATOR + "\"" + escapedValue
                    + "\"");
            if (n > 1) {
                sb.append(", ");
                n--;
            }
        }
        return sb.toString();
    }

    public static Map<String, String> toMap(String s) {
        Map<String, String> m = new HashMap<String, String>();
        if (StringUtils.isEmpty(s)) {
            return m;
        }
        String[] tokens = s.split(", ");
        for (String token : tokens) {
            try {
                String[] kv = token.split(K_V_SEPARATOR);
                String k = kv[0];
                k = k.trim().substring(1, k.length() - 1);
                String v = kv[1];
                v = v.trim().substring(1, v.length() - 1);
                m.put(k, unescapeValue(v));
            } catch (java.lang.ArrayIndexOutOfBoundsException oob) {
                LOGGER.warn("{}: failed to be mapped", token, oob);
            }
        }
        return m;
    }
}