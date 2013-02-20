package com.demo.sendhubdemo;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

public class SendHubGroupsActivity extends ListActivity {

	private String[] contacts;

	private static final int VIEW_CONTACTS = 0;

	private static final int ADD_CONTACT = 1;
	
	private static final int SEND_GROUP_MESSAGE = 2;

	/** Called when the activity is first created. */
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		/* Fill up the contact details */
		this.contacts = new String[] { "This", "is", "unimplemented" };

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, this.contacts);
		setListAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add(0, VIEW_CONTACTS, 0, "View contacts");
		menu.add(0, ADD_CONTACT, 1, "Add Contact");
		menu.add(0, SEND_GROUP_MESSAGE, 2, "Send Message");

		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		Intent myIntent = null;
		switch (item.getItemId()) {
		case (VIEW_CONTACTS):
			myIntent = new Intent(SendHubGroupsActivity.this,
					SendHubContactsActivity.class);
			break;
		case (ADD_CONTACT):
			myIntent = new Intent(SendHubGroupsActivity.this,
					SendHubContactsAddActivity.class);
			break;
		case (SEND_GROUP_MESSAGE):
			myIntent = new Intent(SendHubGroupsActivity.this,
					SendHubSendMessageActivity.class);
			break;
		}
		SendHubGroupsActivity.this.startActivity(myIntent);
		finish();
		return false;
	}

}
