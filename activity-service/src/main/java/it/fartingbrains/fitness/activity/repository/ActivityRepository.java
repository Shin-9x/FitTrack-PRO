package it.fartingbrains.fitness.activity.repository;

import it.fartingbrains.fitness.activity.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ActivityRepository extends JpaRepository<Activity, UUID> {
}
