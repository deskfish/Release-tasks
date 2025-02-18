package com.sunlacey.repository;

import com.sunlacey.eneity.UserRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserRecordRepository extends JpaRepository<UserRecord, Long> {
    UserRecord findByTaskIdAndUsername(long taskId, String username);
    List<UserRecord> findAllByTaskId(long taskId);
}