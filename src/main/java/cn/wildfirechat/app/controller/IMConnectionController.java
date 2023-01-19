package cn.wildfirechat.app.controller;

import cn.wildfirechat.app.RestResult;
import cn.wildfirechat.app.jpa.Member;
import cn.wildfirechat.app.jpa.MemberRepository;
import cn.wildfirechat.app.pojo.MemberInfoRequest;
import cn.wildfirechat.app.pojo.vo.MemberInfoVO;
import cn.wildfirechat.app.tools.ReflectionUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class IMConnectionController {
	private ObjectMapper objectMapper = new ObjectMapper();

	private static final Logger LOG = LoggerFactory.getLogger(IMConnectionController.class);

	@Autowired
	private MemberRepository memberRepository;

	/*
	 * 用户明细
	 */
	@PostMapping(value = "/app/user/info")
	public Object appUserInfo(@RequestBody MemberInfoRequest request) throws Exception {
		String json = objectMapper.writeValueAsString(request);
		LOG.info("onUserOnlineEvent（用户明细） json: {}", json);
		Member member = memberRepository.findByUid(request.getUid());
		MemberInfoVO vo = MemberInfoVO.builder().build();
		ReflectionUtils.copyFields(vo, member, ReflectionUtils.STRING_TRIM_TO_NULL);
		return RestResult.ok(vo);
	}
}