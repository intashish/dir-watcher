package com.ashish.dir_watcher;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BatchSyncEvent {

	@Autowired
	private DirDetailRepository dirDetailRepository;
	@Autowired
	private Path path;
	@Autowired
	private DirDetailsEntity dirDetailsEntity;
//	@Autowired
	private Services services;
//	@Autowired
	private final WatchService watchService;
	private final List<DirDetailsEntity> fileChanges; // List to collect changes
	@Autowired
	private final ScheduledExecutorService scheduler;

	public BatchSyncEvent(Path path, Services services) throws Exception {
		// Initialize watch service
		this.watchService = FileSystems.getDefault().newWatchService();
		this.fileChanges = new ArrayList<>();
		this.scheduler = Executors.newScheduledThreadPool(1);
		this.path = path;
		this.services = services;

		// Register directory for watching
		path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY,
				StandardWatchEventKinds.ENTRY_DELETE);

		// Start the watch service to collect changes
//		startWatchService();

		// Schedule a task to log changes every minute
		scheduleLogTask();
	}

	void startWatchService() {
		new Thread(() -> {
			while (true) {
				try {
					// Wait for a watch key to be signaled
					WatchKey key = watchService.take();
					Path dir = (Path) key.watchable();
					// Retrieve events for the watch key
					for (WatchEvent<?> event : key.pollEvents()) {
						WatchEvent.Kind<?> kind = event.kind();
						Path fileName = (Path) event.context();
						Path fullPath = dir.resolve(fileName);
						synchronized (fileChanges) {
							// Collect the change events
							DirDetailsEntity entity = new DirDetailsEntity(fullPath);
							entity.setFilename(fileName);
							fileChanges.add(entity);
							System.out.println("File " + fullPath + " was " + kind.name());
						}
					}

					// Reset the key -- this step is critical to receive further watch events
					boolean valid = key.reset();
					if (!valid) {
						break; // Exit loop if directory is no longer accessible
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					break;
				}
			}
		}).start();
	}

	private void scheduleLogTask() {
		scheduler.scheduleAtFixedRate(() -> {
			synchronized (fileChanges) {
				if (!fileChanges.isEmpty()) {
					System.out.println("Logging file changes for the last minute:");
					for (DirDetailsEntity entry : fileChanges) {

						System.out.println("file entry ===>" + readFileContent(entry.path));
						entry.setCurrent(readFileContent(entry.path));
						System.out.println(entry);
						try {

							services.saveDirDetails(entry);
						} catch (Exception e) {
							System.out.println(e);
						}
//						service.saveDirDetails(entry);
						System.out.println("entry data == " + entry);
					}

					fileChanges.clear(); // Clear the list after logging
				} else {
					System.out.println("No file changes detected in the last minute.");
				}
			}
		}, 1, 1, TimeUnit.MINUTES); // Schedule the task to run every minute
	}

	// Method to read the content of a file
	private String readFileContent(Path filePath) {
		try {
			List<String> lines = Files.readAllLines(filePath);
			return String.join("\n", lines);
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}

}
