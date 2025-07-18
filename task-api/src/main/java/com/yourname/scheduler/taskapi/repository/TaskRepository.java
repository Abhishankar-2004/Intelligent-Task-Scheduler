package com.yourname.scheduler.taskapi.repository;

import com.yourname.scheduler.taskapi.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    // Spring Data JPA provides findAll(), findById(), save(), deleteById(), etc. for free!
}