package com.rabs.libraryevents.consumer.repository;

import com.rabs.libraryevents.consumer.entity.LibraryEvent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LibraryEventsRepository extends CrudRepository<LibraryEvent, Integer> {
}
