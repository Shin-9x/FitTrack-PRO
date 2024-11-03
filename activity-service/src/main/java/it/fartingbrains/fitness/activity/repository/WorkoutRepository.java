package it.fartingbrains.fitness.activity.repository;

import it.fartingbrains.fitness.activity.entity.Workout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WorkoutRepository extends JpaRepository<Workout, UUID> {
}
