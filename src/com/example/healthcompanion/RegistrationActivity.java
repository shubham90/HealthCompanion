package com.example.healthcompanion;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class RegistrationActivity extends Activity {
	
	// UI Elements
	EditText emailText, passwordText, nameText, ageText;
	Button signUpButton;
	RadioGroup radioSex;
	RadioButton radioButtonSex;
	String email,password,name,sex;
	int age;
	Firebase healthcompFB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);
		
		//Settign up Firebase
        Firebase.setAndroidContext(this);
        
        // Setting up UI elements
        emailText = (EditText) findViewById(R.id.emailText);
        passwordText = (EditText) findViewById(R.id.passwordText);
        nameText = (EditText) findViewById(R.id.nameText);
        ageText = (EditText) findViewById(R.id.ageText);
        
        radioSex = (RadioGroup) findViewById(R.id.radioSex);
        
        signUpButton = (Button) findViewById(R.id.signUpButton);
        
        
        // Initializing FireBase
        healthcompFB = new Firebase("https://healthcompanion.firebaseio.com");
        
     // Button click listener
        signUpButton.setOnClickListener(new OnClickListener() {
        
        	 
			@Override
			public void onClick(View arg0) {
				email = emailText.getText().toString();
				password = passwordText.getText().toString();
				//Toast.makeText(getApplicationContext(), email+" "+password, Toast.LENGTH_LONG);
				CreateUser cu = new CreateUser();
				cu.execute();
			}
 
		});
        
	}
	
	
	private class CreateUser extends AsyncTask<Void,Void,String>
	{

		@Override
		protected String doInBackground(Void... params) {
			healthcompFB.createUser(email, password, new Firebase.ResultHandler() {
			    @Override
			    public void onSuccess() {
			    	Toast.makeText(getApplicationContext(), "Registration Successfull!", Toast.LENGTH_LONG).show();
			        
			    	SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
			    	Editor editor = pref.edit();
			    	editor.putString("username", email);
			    	editor.commit();
			    	
			    	// Logging user in on FireBase
			    	healthcompFB.authWithPassword(email, password, new Firebase.AuthResultHandler() {
			    	    @Override
			    	    public void onAuthenticated(AuthData authData) {
			    	        Log.d("UserAutoLogin","User ID: " + authData.getUid() + ", Provider: " + authData.getProvider());
			    	    }
			    	    @Override
			    	    public void onAuthenticationError(FirebaseError firebaseError) {
			    	        Log.d("AutoLoginError",firebaseError.getDetails());
			    	    }
			    	});
			    	
			    	// Navigating to home activity
			    	Intent goToHome = new Intent(RegistrationActivity.this,HomeActivity.class);
			    	startActivity(goToHome);
			    	finish();
			    }

			    @Override
			    public void onError(FirebaseError firebaseError) {
			    	Toast.makeText(getApplicationContext(), "Registration Failed! "+firebaseError.getMessage(), Toast.LENGTH_LONG).show();
			    	Log.d("RegistrationError",firebaseError.getMessage());
			    
			    }
			});
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			Toast.makeText(getApplicationContext(), "Registration Successfull!", Toast.LENGTH_LONG).show();
		}
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.registration, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
