package com.ashish.dir_watcher;

import java.nio.file.Path;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

@Document(collection = "file_details")
// @Component
@Getter
@Setter
@ToString
public class DirDetailsEntity {

	@Id
	private ObjectId id;
	protected Path filename;
	protected Path path;
//	private String previous;
	private String current;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Path getFilename() {
		return filename;
	}

	public void setFilename(Path filename) {
		this.filename = filename;
	}

	public Path getPath() {
		return path;
	}

	public void setPath(Path path) {
		this.path = path;
	}

	public String getCurrent() {
		return current;
	}

	public void setCurrent(String current) {
		this.current = current;
	}

	@Override
	public String toString() {
		return "DirDetailsEntity{" +
				"id=" + id +
				", filename=" + filename +
				", path=" + path +
				", current='" + current + '\'' +
				'}';
	}
}
