package cs213.project.photoAlbum;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
/**
 * 
 * @author Yunfei
 *
 */
public class CreateAlbum extends Activity {
	
	public static final String ALBUM_NAME_KEY = "albumName";
	public static final String ALBUM_ID_KEY = "albumID";
	
	EditText albumName;	
	Button albumSave;
	Button albumCancel;
	Button upload;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_album);
		
		albumName = (EditText) findViewById(R.id.album_name);
		albumSave = (Button)findViewById(R.id.album_save);
		albumCancel = (Button)findViewById(R.id.album_cancel);
		
		// check if bundle has been sent in, for show/edit, bundle is the pack of info that's passed between activities
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
        	albumName.setText(bundle.getString(ALBUM_NAME_KEY));
        }
        
        albumSave.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				String name = albumName.getText().toString();
				
				if(name==null || name.equals("")){
					Toast.makeText(getApplicationContext(), "Album Name Cannot Be Empty", Toast.LENGTH_SHORT).show();
					return;
				}
				// get info and bundle them
        		Bundle bundle = new Bundle();
        		bundle.putString(ALBUM_NAME_KEY, name);
        	
        		//intent tells android you want to do something, right here starting a new activity
        		Intent intent = new Intent();
        		intent.putExtras(bundle);
        		setResult(RESULT_OK,intent);
        		finish();
				
			}
        	
        });
        
        albumCancel.setOnClickListener(new OnClickListener() {
        	public void onClick(View view) {
        		setResult(RESULT_CANCELED);
        		finish();
        	}
        });
	}

}
