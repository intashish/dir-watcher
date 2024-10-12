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

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BatchSyncEvent {

	@Autowired
	public Path path;

	@Autowired
	private Services services;

	private WatchService watchService;
	private final List<DirDetailsEntity> fileChanges = new ArrayList<>(); // List to collect changes

	@PostConstruct
	public void initBatchSyncEvent() throws Exception {
		System.out.println("Testing initBatchSyncEvent");

		watchService = FileSystems.getDefault().newWatchService();
		path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY,
				StandardWatchEventKinds.ENTRY_DELETE);
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
							DirDetailsEntity entity = new DirDetailsEntity();

							entity.setPath(fullPath);
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

	@Scheduled(fixedDelay = 10_000)
	private void scheduleLogTask() {
		System.out.println("Testing scheduleLogTask");
		synchronized (fileChanges) {
			if (!fileChanges.isEmpty()) {
				System.out.println("Logging file changes for the last minute:");
				for (DirDetailsEntity entry : fileChanges) {

					System.out.println("file entry ===>" + readFileContent(entry.path));
					entry.setCurrent(readFileContent(entry.path));
					System.out.println(entry);
					try {
						System.out.println("Calling services.saveDirDetails(entry)");
						services.saveDirDetails(entry);
					} catch (Exception e) {
						System.out.println("Error saving details");
						System.out.println(e);
						e.printStackTrace();
					}
//						service.saveDirDetails(entry);
					System.out.println("entry data == " + entry);
				}

				fileChanges.clear(); // Clear the list after logging
			} else {
				System.out.println("No file changes detected in the last minute.");
			}
		}
	}

	// Method to read the content of a file
	private String readFileContent(Path filePath) {
		try {
			System.out.println("readFileContent::filePath: " + filePath);
			List<String> lines = Files.readAllLines(filePath);
			return String.join("\n", lines);
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}

}
