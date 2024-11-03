package it.fartingbrains.fitness.activity.repository;

import it.fartingbrains.fitness.activity.entity.Set;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SetRepository extends JpaRepository<Set, UUID> {
}
