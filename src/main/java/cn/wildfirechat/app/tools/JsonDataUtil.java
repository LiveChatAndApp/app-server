package cn.wildfirechat.app.tools;

import cn.wildfirechat.app.enums.HttpRequestEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * JSON請求工具類
 * 
 * Content-Type : application/json Charset : UTF-8
 *
 */
public class JsonDataUtil {

	public static <T> T post(String url, Object request, Class<T> responseType) throws IOException {
		HttpPost post = new HttpPost(url);
		post.setHeader("Content-Type", "application/json");
		post.setEntity(new StringEntity(getObjectMapper().writeValueAsString(request), "UTF-8"));
		return executeHttp(post, responseType);
	}

	public static <T> T post(String url, Object request, Class<T> responseType, Map<String, String> headerMap) throws IOException {
		HttpPost post = new HttpPost(url);
		post.setHeader("Content-Type", "application/json");
		headerMap.forEach( (k, v) -> post.setHeader(k,v));
		post.setEntity(new StringEntity(getObjectMapper().writeValueAsString(request), "UTF-8"));
		return executeHttp(post, responseType);
	}
	
	public static <T> T postForJsonString(String url, String json, Class<T> responseType) throws IOException {
		HttpPost post = new HttpPost(url);
		post.setHeader("Content-Type", "application/json");
		post.setEntity(new StringEntity(json, "UTF-8"));
		return executeHttp(post, responseType);
	}

	public static <T> T post(String url, Object request, Class<T> responseType, HttpRequestEnum enums) throws IOException {
		HttpPost post = new HttpPost(url);
		post.setHeader("Content-Type", "application/json");
		post.setEntity(new StringEntity(getObjectMapper().writeValueAsString(request), "UTF-8"));
		return executeHttp(post, responseType, enums);
	}
	
	public static <T> T postForJsonString(String url, String json, Class<T> responseType, HttpRequestEnum enums) throws IOException {
		HttpPost post = new HttpPost(url);
		post.setHeader("Content-Type", "application/json");
		post.setEntity(new StringEntity(json, "UTF-8"));
		return executeHttp(post, responseType, enums);
	}

	public static HttpResponse post(String url, Object request) throws IOException {
		HttpPost post = new HttpPost(url);
		post.setHeader("Content-Type", "application/json");
		post.setEntity(new StringEntity(getObjectMapper().writeValueAsString(request), "UTF-8"));
		return executeHttpForResponse(post);
	}

	public static HttpResponse post(String url, Map request) throws IOException {
		HttpPost post = new HttpPost(url);
		post.setHeader("Content-Type", "application/json");
		post.setEntity(new StringEntity(getObjectMapper().writeValueAsString(request), "UTF-8"));
		return executeHttpForResponse(post);
	}

	private static HttpResponse executeHttpForResponse(HttpRequestBase httpRequestBase) throws IOException {
		int timeout = 15; // 代付商的連線timeout先拉到60秒
		RequestConfig config = RequestConfig.custom().setConnectTimeout(timeout * 1000)
				.setConnectionRequestTimeout(timeout * 1000).setSocketTimeout(timeout * 1000).build();

		HttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
		return client.execute(httpRequestBase);
	}

	private static HttpResponse executeHttpForResponse(HttpRequestBase httpRequestBase, HttpRequestEnum enums) throws IOException {
		int timeout = enums.getSeconds(); // 代付商的連線timeout先拉到60秒
		RequestConfig config = RequestConfig.custom().setConnectTimeout(timeout * 1000)
				.setConnectionRequestTimeout(timeout * 1000).setSocketTimeout(timeout * 1000).build();

		HttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
		return client.execute(httpRequestBase);
	}

	public static String getBody(HttpResponse httpResponse) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(httpResponse.getEntity().getContent()));
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

	private static <T> T executeHttp(HttpRequestBase httpRequestBase, Class<T> responseType, HttpRequestEnum enums) throws IOException {
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

}
