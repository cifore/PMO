package com.pmo.dashboard.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pmo.dashboard.entity.Resume;
import com.pmo.dashboard.entity.User;
import com.pmo.dashboard.util.Utils;
import com.pom.dashboard.service.ResumeService;

/**
 * 候选人的信息的Controller
 * 
 * @author dilu
 * @version 1.0 2017-8-25 15:09:30
 */
@Controller
@RequestMapping(value="/resume")
public class ResumeController {
	
	@Resource
	private ResumeService resumeService;
	
	/**
	 * 修改候选人的方法
	 * 
	 * @param candidateId
	 * @param model
	 * @return
	 */
	@RequestMapping("/toUpdateResume")
	public String updateResume(String candidateId,Model model){
		
		//System.out.println(candidateId);
		return null;
		
	}
	/**
	 * 跳转到录入信息的页面
	 * 
	 * @return 信息录入页面
	 */
	@RequestMapping("/input")
	public String input(){
		
		return "resume/add";
	}
	
	/**
	 * 根据手机号查询候选人的方法
	 * 
	 * @param tel
	 * @return
	 */
	@RequestMapping("/checkTel")
	@ResponseBody
	public String checkTel(String tel){
		boolean result = true;
		Resume c=resumeService.searchTel(tel);
		if(c!=null){
			result = false;
		}
		Map<String, Boolean> map = new HashMap<>();
        map.put("valid", result);
        ObjectMapper mapper = new ObjectMapper();
        String resultString = "";
        try {
            resultString = mapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return resultString;
	}
	
	/**
	 * 录入候选人的方法
	 * 
	 * @param resume
	 * @param model
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/add")
	@ResponseBody
	public String add(Resume resume,Model model,HttpSession session ) throws Exception{
		
			//设置候选人id
			String uuid = Utils.getUUID();
			resume.setId(uuid);
			
			//设置候选人的HR
			User user = (User) session.getAttribute("loginUser");
			String userId = user.getUserId();
			resume.setHr(userId);
			//设置创建人
			resume.setCreate_user(userId);
			
			//设置创建时间
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			String todayDate = df.format(new Date());
			resume.setCreate_date(todayDate);
			//将当前时间转换成毫秒
			long nt = df.parse(todayDate).getTime();
			//获取毕业时间
			String graduate_date = resume.getGraduate_date();
			//将毕业时间转换成毫秒
			long gt = df.parse(graduate_date).getTime();
			//计算出差异
			long cha = nt-gt;
			//将差异转换为年数,向下取整 
			int year = (int) Math.floor(cha/ 1000 / 60 / 60 / 24/ 30 /12);
			// 设置工作年限
			resume.setExperience_years(String.valueOf(year));
			
		try{
			//调用service层  执行保存操作
			resumeService.add(resume);
			return "0";
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return "1";
		}
		
	}
}
