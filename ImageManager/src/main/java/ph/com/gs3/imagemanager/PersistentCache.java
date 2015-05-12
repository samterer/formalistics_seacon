package ph.com.gs3.imagemanager;

import java.io.File;

import android.content.Context;

/**
 * Manages cached images that are persisted (stored) in the external storage.
 * Files are stored with their url's hash code as file name.
 * 
 * @author ervinne
 * 
 */
public class PersistentCache {

	public static final String TAG = PersistentCache.class.getSimpleName();

	private File cacheDir;

	public PersistentCache(Context context, String path) {

		// Check for available external storage first, if not available, use the
		// default cache directory instead
		if ( android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED) ) {
			cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), path);
		} else {
			cacheDir = context.getCacheDir();
		}

		if ( !cacheDir.exists() ) {
			cacheDir.mkdirs();
		}

	}

	public File getFile( String url ) {
		// String filename = URLEncoder.encode(url, "UTF-8");
		String filename = String.valueOf(url.hashCode());

		File file = new File(cacheDir, filename);

		return file;

	}

	public void clear() {

		File[] files = cacheDir.listFiles();

		if ( files == null ) {
			return;
		}

		for ( File file : files ) {
			file.delete();
		}

	}

}