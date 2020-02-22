package com.lagou.edu.dao;

import com.lagou.edu.pojo.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 简历接口 符合SpringDataJpa要求的DAO接口
 *  JpaRepository<操作的实体类类型，主键类型>
 *      封装了基本的CURD操作
 *
 *  JpaSpecificationExecutor<操作的实体类类型>
 *      封装了复杂的查询（分页，排序等）
 */
public interface ResumeDao extends JpaRepository<Resume,Long>, JpaSpecificationExecutor<Resume> {
    /**
     * 使用原生sql查询，将nativeQuery属性设置为true,默认为false
     * @return
     */
    @Query(value="select * from tb_resume",nativeQuery = true)
    List<Resume> findAll();

    /**
     * 方法命名规则
     */
    void deleteById(long id);

}
