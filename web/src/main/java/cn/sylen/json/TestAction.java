package cn.sylen.json;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

import cn.sylen.TestService;
import cn.sylen.common.util.Result;

@Controller
@ResponseBody
@Api(value = "TestAction", description = "测试模块接口")
@RequestMapping("/testAction")
public class TestAction extends AbstractAction {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private TestService testService;
	
	@ApiOperation(value="Hello world",httpMethod="GET",notes="hello world")
    @RequestMapping("/hello")
    @ResponseBody
    public String hello() {
    	return "Hello world!";
    }
	
	@ApiOperation(value="登陆测试",httpMethod="POST",notes="登陆测试")
    @RequestMapping("/loginTest")
    @ResponseBody
    public Result<?> loginTest(
    		@ApiParam("登陆名") @RequestParam(value="account", required = false) String userName,
    		@ApiParam("密码") @RequestParam(value="password", required = false) String password) {
		Boolean flag = testService.doLogin(userName, password);
		if (flag) {
			return buildSuccessResult("登陆成功！");
		} else {
			return buildFailuerResult("登录失败！");
		}
    }
	
}
