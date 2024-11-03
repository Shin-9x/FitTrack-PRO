package it.fartingbrains.fitness.activity.repository;

import it.fartingbrains.fitness.activity.entity.WorkoutActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WorkoutActivityRepository extends JpaRepository<WorkoutActivity, UUID> {
}
