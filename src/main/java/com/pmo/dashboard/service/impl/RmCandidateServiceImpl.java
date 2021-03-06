package com.pmo.dashboard.service.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.pmo.dashboard.dao.CSDeptMapper;
import com.pmo.dashboard.dao.CandidateMapper;
import com.pmo.dashboard.dao.InterviewMapper;
import com.pmo.dashboard.dao.RmCandidateMapper;
import com.pmo.dashboard.dao.UserMapper;
import com.pmo.dashboard.entity.CSDept;
import com.pmo.dashboard.entity.CandidateInfo;
import com.pmo.dashboard.entity.CandidateInterview;
import com.pmo.dashboard.entity.CandidatePush;
import com.pmo.dashboard.entity.PageCondition;
import com.pmo.dashboard.entity.User;
import com.pom.dashboard.service.RmCandidateService;

/**
 * RM后人选人实现类
 * @author blkmk7
 *
 */
@Service
public class RmCandidateServiceImpl implements RmCandidateService {
	
	@Resource
	RmCandidateMapper rmCandidateMapper;
	
	@Resource
	InterviewMapper interviewMapper;
	
	@Resource
	CandidateMapper candidateMapper;
	
	@Override
	public List<CandidatePush> queryPushedCandidate(String csSubDeptId, String status,PageCondition pageCondition) {
		Map<String, Object> params = new HashMap<String,Object>();
		params.put("csSubDeptId", csSubDeptId);
		params.put("status", status);
		params.put("pageCondition", pageCondition);
		//计算查询起始行号
		int num = (pageCondition.getCurrPage() - 1)*pageCondition.getPageSize();
		params.put("num", num);
		//查找面试表中登录rm的部门下的最近一次面试的数据关联到推送表
		List<CandidatePush> candidateList = rmCandidateMapper.queryPushedCandidate(params);
		//将面试表的id关联到推送表的InterviewId
		/*for (CandidatePush candidatePush : candidateList) {
			List<CandidateInterview> interviewList = candidatePush.getInterviewList();
			//candidatePush.setInterviewId(interviewList.get(0).getInterviewId());
			Map<String, Object> params1 = new HashMap<String,Object>();
			params1.put("interviewId", interviewList.get(0).getInterviewId());
			params1.put("candidateId", candidatePush.getCandidateId());
			rmCandidateMapper.updateInterviewId(params1);
		}*/
		//设置总页数
		int queryCandidateCount = rmCandidateMapper.queryCandidateCount(params);
		pageCondition.setTotalPage((int) Math.ceil(queryCandidateCount*1.0 / pageCondition.getPageSize()));
		return candidateList;
	}

	@Override
	public void addInterview(CandidateInterview candidateInterview,String pushId) {
		interviewMapper.addInterview(candidateInterview);
		Map<String, Object> params = new HashMap<String,Object>();
		String candidateId = candidateInterview.getCandidateId();
		params.put("candidateId", candidateId);
		//候选人表interviewStatus=2 表示面试中
		params.put("interviewStatus", "2");
		candidateMapper.updateInterviewStatusById(params);
	}

	@Override
	public void addNextInterview(CandidateInterview candidateInterview, String pushId) {
		String candidateId = candidateInterview.getCandidateId();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("candidateId", candidateId);
		params.put("csSubDeptId", candidateInterview.getCssubDept());
		//获取最新的一轮面试数据
		CandidateInterview newCandidateInterview = interviewMapper.queryNewInterviewByCandidateId(params);
		//设置下一轮面试的fatherid
		candidateInterview.setFatherInterviewId(newCandidateInterview.getFatherInterviewId());
		int newSerial = Integer.valueOf(newCandidateInterview.getInterviewSerial())+1;
		//设置面试轮次加一
		candidateInterview.setInterviewSerial(String.valueOf(newSerial));
		interviewMapper.addInterview(candidateInterview);
		//候选人表interviewStatus=2 表示面试中
		params.put("interviewStatus", "2");
		candidateMapper.updateInterviewStatusById(params);
	}

	@Override
	public void interviewBack(String pushId) {
		//0表示未推送
		String status = "0";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", status);
		params.put("pushId", pushId);
		rmCandidateMapper.updateCandidateStatus(params);
	}

}
