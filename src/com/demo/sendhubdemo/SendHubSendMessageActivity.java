package com.demo.sendhubdemo;

import org.json.JSONArray;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SendHubSendMessageActivity extends Activity {

	private String mContactName;
	private String mContactNumber;

	private String mMessage;

	// UI references.
	private EditText mMessageView;

	private View focusView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_send_hub_message);
		
		// TODO Auto-generated method stub
		mContactName = getIntent().getExtras().getString(
				SendHubUtils.CONTACT_NAME);
		mContactNumber = getIntent().getExtras().getString(
				SendHubUtils.CONTACT_NUMBER);

		mMessageView = (EditText) findViewById(R.id.message_content);

		findViewById(R.id.message_send_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptSendMessage(mContactName, mContactNumber);
					}
				});
		
		findViewById(R.id.message_cancel_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptSaveMessage(mContactName, mContactNumber);
						finish();
					}
				});

	}

	private void attemptSendMessage(String name, String number) {
		// Reset errors.
		mMessageView.setError(null);
		
		// Store values at the time of the login attempt.
		mMessage = mMessageView.getText().toString();
		
		boolean cancel = false;
		focusView = null;

		// Check if message is empty.
		if (TextUtils.isEmpty(mMessage)) {
			mMessageView.setError(getString(R.string.error_field_required));
			focusView = mMessageView;
			cancel = true;
		}
		
		if (cancel) {
			focusView.requestFocus();
		} else {
			String id = number.split("_")[1];
			JSONArray ids = new JSONArray();
			ids.put(Integer.valueOf(id));
			number = number.split("_")[0];
			if (SendHubSession.getCurrentSessionDataManager().sendMessage(ids, mMessage)) {
				Toast.makeText(getApplicationContext(), "Message successfully sent", Toast.LENGTH_SHORT).show();
				finish();
			} else {
				Toast.makeText(getApplicationContext(), "There was a problem sending the message", Toast.LENGTH_LONG).show();
				mMessageView.requestFocus();
			}
		}
	}
	
	private void attemptSaveMessage(String name, String number) {
		
	}
}
