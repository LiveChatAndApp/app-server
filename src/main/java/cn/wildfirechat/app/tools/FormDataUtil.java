package cn.wildfirechat.app.tools;

import cn.wildfirechat.app.enums.HttpRequestEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.util.UriComponentsBuilder;

import javax.net.ssl.SSLContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Modifier;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FormDataUtil {
    public static <T> T post(String url, Object request, Class<T> responseType) throws IOException {
        HttpPost post = new HttpPost(url);
        post.setEntity(new UrlEncodedFormEntity(toFormData(request)));
        return executeHttp(post, responseType);
    }

    public static <T> T post(String url, Object request, Class<T> responseType, HttpRequestEnum enums) throws Exception {
        HttpPost post = new HttpPost(url);
        post.setEntity(new UrlEncodedFormEntity(toFormData(request)));
        return executeHttp(post, responseType, enums);
    }

    public static <T> T post(String url, String jsonString, Class<T> responseType) throws IOException {
        HttpPost post = new HttpPost(url);
        post.setHeader("Content-Type", "application/json");
        post.setEntity(new StringEntity(jsonString, "UTF-8"));
        return executeHttp(post, responseType);
    }

    public static <T> T post(String url, String jsonString, Class<T> responseType, HttpRequestEnum enums) throws Exception {
        HttpPost post = new HttpPost(url);
        post.setHeader("Content-Type", "application/json");
        post.setEntity(new StringEntity(jsonString, "UTF-8"));
        return executeHttp(post, responseType, enums);
    }

    public static <T> T post(String url, String jsonString, Class<T> responseType, Map<String, String> headerMap) throws IOException {
        HttpPost post = new HttpPost(url);
        headerMap.forEach( (k, v) -> post.setHeader(k,v));
        post.setHeader("Content-Type", "application/json");
        post.setEntity(new StringEntity(jsonString, "UTF-8"));
        return executeHttp(post, responseType);
    }

    public static <T> T post(String url, Map request, Class<T> responseType) throws Exception {
        return post(url,request, responseType, HttpRequestEnum.THIRD_GENERAL);
    }
    
    public static <T> T post(String url, Map request, Class<T> responseType, HttpRequestEnum enums) throws Exception {
        HttpPost post = new HttpPost(url);
        List<NameValuePair> list = new ArrayList<>();
        Iterator iterator = request.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String,String> elem = (Map.Entry<String, String>) iterator.next();
            list.add(new BasicNameValuePair(elem.getKey(),String.valueOf(elem.getValue())));
        }
        post.setEntity(new UrlEncodedFormEntity(list,"UTF-8"));
        return executeHttp(post, responseType, enums);
    }

    public static HttpResponse post(String url, Object request) throws IOException {
        HttpPost post = new HttpPost(url);
        post.setEntity(new UrlEncodedFormEntity(toFormData(request)));
        return executeHttpForResponse(post);
    }

    public static HttpResponse post(String url, Map request, HttpRequestEnum enums) throws Exception {
        HttpPost post = new HttpPost(url);
        List<NameValuePair> list = new ArrayList<>();
        Iterator iterator = request.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String,String> elem = (Map.Entry<String, String>) iterator.next();
            list.add(new BasicNameValuePair(elem.getKey(),String.valueOf(elem.getValue())));
        }
        post.setEntity(new UrlEncodedFormEntity(list, "UTF-8"));
        return executeHttpForResponse(post,enums);
    }
    
    public static HttpResponse post(String url, Map request) throws IOException {
        HttpPost post = new HttpPost(url);
        List<NameValuePair> list = new ArrayList<>();
        Iterator iterator = request.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String,String> elem = (Map.Entry<String, String>) iterator.next();
            list.add(new BasicNameValuePair(elem.getKey(),String.valueOf(elem.getValue())));
        }
        post.setEntity(new UrlEncodedFormEntity(list, "UTF-8"));
        return executeHttpForResponse(post);
    }

    public static HttpResponse post(String url, Map request, Map header) throws IOException {
        HttpPost post = new HttpPost(url);
        List<NameValuePair> list = new ArrayList<>();
        Iterator iterator = request.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String,String> elem = (Map.Entry<String, String>) iterator.next();
            list.add(new BasicNameValuePair(elem.getKey(),String.valueOf(elem.getValue())));
        }
        post.setEntity(new UrlEncodedFormEntity(list, "UTF-8"));

        Iterator iteratorHeader = header.entrySet().iterator();
        while(iteratorHeader.hasNext()){
            Map.Entry<String,String> elemHeader = (Map.Entry<String, String>) iteratorHeader.next();
            post.setHeader(elemHeader.getKey(),elemHeader.getValue());
        }
        return executeHttpForResponse(post);
    }

    public static <T> T get(String url, Object request, Class<T> responseType) throws IOException {
        HttpGet get = new HttpGet(toQueryString(url, request));
        return executeHttp(get, responseType);
    }

    public static <T> T get(String url, Map request, Class<T> responseType) throws IOException {
        HttpGet get = new HttpGet(toQueryString(url, request));
        return executeHttp(get, responseType);
    }

    public static <T> T get(String url, Class<T> responseType) throws IOException {
        HttpGet get = new HttpGet(url);
        return executeHttp(get, responseType);
    }

    public static <T> T get(String url, Map request, Class<T> responseType, HttpRequestEnum enums) throws Exception {
        String executeUrl = toQueryString(url, request);
        HttpGet get = new HttpGet(executeUrl);
        return executeHttp(get, responseType, enums);
    }

    public static HttpResponse get(String url, Object request, Map<String, String> headerMap) throws IOException {
        HttpGet get = new HttpGet(toQueryString(url, request));
        System.out.println(get);
        headerMap.forEach( (k, v) -> get.setHeader(k,v));
        return executeHttpForResponse(get);
    }

    public static HttpResponse get(String url, Object request, Map<String, String> headerMap, HttpRequestEnum enums) throws Exception {
        HttpGet get = new HttpGet(toQueryString(url, request));
        System.out.println(get);
        headerMap.forEach( (k, v) -> get.setHeader(k,v));
        return executeHttpForResponse(get, enums);
    }

    public static HttpResponse get(String url, Map request) throws IOException {
        HttpGet get = new HttpGet(toQueryString(url, request));
        return executeHttpForResponse(get);
    }

    public static HttpResponse get(String url) throws IOException {
        HttpGet get = new HttpGet(url);
        return executeHttpForResponse(get);
    }

    private static HttpResponse executeHttpForResponse(HttpRequestBase httpRequestBase) throws IOException {
    	int timeout = 15; // 代付商的連線timeout先拉到60秒
    	RequestConfig config = RequestConfig.custom()
    			  .setConnectTimeout(timeout * 1000)
    			  .setConnectionRequestTimeout(timeout * 1000)
    			  .setSocketTimeout(timeout * 1000).build();
    	
        HttpClient client = HttpClientBuilder
        		.create()
        		.setDefaultRequestConfig(config)
        		.build();
        return client.execute(httpRequestBase);
    }

    private static HttpResponse executeHttpForResponse(HttpRequestBase httpRequestBase, HttpRequestEnum enums) throws Exception {
        int timeout = enums.getSeconds();
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(timeout * 1000)
                .setConnectionRequestTimeout(timeout * 1000)
                .setSocketTimeout(timeout * 1000).build();

        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
        SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
        CloseableHttpClient client = HttpClients.custom().setSSLSocketFactory(csf).setDefaultRequestConfig(config).build();
//        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();

//        client = HttpClientBuilder
//                .create()
//                .setDefaultRequestConfig(config)
//                .build();
        return client.execute(httpRequestBase);
    }

    public static String getBody(HttpResponse httpResponse) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            result.append(line);
        }
        return result.toString();
    }

    private static <T> T executeHttp(HttpRequestBase httpRequestBase, Class<T> responseType) throws IOException {
        HttpResponse response = executeHttpForResponse(httpRequestBase);
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            throw new HttpResponseException(response.getStatusLine().getStatusCode(), getBody(response));
        }
        String body = getBody(response);
        return getObjectMapper().readValue(body, responseType);
    }

    private static <T> T executeHttp(HttpRequestBase httpRequestBase, Class<T> responseType, HttpRequestEnum enums) throws Exception {
        HttpResponse response = executeHttpForResponse(httpRequestBase, enums);
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            throw new HttpResponseException(response.getStatusLine().getStatusCode(), getBody(response));
        }

        String body = getBody(response);
        return getObjectMapper().readValue(body, responseType);
    }

    private static ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }

    private static List<NameValuePair> toFormData(Object request) {
        List<NameValuePair> formData = new ArrayList<>();

        ReflectionUtils.doWithFields(request.getClass(), field -> {
            field.setAccessible(true);
            if (Modifier.isStatic(field.getModifiers())) {
                return;
            }
            String name = field.getName();
            Object value = field.get(request);
            if (value != null) {
                if (value instanceof String && ((String) value).length() == 0) {
                    return;
                }
                formData.add(new BasicNameValuePair(name, value.toString()));
            }
        });
        return formData;
    }

    private static String toQueryString(String url, Object request) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(url);

        ReflectionUtils.doWithFields(request.getClass(), field -> {
            field.setAccessible(true);
            if (Modifier.isStatic(field.getModifiers())) {
                return;
            }
            String name = field.getName();
            Object value = field.get(request);
            if (value != null) {
                if (value instanceof String && ((String) value).length() == 0) {
                    return;
                }
                uriComponentsBuilder.queryParam(name, value);
            }
        });

        return uriComponentsBuilder.build().encode().toString();
    }

    private static String toQueryString(String url, Map<String, Object> map) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(url);

        map.forEach((name, value) -> {
            if (value != null) {
                if (value instanceof String && ((String) value).length() == 0) {
                    return;
                }
                uriComponentsBuilder.queryParam(name, value);
            }
        });
        return uriComponentsBuilder.build().encode().toString();
    }
}
