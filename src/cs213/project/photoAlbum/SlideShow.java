package cs213.project.photoAlbum;

import java.io.IOException;
import java.util.ArrayList;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 
 * @author Yunfei Lu
 *
 */
public class SlideShow extends Activity{

	protected static final int DELETE = 0;
	protected static final int CANCEL = 1;
	Album current;
	int selectedItem = 0;

	ListView personTagListView, placeTagListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.slide_show);
		
		personTagListView = (ListView) findViewById(R.id.personlistView);
		placeTagListView = (ListView) findViewById(R.id.placetaglistView);
		registerForContextMenu(personTagListView);
		registerForContextMenu(placeTagListView);
		
		//get the album name that's selected from the album list
		String albumName = this.getIntent().getStringExtra("albumName");

		for(int i = 0; i<AlbumList.albumList.getAlbums().size(); i++){
			if(AlbumList.albumList.getAlbums().get(i).toString().equalsIgnoreCase(albumName)){
				current = AlbumList.albumList.getAlbums().get(i);
			}
		}

		Gallery gallery = (Gallery) findViewById(R.id.slideshow);
		gallery.setAdapter(new GalleryImageAdapter(this, R.layout.photo, current.getPhotos()));

		gallery.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				//Toast.makeText(SlideShow.this, "" + position, Toast.LENGTH_SHORT).show();
				selectedItem = position;
				showTagList();
			}
		});

		//add person tags
		TextView tagPeople = (TextView)findViewById(R.id.tagPeople);	    
		tagPeople.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				tag("person", selectedItem);		
			}    	
		});

		//add place tags
		TextView tagPlaces = (TextView)findViewById(R.id.tagPlaces);	    
		tagPlaces.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				tag("place", selectedItem);			
			}    	
		});
		
		//delete a person tag
    	personTagListView.setOnItemClickListener(new OnItemClickListener() {
    	    public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
    	    	deleteTagDialog("person", position);
    	            
    	    }
    	  });
    	
    	//delete a person tag
    	placeTagListView.setOnItemClickListener(new OnItemClickListener() {
    	    public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
    	    	deleteTagDialog("place", position);    
    	    }
    	  });
	}

	protected void deleteTagDialog(final String type, int item) {
		final String[] items = {"Delete", "Cancel"};

    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle("Options:");
    	builder.setItems(items, new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int item) {
    	    	
    	    	switch(item){
    	    	case DELETE: deleteTag(type, selectedItem);
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

	protected void deleteTag(String type, int item) {
		if(type.equalsIgnoreCase("person")){
			current.getPhotos().get(selectedItem).getPersonTags().remove(selectedItem);	
			try {
				AlbumList.albumList.store();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(type.equalsIgnoreCase("place")){
			current.getPhotos().get(selectedItem).getPlaceTags().remove(selectedItem);	
			try {
				AlbumList.albumList.store();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		showTagList();		
	}

	private void tag(final String type, final int item){

		AlertDialog.Builder tag = new AlertDialog.Builder(this);
		tag.setTitle("Enter "+type+" name:");
		final EditText input = new EditText(this);
		tag.setView(input);

		tag.setPositiveButton("Ok", new DialogInterface.OnClickListener() {	
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String value = input.getText().toString();

				if(type.equalsIgnoreCase("person")){
					current.getPhotos().get(item).addPersonTag(value);
					try {
						AlbumList.albumList.store();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else if(type.equalsIgnoreCase("place")){
					current.getPhotos().get(item).addPlaceTag(value);
					try {
						AlbumList.albumList.store();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				showTagList();
			}
		});
		tag.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Canceled
			}
		});
		tag.show();
	}

	protected void showTagList() {

		Log.e("showing tags",current.getPhotos().get(selectedItem).getPersonTags().size()+"");
		personTagListView.setAdapter(new ArrayAdapter<String>(this, R.layout.tag, current.getPhotos().get(selectedItem).getPersonTags()));
		Log.e("showing tags","done");

		Log.e("showing tags",current.getPhotos().get(selectedItem).getPlaceTags().size()+"");
		placeTagListView.setAdapter(new ArrayAdapter<String>(this, R.layout.tag, current.getPhotos().get(selectedItem).getPlaceTags()));
		Log.e("showing tags","done");
	}
}

class GalleryImageAdapter extends ArrayAdapter<Photo> {

	private ArrayList<Photo> photos;
	private Context ctx;
	int mGalleryItemBackground;


	public GalleryImageAdapter(Context ctx, int textViewResourceId, ArrayList<Photo> arrayList) {
		super(ctx, textViewResourceId, arrayList);
		this.ctx = ctx;
		photos = arrayList;

		TypedArray attr = ctx.obtainStyledAttributes(R.styleable.HelloGallery);
		mGalleryItemBackground = attr.getResourceId(R.styleable.HelloGallery_android_galleryItemBackground, 0);
		attr.recycle();
	}

	@Override
	public int getCount() {
		return photos.size();
	}

	@Override
	public Photo getItem(int position) {
		return photos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView = new ImageView(ctx);

		imageView.setImageBitmap(photos.get(position).getBitmap());
		imageView.setLayoutParams(new Gallery.LayoutParams(550, 600));
		imageView.setScaleType(ImageView.ScaleType.FIT_XY);
		imageView.setBackgroundResource(mGalleryItemBackground);

		return imageView;
	}
}

