package cs213.project.photoAlbum;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import android.content.Context;
import android.widget.Toast;
/**
 * 
 * @author Yunfei
 *
 */
public class AlbumList implements AlbumInterface, Serializable{

	private static final long serialVersionUID = -4274278325765352997L;

	public static final int MEDIA_TYPE_IMAGE = 1;

	//single instance
	static AlbumList albumList;
	ArrayList<Album> albums;
	static Context ctx;
	public int maxId;

	public static final String ALBUMS_FILE = "albums.dat";
	File photoFile;

	//make constructor private for single instance control
	private AlbumList() {
		albums = new ArrayList<Album>();
		maxId = -1;
	}

	// deal out the singleton
	public static AlbumList getInstance() {
		if (albumList == null) {
			albumList = new AlbumList();

		}
		return albumList;

	}

	public Album add(String name) {
		// name and artist are mandatory
		if (name == null) {
			throw new IllegalArgumentException("Name is mandatory");
		}

		// set id to next available
		maxId++;

		// create Album object
		Album album = new Album(maxId, name);

		// if this is the first add, it's easy
		if (albums.size() == 0) {
			albums.add(album);
			// write through
			try {
				store();
				return album;
			} catch (IOException e) {
				//return null;
			}

		}
		// search in array list and add at correct spot
		int lo=0, hi=albums.size()-1, mid=-1, c=0;
		while (lo <= hi) {
			mid = (lo+hi)/2;
			c = album.compareTo(albums.get(mid));
			if (c == 0) {  // duplicate name
				break;
			}
			if (c < 0) {
				hi = mid-1;
			} else {
				lo = mid+1;
			}
		}
		int pos = c <= 0 ? mid : mid+1;
		// insert at pos
		albums.add(pos,album);
		// write through
		try {
			store();
			return album;
		} catch (IOException e) {
			// could pop up error dialog here if appropriate
			return null;
		}

	}

	public void setContext(Context ctx) {
		this.ctx = ctx;		
	}

	@Override
	public void load() throws IOException, ClassNotFoundException {	

		ObjectInputStream ois = new ObjectInputStream(ctx.openFileInput(ALBUMS_FILE));
		albums = (ArrayList<Album>)ois.readObject();
		ois.close();
		//try reading the file

	}

	@Override
	public void store() throws IOException {

		ObjectOutputStream os = new ObjectOutputStream(ctx.openFileOutput(ALBUMS_FILE, Context.MODE_PRIVATE));
		os.writeObject(albums);
		os.close();

	}

	/** Create a folder for saving an image or video */


	@Override
	public ArrayList<Album> getAlbums() {
		return albums;
	}

	public void searchTags(String searchString){
			
		ArrayList<Photo> rPhotos = new ArrayList<Photo>();

		StringTokenizer st = new StringTokenizer(searchString);
		String [] tokens = new String [st.countTokens()];
		for(int i = 0; i < st.countTokens(); i++){
			tokens[i] = st.nextToken();
		}

		add("Search Results");

		boolean found = false;

		for(int x = 0; x < albums.size()-1; x++/*Album a : albums*/){
			for(Photo p : albums.get(x).getPhotos()){
				found = false;
				for(String p1 : p.getPersonTags()){
					for(String t : tokens){
						if(p1.contains(t)){
							albumList.albums.get(maxId).getPhotos().add(p);
							found = true;
							break;
						}
					}
					if(found == true){
						break;
					}
				}
				if(found == true){
					break;
				}

				for(String p1 : p.getPlaceTags()){	
					for(String t : tokens){
						if(p1.contains(t)){
							albumList.albums.get(maxId).getPhotos().add(p);
							found = true;
							break;
						}
					}
					if(found == true){
						break;
					}
				}
			}
		}

		return;


	}
}
