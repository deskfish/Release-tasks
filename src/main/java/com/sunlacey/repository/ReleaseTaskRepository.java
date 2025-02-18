package com.sunlacey.repository;

import com.sunlacey.eneity.ReleaseTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReleaseTaskRepository extends JpaRepository<ReleaseTask, Long> {
    List<ReleaseTask> findByTaskName(String taskName);

    // 添加根据 ID 查询任务的方法
    ReleaseTask findById(long id);
}