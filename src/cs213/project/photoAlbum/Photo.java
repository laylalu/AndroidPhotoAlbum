package cs213.project.photoAlbum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

import android.graphics.Bitmap;

/**
 * 
 * @author Yunfei
 *
 */
public class Photo implements Comparator<Photo>, Serializable{
	
	private static final long serialVersionUID = -640391495288269418L;
	String caption;
	ArrayList<String> personTag, placeTag;

	Bitmap image;
	
	public Photo(Bitmap yourSelectedImage) {
		this.image = yourSelectedImage;
		
		this.personTag = new ArrayList<String>();
		this.placeTag = new ArrayList<String>();			
	}

	@Override
	public int compare(Photo arg0, Photo arg1) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public String toString(){
		return this.caption;
	}
	
	public Bitmap getBitmap(){
		return this.image;
	}
	
	public void addPersonTag(String person){
		personTag.add(person);
	}
	
	public void addPlaceTag(String place){
		placeTag.add(place);
	}
	
	public ArrayList<String> getPersonTags(){
		return personTag;
	}
	
	public ArrayList<String> getPlaceTags(){
		return placeTag;
	}
}
