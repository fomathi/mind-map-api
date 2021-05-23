package com.mind.map.api;

import com.mind.map.api.domain.Map;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MapRepository extends MongoRepository<Map, String> {
    Map findByName(String name);
}