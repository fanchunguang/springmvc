package com.lagou.edu.controller;

import com.lagou.edu.pojo.Resume;
import com.lagou.edu.service.IResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ResumeController {

    @Autowired
    private IResumeService service;

    @RequestMapping(value="/resume/list",method = RequestMethod.POST)
    @ResponseBody
    public Object getResumeList(){
        List<Resume> resumes = service.listAll();
        Map<String,Object> map = new HashMap<>();
        map.put("data",resumes);
        return map;
    }

    @RequestMapping(value="/resume/del/{resumeId}",method = RequestMethod.POST)
    @ResponseBody
    public Object deleteResume(@PathVariable("resumeId") long resumeId){
        service.deleteById(resumeId);
        Map<String,Object> map = new HashMap<>();
        map.put("success",true);
        return map;
    }

    @RequestMapping(value="/resume/save",method = RequestMethod.POST)
    @ResponseBody
    public Object saveResume(Resume resume){
        service.save(resume);
        Map<String,Object> map = new HashMap<>();
        map.put("success",true);
        return map;
    }

}