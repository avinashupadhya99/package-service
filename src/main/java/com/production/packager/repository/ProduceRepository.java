package com.production.packager.repository;

import java.util.List;
import java.util.Optional;

import com.production.packager.model.Produce;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProduceRepository extends CrudRepository<Produce, Long> {
    Optional<Produce> findById(Long id);
    List<Produce> findByPackaged(boolean packaged);
}
