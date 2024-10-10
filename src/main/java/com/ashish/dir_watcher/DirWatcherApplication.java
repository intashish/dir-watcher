package com.ashish.dir_watcher;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DirWatcherApplication {

	@Bean
	public static Path directoryPath() {
		return Paths.get("C:/Users/Ashish/OneDrive/Desktop/Dir_monitor");
	}
	@Bean
	public static Services getService() {
		return new Services();
	}

	@Bean
	public static BatchSyncEvent startBatchEvent() throws Exception {
		return new BatchSyncEvent(directoryPath(), getService());
	}

	@Bean
	public ScheduledExecutorService scheduledExecutorService() {
		// You can configure the thread pool size as needed
		return Executors.newScheduledThreadPool(1);
	}

	public static void main(String[] args) {
		SpringApplication.run(DirWatcherApplication.class, args);

		try {
			startBatchEvent().startWatchService();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
