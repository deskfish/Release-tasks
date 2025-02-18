package com.sunlacey.eneity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_records")
public class UserRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private Long taskId;

    @Column(nullable = false)
    private LocalDateTime operationTime;

    @Column(nullable = false)
    private Boolean agreed;

    @Column(nullable = false)
    private Boolean operated = false;

    public UserRecord() {}

    public UserRecord(Long id, Long taskId, String username, Boolean operated) {
        this.id = id;
        this.taskId = taskId;
        this.username = username;
        this.operated = operated;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public LocalDateTime getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(LocalDateTime operationTime) {
        this.operationTime = operationTime;
    }

    public Boolean getAgreed() {
        return agreed;
    }

    public void setAgreed(Boolean agreed) {
        this.agreed = agreed;
    }

    public Boolean isOperated() {
        return operated;
    }

    public void setOperated(Boolean operated) {
        this.operated = operated;
    }
}