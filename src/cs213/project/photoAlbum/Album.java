	package cs213.project.photoAlbum;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import android.content.Context;
/**
 * 
 * @author Yunfei
 *
 */
public class Album implements AlbumInterface, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2682470447880282005L;

	Context ctx;
	
	String name;
	int id;
	ArrayList<Photo> photos;
	
	public Album(int id, String name) {
		this.id = id;
		this.name = name;
		photos = new ArrayList<Photo>();
	}

	private Album() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setContext(Context ctx) {
		this.ctx = ctx;		
	}

	@Override
	public void load() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void store() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ArrayList<Album> getAlbums() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Album add(String name) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Photo addPhoto(Photo p){ //add the pic into the album
		photos.add(p);
		try {
			AlbumList.albumList.store();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return p;
	}

	public int compareTo(Album other) {
		return name.compareToIgnoreCase(other.name);
	}

	public String getString() {
		return name;
	}

	public static Album parseAlbum(String albumInfo) {
		Album album = new Album();
		album.id = -1;
		album.name = albumInfo;
		
		return album;
	}
	@Override
	public String toString() {
		return this.name;
	}

	public ArrayList<Photo> getPhotos() {
		return photos;
	}
}
