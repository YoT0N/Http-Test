package edu.ilkiv.lab5.repository;

/*
  @author Bodya
  @project lab5
  @class BusRepository
  version 1.0.0
  @since 18.04.2025 - 16:40 
*/

import edu.ilkiv.lab5.model.Bus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusRepository extends MongoRepository<Bus, String> {
}
