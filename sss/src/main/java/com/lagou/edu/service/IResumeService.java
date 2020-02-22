package com.lagou.edu.service;

import com.lagou.edu.pojo.Resume;

import java.util.List;

/**
 * service层
 */
public interface IResumeService {
    /**
     * 查询所有
     * @return
     */
    List<Resume> listAll();
    /**
     * 根据id查询单条记录
     * @param resumeId
     * @return
     */
    Resume listOne(long resumeId);
    /**
     * 保存
     * @param resume
     */
    void save(Resume resume);

    /**
     * 根据id删除
     * @param resumeId
     */
    void deleteById(long resumeId);
}
