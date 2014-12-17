package cs213.project.photoAlbum;

import java.io.IOException;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 
 * @author Yunfei
 *
 */
public class PhotoAlbumActivity extends Activity {
	
	private static final int CREATE_ALBUM_ACTIVITY = 0;
	private static final int DISPLAY_ALBUM_ACTIVITY = 0;
	
	static final int DISPLAY = 0;
	static final int EDIT = 1;
	static final int DELETE = 2;
	static final int CANCEL = 3;
	
	static int selectedItem=0;
	
	ListView albumListView;
	AlbumList albumList;
	Context ctx = this;
	
	private String query;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        albumListView = (ListView) findViewById(R.id.album_list);
        registerForContextMenu(albumListView);
        albumList = AlbumList.getInstance();
        
        albumList.setContext(this);
        
        try {
        	albumList.load();

        } catch (IOException e) {
        	//show a dialog
        } catch (ClassNotFoundException e) {

        }

        TextView createAlbum = (TextView) findViewById(R.id.create_album);
        createAlbum.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				createAlbum();			
			}
        	
        });
        handleIntent(getIntent());
    }
    
    protected void createAlbum() {  	
    	Intent intent = new Intent(ctx, CreateAlbum.class);
    	startActivityForResult(intent, CREATE_ALBUM_ACTIVITY);	
		
	}

	private void handleIntent(Intent intent) {
    	
		
		// jpak added section
		

	    	if (Intent.ACTION_SEARCH.equals(intent.getAction())) {  // search intent
	    		String query = intent.getStringExtra(SearchManager.QUERY);
	    		showSearchResults(query);
	    	} else {
	    		
	    	}
	    
		// end jpak added section
		
		
    	showAlbumList();
    	
    	//select the album
    	albumListView.setOnItemClickListener(new OnItemClickListener() {
    	    public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
    	      selectedItem = position;	      
    	      optionDialog(); // When clicked, show the option dialog
    	    }
    	});
    	
    	
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        
        selectedItem = (int)info.id;
    	       
        return true;
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        return true;
    }
    
    
    
    private void optionDialog() {
    	final String[] items = {"Open", "Rename", "Delete", "Cancel"};

    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle("Options:");
    	builder.setItems(items, new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int item) {
    	    	
    	    	switch(item){
    	    	case DISPLAY: display(selectedItem);
    	    					break;
    	    	case EDIT: 	editAlbum(selectedItem);
    	    					break;
    	    	case DELETE: deleteAlbum(selectedItem);
    	    					break;
    	    	case CANCEL: dialog.cancel(); 
    	    					break;
    	    	default: return;
    	    	}
    	    }
    	});
    	AlertDialog alert = builder.create();
    	alert.show();
		
	}
    
    private void editAlbum(final int item){
    	
    	AlertDialog.Builder rename = new AlertDialog.Builder(ctx);
    	rename.setTitle("Enter new album name");
    	final EditText input = new EditText(ctx);
    	rename.setView(input);
    	
    	rename.setPositiveButton("Ok", new DialogInterface.OnClickListener() {	
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String value = input.getText().toString();
				if(value==null || value.equals("")){
					Toast.makeText(getApplicationContext(), "Album Name Cannot Be Empty", Toast.LENGTH_SHORT).show();
					return;
				}
		    	albumList.getAlbums().get(item).name = value;
		    	showAlbumList();
			}
		});
    	rename.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Canceled
			}
		});
    	rename.show();
    }

	protected void display(int index) {
		Intent intent = new Intent(ctx, DisplayAlbum.class).putExtra("album", albumList.getAlbums().get(index).toString());		
    	startActivityForResult(intent, DISPLAY_ALBUM_ACTIVITY);	
	}

	protected void deleteAlbum(int index) {
	
		Album al = albumList.getAlbums().remove(index);

    	if (al == null) {
    		Toast.makeText(getApplicationContext(), "Could not delete.", Toast.LENGTH_SHORT).show();
    	} else {
    		try {
				albumList.store();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		albumListView.setAdapter(new ArrayAdapter<Album>(ctx, android.R.layout.simple_list_item_1, albumList.getAlbums()));
    	}
		
	}

	private void showAlbumList(){
    	Log.e("showing albumbs",albumList.getAlbums().size()+"");
    	albumListView.setAdapter(new ArrayAdapter<Album>(ctx, android.R.layout.simple_list_item_1, albumList.getAlbums()));
    	Log.e("showing albumbs","done");
    	//albumListView.setSelection(lastPickedIndex);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	if (resultCode != RESULT_OK) {
    		return;
    	}
    	Bundle bundle = intent.getExtras(); //the info that's sent back
    	if (bundle == null) {
    		return;
    	}
    	
    	String name = bundle.getString(CreateAlbum.ALBUM_NAME_KEY); //gets the name that's sent back
    	
    	if (requestCode == CREATE_ALBUM_ACTIVITY) {
    		Album album = albumList.add(name);
    		if (album == null) {
    			
    			Toast.makeText(getApplicationContext(), "Album is not successfuly added", Toast.LENGTH_SHORT).show();
    			return;
    		}
    		
    	}
    	albumListView.setAdapter(new ArrayAdapter<Album>(ctx, android.R.layout.simple_list_item_1, albumList.getAlbums()));
    }
     
    protected Dialog onCreateDialog(int dialog) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.search_empty, new Object[] {query}))
		       .setCancelable(false)
		       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                
		           }
		       });
		       
		return builder.create();
	}
	
	@Override
	protected void onPrepareDialog(int dialogCode, Dialog dialog) {
		((AlertDialog)dialog).setMessage(getString(R.string.search_empty, new Object[] {query}));
	}
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }
    
  
    private void showSearchResults(String query) {   
    	
    	
    	if(query == null || query.equals("") || albumList.getAlbums().size() == 0){
			return;
		}
    	
    	albumList.searchTags(query);
    	display(albumList.maxId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.search:
                onSearchRequested();
                return true;
                
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}