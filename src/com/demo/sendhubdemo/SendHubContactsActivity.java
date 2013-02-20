package com.demo.sendhubdemo;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

public class SendHubContactsActivity extends ListActivity {

	private static final int VIEW_GROUPS = 0;

	private static final int ADD_CONTACT = 1;

	private List<String> contacts = new ArrayList<String>();

	private SendHubDataManager dataManager;

	/** Called when the activity is first created. */
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		dataManager = new SendHubDataManager(this);
		SendHubSession.setCurrentSessionDataManager(dataManager);
		dataManager.open();

		/* Fill up the contact details */
		fillData();
	}

	private void fillData() {
		contacts = dataManager.getAllContacts();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, this.contacts);
		setListAdapter(adapter);
	}

	@Override
	public void onResume() {
		super.onResume();
		this.fillData();
	}

	@Override
	protected void onListItemClick(android.widget.ListView l,
			android.view.View v, int position, long id) {
		String selectedContact = (String) getListView().getItemAtPosition(
				position);
		String number = SendHubSession.getCurrentSessionDataManager()
				.getNumber(selectedContact);

		// Bundle id in intent
		Intent myIntent = new Intent(SendHubContactsActivity.this,
				SendHubContactsAddActivity.class);
		myIntent.putExtra(SendHubUtils.CONTACT_NAME, selectedContact);
		myIntent.putExtra(SendHubUtils.CONTACT_NUMBER, number);
		SendHubContactsActivity.this.startActivity(myIntent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add(0, VIEW_GROUPS, 0, "View groups");
		menu.add(0, ADD_CONTACT, 1, "Add Contact");

		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		Intent myIntent = null;
		switch (item.getItemId()) {
		case (VIEW_GROUPS):
			myIntent = new Intent(SendHubContactsActivity.this,
					SendHubGroupsActivity.class);
			break;
		case (ADD_CONTACT):
			myIntent = new Intent(SendHubContactsActivity.this,
					SendHubContactsAddActivity.class);
			myIntent.putExtra(SendHubUtils.CONTACT_NAME, "");
			myIntent.putExtra(SendHubUtils.CONTACT_NUMBER, "");
			break;
		default:
			Log.e("SendhubContactsActivity", "Invalid menu item");
			break;
		}
		SendHubContactsActivity.this.startActivity(myIntent);
		return false;
	}
}
