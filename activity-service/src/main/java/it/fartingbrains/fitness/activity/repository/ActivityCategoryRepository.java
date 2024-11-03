package it.fartingbrains.fitness.activity.repository;

import it.fartingbrains.fitness.activity.entity.ActivityCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityCategoryRepository extends JpaRepository<ActivityCategory, Long> {
}
