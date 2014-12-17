package cs213.project.photoAlbum;

import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import android.content.Context;
/**
 * 
 * @author Yunfei
 *
 */
public interface AlbumInterface {
	void setContext(Context ctx);
	void load() throws IOException, ClassNotFoundException;
	void store() throws IOException;
	ArrayList<Album> getAlbums();
	Album add(String name);
}
