package com.ashish.dir_watcher;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DirWatcherApplication {

	@Bean
	public static Path directoryPath() {
		return Paths.get("D:/Test");
	}

	@Autowired
	public BatchSyncEvent startBatchEvent;

	public static void main(String[] args) {
		SpringApplication.run(DirWatcherApplication.class, args);
	}

	@PostConstruct
	public void init() {
		System.out.println("All beans created. Calling init method");
		try {
			startBatchEvent.startWatchService();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
