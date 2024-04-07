package com.gearwenxin.core;

import org.apache.commons.codec.EncoderException;
import org.apache.http.client.utils.URIBuilder;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.codec.net.URLCodec;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.*;

public class AuthEncryption {

    private static final String[] DEFAULT_HEADERS = {"host", "content-length", "content-type", "content-md5"};
    private static final String ACCESS_KEY = "";
    private static final String SECRET_KEY = "";
    private static final String AUTH_VERSION = "1";
    private static final String EXPIRATION_IN_SECONDS = "1800";
    private static String g_signed_headers = "";

    public static void main(String[] args) throws URISyntaxException {
        URI uri = new URIBuilder()
                .setScheme("http")
                .setHost("https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions")
                .setPath("/path")
                .setParameter("AccessKey", ACCESS_KEY)
                .setParameter("SecretKey", SECRET_KEY)
                .build();

        System.out.println(generateAuthorization(uri));
    }

    private static String getTimestamp() {
        return Instant.now().toString().replace("Z", "+0000");
    }

    private static String normalize(String string, boolean encodingSlash) {
        if (string == null) {
            return "";
        }
        String result = null;
        try {
            result = new URLCodec().encode(string);
        } catch (EncoderException e) {
            throw new RuntimeException(e);
        }
        result = result.replace("+", "%20");
        result = result.replace("*", "%2A");
        result = result.replace("%7E", "~");
        if (!encodingSlash) {
            result = result.replace("%2F", "/");
        }
        return result;
    }

    private static String generateCanonicalUri(URI uri) {
        if (uri.getPath() == null) {
            return "";
        }
        return normalize(uri.getPath(), true);
    }

    private static String generateCanonicalQueryString(URI uri) {
        List<NameValuePair> params = URLEncodedUtils.parse(uri, Charset.defaultCharset());
        params.removeIf(param -> param.getName().equalsIgnoreCase("authorization"));
        params.sort(Comparator.comparing(NameValuePair::getName));
        StringBuilder sb = new StringBuilder();
        for (NameValuePair param : params) {
            sb.append(normalize(param.getName(), false)).append("=").append(normalize(param.getValue(), false)).append("&");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    private static String generateCanonicalHeaders(URI uri) {
        Map<String, String> headers = new HashMap<>();
        headers.put("host", uri.getHost());
        headers.put("content-length", "0");
        headers.put("content-type", "application/json");
        headers.put("content-md5", "");

        List<String> keyStrList = new ArrayList<>(Arrays.asList(DEFAULT_HEADERS));
        List<String> usedHeaderStrList = new ArrayList<>();
        for (String key : keyStrList) {
            String value = headers.get(key);
            if (value == null || value.isEmpty()) {
                continue;
            }
            usedHeaderStrList.add(normalize(key, false) + ":" + normalize(value, false));
        }
        Collections.sort(usedHeaderStrList);
        List<String> usedHeaderKeys = new ArrayList<>();
        for (String item : usedHeaderStrList) {
            usedHeaderKeys.add(item.split(":")[0]);
        }
        g_signed_headers = String.join(";", usedHeaderKeys);
        return String.join("\n", usedHeaderStrList);
    }

    private static String generateAuthorization(URI uri) {
        String timestamp = getTimestamp();
        String signingKeyStr = "bce-auth-v" + AUTH_VERSION + "/" + ACCESS_KEY + "/" + timestamp + "/" + EXPIRATION_IN_SECONDS;
        String signingKey = HmacUtils.hmacSha256Hex(SECRET_KEY, signingKeyStr);

        String canonicalUri = generateCanonicalUri(uri);
        String canonicalQueryString = generateCanonicalQueryString(uri);
        String canonicalHeaders = generateCanonicalHeaders(uri);

        String canonicalRequest = "GET" + "\n" + canonicalUri + "\n" + canonicalQueryString + "\n" + canonicalHeaders;
        String signature = HmacUtils.hmacSha256Hex(signingKey, canonicalRequest);

        return signingKeyStr + "/" + g_signed_headers + "/" + signature;
    }
}
