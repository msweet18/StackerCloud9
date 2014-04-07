package edu.msu.BlueSky.stacker;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.os.Bundle;
import android.util.Xml;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;

public class MainActivity extends Activity {
	
	private String player1Name;
	private String player2Name;
	
	private static final String LOGIN_URL = "https://www.cse.msu.edu/~sweetmax/cse476Proj2/stacker_login.php";
    private String userNameString;
    private String passwordString;
    
    private EditText userNameText;
    private EditText passwordText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log_in);
	}
	
	public void onStartGame(View view) {
		Intent intent = new Intent(this, GameActivity.class);
		player1Name = ((EditText)this.findViewById(R.id.player1Name)).getText().toString();
		player2Name = ((EditText)this.findViewById(R.id.player2Name)).getText().toString();
		intent.putExtra ( "player1Name", player1Name );
		intent.putExtra ( "player2Name", player2Name );
		startActivity(intent);
	}
	
	public void onCreateUser(View view) {
		Intent intent = new Intent(this, CreateUserActivity.class);
		startActivity(intent);
		
	}
	
	public void onLogin(View view) {
		 
	    userNameText = (EditText)findViewById(R.id.loginName);
		passwordText = (EditText)findViewById(R.id.loginPassword);
		
		checkUserExists(view);
	}


	public void checkUserExists(final View view) {
		
		userNameString = userNameText.getText().toString();
		passwordString = passwordText.getText().toString();
		
		final String query = LOGIN_URL + "?username=" + userNameString + "&password=" + passwordString;
        
		
		userNameText.setText("");
		passwordText.setText("");
		
		new Thread(new Runnable() {
	
	        @Override
	        public void run() {
	        	InputStream stream = checkServer(query);
				
	        	boolean fail = stream == null;
                if(!fail) {
                    try {
                    	 XmlPullParser xml = Xml.newPullParser();
                         xml.setInput(stream, "UTF-8");       
                         
                         xml.nextTag();      // Advance to first tag
                         xml.require(XmlPullParser.START_TAG, null, "stacker");
                         String login = xml.getAttributeValue(null, "login");
                         
                         if(login.equals("yes")){
                        	 startTheGame();
                        	// User Exists!
                             view.post(new Runnable() {

                                 @Override
                                 public void run() {
                                     Toast.makeText(view.getContext(), R.string.catalog_user_exists, Toast.LENGTH_SHORT).show();
                                 }
                             });
                        	 
                        	 skipToEndTag(xml);
                        	 
                         }else {
                             fail = true;
                          // Error condition!
                             view.post(new Runnable() {

                                 @Override
                                 public void run() {
                                     Toast.makeText(view.getContext(), R.string.catalog_user_does_not_exist, Toast.LENGTH_SHORT).show();
                                 }
                             });
                         }
                    } catch(IOException ex) {
                        fail = true;
                    } catch(XmlPullParserException ex) {
                        fail = true;
                    } finally {
                        try {
                            stream.close(); 
                        } catch(IOException ex) {
                        }
                    }
                
                }
                
	        }
	    	}).start();
		
	}
	
	public void startTheGame(){
			Intent intent = new Intent(this, GameActivity.class);
			player1Name = "bob";
			player2Name = "damian";
			intent.putExtra ( "player1Name", player1Name );
			intent.putExtra ( "player2Name", player2Name );
			startActivity(intent);
	}
	
	public InputStream checkServer(final String query) {

		try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int responseCode = conn.getResponseCode();
            if(responseCode != HttpURLConnection.HTTP_OK) {
            	return null;
            }
            
            InputStream stream = conn.getInputStream();
            return stream;

        } catch (MalformedURLException e) {
          //   Should never happen
        	return null;
        } catch (IOException ex) {
        	return null;
        }
    		
	}
	
	public static void skipToEndTag(XmlPullParser xml) 
            throws IOException, XmlPullParserException {
        int tag;
        do
        {
            tag = xml.next();
            if(tag == XmlPullParser.START_TAG) {
                // Recurse over any start tag
                skipToEndTag(xml);
            }
        } while(tag != XmlPullParser.END_TAG && 
        tag != XmlPullParser.END_DOCUMENT);
    }

}

