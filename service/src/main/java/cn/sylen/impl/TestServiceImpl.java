package cn.sylen.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.sylen.TestService;
import cn.sylen.common.vo.TestEntity;
import cn.sylen.dao.TestDao;

@Service
public class TestServiceImpl implements TestService {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private TestDao testDao;

	@Override
	public Boolean doLogin(String userName, String password) {
		TestEntity testEntity = new TestEntity();
		testEntity.setUserName(userName);
		testEntity.setPassword(password);
		
		TestEntity targetEntity = testDao.findObjectBySample(testEntity, TestEntity.class);
		
		if(targetEntity != null) {
			targetEntity.setLoginTimes(targetEntity.getLoginTimes() + 1);
			testDao.update(targetEntity, true);
			logger.info("login success!");
			return true;
		} else {
			logger.info("login fail!");
			return false;
		}
		
	}
	
}
