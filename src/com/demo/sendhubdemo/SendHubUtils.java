package com.demo.sendhubdemo;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class SendHubUtils {
	public static final String CONTACT_NAME = "contact_name";

	public static final String CONTACT_NUMBER = "contact_number";

	public static final String SENDHUB_API_URL = "http://api.sendhub.com/v1/profile/?";

	public static final String SENDHUB_API_CONTACT = "https://api.sendhub.com/v1/contacts/?";

	public static final String SENDHUB_API_MESSAGE = "https://api.sendhub.com/v1/messages/?";

	private static HttpClient createSendHubHttpClient() {
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params,
				HTTP.DEFAULT_CONTENT_CHARSET);
		HttpProtocolParams.setUseExpectContinue(params, true);

		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		schReg.register(new Scheme("https",
				SSLSocketFactory.getSocketFactory(), 443));
		ClientConnectionManager conMgr = new ThreadSafeClientConnManager(
				params, schReg);

		return new DefaultHttpClient(conMgr, params);
	}

	public static HttpResponse postData(String url,
			List<NameValuePair> nameValuePairs, Header header,
			Map<String, Object> data) {
		// Create a new HttpClient and Post Header
		HttpClient httpclient = createSendHubHttpClient();
		String params = URLEncodedUtils.format(nameValuePairs, "utf-8");
		HttpPost httppost = new HttpPost(url + params);

		try {
			JSONObject holder = new JSONObject();
			for (Entry<String, Object> e : data.entrySet()) {
				holder.put(e.getKey(), e.getValue());
			}
			Log.d("json", "Data: " + holder.toString());
			StringEntity se = new StringEntity(holder.toString());
			se.setContentType(header);
			httppost.setEntity(se);
			Log.d("json", httppost.getURI() + " "
					+ httppost.getParams().toString());
			// Execute HTTP Post Request
			// Log.d("Http response", httppost.get);
			HttpResponse response = httpclient.execute(httppost);
			Log.d("json",
					String.valueOf(response.getStatusLine().getStatusCode()));
			return response;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
		} catch (IOException e) {
			// TODO Auto-generated catch block
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public static HttpResponse getData(String url,
			List<NameValuePair> nameValuePairs, Header header) {
		// Create a new HttpClient and Post Header
		HttpClient httpclient = createSendHubHttpClient();

		try {
			// Add your data
			String params = URLEncodedUtils.format(nameValuePairs, "utf-8");
			HttpGet httpget = new HttpGet(url + params);

			httpget.addHeader(header);

			// Execute HTTP Get Request
			HttpResponse response = httpclient.execute(httpget);
			Log.d("Http response",
					String.valueOf(response.getStatusLine().getStatusCode()));
			return response;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}

		return null;
	}

}
