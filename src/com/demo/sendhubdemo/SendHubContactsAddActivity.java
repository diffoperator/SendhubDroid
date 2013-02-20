package com.demo.sendhubdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SendHubContactsAddActivity extends Activity {

	/**
	 * The default name.
	 */
	public static final String EXTRA_NAME = "John Doe";

	/**
	 * Default number
	 */
	public static final String EXTRA_NUMBER = "201-555-1234";

	private String mContactName;
	private String mContactNumber;
	private String mContactId;

	private boolean replace = false;

	// UI references.
	private EditText mContactNameView;
	private EditText mContactNumberView;

	private View focusView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// TODO Auto-generated method stub
		setContentView(R.layout.activity_send_hub_contact_add);

		mContactName = getIntent().getExtras().getString(
				SendHubUtils.CONTACT_NAME);
		if (!TextUtils.isEmpty(mContactName))
			replace = true;
		mContactNameView = (EditText) findViewById(R.id.contact_name);
		mContactNameView.setText(mContactName);

		mContactNumber = getIntent().getExtras().getString(
				SendHubUtils.CONTACT_NUMBER);
		mContactNumberView = (EditText) findViewById(R.id.contact_number);
		if (mContactNumber.indexOf("_") > 0) {
			mContactNumberView.setText(mContactNumber.split("_")[0]);
			mContactId = mContactNumber.split("_")[1];
		} else
			mContactNumberView.setText(mContactNumber);

		findViewById(R.id.contact_add_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						mContactName = mContactNameView.getText().toString();
						mContactNumber = mContactNumberView.getText()
								.toString();
						attemptSaveDetails(mContactName, mContactNumber);
					}
				});

		findViewById(R.id.contact_message_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						mContactName = mContactNameView.getText().toString();
						mContactNumber = mContactNumberView.getText()
								.toString();
						attemptSendMessage(mContactName, mContactNumber);
					}
				});
	}

	private void attemptSendMessage(String name, String number) {
		/* Check if the details have been changed */
		// attemptSaveDetails(name, number.split("_")[0]);

		/* Check if an id is there */
		if (mContactId == null) {
			Toast.makeText(getApplicationContext(), "Add contact first",
					Toast.LENGTH_LONG).show();
			return;
		}

		Intent myIntent = new Intent(SendHubContactsAddActivity.this,
				SendHubSendMessageActivity.class);
		myIntent.putExtra(SendHubUtils.CONTACT_NAME, name);
		myIntent.putExtra(SendHubUtils.CONTACT_NUMBER, number + "_"
				+ mContactId);
		SendHubContactsAddActivity.this.startActivity(myIntent);
	}

	private void attemptSaveDetails(String name, String number) {
		boolean cancel = validateContactDetails();

		if (cancel) {
			focusView.requestFocus();
		} else {
			if (!replace) {
				if (SendHubSession.getCurrentSessionDataManager().addContact(
						name, number)) {
					Toast.makeText(getApplicationContext(), "Contact added",
							Toast.LENGTH_SHORT).show();
				} else
					Toast.makeText(getApplicationContext(),
							"Could not add contact because of server failure",
							Toast.LENGTH_LONG).show();
			} else
				SendHubSession.getCurrentSessionDataManager().replaceNumber(
						name, number);
			finish();
		}
	}

	private boolean validateContactDetails() {
		// Reset errors.
		mContactNameView.setError(null);
		mContactNumberView.setError(null);

		// Store values at the time of the login attempt.
		mContactName = mContactNameView.getText().toString();
		mContactNumber = mContactNumberView.getText().toString();

		boolean cancel = false;
		focusView = null;

		// Check for a valid phone number.
		if (TextUtils.isEmpty(mContactNumber)) {
			mContactNumberView
					.setError(getString(R.string.error_field_required));
			focusView = mContactNumberView;
			cancel = true;
		} else if (!PhoneNumberUtils.isGlobalPhoneNumber(mContactNumber)) {
			mContactNumberView
					.setError(getString(R.string.error_invalid_number));
			focusView = mContactNameView;
			cancel = true;
		}

		// Check if name is empty.
		if (TextUtils.isEmpty(mContactName)) {
			mContactNameView.setError(getString(R.string.error_field_required));
			focusView = mContactNameView;
			cancel = true;
		}
		return cancel;
	}

}
