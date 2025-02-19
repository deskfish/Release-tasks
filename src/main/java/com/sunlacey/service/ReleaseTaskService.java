package com.sunlacey.service;

import com.sunlacey.eneity.ReleaseTask;
import com.sunlacey.eneity.UserRecord;
import com.sunlacey.repository.ReleaseTaskRepository;
import com.sunlacey.repository.UserRecordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class ReleaseTaskService {
    @Resource
    private ReleaseTaskRepository releaseTaskRepository;

    @Resource
    private UserRecordRepository userRecordRepository;

    public List<UserRecord> getUserRecordsByTaskId(long taskId) {
        log.info("获取任务的用户记录，任务ID: {}", taskId);
        List<UserRecord> records = userRecordRepository.findAllByTaskId(taskId);
        log.info("成功获取用户记录，任务ID: {}, 记录数量: {}", taskId, records.size());
        return records;
    }

    public ReleaseTask createTask(String taskName, String taskDetail, List<String> selectedUsers) {
        try {
            log.info("开始创建发布任务，任务名称: {}, 需要同意人数: {}, 选择用户数: {}", taskName, selectedUsers.size(), selectedUsers.size());
            // 确保所有字段都被传递
            ReleaseTask task = new ReleaseTask(null, taskName, taskDetail, selectedUsers.size(), 0, false, false, null);
            releaseTaskRepository.save(task);
            log.info("任务基本信息已保存，任务ID: {}", task.getId());

            // 为每个选中的用户创建记录
            log.info("开始创建用户记录，任务ID: {}", task.getId());
            try {
                for (String username : selectedUsers) {
                    try {
                        UserRecord userRecord = new UserRecord(null, task.getId(), username, false);
                        userRecord.setOperationTime(LocalDateTime.now());  // 设置初始操作时间为当前时间
                        userRecord.setAgreed(false);  // 初始时设置为false而不是null
                        userRecordRepository.save(userRecord);
                        log.debug("已创建用户权限记录，任务ID: {}, 授权用户: {}", task.getId(), username);
                    } catch (Exception e) {
                        log.error("创建用户权限记录失败，任务ID: {}, 用户: {}, 错误: {}", task.getId(), username, e.getMessage(), e);
                        throw e;
                    }
                }
                log.info("完成用户权限记录创建，任务ID: {}, 授权用户数: {}", task.getId(), selectedUsers.size());
            } catch (Exception e) {
                log.error("创建用户记录过程中发生错误，任务ID: {}, 错误: {}", task.getId(), e.getMessage(), e);
                throw e;
            }

            return releaseTaskRepository.findById(task.getId()).get();
        } catch (Exception e) {
            log.error("创建任务失败，任务名称: {}, 错误: {}", taskName, e.getMessage(), e);
            throw new RuntimeException("创建任务失败: " + e.getMessage(), e);
        }
    }

    public ReleaseTask getTaskById(long id) {
        log.info("正在查询任务，任务ID: {}", id);
        ReleaseTask task = releaseTaskRepository.findById(id);
        if (task == null) {
            log.warn("未找到指定任务，任务ID: {}", id);
            return null;
        }
        
        // 获取任务相关的所有用户记录（包括已点击和未点击的）
        List<UserRecord> userRecords = getUserRecordsByTaskId(id);
        task.setUserRecords(userRecords);
        
        log.info("成功获取任务信息和用户记录，任务ID: {}, 任务名称: {}, 用户记录数: {}", 
                id, task.getTaskName(), userRecords.size());
        return task;
    }

    public void updateAgreedStatus(long id, boolean agreed, String username) {
        log.info("开始更新任务状态，任务ID: {}, 用户: {}, 是否同意: {}", id, username, agreed);
        ReleaseTask task = releaseTaskRepository.findById(id);
        if (task != null) {
            // 检查用户是否有权限操作该任务
            UserRecord userRecord = userRecordRepository.findByTaskIdAndUsername(id, username);
            if (userRecord == null) {
                log.warn("用户无权操作此任务，任务ID: {}, 用户: {}", id, username);
                throw new RuntimeException("您没有权限操作此任务");
            }

            // 检查用户是否已经操作过该任务
            if (userRecord.isOperated()) {
                log.warn("用户重复操作，任务ID: {}, 用户: {}", id, username);
                throw new RuntimeException("您已经操作过此任务");
            }

            // 记录用户操作
            userRecord.setOperated(true);
            userRecord.setAgreed(agreed);
            userRecord.setOperationTime(LocalDateTime.now());
            userRecordRepository.save(userRecord);

            if (agreed) {
                task.setCurrentAgreeCount(task.getCurrentAgreeCount() + 1);
                log.info("用户同意任务，当前同意数: {}/{}", task.getCurrentAgreeCount(), task.getNeedAgreesCount());
                //如果同意人数达到要求，结束任务
                if (task.getCurrentAgreeCount() >= task.getNeedAgreesCount()) {
                    task.setEnd(true);
                    log.info("任务已达到所需同意人数，任务结束，任务ID: {}", id);
                }
            } else {
                //如果拒绝，直接结束任务
                task.setEnd(true);
                task.setTaskInvalid(true);
                log.info("用户拒绝任务，任务已失效，任务ID: {}", id);
            }
            releaseTaskRepository.save(task);
        }
    }
}