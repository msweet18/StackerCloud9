package edu.msu.BlueSky.stacker;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.os.Bundle;
import android.app.Activity;
import android.util.Xml;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class CreateUserActivity extends Activity {

	private static final String SAVE_URL = "https://www.cse.msu.edu/~averycon/cse476/stacker_create_user.php";
    private String userNameString;
    private String passwordString;
    private String userNameConfirmString;
    private String passwordConfirmString;
    
    private EditText userNameText;
    private EditText passwordText;
    private EditText userNameConfirmText;
    private EditText passwordConfirmText;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_user);
		
		userNameText = (EditText)findViewById(R.id.newName);
		passwordText = (EditText)findViewById(R.id.newPassword);
		
		userNameConfirmText = (EditText)findViewById(R.id.confirmName);
		passwordConfirmText = (EditText)findViewById(R.id.confirmPassword);
	}

	public void createNewUser(final View view) {
		
		userNameString = userNameText.getText().toString();
		passwordString = passwordText.getText().toString();
		userNameConfirmString = userNameConfirmText.getText().toString();
		passwordConfirmString = passwordConfirmText.getText().toString();
		
		int compareName = userNameString.compareToIgnoreCase(userNameConfirmString);
		int comparePassword = passwordString.compareToIgnoreCase(passwordConfirmString);
		
		if(compareName != 0 || comparePassword != 0){
			
			// Error condition!
            view.post(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(view.getContext(), R.string.catalog_fail, Toast.LENGTH_SHORT).show();
                }
            });
            
			return;
		}
		
		final String query = SAVE_URL + "?username=" + userNameString + "&password=" + passwordString;
        		
		userNameText.setText("");
		passwordText.setText("");
		userNameConfirmText.setText("");
		passwordConfirmText.setText("");
		
		new Thread(new Runnable() {
	
	        @Override
	        public void run() {
	        	InputStream stream = openFromServer(query);
				
	        	boolean fail = stream == null;
                if(!fail) {
                    try {
                    	 XmlPullParser xml = Xml.newPullParser();
                         xml.setInput(stream, "UTF-8");       
                         
                         xml.nextTag();      // Advance to first tag
                         xml.require(XmlPullParser.START_TAG, null, "stacker");
                         String create = xml.getAttributeValue(null, "create");
                         
                         if(create.equals("yes")){
                        	 
                        	// new user created
                             view.post(new Runnable() {

                                 @Override
                                 public void run() {
                                     Toast.makeText(view.getContext(), R.string.catalog_new_user, Toast.LENGTH_SHORT).show();
                                 }
                             });
                        	 
                        	 skipToEndTag(xml);
                        	 
                         }else {
                        	 
                        	// Error condition!
                             view.post(new Runnable() {
                                 @Override
                                 public void run() {
                                     Toast.makeText(view.getContext(), R.string.catalog_user_already_exists, Toast.LENGTH_SHORT).show();
                                 }
                             });
                             
                             fail = true;
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
	
	public InputStream openFromServer(final String query) {
				
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
