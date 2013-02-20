package com.demo.sendhubdemo;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class SendHubSession {
	public static SendHubSession session;

	public static String getCurrentSessionNumber() {
		if (session == null)
			throw new NullPointerException("No valid auth session");
		return session.getNumber();
	}

	public static String getCurrentSessionAPIKey() {
		if (session == null)
			throw new NullPointerException("No valid auth session");
		return session.getApiKey();
	}

	public static SendHubDataManager getCurrentSessionDataManager() {
		if (session == null)
			throw new NullPointerException("No valid auth session");
		return session.getDataManager();
	}

	public static void setCurrentSessionDataManager(SendHubDataManager dmgr) {
		if (session == null)
			throw new NullPointerException("No valid auth session");
		session.dataManager = dmgr;
	}

	private String phoneNumber;

	private String apiKey;

	private SendHubDataManager dataManager;

	public String getApiKey() {
		return this.apiKey;
	}

	public String getNumber() {
		return this.phoneNumber;
	}

	public SendHubDataManager getDataManager() {
		return this.dataManager;
	}

	public static boolean newAuth(String number, String key) {
		boolean success = true;
		/* Authenticate with SendHub using the given number and key */
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("username", number));
		nameValuePairs.add(new BasicNameValuePair("api_key", key));

		success = SendHubUtils
				.getData(SendHubUtils.SENDHUB_API_URL, nameValuePairs, null)
				.getStatusLine().getStatusCode() == 200;

		if (success) {
			SendHubSession.session = new SendHubSession(number, key);
			return success;
		}

		return false;
	}

	private SendHubSession(String s, String k) {
		this.phoneNumber = s;
		this.apiKey = k;
	}

}
