package cn.wildfirechat.app.controller;

import cn.wildfirechat.app.RestResult;
import cn.wildfirechat.app.enums.MessageChatEnum;
import cn.wildfirechat.app.enums.RelateVerifyEnum;
import cn.wildfirechat.app.jpa.Friend;
import cn.wildfirechat.app.jpa.FriendRepository;
import cn.wildfirechat.app.jpa.Member;
import cn.wildfirechat.app.jpa.MemberRepository;
import cn.wildfirechat.app.model.form.AddFriendRequestForm;
import cn.wildfirechat.app.model.form.RespondFriendInviteForm;
import cn.wildfirechat.app.model.vo.FriendRequestVO;
import cn.wildfirechat.app.tools.*;
import cn.wildfirechat.sdk.RelationAdmin;
import cn.wildfirechat.sdk.model.IMResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static cn.wildfirechat.app.RestResult.RestCode.ERROR_SERVER_ERROR;

@Api(tags = "索引接口")
@RestController
@RequestMapping("/index")
public class IndexController {
	private static final Logger LOG = LoggerFactory.getLogger(IndexController.class);

	@Autowired
	private UploadFileUtils uploadFileUtils;

	@ApiOperation(value = "查询图檔domain")
	@GetMapping("/getImagePathDomain")
	public RestResult getImagePathDomain() {
		try {
			return RestResult.ok(uploadFileUtils.getDomain());
		} catch (Exception e) {
			LOG.error("查询图檔domain exception", e);
			return RestResult.error(ERROR_SERVER_ERROR);
		}
	}

}
