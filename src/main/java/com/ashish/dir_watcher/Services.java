package com.ashish.dir_watcher;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class Services {

//	@Autowired
//	public DirDetailsEntity dirDetailsEntity;
	@Autowired
	private DirDetailRepository dirDetailRepository;

	public void saveDirDetails(DirDetailsEntity dirDetailsEntity) {
		dirDetailRepository.save(dirDetailsEntity);
	}

	public Optional<DirDetailsEntity> getDirDetails(ObjectId id) {
		return dirDetailRepository.findById(id);
	}
}
