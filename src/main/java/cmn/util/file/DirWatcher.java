package cmn.util.file;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import cmn.util.exception.UtilException;

public class DirWatcher implements Runnable {

	private static WatchService watcher;
	
	public void init(String dir) throws Exception {
		File file = new File(dir);
		if (!file.isDirectory() && !file.exists()) {
			throw new UtilException("Watcher service should be watching directory, check your file {}", dir);
		}
		Path path = FileSystems.getDefault().getPath(dir); 
		watcher = path.getFileSystem().newWatchService();
		path.register(watcher, StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);		
	}

	@Override
	public void run() {
        try {
        	while (true) {
            	WatchKey watchKey = watcher.take();

                // poll for file system events on the WatchKey
                for (final WatchEvent<?> event : watchKey.pollEvents()) {
                	//Calling method
                    takeActionOnChangeEvent(event);
                }

                //Break out of the loop if watch directory got deleted
                if (!watchKey.reset()) {
                    watchKey.cancel();
                    watcher.close();
                    System.out.println("Watch directory got deleted. Stop watching it.");
                    //Break out from the loop
                    break;
                }
            }
        }
        catch (InterruptedException interruptedException) {
        	System.out.println("Thread got interrupted:"+interruptedException);
        	return;
        } 
        catch (Exception exception) {
        	exception.printStackTrace();
        	return;
        }
	}	

	private void takeActionOnChangeEvent(WatchEvent<?> event) {
	    
		Kind<?> kind = event.kind();
	    
		if (kind.equals(StandardWatchEventKinds.ENTRY_CREATE)) {
	        Path entryCreated = (Path) event.context();
	        System.out.println("New entry created:" + entryCreated);
	    } else if (kind.equals(StandardWatchEventKinds.ENTRY_DELETE)) {
	        Path entryDeleted = (Path) event.context();
	        System.out.println("Exissting entry deleted:" + entryDeleted);
	    } else if (kind.equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
	        Path entryModified = (Path) event.context();
	        System.out.println("Existing entry modified:"+ entryModified);
	    }
	}
	
	public static void main(String[] args) throws Exception {
		DirWatcher dirWatcher = new DirWatcher();
		dirWatcher.init("D:/temp");
		Thread thread = new Thread(dirWatcher);
		thread.start();
	}
}
