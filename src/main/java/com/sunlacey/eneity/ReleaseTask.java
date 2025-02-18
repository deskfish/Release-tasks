package com.sunlacey.eneity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.List;

/**
 * 发布任务实体类
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReleaseTask {

    /**
     * 任务ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务详情
     */
    private String taskDetail;

    /**
     * 需要同意的人数
     */
    private int needAgreesCount;

    /**
     * 当前已同意的人数
     */
    private int currentAgreeCount;

    /**
     * 任务是否结束
     */
    private boolean isEnd = false;

    /**
     * 任务是否失效
     */
    private boolean taskInvalid = false;

    /**
     * 用户记录列表
     */
    @Transient
    private List<UserRecord> userRecords;
}