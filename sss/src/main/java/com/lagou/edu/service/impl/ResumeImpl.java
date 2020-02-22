package com.lagou.edu.service.impl;

import com.lagou.edu.dao.ResumeDao;
import com.lagou.edu.pojo.Resume;
import com.lagou.edu.service.IResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

/**
 * 简历接口实现类
 */
@Service
public class ResumeImpl implements IResumeService {
    @Autowired
    private ResumeDao resumeDao;

    @Override
    public List<Resume> listAll() {
        List<Resume> all = resumeDao.findAll();
        return all;
    }

    @Override
    public Resume listOne(long resumeId) {
        Optional<Resume> resume = resumeDao.findById(resumeId);
        return resume.get();
    }

    @Override
    public void save(Resume resume) {
        if(resume ==null){
            return;
        }
        resumeDao.save(resume);
    }

    @Override
    public void deleteById(long resumeId) {
        resumeDao.deleteById(resumeId);
    }
}
