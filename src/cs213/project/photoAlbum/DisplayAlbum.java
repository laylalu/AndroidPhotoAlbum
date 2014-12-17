package cs213.project.photoAlbum;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author Yunfei Lu
 *
 */
public class DisplayAlbum extends Activity{
	//integers for dialog
	static final int DISPLAY = 0;
	static final int DELETE = 1;
	static final int MOVE = 2;
	static final int  CANCEL= 3;
	
	static int selectedItem=0;

	private static final int SELECT_PHOTO = 100;
	private static final int SLIDE_SHOW_ACTIVITY = 0;
	
	Album current;
	GridView gridview;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.album);
		
		//get the album name that's selected from the album list
		String albumName = this.getIntent().getStringExtra("album");

		for(int i = 0; i<AlbumList.albumList.getAlbums().size(); i++){
			if(AlbumList.albumList.getAlbums().get(i).toString().equalsIgnoreCase(albumName)){
				current = AlbumList.albumList.getAlbums().get(i);
			}
		}

		gridview = (GridView) findViewById(R.id.album);
		gridview.setAdapter(new ImageAdapter(this, R.layout.photo, current.getPhotos()));
		
		//click add photo textview to add photos
		TextView addPhoto = (TextView)findViewById(R.id.add_photo);	    
		addPhoto.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				openStorage();		
			}    	
		});

		//option dialog for the selected photo
		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				selectedItem = position;	      
				optionDialog(); // When clicked, show the option dialog
			}
		});
	}

	private void optionDialog() {
		final String[] items = {"Slide Show", "Delete", "Move", "Cancel"};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Options:");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {

				switch(item){
				case DISPLAY: slideShow(selectedItem); //triggers a slide show
					break;
				case DELETE: deletePhoto(selectedItem); //deletes the selected photo
					break;
				case MOVE: movePhoto(selectedItem);
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
	
	protected void movePhoto(final int index) {
		
		AlertDialog.Builder move = new AlertDialog.Builder(this);
    	move.setTitle("Move Picture");
    	move.setMessage("Enter the album name you want to move to:");
    	
    	final EditText input = new EditText(this);
    	move.setView(input); 	
    	move.setPositiveButton("Ok", new DialogInterface.OnClickListener() {	
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				String value = input.getText().toString();
				Album destination = null;
				for(int i = 0; i<AlbumList.albumList.getAlbums().size(); i++){
					if(AlbumList.albumList.getAlbums().get(i).toString().equalsIgnoreCase(value)){
						destination = AlbumList.albumList.getAlbums().get(i);
						break;
					}
				}
				destination.addPhoto(current.getPhotos().get(index));
		    	current.getPhotos().remove(index);
		    	gridview.invalidateViews();
			}
		});
    	move.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Canceled
			}
		});
    	move.show();
		
	}

	protected void slideShow(int index) {
		
		Intent intent = new Intent(this, SlideShow.class).putExtra("albumName", current.toString());		
    	startActivityForResult(intent, SLIDE_SHOW_ACTIVITY);	
	}

	protected void deletePhoto(int index) {
		current.getPhotos().remove(index);
		gridview.invalidateViews();
	} 

	//opens the gallery in the phone or the sd card for the user to pick a photo
	protected void openStorage() {
		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setType("image/*");
		startActivityForResult(photoPickerIntent, SELECT_PHOTO);    		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) { 
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent); 

		switch(requestCode) { 
		case SELECT_PHOTO:
			if(resultCode == RESULT_OK){  
				Uri selectedImage = imageReturnedIntent.getData();
				try {
					Bitmap yourSelectedImage = decodeUri(selectedImage);
					current.addPhoto(new Photo(yourSelectedImage));
					gridview.invalidateViews();

					//save
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	           
			}
		}
	}

	Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {

		// Decode image size
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

		// The new size we want to scale to
		final int REQUIRED_SIZE = 140;

		// Find the correct scale value. It should be the power of 2.
		int width_tmp = o.outWidth, height_tmp = o.outHeight;
		int scale = 1;
		while (true) {
			if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
				break;
			}
			width_tmp /= 2;
			height_tmp /= 2;
			scale *= 2;
		}

		// Decode with inSampleSize
		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;
		return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);

	}

}

//the image adapter for the grid view of the photos
class ImageAdapter extends ArrayAdapter<Photo> {

	private ArrayList<Photo> photos;
	private Context ctx;
	
	public ImageAdapter(Context context, int textViewResourceId, ArrayList<Photo> arrayList) {
		super(context, textViewResourceId, arrayList);

		ctx = context;
		photos = arrayList;
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

		ImageView imageView;
		if (convertView == null) {  // if it's not recycled, initialize some attributes
			imageView = new ImageView(ctx);
			imageView.setLayoutParams(new GridView.LayoutParams(140, 140));
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageView.setPadding(8, 8, 8, 8);

		} else {
			imageView = (ImageView) convertView;
		}

		imageView.setImageBitmap(photos.get(position).getBitmap());
		return imageView;
	}


}

