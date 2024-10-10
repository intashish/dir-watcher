package com.ashish.dir_watcher;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DirDetailRepository extends MongoRepository<DirDetailsEntity, ObjectId>{

}
