package DirectoryScan.Service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import DirectoryScan.utility.CommonUtils;

@Service
public class DirectoryWatchService {
	private static final Logger logger = LoggerFactory.getLogger(DirectoryWatchService.class);
	@Autowired
	private CacheService cacheService;
	@Autowired
	private ParserService parserService;
	private boolean startWatch;
	WatchService watchService;
	private ExecutorService executorService;
	private String watchDir;
	private List<String> fileExtensions;
	/**
	 * maintain a map of watch keys and directories Map<WatchKey, Path> keys to
	 * correctly identify which directory has been modified
	 */
	private Map<WatchKey, Path> keys = new HashMap<WatchKey, Path>();

	public void initializeAndStartDirWatching(String watchDir, List<String> fileExtensions) {
		try {
			this.watchDir = watchDir;
			this.watchService = FileSystems.getDefault().newWatchService();
			this.executorService = Executors.newFixedThreadPool(5);
			this.fileExtensions = fileExtensions;
			this.walkAndRegisterDirectories(Paths.get(watchDir));
			this.startWatch = true;
			this.startWatching();
		} catch (IOException | InterruptedException e) {
			logger.info(e.getMessage());
		}
	}

	/**
	 * This method create worker job for Watching the existing and new directory for
	 * new file and it also delegate method call for maintaining cache.
	 */
	private void startWatching() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (startWatch) {
					WatchKey key;
					try {
						key = watchService.take();
					} catch (InterruptedException x) {
						return;
					}
					Path dir = keys.get(key);
					if (dir == null) {
						System.err.println("WatchKey not recognized!!");
						continue;
					}

					for (WatchEvent<?> event : key.pollEvents()) {
						Path name = ((WatchEvent<Path>) event).context();

						Path child = dir.resolve(name);
						if (Files.isDirectory(child)) {
							try {
								walkAndRegisterDirectories(child);
							} catch (IOException | InterruptedException e) {
								logger.info(e.getMessage());
							}
						} else {
							if (CommonUtils.checkFileType(name.toString(), fileExtensions)) {
								createCache(child);
							}
						}
					}

					/* reset key and remove from set if directory no longer accessible */
					boolean valid = key.reset();
					if (!valid) {
						keys.remove(key);
						// all directories are inaccessible
						if (keys.isEmpty()) {
							break;
						}
					}

				}

			}
		}).start();

	}

	/**
	 * This method is used to register directory for watch.
	 * 
	 * @param dir , it is directory that needs to watch.
	 * @throws IOException
	 */
	private void registerDirectory(Path dir) throws IOException {
		WatchKey key = dir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
				StandardWatchEventKinds.ENTRY_MODIFY);
		createCache(dir);
		keys.put(key, dir);
	}

	/**
	 * Register the given directory, and all its sub-directories, with the
	 * WatchService.
	 * 
	 * @throws InterruptedException
	 */
	private void walkAndRegisterDirectories(final Path start) throws IOException, InterruptedException {
		if (start.toFile().isDirectory()) {
			File[] files = start.toFile().listFiles();
			for (int index = 0; index < files.length; index++) {
				Thread.sleep(100);
				File file = files[index];
				if (file.isDirectory()) {
					walkAndRegisterDirectories(Paths.get(file.getAbsolutePath()));
				}
			}
			registerDirectory(start);
		}
	}

	private void createCache(Path path) {
		createCache(path, true);
	}

	/**
	 * This method crate separate worker job for each resources .This method is used
	 * to maintain map for already scan file , folder and sub folder in the form of
	 * map. This method build DirectoryScanData data and store it in map as key and
	 * value pair for each scan resources.
	 * 
	 * @param path
	 * @param persist
	 */
	private void createCache(Path path, boolean persist) {
		executorService.execute(() -> {
			File fileFolder = path.toFile();
			String relativePath = CommonUtils.relativePath(path.toString(), watchDir);
			if (fileFolder.isFile()) {
				if (CommonUtils.checkFileType(path.getFileName().toString(), fileExtensions)) {
					cacheService.add(relativePath, parserService.buildDirectoryScanData(path));
					if (persist) {
						cacheService.getOrAddChildFolder(relativePath);
					}
				}
			} else {
				File[] files = fileFolder.listFiles();
				CountDownLatch latch = new CountDownLatch(files.length);
				for (File file : files) {
					try {
						createCache(Paths.get(file.getAbsolutePath()), false);
					} finally {
						latch.countDown();
					}
				}
				try {
					latch.await();
				} catch (Exception e) {
					logger.info(e.getMessage());
				}
				if (persist) {
					cacheService.getOrAddChildFolder(relativePath);
				}
			}
		});

	}

}
