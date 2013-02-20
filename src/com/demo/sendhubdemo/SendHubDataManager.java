package com.demo.sendhubdemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

public class SendHubDataManager {

	private SQLiteDatabase database;
	private SendHubSQLiteHelper dbHelper;
	private String[] allColumns = { SendHubSQLiteHelper.COLUMN_ID,
			SendHubSQLiteHelper.COLUMN_CONTACT_NAME,
			SendHubSQLiteHelper.COLUMN_CONTACT_NUMBER };

	public SendHubDataManager(Context context) {
		dbHelper = new SendHubSQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	private class PostContactRequestAsync extends
			AsyncTask<Map<String, Object>, Void, String> {
		@Override
		protected String doInBackground(Map<String, Object>... params) {
			String id = "";

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("username",
					SendHubSession.getCurrentSessionNumber()));
			nameValuePairs.add(new BasicNameValuePair("api_key", SendHubSession
					.getCurrentSessionAPIKey()));

			HttpResponse resp = SendHubUtils.postData(
					SendHubUtils.SENDHUB_API_CONTACT, nameValuePairs,
					new BasicHeader(HTTP.CONTENT_TYPE, "application/json"),
					params[0]);
			Log.d("json", resp.getStatusLine().toString());
			if (resp.getStatusLine().getStatusCode() == 201) {
				id = extractIdFromResponse(resp);
				if (id == null) // TODO: Notify user that we have a problem
					return "";
			}
			return id;
		}
	}

	private class PostMessageRequestAsync extends
			AsyncTask<Map<String, Object>, Void, Integer> {
		@Override
		protected Integer doInBackground(Map<String, Object>... params) {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("username",
					SendHubSession.getCurrentSessionNumber()));
			nameValuePairs.add(new BasicNameValuePair("api_key", SendHubSession
					.getCurrentSessionAPIKey()));

			HttpResponse resp = SendHubUtils.postData(
					SendHubUtils.SENDHUB_API_MESSAGE, nameValuePairs,
					new BasicHeader(HTTP.CONTENT_TYPE, "application/json"),
					params[0]);
			Log.d("json", resp.getStatusLine().toString());
			return resp.getStatusLine().getStatusCode();
		}
	}

	private class GetContactsRequestAsync extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("username",
					SendHubSession.getCurrentSessionNumber()));
			nameValuePairs.add(new BasicNameValuePair("api_key", SendHubSession
					.getCurrentSessionAPIKey()));

			HttpResponse resp = SendHubUtils.getData(
					SendHubUtils.SENDHUB_API_CONTACT, nameValuePairs,
					new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			Log.d("json", resp.toString());
			return resp.toString();
		}
	}

	@SuppressWarnings("unchecked")
	public boolean addContact(String name, String number) {
		String id = "0";

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("name", name);
		data.put("number", number);

		try {
			id = new PostContactRequestAsync().execute(data).get();
			if (TextUtils.isEmpty(id))
				return false;
			ContentValues values = new ContentValues();
			values.put(SendHubSQLiteHelper.COLUMN_CONTACT_NAME, name);
			values.put(SendHubSQLiteHelper.COLUMN_CONTACT_NUMBER, number + "_"
					+ id);
			database.insert(SendHubSQLiteHelper.TABLE_CONTACTS, null, values);
			return true;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public boolean sendMessage(JSONArray id, String message) {
		boolean result = false;
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			Log.d("json", "We are using id: " + id.get(0));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		data.put("contacts", id);
		data.put("text", message);
		try {
			result = new PostMessageRequestAsync().execute(data).get() == 201;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	private String extractIdFromResponse(HttpResponse response) {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(response
					.getEntity().getContent(), "UTF-8"));
			String json = reader.readLine();
			JSONTokener tokener = new JSONTokener(json);
			JSONObject finalResult = new JSONObject(tokener);
			Log.d("json", finalResult.toString());
			return (String) finalResult.get("id");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void replaceNumber(String name, String number) {
		String id = "";
		ContentValues values = new ContentValues();
		values.put(SendHubSQLiteHelper.COLUMN_CONTACT_NAME, name);
		values.put(SendHubSQLiteHelper.COLUMN_CONTACT_NUMBER, number);
		values.put(SendHubSQLiteHelper.COLUMN_CONTACT_ID, id);
		database.replace(SendHubSQLiteHelper.TABLE_CONTACTS, null, values);
	}

	public List<String> getAllContacts() {
		List<String> contacts = new ArrayList<String>();
		
		// Get contacts from the server first
		try {
			JSONObject respObject = new JSONObject(new JSONTokener(
					new GetContactsRequestAsync().execute().get()));
			JSONArray respIds = respObject.getJSONArray("id");
			for (int i = 0; i < respIds.length(); i++) {
				contacts.add(respIds.getJSONObject(i).getString("name"));
			}
			return contacts;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Didn't work, try local database
		Cursor cursor = database.query(SendHubSQLiteHelper.TABLE_CONTACTS,
		allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			contacts.add(cursor.getString(1));
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return contacts;
	}

	public String getNumber(String name) {
		String number = "";
		// Try from the server
		try {
			JSONObject respObject = new JSONObject(new JSONTokener(
					new GetContactsRequestAsync().execute().get()));
			JSONArray respIds = respObject.getJSONArray("id");
			for (int i = 0; i < respIds.length(); i++) {
				if (respIds.getJSONObject(i).getString("name").equals(name)) {
					return name + "_" + respIds.getJSONObject(i).getString("id");
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		// Didn't work, try local database
		Cursor cursor = database.query(SendHubSQLiteHelper.TABLE_CONTACTS,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			if (cursor.getString(1).equals(name)) {
				number = cursor.getString(2);
				break;
			}
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return number;
	}

	public void deleteNumber(String name, String number) {

	}
}
