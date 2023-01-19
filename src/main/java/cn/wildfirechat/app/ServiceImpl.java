package cn.wildfirechat.app;

import cn.wildfirechat.app.config.RedisConfig;
import cn.wildfirechat.app.enums.*;
import cn.wildfirechat.app.exception.IMServerException;
import cn.wildfirechat.app.jpa.*;
import cn.wildfirechat.app.model.vo.GeneralOrderVO;
import cn.wildfirechat.app.model.vo.MyInfoVO;
import cn.wildfirechat.app.model.vo.RechargeChannelVO;
import cn.wildfirechat.app.model.vo.UserAssertVO;
import cn.wildfirechat.app.pojo.*;
import cn.wildfirechat.app.pojo.vo.ChatRoomVO;
import cn.wildfirechat.app.service.LogService;
import cn.wildfirechat.app.shiro.AuthDataSource;
import cn.wildfirechat.app.shiro.PhoneCodeToken;
import cn.wildfirechat.app.shiro.TokenAuthenticationToken;
import cn.wildfirechat.app.sms.SmsService;
import cn.wildfirechat.app.tools.*;
import cn.wildfirechat.common.ErrorCode;
import cn.wildfirechat.pojos.*;
import cn.wildfirechat.proto.ProtoConstants;
import cn.wildfirechat.sdk.*;
import cn.wildfirechat.sdk.model.IMResult;
import com.aliyun.oss.*;
import com.aliyun.oss.model.PutObjectRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.http.HttpProtocol;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import io.minio.errors.MinioException;
import org.apache.http.HttpHeaders;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.crypto.hash.Sha1Hash;
import org.apache.shiro.subject.Subject;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;
import org.springframework.util.Base64Utils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static cn.wildfirechat.app.RestResult.RestCode.*;
import static cn.wildfirechat.app.jpa.PCSession.PCSessionStatus.Session_Canceled;
import static cn.wildfirechat.app.jpa.PCSession.PCSessionStatus.Session_Created;
import static cn.wildfirechat.app.jpa.PCSession.PCSessionStatus.Session_Pre_Verify;
import static cn.wildfirechat.app.jpa.PCSession.PCSessionStatus.Session_Scanned;

@org.springframework.stereotype.Service
public class ServiceImpl implements Service {
	private static final Logger LOG = LoggerFactory.getLogger(ServiceImpl.class);

	@Autowired
	private SmsService smsService;

	@Autowired
	private IMConfig mIMConfig;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private InviteCodeRepository inviteCodeRepository;

	@Autowired
	private AnnouncementRepository announcementRepository;

	@Autowired
	private FavoriteRepository favoriteRepository;

	@Autowired
	private UserPasswordRepository userPasswordRepository;

	@Autowired
	private WithdrawOrderRepository withdrawOrderRepository;

	@Autowired
	private RechargeOrderRepository rechargeOrderRepository;

	@Autowired
	private MemberBalanceRepository memberBalanceRepository;

	@Autowired
	private MemberBalanceLogRepository memberBalanceLogRepository;

	@Autowired
	private WithdrawPaymentMethodRepository withdrawPaymentMethodRepository;

	@Autowired
	private RechargeChannelRepository rechargeChannelRepository;

	@Autowired
	private DefaultMemberRepository defaultMemberRepository;

	@Autowired
	private FriendRepository friendRepository;

	@Autowired
	private ChatRoomRepository chatRoomRepository;

	@Value("${sms.super_code}")
	private String superCode;

	@Value("${logs.user_logs_path}")
	private String userLogPath;

	@Value("${upload.real.path}")
	private String uploadPath;

	@Value("${http.file.path}")
	private String httpFilePath;

	@Value("${http.file.path.domain.variable}")
	private String httpFilePathDomainVariable;

	@Autowired
	private ShortUUIDGenerator userNameGenerator;

	@Autowired
	private AuthDataSource authDataSource;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	private RateLimiter rateLimiter;

	@Autowired
	private LogService logService;

	@Autowired
	private RedisUtil redisUtil;

	@Resource
	private ObjectMapper objectMapper;

	@Value("${wfc.compat_pc_quick_login}")
	protected boolean compatPcQuickLogin;

	@Value("${media.server.media_type}")
	private int ossType;

	@Value("${media.server_url}")
	private String ossUrl;

	@Value("${media.access_key}")
	private String ossAccessKey;

	@Value("${media.secret_key}")
	private String ossSecretKey;

	@Value("${media.bucket_general_name}")
	private String ossGeneralBucket;
	@Value("${media.bucket_general_domain}")
	private String ossGeneralBucketDomain;

	@Value("${media.bucket_image_name}")
	private String ossImageBucket;
	@Value("${media.bucket_image_domain}")
	private String ossImageBucketDomain;

	@Value("${media.bucket_voice_name}")
	private String ossVoiceBucket;
	@Value("${media.bucket_voice_domain}")
	private String ossVoiceBucketDomain;

	@Value("${media.bucket_video_name}")
	private String ossVideoBucket;
	@Value("${media.bucket_video_domain}")
	private String ossVideoBucketDomain;

	@Value("${media.bucket_file_name}")
	private String ossFileBucket;
	@Value("${media.bucket_file_domain}")
	private String ossFileBucketDomain;

	@Value("${media.bucket_sticker_name}")
	private String ossStickerBucket;
	@Value("${media.bucket_sticker_domain}")
	private String ossStickerBucketDomain;

	@Value("${media.bucket_moments_name}")
	private String ossMomentsBucket;
	@Value("${media.bucket_moments_domain}")
	private String ossMomentsBucketDomain;

	@Value("${media.bucket_favorite_name}")
	private String ossFavoriteBucket;
	@Value("${media.bucket_favorite_domain}")
	private String ossFavoriteBucketDomain;

	@Value("${local.media.temp_storage}")
	private String ossTempPath;

	@Value("${userFromImFail.useAdminInstead: false}")
	private boolean userFromImFailUseAdminInstead;

	@Value("${sms.test.environment}")
	private boolean smsTestEnvironment;

	@Autowired
	private UploadFileUtils uploadFileUtils;

	private ConcurrentHashMap<String, Boolean> supportPCQuickLoginUsers = new ConcurrentHashMap<>();

	@PostConstruct
	private void init() {
		AdminConfig.initAdmin(mIMConfig.admin_url, mIMConfig.admin_secret);
		rateLimiter = new RateLimiter(60, 200);
	}

	private String getIp() {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();
		HttpServletRequest request = requestAttributes.getRequest();
		String ip = request.getHeader("X-Real-IP");
		if (!StringUtils.isEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
			return ip;
		}
		ip = request.getHeader("X-Forwarded-For");
		if (!StringUtils.isEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
			// 多次反向代理后会有多个IP值，第一个为真实IP。
			int index = ip.indexOf(',');
			if (index != -1) {
				return ip.substring(0, index);
			} else {
				return ip;
			}
		} else {
			return request.getRemoteAddr();
		}
	}

	private String getDevice() {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();
		HttpServletRequest request = requestAttributes.getRequest();
		return UserAgentUtils.getDeviceType(UserAgentUtils.getUserAgent(request));
	}

	@Override
	public RestResult sendLoginCode(String mobile) {
		String remoteIp = getIp();
		LOG.info("request send sms from {}", remoteIp);

		// 判断当前IP发送是否超频。
		// 另外 cn.wildfirechat.app.shiro.AuthDataSource.Count 会对用户发送消息限频
		if (!rateLimiter.isGranted(remoteIp)) {
			return RestResult.result(ERROR_SEND_SMS_OVER_FREQUENCY.code, "IP " + remoteIp + " 请求短信超频", null);
		}

		// 如果用户会员已注册已有设置密码,则挡掉,不再发送登入码
		Member memberEntity = memberRepository.findByPhone(mobile);
		if (memberEntity != null) {
			Optional<UserPassword> up = userPasswordRepository.findById(memberEntity.getUid());
			if (up.isPresent() && StringUtil.isNotBlank(up.get().getPassword())) {
				LOG.info("用户:{} 该手机号用户已存在", memberEntity.getMemberName());
				return RestResult.error(RestResult.RestCode.ERROR_MOBILE_MEMBER_EXIST);
			}
		}

		try {
			// String code = Utils.getRandomCode(6);
			// String code = "000000";//测试阶段默认
			String code = smsTestEnvironment ? "000000" : Utils.getRandomCode(6);// 测试阶段默认
			RestResult.RestCode restCode = authDataSource.insertRecord(mobile, code);

			if (restCode != SUCCESS) {
				return RestResult.error(restCode);
			}

			if (!smsTestEnvironment) {
				restCode = smsService.sendCode(mobile, code);// 测试环境暫且不用簡訊功能
			}
			if (restCode == RestResult.RestCode.SUCCESS) {
				return RestResult.ok(restCode);
			} else {
				authDataSource.clearRecode(mobile);
				return RestResult.error(restCode);
			}
		} catch (Exception e) {
			// json解析错误
			e.printStackTrace();
			authDataSource.clearRecode(mobile);
		}
		return RestResult.error(RestResult.RestCode.ERROR_SERVER_ERROR);
	}

	@Override
	public RestResult sendResetCode(String mobile) {

		Member member = memberRepository.findByPhone(mobile);
		if (ObjectUtils.isEmpty(member)) {
			return RestResult.error(ERROR_MEMBER_NOT_EXIST);
		}
		String userId = member.getUid();
		String remoteIp = getIp();

		// Subject subject = SecurityUtils.getSubject();
		// String userId = (String) subject.getSession().getAttribute("userId");
		// String remoteIp = getIp();
		// LOG.info("request send sms from {}", remoteIp);
		//
		// if (StringUtils.isEmpty(userId)) {
		// if (StringUtils.isEmpty(mobile)) {
		// return RestResult.error(ERROR_INVALID_PARAMETER);
		// }
		// } else {
		// try {
		// IMResult<InputOutputUserInfo> outputUserInfoIMResult =
		// UserAdmin.getUserByUserId(userId);
		// if (outputUserInfoIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
		// mobile = outputUserInfoIMResult.getResult().getMobile();
		// } else {
		// if (StringUtils.isEmpty(mobile)) {
		// return RestResult.error(ERROR_NOT_EXIST);
		// }
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// if (StringUtils.isEmpty(mobile)) {
		// return RestResult.error(RestResult.RestCode.ERROR_SERVER_ERROR);
		// }
		// }
		// }

		// 判断当前IP发送是否超频。
		// 另外 cn.wildfirechat.app.shiro.AuthDataSource.Count 会对用户发送消息限频
		if (!rateLimiter.isGranted(remoteIp)) {
			return RestResult.result(ERROR_SEND_SMS_OVER_FREQUENCY.code, "IP " + remoteIp + " 请求短信超频", null);
		}

		if (smsTestEnvironment) {
			// 测试环境替代,不使用sms
			Optional<UserPassword> optional = userPasswordRepository.findById(userId);
			UserPassword up = optional.orElseGet(() -> new UserPassword(userId));
			String code = "000000";
			up.setResetCode(code);
			up.setResetCodeTime(System.currentTimeMillis());
			up.setRegister(false);
			userPasswordRepository.save(up);
			return RestResult.ok(RestResult.RestCode.SUCCESS);
		} else {
			// 使用sms
			try {
				String code = Utils.getRandomCode(6);
				RestResult.RestCode restCode = smsService.sendCode(mobile, code);
				if (restCode == RestResult.RestCode.SUCCESS) {
					Optional<UserPassword> optional = userPasswordRepository.findById(userId);
					UserPassword up = optional.orElseGet(() -> new UserPassword(userId));
					up.setResetCode(code);
					up.setResetCodeTime(System.currentTimeMillis());
					userPasswordRepository.save(up);
					return RestResult.ok(restCode);
				} else {
					return RestResult.error(restCode);
				}
			} catch (Exception e) {
				// json解析错误
				e.printStackTrace();
			}
			return RestResult.error(RestResult.RestCode.ERROR_SERVER_ERROR);
		}

	}

	@Override
	public RestResult loginWithMobileCode(HttpServletRequest httpRequest, HttpServletResponse httpResponse, String mobile, String code,
			String inviteCode, String clientId, int platform) {
		Subject subject = SecurityUtils.getSubject();
		// 在认证提交前准备 token（令牌）
		PhoneCodeToken token = new PhoneCodeToken(mobile, code);
		// 执行认证登陆
		try {
			subject.login(token);
		} catch (UnknownAccountException uae) {
			return RestResult.error(RestResult.RestCode.ERROR_SERVER_ERROR);
		} catch (IncorrectCredentialsException ice) {
			return RestResult.error(RestResult.RestCode.ERROR_CODE_INCORRECT);
		} catch (LockedAccountException lae) {
			return RestResult.error(RestResult.RestCode.ERROR_CODE_INCORRECT);
		} catch (ExcessiveAttemptsException eae) {
			return RestResult.error(RestResult.RestCode.ERROR_CODE_INCORRECT);
		} catch (AuthenticationException ae) {
			return RestResult.error(RestResult.RestCode.ERROR_CODE_INCORRECT);
		}
		if (subject.isAuthenticated()) {
			long timeout = subject.getSession().getTimeout();
			LOG.info("Login success " + timeout);
			authDataSource.clearRecode(mobile);
		} else {
			return RestResult.error(RestResult.RestCode.ERROR_CODE_INCORRECT);
		}

		// 检查invite code
		if (StringUtils.isEmpty(inviteCode)) {
			return RestResult.error(RestResult.RestCode.ERROR_INVITE_CODE_INCORRECT);
		}
		InviteCode iCode = inviteCodeRepository.findByInviteCodeAndStatus(inviteCode,
				InviteCodeStatusEnum.OPEN.getValue());
		if (ObjectUtils.isEmpty(iCode)) {
			LOG.info("查询无此invite code:{}", inviteCode);
			return RestResult.error(RestResult.RestCode.ERROR_INVITE_CODE_INCORRECT);
		}

		// 如果用户会员已有设置密码,则挡掉,不走验证码登入
		Member memberEntity = memberRepository.findByPhone(mobile);
		if (memberEntity != null) {
			if (memberEntity.getLoginEnable()){
				Optional<UserPassword> up = userPasswordRepository.findById(memberEntity.getUid());
				if (up.isPresent() && StringUtil.isNotBlank(up.get().getPassword())) {
					LOG.info("用户:{} 已设置密码完成,禁止以验证码流程登入", memberEntity.getMemberName());
					return RestResult.error(RestResult.RestCode.ERROR_MEMBER_PROHIBIT_CODE_LOGIN);
				}
			} else {
				LOG.info("用户:{} 目前设定为不能登陆状态", memberEntity.getMemberName());
				return RestResult.error(RestResult.RestCode.ERROR_MEMBER_LOGIN_DISABLE);
			}

		}

		return onLoginSuccess(httpRequest, httpResponse, mobile, inviteCode, clientId, platform, true);
	}

	@Override
	public RestResult loginWithPassword(HttpServletRequest httpRequest, HttpServletResponse response, String mobile, String password, String clientId,
			int platform) {
		try {
			Member member = memberRepository.findByPhone(mobile);
			if (ObjectUtils.isEmpty(member)) {
				return RestResult.error(ERROR_MOBILE_PASSWORD_WRONG);
			}

			IMResult<InputOutputUserInfo> userResult = UserAdmin.getUserByMobile(mobile);
			if (userResult.getErrorCode() == ErrorCode.ERROR_CODE_NOT_EXIST) {
				return RestResult.error(ERROR_NOT_EXIST);
			}
			if (userResult.getErrorCode() != ErrorCode.ERROR_CODE_SUCCESS) {
				return RestResult.error(RestResult.RestCode.ERROR_SERVER_ERROR);
			}

			if (!member.getLoginEnable()){
				OperateLogList list = new OperateLogList();
				list.addLog("用户账号", member.getMemberName(), false);
				list.addLog("操作设备", getDevice(), false);
				list.addLog("拒绝登入原因", ERROR_MEMBER_LOGIN_DISABLE.msg, false);
				logService.addOperateLog(userResult.result, "/login/fail", list, getIp());

				return RestResult.error(ERROR_MEMBER_LOGIN_DISABLE);
			}

			Optional<UserPassword> optional = userPasswordRepository.findById(userResult.getResult().getUserId());
			if (!optional.isPresent()) {
				return RestResult.error(ERROR_MOBILE_PASSWORD_WRONG);
			}
			UserPassword up = optional.get();
			if (up.getTryCount() > 5) {
				if (System.currentTimeMillis() - up.getLastTryTime() < 5 * 60 * 1000) {
					return RestResult.error(ERROR_FAILURE_TOO_MUCH_TIMES);
				}
				up.setTryCount(0);
			}
			up.setTryCount(up.getTryCount() + 1);
			up.setLastTryTime(System.currentTimeMillis());
			userPasswordRepository.save(up);
			Subject subject = SecurityUtils.getSubject();
			// 在认证提交前准备 token（令牌）
			UsernamePasswordToken token = new UsernamePasswordToken(userResult.getResult().getUserId(), password);
			// 执行认证登陆
			try {
				subject.login(token);
			} catch (UnknownAccountException uae) {
				return RestResult.error(RestResult.RestCode.ERROR_SERVER_ERROR);
			} catch (IncorrectCredentialsException ice) {
				return RestResult.error(RestResult.RestCode.ERROR_MOBILE_PASSWORD_WRONG);
			} catch (LockedAccountException lae) {
				return RestResult.error(RestResult.RestCode.ERROR_MOBILE_PASSWORD_WRONG);
			} catch (ExcessiveAttemptsException eae) {
				return RestResult.error(RestResult.RestCode.ERROR_MOBILE_PASSWORD_WRONG);
			} catch (AuthenticationException ae) {
				// 新增日志(登录失败)
				OperateLogList list = new OperateLogList();
				list.addLog("用户账号", userResult.getResult().getName(), false);
				list.addLog("操作设备", getDevice(), false);
				list.addLog("拒绝登入原因", "密码错误", false);
				logService.addOperateLog(userResult.getResult(), "/login/fail", list, getIp());
				return RestResult.error(RestResult.RestCode.ERROR_MOBILE_PASSWORD_WRONG);
			}
			if (subject.isAuthenticated()) {
				long timeout = subject.getSession().getTimeout();
				LOG.info("Login success " + timeout);
				up.setTryCount(0);
				up.setLastTryTime(0);
				userPasswordRepository.save(up);
			}

			// 新增日志(登录成功)
			OperateLogList list = new OperateLogList();
			list.addLog("用户账号", userResult.getResult().getName(), false);
			list.addLog("操作设备", getDevice(), false);
			logService.addOperateLog(userResult.getResult(), "/login", list, getIp());

		} catch (Exception e) {
			e.printStackTrace();
			return RestResult.error(RestResult.RestCode.ERROR_SERVER_ERROR);
		}

		return onLoginSuccess(httpRequest, response, mobile, null, clientId, platform, false);
	}

	@Override
	public RestResult changePassword(String oldPwd, String newPwd) {
		Subject subject = SecurityUtils.getSubject();
		String userId = (String) subject.getSession().getAttribute("userId");
		Optional<UserPassword> optional = userPasswordRepository.findById(userId);
		if (optional.isPresent()) {
			try {
				if (verifyPassword(optional.get(), oldPwd)) {
					changePassword(optional.get(), newPwd);
					return RestResult.ok(null);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			return RestResult.error(ERROR_NOT_EXIST);
		}
		return RestResult.error(ERROR_SERVER_ERROR);
	}

	@Override
	public RestResult resetPassword(String mobile, String resetCode, String newPwd) {
		Subject subject = SecurityUtils.getSubject();
		String userId = (String) subject.getSession().getAttribute("userId");
		Long memberId = (Long) subject.getSession().getAttribute("memberId");
		LOG.info("重设密码, uid = {}, memberId = {}", userId, memberId);

		if (!StringUtil.isEmpty(mobile)) {
			try {
				IMResult<InputOutputUserInfo> userResult = UserAdmin.getUserByMobile(mobile);
				if (userResult.getCode() != ErrorCode.ERROR_CODE_SUCCESS.getCode()) {
					return RestResult.error(ERROR_SERVER_ERROR);
				}
				if (StringUtil.isEmpty(userId)) {
					userId = userResult.getResult().getUserId();
				} else {
					if (!userId.equals(userResult.getResult().getUserId())) {
						// 错误。。。。
						LOG.error("reset password error, user is correct {}, {}", userId,
								userResult.getResult().getUserId());
						return RestResult.error(ERROR_SERVER_ERROR);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				return RestResult.error(ERROR_SERVER_ERROR);
			}
		}

		Optional<UserPassword> optional = userPasswordRepository.findById(userId);
		if (optional.isPresent()) {
			UserPassword up = optional.get();
			if (resetCode.equals(up.getResetCode())) {
				if (System.currentTimeMillis() - up.getResetCodeTime() > 10 * 60 * 60 * 1000) {
					return RestResult.error(ERROR_CODE_EXPIRED);
				}
				if(up.isRegister()){
					Member member = memberRepository.findByUid(userId);
					String inviteCodeString = member.getInviteCode();

					InviteCode inviteCode = inviteCodeRepository.findInviteCodeByInviteCode(inviteCodeString);
					Long inviteCodeId = inviteCode.getId();
					LOG.info("邀请码:{}", inviteCodeId);

					if (inviteCodeId != null) {
						List<DefaultMember> defaultMemberList = defaultMemberRepository
								.findDefaultMembersByInviteCodeIdAndGlobal(inviteCodeId);
						LOG.info("预设好友:{}", defaultMemberList);
						String uid = userId;
						defaultMemberList.parallelStream().forEach(defaultMember -> {
							try {
								if (defaultMember.getMember() != null) {
									Member targetMember = defaultMember.getMember();
									LOG.info("新增预设好友:{}", targetMember.getId());
									String targetUid = targetMember.getUid();
									IMResult<Void> voidIMResult = RelationAdmin.setUserFriend(uid, targetUid, true, null);
									LOG.info("新增预设好友response:{} ", voidIMResult);
									if (voidIMResult.code == ErrorCode.ERROR_CODE_SUCCESS.getCode()) {
										LOG.info("新增预设好友:{} 成功", targetMember.getId());
										IMUtils.sendSystemMessage(targetUid, uid, MessageChatEnum.SYSTEM_FRIEND_ALREADY_MESSAGE);
										IMUtils.sendTextMessage(targetUid, uid, defaultMember.getWelcomeText());
									}
								} else if (defaultMember.getGroup() != null) {
									Group group = defaultMember.getGroup();
									LOG.info("新增预设群: {}", group.getId());
									String targetGid = group.getGid();
									Member mangerMember = memberRepository.findById(group.getManagerId()).orElse(new Member());
									PojoGroupMember groupMember = new PojoGroupMember();
									groupMember.setMember_id(uid);
									groupMember.setType(0);
									groupMember.setCreateDt(new Date().getTime());
									IMResult<Void> voidIMResult = GroupAdmin.addGroupMembers(mangerMember.getUid(), targetGid, Collections.singletonList(groupMember), null, null);
									if (voidIMResult.code == ErrorCode.ERROR_CODE_SUCCESS.getCode()) {
										LOG.info("新增预设群:{} 成功", targetGid);
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
								LOG.error("注册后 新增预设好友 exception", e);
							}
						});
					}
				}
				try {
					changePassword(up, newPwd);
					up.setResetCode(null);
					userPasswordRepository.save(up);
					return RestResult.ok(null);
				} catch (Exception e) {
					e.printStackTrace();
					return RestResult.error(ERROR_SERVER_ERROR);
				}
			} else {
				return RestResult.error(ERROR_CODE_INCORRECT);
			}
		} else {
			return RestResult.error(ERROR_NOT_EXIST);
		}
	}

	private void changePassword(UserPassword up, String password) throws Exception {
		MessageDigest digest = MessageDigest.getInstance(Sha1Hash.ALGORITHM_NAME);
		digest.reset();
		String salt = UUID.randomUUID().toString();
		digest.update(salt.getBytes(StandardCharsets.UTF_8));
		byte[] hashed = digest.digest(password.getBytes(StandardCharsets.UTF_8));
		String hashedPwd = Base64.getEncoder().encodeToString(hashed);
		up.setPassword(hashedPwd);
		up.setSalt(salt);
		userPasswordRepository.save(up);
	}

	private boolean verifyPassword(UserPassword up, String password) throws Exception {
		String salt = up.getSalt();
		MessageDigest digest = MessageDigest.getInstance(Sha1Hash.ALGORITHM_NAME);
		if (salt != null) {
			digest.reset();
			digest.update(salt.getBytes(StandardCharsets.UTF_8));
		}

		byte[] hashed = digest.digest(password.getBytes(StandardCharsets.UTF_8));
		String hashedPwd = Base64.getEncoder().encodeToString(hashed);
		return hashedPwd.equals(up.getPassword());
	}

	private RestResult onLoginSuccess(HttpServletRequest httpRequest, HttpServletResponse httpResponse, String mobile, String inviteCodeId,
			String clientId, int platform, boolean withResetCode) {
		Subject subject = SecurityUtils.getSubject();
		try {
			Member memberEntity = memberRepository.findByPhone(mobile);
			// 如果用户信息不存在，创建用户
			InputOutputUserInfo user;
			boolean isNewUser = false;

			Long memberId = null;
			if (null == memberEntity) {
				isNewUser = true;

				// 获取用户名。如果用的是shortUUID生成器，是有极小概率会重复的，所以需要去检查是否已经存在相同的userName。
				// ShortUUIDGenerator内的main函数有测试代码，可以观察一下碰撞的概率，这个重复是理论上的，作者测试了几千万次次都没有产生碰撞。
				// 另外由于并发的问题，也有同时生成相同的id并同时去检查的并同时通过的情况，但这种情况概率极低，可以忽略不计。
				String userName;
				int tryCount = 0;
				do {
					tryCount++;
					userName = userNameGenerator.getUserName(mobile);
					if (tryCount > 10) {
						return RestResult.error(ERROR_SERVER_ERROR);
					}
				} while (!isUsernameAvailable(userName));

				// 获取用户displayName
				String displayName;
				if (mIMConfig.use_random_name) {
					displayName = "用户" + (int) (Math.random() * 10000);
				} else {
					displayName = mobile;
				}

				Member savedMember;
				try {
					// 在admin扩充服务新增对应会员
					String channel = UserAgentUtils.getOs(httpRequest);
					Member newMember = Member.builder().memberName(userName).uid(userName).nickName(displayName)
							.phone(mobile).inviteCode(inviteCodeId).registerArea(IpTools.LOCAL_AREA)
							.registerIp(IpTools.LOCAL_IP).loginEnable(true).createGroupEnable(false)
							.addFriendEnable(true).createGroupEnable(true)
							.accountType(MemberAccountTypeEnum.ORDINARY.getValue())
							.gender(MemberGenderEnum.SECRET.getValue()).channel(channel).build();
					savedMember = memberRepository.save(newMember);
					memberId = savedMember.getId();
				} catch (Exception e) {
					LOG.info("User 新用户注册失败:{}", e.getMessage());
					return RestResult.error(RestResult.RestCode.ERROR_SERVER_ERROR);
				}

				// redis记录首页相关资讯,除非有实时需求,否则改用DB查询就好
//				redisUtil.increment(RedisConfig.ADD_MEMBER_COUNT_CURR_DATE_KEY, 1);
//				redisUtil.setExpireAt(RedisConfig.ADD_MEMBER_COUNT_CURR_DATE_KEY, DateUtils.getTailDay(new Date()));

				user = new InputOutputUserInfo();
				user.setName(userName);
				user.setDisplayName(displayName);
				user.setMobile(mobile);
				user.setGender(MemberGenderEnum.SECRET.getValue());
				IMResult<OutputCreateUser> userIdResult = UserAdmin.createUser(user);// 同步IM服务,新用户注册
				if (userIdResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
					user.setUserId(userIdResult.getResult().getUserId());
					savedMember.setUid(userIdResult.getResult().getUserId());
					memberRepository.save(savedMember);
				} else {
					LOG.info("同步IM服务,新用户注册失败 Create user failure {}", userIdResult.code);
					return RestResult.error(RestResult.RestCode.ERROR_SERVER_ERROR);
				}

			} else {
				// admin 已存在用户
				// 未存在密码
				if (StringUtil.isNotEmpty(inviteCodeId)) {
					memberEntity.setInviteCode(inviteCodeId);
					memberRepository.save(memberEntity);
				}

				// 使用电话号码查询用户信息。
				IMResult<InputOutputUserInfo> userResult = UserAdmin.getUserByMobile(mobile);

				if (userResult.getErrorCode() == ErrorCode.ERROR_CODE_NOT_EXIST) {
					LOG.info("User not exist, try to create");
					return RestResult.error(RestResult.RestCode.ERROR_IM_USER_NOT_EXIST);
				} else if (userResult.getCode() != 0) {
					// 从IM 服务获取User资讯失败
					LOG.error("Get user failure {}", userResult.code);
					return RestResult.error(RestResult.RestCode.ERROR_SERVER_ERROR);
				} else {
					user = userResult.getResult();
				}

				Member _member = memberRepository.findByUid(user.getUserId());
				memberId = _member.getId();
			}

			// 使用用户id获取token
			IMResult<OutputGetIMTokenData> tokenResult = UserAdmin.getUserToken(user.getUserId(), clientId, platform);
			if (tokenResult.getErrorCode() != ErrorCode.ERROR_CODE_SUCCESS) {
				LOG.error("Get user failure {}", tokenResult.code);
				return RestResult.error(RestResult.RestCode.ERROR_SERVER_ERROR);
			}

			subject.getSession().setAttribute("userId", user.getUserId());//t_member 的 uid
			subject.getSession().setAttribute("memberId", memberId);//t_member 的 id

			// 返回用户id，token和是否新建
			LoginResponse response = new LoginResponse();
			response.setUserId(user.getUserId());
			response.setToken(tokenResult.getResult().getToken());
			response.setRegister(isNewUser);
			response.setPortrait(user.getPortrait());
			response.setUserName(user.getName());
			response.setCreateGroupEnable(memberEntity == null ? false : memberEntity.getCreateGroupEnable());

			if (withResetCode) {
				String code = Utils.getRandomCode(6);
				Optional<UserPassword> optional = userPasswordRepository.findById(user.getUserId());
				UserPassword up;
				if (optional.isPresent()) {
					up = optional.get();
				} else {
					up = new UserPassword(user.getUserId(), null, null);
				}
				up.setResetCode(code);
				up.setResetCodeTime(System.currentTimeMillis());
				up.setRegister(true);
				userPasswordRepository.save(up);
				response.setResetCode(code);
			}

			if (isNewUser) {
				if (!StringUtils.isEmpty(mIMConfig.welcome_for_new_user)) {
					IMUtils.sendTextMessage("admin", user.getUserId(), mIMConfig.welcome_for_new_user);
				}

				if (mIMConfig.new_user_robot_friend && !StringUtils.isEmpty(mIMConfig.robot_friend_id)) {
					RelationAdmin.setUserFriend(user.getUserId(), mIMConfig.robot_friend_id, true, null);
					if (!StringUtils.isEmpty(mIMConfig.robot_welcome)) {
						IMUtils.sendTextMessage(mIMConfig.robot_friend_id, user.getUserId(), mIMConfig.robot_welcome);
					}
				}

				if (!StringUtils.isEmpty(mIMConfig.new_user_subscribe_channel_id)) {
					try {
						GeneralAdmin.subscribeChannel(mIMConfig.getNew_user_subscribe_channel_id(), user.getUserId());
					} catch (Exception e) {

					}
				}
			} else {
				if (!StringUtils.isEmpty(mIMConfig.welcome_for_back_user)) {
					IMUtils.sendTextMessage("admin", user.getUserId(), mIMConfig.welcome_for_back_user);
				}
				if (!StringUtils.isEmpty(mIMConfig.back_user_subscribe_channel_id)) {
					try {
						IMResult<OutputBooleanValue> booleanValueIMResult = GeneralAdmin.isUserSubscribedChannel(
								user.getUserId(), mIMConfig.getBack_user_subscribe_channel_id());
						if (booleanValueIMResult != null
								&& booleanValueIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS
								&& !booleanValueIMResult.getResult().value) {
							GeneralAdmin.subscribeChannel(mIMConfig.back_user_subscribe_channel_id, user.getUserId());
						}
					} catch (Exception e) {

					}
				}
			}

			Object sessionId = subject.getSession().getId();
			httpResponse.setHeader("authToken", sessionId.toString());
			return RestResult.ok(response);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception happens {}", e);
			return RestResult.error(RestResult.RestCode.ERROR_SERVER_ERROR);
		}
	}

	@Override
	public RestResult sendDestroyCode() {
		Subject subject = SecurityUtils.getSubject();
		String userId = (String) subject.getSession().getAttribute("userId");
		try {
			IMResult<InputOutputUserInfo> getUserResult = UserAdmin.getUserByUserId(userId);
			if (getUserResult != null && getUserResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
				String mobile = getUserResult.getResult().getMobile();
				if (!StringUtils.isEmpty(mobile)) {
					return sendLoginCode(mobile);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return RestResult.error(RestResult.RestCode.ERROR_SERVER_ERROR);
		}
		return RestResult.error(RestResult.RestCode.ERROR_NOT_EXIST);
	}

	@Override
	public RestResult destroy(HttpServletResponse response, String code) {
		Subject subject = SecurityUtils.getSubject();
		String userId = (String) subject.getSession().getAttribute("userId");
		try {
			IMResult<InputOutputUserInfo> getUserResult = UserAdmin.getUserByUserId(userId);
			if (getUserResult != null && getUserResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
				String mobile = getUserResult.getResult().getMobile();
				if (!StringUtils.isEmpty(mobile)) {
					if (authDataSource.verifyCode(mobile, code) == SUCCESS) {
						UserAdmin.destroyUser(userId);
						authDataSource.clearRecode(mobile);
						userPasswordRepository.deleteById(userId);
						subject.logout();
						return RestResult.ok(null);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return RestResult.error(RestResult.RestCode.ERROR_SERVER_ERROR);
		}
		return RestResult.error(RestResult.RestCode.ERROR_NOT_EXIST);
	}

	private boolean isUsernameAvailable(String username) {
		try {
			IMResult<InputOutputUserInfo> existUser = UserAdmin.getUserByName(username);
			if (existUser.code == ErrorCode.ERROR_CODE_NOT_EXIST.code) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private void sendPcLoginRequestMessage(String fromUser, String toUser, int platform, String token) {
		Conversation conversation = new Conversation();
		conversation.setTarget(toUser);
		conversation.setType(ProtoConstants.ConversationType.ConversationType_Private);
		MessagePayload payload = new MessagePayload();
		payload.setType(94);
		if (platform == ProtoConstants.Platform.Platform_WEB) {
			payload.setPushContent("Web端登录请求");
		} else if (platform == ProtoConstants.Platform.Platform_OSX) {
			payload.setPushContent("Mac 端登录请求");
		} else if (platform == ProtoConstants.Platform.Platform_LINUX) {
			payload.setPushContent("Linux 端登录请求");
		} else if (platform == ProtoConstants.Platform.Platform_Windows) {
			payload.setPushContent("Windows 端登录请求");
		} else {
			payload.setPushContent("PC 端登录请求");
		}

		payload.setExpireDuration(60 * 1000);
		payload.setPersistFlag(ProtoConstants.PersistFlag.Not_Persist);
		JSONObject data = new JSONObject();
		data.put("p", platform);
		data.put("t", token);
		payload.setBase64edData(Base64Utils.encodeToString(data.toString().getBytes()));

		try {
			IMResult<SendMessageResult> resultSendMessage = MessageAdmin.sendMessage(fromUser, conversation, payload);
			if (resultSendMessage != null && resultSendMessage.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
				LOG.info("send message success");
			} else {
				LOG.error("send message error {}",
						resultSendMessage != null ? resultSendMessage.getErrorCode().code : "unknown");
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("send message error {}", e.getLocalizedMessage());
		}

	}

	@Override
	public RestResult createPcSession(CreateSessionRequest request) {
		String userId = request.getUserId();
		// pc端切换登录用户时，还会带上之前的cookie，通过请求里面是否带有userId来判断是否是切换到新用户
		if (request.getFlag() == 1 && !StringUtils.isEmpty(userId)) {
			Subject subject = SecurityUtils.getSubject();
			userId = (String) subject.getSession().getAttribute("userId");
		}

		if (compatPcQuickLogin) {
			if (userId != null && supportPCQuickLoginUsers.get(userId) == null) {
				userId = null;
			}
		}

		PCSession session = authDataSource.createSession(userId, request.getClientId(), request.getToken(),
				request.getPlatform());
		if (userId != null) {
			sendPcLoginRequestMessage("admin", userId, request.getPlatform(), session.getToken());
		}
		SessionOutput output = session.toOutput();
		LOG.info("client {} create pc session, key is {}", request.getClientId(), output.getToken());
		return RestResult.ok(output);
	}

	@Override
	public RestResult loginWithSession(String token) {
		Subject subject = SecurityUtils.getSubject();
		// 在认证提交前准备 token（令牌）
		// comment start 如果确定登录不成功，就不通过Shiro尝试登录了
		TokenAuthenticationToken tt = new TokenAuthenticationToken(token);
		PCSession session = authDataSource.getSession(token, false);

		if (session == null) {
			return RestResult.error(ERROR_CODE_EXPIRED);
		} else if (session.getStatus() == Session_Created) {
			return RestResult.error(ERROR_SESSION_NOT_SCANED);
		} else if (session.getStatus() == Session_Scanned) {
			session.setStatus(Session_Pre_Verify);
			authDataSource.saveSession(session);
			LoginResponse response = new LoginResponse();
			try {
				IMResult<InputOutputUserInfo> result = UserAdmin.getUserByUserId(session.getConfirmedUserId());
				if (result.getCode() == 0) {
					response.setUserName(result.getResult().getDisplayName());
					response.setPortrait(result.getResult().getPortrait());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return RestResult.result(ERROR_SESSION_NOT_VERIFIED, response);
		} else if (session.getStatus() == Session_Pre_Verify) {
			return RestResult.error(ERROR_SESSION_NOT_VERIFIED);
		} else if (session.getStatus() == Session_Canceled) {
			return RestResult.error(ERROR_SESSION_CANCELED);
		}
		// comment end

		// 执行认证登陆
		// comment start 由于PC端登录之后，可以请求app server创建群公告等。为了保证安全, PC端登录时，也需要在app
		// server创建session。
		try {
			subject.login(tt);
		} catch (UnknownAccountException uae) {
			return RestResult.error(RestResult.RestCode.ERROR_SERVER_ERROR);
		} catch (IncorrectCredentialsException ice) {
			return RestResult.error(RestResult.RestCode.ERROR_CODE_INCORRECT);
		} catch (LockedAccountException lae) {
			return RestResult.error(RestResult.RestCode.ERROR_CODE_INCORRECT);
		} catch (ExcessiveAttemptsException eae) {
			return RestResult.error(RestResult.RestCode.ERROR_CODE_INCORRECT);
		} catch (AuthenticationException ae) {
			return RestResult.error(RestResult.RestCode.ERROR_CODE_INCORRECT);
		}
		if (subject.isAuthenticated()) {
			LOG.info("Login success");
		} else {
			return RestResult.error(RestResult.RestCode.ERROR_CODE_INCORRECT);
		}
		// comment end

		session = authDataSource.getSession(token, true);
		if (session == null) {
			subject.logout();
			return RestResult.error(RestResult.RestCode.ERROR_CODE_EXPIRED);
		}
		subject.getSession().setAttribute("userId", session.getConfirmedUserId());

		try {
			// 使用用户id获取token
			IMResult<OutputGetIMTokenData> tokenResult = UserAdmin.getUserToken(session.getConfirmedUserId(),
					session.getClientId(), session.getPlatform());
			if (tokenResult.getCode() != 0) {
				LOG.error("Get user failure {}", tokenResult.code);
				subject.logout();
				return RestResult.error(RestResult.RestCode.ERROR_SERVER_ERROR);
			}
			// 返回用户id，token和是否新建
			LoginResponse response = new LoginResponse();
			response.setUserId(session.getConfirmedUserId());
			response.setToken(tokenResult.getResult().getToken());
			return RestResult.ok(response);
		} catch (Exception e) {
			e.printStackTrace();
			subject.logout();
			return RestResult.error(RestResult.RestCode.ERROR_SERVER_ERROR);
		}
	}

	@Override
	public RestResult scanPc(String token) {
		Subject subject = SecurityUtils.getSubject();
		String userId = (String) subject.getSession().getAttribute("userId");

		LOG.info("user {} scan pc, session is {}", userId, token);
		return authDataSource.scanPc(userId, token);
	}

	@Override
	public RestResult confirmPc(ConfirmSessionRequest request) {
		Subject subject = SecurityUtils.getSubject();
		String userId = (String) subject.getSession().getAttribute("userId");
		if (compatPcQuickLogin) {
			if (request.getQuick_login() > 0) {
				supportPCQuickLoginUsers.put(userId, true);
			} else {
				supportPCQuickLoginUsers.remove(userId);
			}
		}

		LOG.info("user {} confirm pc, session is {}", userId, request.getToken());
		return authDataSource.confirmPc(userId, request.getToken());
	}

	@Override
	public RestResult cancelPc(CancelSessionRequest request) {
		return authDataSource.cancelPc(request.getToken());
	}

	@Override
	public RestResult changeName(String newName) {
		Subject subject = SecurityUtils.getSubject();
		String userId = (String) subject.getSession().getAttribute("userId");
		try {
			IMResult<InputOutputUserInfo> existUser = UserAdmin.getUserByName(newName);
			if (existUser != null) {
				if (existUser.code == ErrorCode.ERROR_CODE_SUCCESS.code) {
					if (userId.equals(existUser.getResult().getUserId())) {
						return RestResult.ok(null);
					} else {
						return RestResult.error(ERROR_USER_NAME_ALREADY_EXIST);
					}
				} else if (existUser.code == ErrorCode.ERROR_CODE_NOT_EXIST.code) {
					existUser = UserAdmin.getUserByUserId(userId);
					if (existUser == null || existUser.code != ErrorCode.ERROR_CODE_SUCCESS.code
							|| existUser.getResult() == null) {
						return RestResult.error(ERROR_SERVER_ERROR);
					}

					existUser.getResult().setName(newName);
					IMResult<OutputCreateUser> createUser = UserAdmin.createUser(existUser.getResult());
					if (createUser.code == ErrorCode.ERROR_CODE_SUCCESS.code) {
						return RestResult.ok(null);
					} else {
						return RestResult.error(ERROR_SERVER_ERROR);
					}
				} else {
					return RestResult.error(ERROR_SERVER_ERROR);
				}
			} else {
				return RestResult.error(ERROR_SERVER_ERROR);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return RestResult.error(ERROR_SERVER_ERROR);
		}
	}

	@Override
	public RestResult complain(String text) {
		Subject subject = SecurityUtils.getSubject();
		String userId = (String) subject.getSession().getAttribute("userId");
		LOG.error("Complain from user {} where content {}", userId, text);
		IMUtils.sendTextMessage(userId, "cgc8c8VV", text);
		return RestResult.ok(null);
	}

	@Override
	public RestResult getGroupAnnouncement(String groupId) {
		Optional<Announcement> announcement = announcementRepository.findById(groupId);
		if (announcement.isPresent()) {
			GroupAnnouncementPojo pojo = new GroupAnnouncementPojo();
			pojo.groupId = announcement.get().getGroupId();
			pojo.author = announcement.get().getAuthor();
			pojo.text = announcement.get().getAnnouncement();
			pojo.timestamp = announcement.get().getTimestamp();
			return RestResult.ok(pojo);
		} else {
			return RestResult.error(ERROR_GROUP_ANNOUNCEMENT_NOT_EXIST);
		}
	}

	@Override
	public RestResult putGroupAnnouncement(GroupAnnouncementPojo request) {
		if (!StringUtils.isEmpty(request.text)) {
			Subject subject = SecurityUtils.getSubject();
			String userId = (String) subject.getSession().getAttribute("userId");
			boolean isGroupMember = false;
			try {
				IMResult<OutputGroupMemberList> imResult = GroupAdmin.getGroupMembers(request.groupId);
				if (imResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS && imResult.getResult() != null
						&& imResult.getResult().getMembers() != null) {
					for (PojoGroupMember member : imResult.getResult().getMembers()) {
						if (member.getMember_id().equals(userId)) {
							if (member.getType() != ProtoConstants.GroupMemberType.GroupMemberType_Removed
									&& member.getType() != ProtoConstants.GroupMemberType.GroupMemberType_Silent) {
								isGroupMember = true;
							}
							break;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (!isGroupMember) {
				return RestResult.error(ERROR_NO_RIGHT);
			}

			Conversation conversation = new Conversation();
			conversation.setTarget(request.groupId);
			conversation.setType(ProtoConstants.ConversationType.ConversationType_Group);
			MessagePayload payload = new MessagePayload();
			payload.setType(1);
			payload.setSearchableContent("@所有人 " + request.text);
			payload.setMentionedType(2);

			try {
				IMResult<SendMessageResult> resultSendMessage = MessageAdmin.sendMessage(request.author, conversation,
						payload);
				if (resultSendMessage != null && resultSendMessage.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
					LOG.info("send message success");
				} else {
					LOG.error("send message error {}",
							resultSendMessage != null ? resultSendMessage.getErrorCode().code : "unknown");
					return RestResult.error(ERROR_SERVER_ERROR);
				}
			} catch (Exception e) {
				e.printStackTrace();
				LOG.error("send message error {}", e.getLocalizedMessage());
				return RestResult.error(ERROR_SERVER_ERROR);
			}
		}

		Announcement announcement = new Announcement();
		announcement.setGroupId(request.groupId);
		announcement.setAuthor(request.author);
		announcement.setAnnouncement(request.text);
		request.timestamp = System.currentTimeMillis();
		announcement.setTimestamp(request.timestamp);

		announcementRepository.save(announcement);
		return RestResult.ok(request);
	}

	@Override
	public RestResult saveUserLogs(String userId, MultipartFile file) {
		File localFile = new File(userLogPath, userId + "_" + file.getOriginalFilename());

		try {
			file.transferTo(localFile);
		} catch (IOException e) {
			e.printStackTrace();
			return RestResult.error(ERROR_SERVER_ERROR);
		}

		return RestResult.ok(null);
	}

	@Override
	public RestResult addDevice(InputCreateDevice createDevice) {
		try {
			Subject subject = SecurityUtils.getSubject();
			String userId = (String) subject.getSession().getAttribute("userId");

			if (!StringUtils.isEmpty(createDevice.getDeviceId())) {
				IMResult<OutputDevice> outputDeviceIMResult = UserAdmin.getDevice(createDevice.getDeviceId());
				if (outputDeviceIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
					if (!createDevice.getOwners().contains(userId)) {
						return RestResult.error(ERROR_NO_RIGHT);
					}
				} else if (outputDeviceIMResult.getErrorCode() != ErrorCode.ERROR_CODE_NOT_EXIST) {
					return RestResult.error(ERROR_SERVER_ERROR);
				}
			}

			IMResult<OutputCreateDevice> result = UserAdmin.createOrUpdateDevice(createDevice);
			if (result != null && result.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
				return RestResult.ok(result.getResult());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return RestResult.error(ERROR_SERVER_ERROR);
	}

	@Override
	public RestResult getDeviceList() {
		Subject subject = SecurityUtils.getSubject();
		String userId = (String) subject.getSession().getAttribute("userId");
		try {
			IMResult<OutputDeviceList> imResult = UserAdmin.getUserDevices(userId);
			if (imResult != null && imResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
				return RestResult.ok(imResult.getResult().getDevices());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return RestResult.error(ERROR_SERVER_ERROR);
	}

	@Override
	public RestResult delDevice(InputCreateDevice createDevice) {
		try {
			Subject subject = SecurityUtils.getSubject();
			String userId = (String) subject.getSession().getAttribute("userId");

			if (!StringUtils.isEmpty(createDevice.getDeviceId())) {
				IMResult<OutputDevice> outputDeviceIMResult = UserAdmin.getDevice(createDevice.getDeviceId());
				if (outputDeviceIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
					if (outputDeviceIMResult.getResult().getOwners().contains(userId)) {
						createDevice.setExtra(outputDeviceIMResult.getResult().getExtra());
						outputDeviceIMResult.getResult().getOwners().remove(userId);
						createDevice.setOwners(outputDeviceIMResult.getResult().getOwners());
						IMResult<OutputCreateDevice> result = UserAdmin.createOrUpdateDevice(createDevice);
						if (result != null && result.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
							return RestResult.ok(result.getResult());
						} else {
							return RestResult.error(ERROR_SERVER_ERROR);
						}
					} else {
						return RestResult.error(ERROR_NO_RIGHT);
					}
				} else {
					if (outputDeviceIMResult.getErrorCode() != ErrorCode.ERROR_CODE_NOT_EXIST) {
						return RestResult.error(ERROR_SERVER_ERROR);
					} else {
						return RestResult.error(ERROR_NOT_EXIST);
					}
				}
			} else {
				return RestResult.error(ERROR_INVALID_PARAMETER);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return RestResult.error(ERROR_SERVER_ERROR);
	}

	@Override
	public RestResult sendMessage(SendMessageRequest request) {
		Subject subject = SecurityUtils.getSubject();
		String userId = (String) subject.getSession().getAttribute("userId");

		Conversation conversation = new Conversation();
		conversation.setType(request.type);
		conversation.setTarget(request.target);
		conversation.setLine(request.line);

		MessagePayload payload = new MessagePayload();
		payload.setType(request.content_type);
		payload.setSearchableContent(request.content_searchable);
		payload.setPushContent(request.content_push);
		payload.setPushData(request.content_push_data);
		payload.setContent(request.content);
		payload.setBase64edData(request.content_binary);
		payload.setMediaType(request.content_media_type);
		payload.setRemoteMediaUrl(request.content_remote_url);
		payload.setMentionedType(request.content_mentioned_type);
		payload.setMentionedTarget(request.content_mentioned_targets);
		payload.setExtra(request.content_extra);

		try {
			IMResult<SendMessageResult> imResult = MessageAdmin.sendMessage(userId, conversation, payload);
			if (imResult != null && imResult.getCode() == ErrorCode.ERROR_CODE_SUCCESS.code) {
				return RestResult.ok(null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return RestResult.error(ERROR_SERVER_ERROR);
	}

	@Override
	public RestResult uploadMedia(int mediaType, MultipartFile file) {
		StringBuilder subDirPathBuilder = new StringBuilder();
		subDirPathBuilder.append("media").append("/").append("fs").append("/");
		subDirPathBuilder.append(mediaType).append("/");
		subDirPathBuilder.append(DateUtils.getNow("yyyy")).append("/");
		subDirPathBuilder.append(DateUtils.getNow("MM")).append("/");
		subDirPathBuilder.append(DateUtils.getNow("dd")).append("/");
		subDirPathBuilder.append(DateUtils.getNow("HH")).append("/");

		UploadFileResponse response = new UploadFileResponse();
		response.url = uploadFileUtils.parseFilePathToUrl(uploadFileUtils.uploadFile(file, subDirPathBuilder.toString(), ""));
		return RestResult.ok(response);
	}

	@Override
	public RestResult putFavoriteItem(FavoriteItem request) {
		Subject subject = SecurityUtils.getSubject();
		String userId = (String) subject.getSession().getAttribute("userId");

		if (!StringUtils.isEmpty(request.url)) {
			try {
				// 收藏时需要把对象拷贝到收藏bucket。
				URL mediaURL = new URL(request.url);

				String bucket = null;
				if (mediaURL.getHost().equals(new URL(ossGeneralBucketDomain).getHost())) {
					bucket = ossGeneralBucket;
				} else if (mediaURL.getHost().equals(new URL(ossImageBucketDomain).getHost())) {
					bucket = ossImageBucket;
				} else if (mediaURL.getHost().equals(new URL(ossVoiceBucketDomain).getHost())) {
					bucket = ossVoiceBucket;
				} else if (mediaURL.getHost().equals(new URL(ossVideoBucketDomain).getHost())) {
					bucket = ossVideoBucket;
				} else if (mediaURL.getHost().equals(new URL(ossFileBucketDomain).getHost())) {
					bucket = ossFileBucket;
				} else if (mediaURL.getHost().equals(new URL(ossMomentsBucketDomain).getHost())) {
					bucket = ossMomentsBucket;
				} else if (mediaURL.getHost().equals(new URL(ossStickerBucketDomain).getHost())) {
					bucket = ossStickerBucket;
				} else if (mediaURL.getHost().equals(new URL(ossFavoriteBucketDomain).getHost())) {
					// It's already in fav bucket, no need to copy
					// bucket = ossFavoriteBucket;
				}

				if (bucket != null) {
					String path = mediaURL.getPath();
					if (ossType == 1) {
						Configuration cfg = new Configuration(Region.region0());
						String fromKey = path.substring(1);
						Auth auth = Auth.create(ossAccessKey, ossSecretKey);

						String toBucket = ossFavoriteBucket;
						String toKey = fromKey;
						if (!toKey.startsWith(userId)) {
							toKey = userId + "-" + toKey;
						}

						BucketManager bucketManager = new BucketManager(auth, cfg);
						bucketManager.copy(bucket, fromKey, toBucket, toKey);
						request.url = ossFavoriteBucketDomain + "/" + fromKey;
					} else if (ossType == 2) {
						OSS ossClient = new OSSClient(ossUrl, ossAccessKey, ossSecretKey);
						path = path.substring(1);
						String objectName = path;
						String toKey = path;
						if (!toKey.startsWith(userId)) {
							toKey = userId + "-" + toKey;
						}

						ossClient.copyObject(bucket, objectName, ossFavoriteBucket, toKey);
						request.url = ossFavoriteBucketDomain + "/" + toKey;
						ossClient.shutdown();
					} else if (ossType == 3) {
						path = path.substring(bucket.length() + 2);
						String objectName = path;
						String toKey = path;
						if (!toKey.startsWith(userId)) {
							toKey = userId + "-" + toKey;
						}
						MinioClient minioClient = new MinioClient(ossUrl, ossAccessKey, ossSecretKey);
						minioClient.copyObject(ossFavoriteBucket, toKey, null, null, bucket, objectName, null, null);
						request.url = ossFavoriteBucketDomain + "/" + toKey;
					} else if (ossType == 4) {
						// Todo 需要把收藏的文件保存为永久存储。
					} else if (ossType == 5) {
						COSCredentials cred = new BasicCOSCredentials(ossAccessKey, ossSecretKey);
						ClientConfig clientConfig = new ClientConfig();
						String[] ss = ossUrl.split("\\.");
						if (ss.length > 3) {
							if (!ss[1].equals("accelerate")) {
								clientConfig.setRegion(new com.qcloud.cos.region.Region(ss[1]));
							} else {
								clientConfig.setRegion(new com.qcloud.cos.region.Region("ap-shanghai"));
								try {
									URL u = new URL(ossUrl);
									clientConfig.setEndPointSuffix(u.getHost());
								} catch (MalformedURLException e) {
									e.printStackTrace();
									return RestResult.error(ERROR_SERVER_ERROR);
								}
							}
						}

						clientConfig.setHttpProtocol(HttpProtocol.https);
						COSClient cosClient = new COSClient(cred, clientConfig);

						path = path.substring(1);
						String objectName = path;
						String toKey = path;
						if (!toKey.startsWith(userId)) {
							toKey = userId + "-" + toKey;
						}

						try {
							cosClient.copyObject(bucket, objectName, ossFavoriteBucket, toKey);
							request.url = ossFavoriteBucketDomain + "/" + toKey;
						} catch (CosClientException e) {
							e.printStackTrace();
							return RestResult.error(ERROR_SERVER_ERROR);
						} finally {
							cosClient.shutdown();
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		request.userId = userId;
		request.timestamp = System.currentTimeMillis();
		favoriteRepository.save(request);
		return RestResult.ok(null);
	}

	@Override
	public RestResult removeFavoriteItems(long id) {
		favoriteRepository.deleteById(id);
		return RestResult.ok(null);
	}

	@Override
	public RestResult getFavoriteItems(long id, int count) {
		Subject subject = SecurityUtils.getSubject();
		String userId = (String) subject.getSession().getAttribute("userId");

		id = id > 0 ? id : Long.MAX_VALUE;
		List<FavoriteItem> favs = favoriteRepository.loadFav(userId, id, count);
		LoadFavoriteResponse response = new LoadFavoriteResponse();
		response.items = favs;
		response.hasMore = favs.size() == count;
		return RestResult.ok(response);
	}

	@Override
	public RestResult getGroupMembersForPortrait(String groupId) {
		try {
			IMResult<OutputGroupMemberList> groupMemberListIMResult = GroupAdmin.getGroupMembers(groupId);
			if (groupMemberListIMResult.getErrorCode() != ErrorCode.ERROR_CODE_SUCCESS) {
				LOG.error("getGroupMembersForPortrait failure {},{}", groupMemberListIMResult.getErrorCode().getCode(),
						groupMemberListIMResult.getErrorCode().getMsg());
				return RestResult.error(ERROR_SERVER_ERROR);
			}
			List<PojoGroupMember> groupMembers = new ArrayList<>();
			for (PojoGroupMember member : groupMemberListIMResult.getResult().getMembers()) {
				if (member.getType() != 4)
					groupMembers.add(member);
			}

			if (groupMembers.size() > 9) {
				groupMembers.sort((o1, o2) -> {
					if (o1.getType() == 2)
						return -1;
					if (o2.getType() == 2)
						return 1;
					if (o1.getType() == 1 && o2.getType() != 1)
						return -1;
					if (o2.getType() == 1 && o1.getType() != 1)
						return 1;
					return Long.compare(o1.getCreateDt(), o2.getCreateDt());
				});
				groupMembers = groupMembers.subList(0, 9);
			}
			List<UserIdPortraitPojo> mids = new ArrayList<>();
			for (PojoGroupMember member : groupMembers) {
				IMResult<InputOutputUserInfo> userInfoIMResult = UserAdmin.getUserByUserId(member.getMember_id());
				if (userInfoIMResult.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
					mids.add(new UserIdPortraitPojo(member.getMember_id(), userInfoIMResult.result.getPortrait()));
				} else {
					mids.add(new UserIdPortraitPojo(member.getMember_id(), ""));
				}
			}
			return RestResult.ok(mids);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("getGroupMembersForPortrait exception", e);
			return RestResult.error(ERROR_SERVER_ERROR);
		}
	}

	@Override
	public RestResult withdrawApply(WithdrawApplyRequest request) {
		try {
			Subject subject = SecurityUtils.getSubject();
			Long memberId = (Long) subject.getSession().getAttribute("memberId");
			LOG.info("withdrawApply memberId: {}, request: {}, TradePwd: {}", memberId, request, request.getTradePwd());

			//验证交易密码
			if(StringUtil.isEmpty(request.getTradePwd())){
				LOG.info("withdrawApply memberId: {}, 交易密碼為空", memberId);
				return RestResult.error(RestResult.RestCode.ERROR_TRADE_PWD_REQUIRED);
			}
			Member member = memberRepository.findById(memberId).orElse(null);
			UserPassword up = userPasswordRepository.findById(member.getUid()).orElse(null);

			LOG.info("withdrawApply member: {}, up: {}",member, up );
			if(!verifyTradePwd(request.getTradePwd(), member.getTradePwd(), Member.MEMBER_TRADE_PWD_SALT)){
				LOG.info("withdrawApply memberId: {}, 交易密碼(SHA256)錯誤:{}", memberId, request.getTradePwd());
				return RestResult.error(RestResult.RestCode.ERROR_TRADE_PWD_NOT_CORRECT);
			}


			BigDecimal amount = request.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP).stripTrailingZeros();
			CurrencyEnum currencyEnum = CurrencyEnum.parse(request.getCurrency());
			if (currencyEnum == null) {
				LOG.info("withdrawApply memberId: {}, 币种不支持", memberId);
				return RestResult.error(RestResult.RestCode.ERROR_CURRENCY_NOT_SUPPORT);
			}

			List<WithdrawPaymentMethod> methods = withdrawPaymentMethodRepository
					.findById(request.getPaymentMethodId());
			if (methods.size() <= 0) {
				LOG.info("withdrawApply memberId: {}, 提现支付方式不存在", memberId);
				return RestResult.error(RestResult.RestCode.ERROR_WITHDRAW_PAYMENT_METHOD_NOT_EXIST);
			}

			MemberBalance balancePO = memberBalanceRepository.findByUserIdAndCurrency(memberId, CurrencyEnum.CNY.name());
			if (balancePO.getBalance().compareTo(amount) >= 0) {
				BigDecimal beforeFreeze = balancePO.getFreeze();
				balancePO.setFreeze(balancePO.getFreeze().add(amount));
				memberBalanceRepository.save(balancePO);

				String memo = MemberBalanceLog.USER_WITHDRAW_APPLY_FREEZE.replace("{amount}", amount.toPlainString())
						.replace("{freeze}", amount.toPlainString());

				MemberBalanceLog balanceLogPO = new MemberBalanceLog(memberId, currencyEnum.name(),
						MemberBalanceLogTypeEnum.WITHDRAW_APPLY.getValue(), amount, balancePO.getBalance(),
						balancePO.getBalance(), beforeFreeze, balancePO.getFreeze(), memo);
				memberBalanceLogRepository.save(balanceLogPO);
			} else {
				LOG.info("withdrawApply memberId: {}, 会员馀额不足", memberId);
				return RestResult.error(RestResult.RestCode.ERROR_MEMBER_BALANCE_INSUFFICIENT);
			}

			WithdrawOrderChannelEnum channelEnum = WithdrawOrderChannelEnum.parse(request.getChannel());
			if (channelEnum == null) {
				return RestResult.error(RestResult.RestCode.ERROR_WITHDRAW_CHANNEL_NOT_EXIST);
			}

			String imagePath = methods.get(0).getImage();
			String info = methods.get(0).getInfo();
			LOG.info("withdrawApply imagePath: {}, info: {}", imagePath, info);

			WithdrawOrder order = new WithdrawOrder(WithdrawOrder.randomOrderCode(), memberId, currencyEnum.name(),
					channelEnum.getValue(), info, request.getAmount(),
					WithdrawOrderStatusEnum.PENDING_REVIEW.getValue(), EditorRoleEnum.MEMBER.getValue());
			withdrawOrderRepository.save(order);
			return RestResult.ok(null);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("withdrawApply exception", e);
			return RestResult.error(ERROR_SERVER_ERROR);
		}
	}

	@Override
	public RestResult rechargeApply(RechargeApplyRequest request) {
		try {
			Subject subject = SecurityUtils.getSubject();
			Long memberId = (Long) subject.getSession().getAttribute("memberId");
			LOG.info("rechargeApply memberId: {}, request: {}", memberId, request);

			BigDecimal amount = request.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP).stripTrailingZeros();

			RechargeOrderMethodEnum methodEnum = RechargeOrderMethodEnum.parse(request.getMethod());
			if (methodEnum == null) {
				return RestResult.error(RestResult.RestCode.ERROR_METHOD_NOT_SUPPORT);
			}

			CurrencyEnum currencyEnum = CurrencyEnum.parse(request.getCurrency());
			if (currencyEnum == null) {
				return RestResult.error(RestResult.RestCode.ERROR_CURRENCY_NOT_SUPPORT);
			}

			RechargeChannel channel = rechargeChannelRepository.findById(request.getChannelId()).orElse(null);
			if (channel == null || StatusBasicEnum.isClose(channel.getStatus())) {
				return RestResult.error(RestResult.RestCode.ERROR_RECHARGE_CHANNEL_NOT_SUPPORT);
			}

			RechargeOrder order = new RechargeOrder(RechargeOrder.randomOrderCode(), memberId, methodEnum.getValue(),
					amount, currencyEnum.name(), channel.getId(), RechargeOrderStatusEnum.CREATE.getValue(),
					EditorRoleEnum.MEMBER.getValue());
			RechargeOrder save = rechargeOrderRepository.save(order);
			return RestResult.ok(save);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("rechargeApply exception", e);
			return RestResult.error(ERROR_SERVER_ERROR);
		}
	}

	@Override
	public RestResult rechargeConfirm(RechargeConfirmRequest request) {
		try {
			RechargeOrder order = rechargeOrderRepository.findById(request.getId()).orElse(null);
			if (order == null || !RechargeOrderStatusEnum.CREATE.equals(RechargeOrderStatusEnum.parse(order.getStatus()))) {
				return RestResult.error(RestResult.RestCode.ERROR_RECHARGE_ORDER_NOT_EXIST_OR_WRONG_STATUS);
			}

			if (request.getPayImageFile() != null && !request.getPayImageFile().isEmpty()) {
				String fileOriginName = request.getPayImageFile().getOriginalFilename();
				String urlPath = uploadFileUtils.uploadFile(request.getPayImageFile(), FileUtils.RECHARGE_PAY_IMAGE_PATH, FileNameUtils.PAY_IMAGE_PREFIX);
				if (urlPath != null) {
					order.setPayImage(urlPath);
					order.setStatus(RechargeOrderStatusEnum.PENDING_REVIEW.getValue());
				}
			}
			rechargeOrderRepository.save(order);
			return RestResult.ok(null);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("rechargeConfirm exception", e);
			return RestResult.error(ERROR_SERVER_ERROR);
		}
	}

	@Override
	public RestResult info(String userId) {
		try {
			LOG.info("查询个人资讯, uid: {}", userId);

			Member member = memberRepository.findByUid(userId);
			return RestResult.ok(convertMemberToMyInfoVO(member));
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("查询个人资讯 exception", e);
			return RestResult.error(ERROR_SERVER_ERROR);
		}
	}

	@Override
	public RestResult updateInfo(UserInfoRequest userInfoRequest, Integer flag) {
		try {
			Subject subject = SecurityUtils.getSubject();
			Long memberId = (Long) subject.getSession().getAttribute("memberId");
			LOG.info("更新个人资讯, memberID: {}, ", memberId);
			Member member = memberRepository.findById(memberId).orElse(null);
			Assert.notNull(member, String.format("查无ID:%s用户", memberId));
			ReflectionUtils.copyFields(member, userInfoRequest, ReflectionUtils.STRING_TRIM_TO_NULL, true);

			if (userInfoRequest.getAvatar() != null && !userInfoRequest.getAvatar().isEmpty()) {
				String urlPath = uploadFileUtils.uploadFile(userInfoRequest.getAvatar(), FileUtils.AVATAR_PATH, FileNameUtils.AVATAR_PREFIX);
				if (urlPath != null) {
					member.setAvatarUrl(urlPath);
				}
			}

			InputOutputUserInfo inputOutputUserInfo = new InputOutputUserInfo();
			ReflectionUtils.copyFields(inputOutputUserInfo, userInfoRequest);
			inputOutputUserInfo.setUserId(member.getUid());
			inputOutputUserInfo.setDisplayName(member.getNickName());
			if(member.getAvatarUrl() != null){
				inputOutputUserInfo.setPortrait(member.getAvatarUrl());
			}else{
				inputOutputUserInfo.setPortrait(null);
			}
			inputOutputUserInfo.setMobile(member.getPhone());
			inputOutputUserInfo.setEmail(member.getEmail());
			inputOutputUserInfo.setSocial(member.getSignature());
			inputOutputUserInfo.setGender(member.getGender());

			IMResult<Void> voidIMResult = UserAdmin.updateUserInfo(inputOutputUserInfo, flag);

			if (voidIMResult.code != ErrorCode.ERROR_CODE_SUCCESS.getCode()) {
//				memberRepository.save(member);
//			} else {
				throw new IMServerException(voidIMResult.getErrorCode());
			}
			return RestResult.ok(null);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("更新个人资讯 exception", e);
			return RestResult.error(ERROR_SERVER_ERROR);
		}
	}

	@Override
	public RestResult generateMyInfoQrcode() {
		try {
			Subject subject = SecurityUtils.getSubject();
			Long memberId = (Long) subject.getSession().getAttribute("memberId");
			LOG.info("产生个人资讯QRCode, memberID: {}, ", memberId);
			Member member = memberRepository.findById(memberId).orElse(null);
			Assert.notNull(member, String.format("查无ID:%s用户", memberId));
			if (StringUtil.isEmpty(member.getQrCodeToken())) {
				member.setQrCodeToken(UUID.randomUUID().toString());
				memberRepository.save(member);
			}

			return RestResult.ok(jwtTokenUtil.generateJwtToken(member));
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("产生个人资讯QRCode exception", e);
			return RestResult.error(ERROR_SERVER_ERROR);
		}
	}

	@Override
	public RestResult changeTradePassword(String newPwd, String doubleCheckPwd, String oldPwd) {
		try {
			Subject subject = SecurityUtils.getSubject();
			Long memberId = (Long) subject.getSession().getAttribute("memberId");
			LOG.info("修改交易密码, memberID: {}, ", memberId);
			Member member = memberRepository.findById(memberId).orElse(null);
			Assert.notNull(member, String.format("查无ID:%s用户", memberId));
			if(!newPwd.equals(doubleCheckPwd)){
				return RestResult.error(ERROR_TRADER_PWD_NOT_THE_SAME);
			}

			UserPassword up = userPasswordRepository.findById(member.getUid()).orElse(null);
			if (StringUtil.isNotEmpty(member.getTradePwd())) {// 修改需要旧交易密码验证
				if(StringUtil.isEmpty(oldPwd)){
					return RestResult.error(ERROR_NEED_TRADE_PWD);
				}
				if(!verifyTradePwd(oldPwd, member.getTradePwd(), Member.MEMBER_TRADE_PWD_SALT)){
					return RestResult.error(ERROR_TRADE_PWD_NOT_CORRECT);
				}
			}
			member.setTradePwd(encryptTradePwd(newPwd, Member.MEMBER_TRADE_PWD_SALT));
			memberRepository.save(member);
			return RestResult.ok(null);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("修改交易密码 exception", e);
			return RestResult.error(ERROR_SERVER_ERROR);
		}
	}

	private String encryptTradePwd(String tradePwd, String salt) throws Exception {
		MessageDigest digest = MessageDigest.getInstance(Sha1Hash.ALGORITHM_NAME);
		digest.reset();
		digest.update(salt.getBytes(StandardCharsets.UTF_8));
		byte[] hashed = digest.digest(tradePwd.getBytes(StandardCharsets.UTF_8));
		String hashedPwd = Base64.getEncoder().encodeToString(hashed);
		return hashedPwd;
	}

	private boolean verifyTradePwd(String inputTradePwd, String tradePwd, String salt) throws Exception {
		MessageDigest digest = MessageDigest.getInstance(Sha1Hash.ALGORITHM_NAME);
		if (salt != null) {
			digest.reset();
			digest.update(salt.getBytes(StandardCharsets.UTF_8));
		}

		byte[] hashed = digest.digest(inputTradePwd.getBytes(StandardCharsets.UTF_8));
		String hashedPwd = Base64.getEncoder().encodeToString(hashed);
		return hashedPwd.equals(tradePwd);
	}

	private MyInfoVO convertMemberToMyInfoVO(Member member) {
		MyInfoVO myInfoVO = new MyInfoVO();
		ReflectionUtils.copyFields(myInfoVO, member, ReflectionUtils.STRING_TRIM_TO_NULL);
		DecimalFormat decimalFormat = new DecimalFormat("0.00");
		BigDecimal balanceDecimal = member.getMemberBalanceList().stream().findFirst().orElse(new MemberBalance()).getBalance();
		String balance = decimalFormat.format(balanceDecimal == null ? BigDecimal.ZERO : balanceDecimal);
		myInfoVO.setBalance(balance);
		myInfoVO.setMobile(member.getPhone());
		myInfoVO.setHasTradePwd(StringUtil.isNotBlank(member.getTradePwd())? 1: 0);
		myInfoVO.setCreateGroupEnable(member.getCreateGroupEnable());
		myInfoVO.setAvatar(member.getAvatarUrl());

//		if (StringUtil.isNotEmpty(member.getAvatarUrl())) {
//			myInfoVO.setAvatar(httpFilePath.concat(member.getAvatarUrl()));
//		}
		return myInfoVO;
	}

	@Override
	public RestResult rechargeChannel(Integer paymentMethod) {
		try {
			LOG.info("rechargeChannel paymentMethod: {}", paymentMethod);
			RechargeOrderMethodEnum methodEnum = RechargeOrderMethodEnum.parse(paymentMethod);
			if (methodEnum == null) {
				return RestResult.error(RestResult.RestCode.ERROR_METHOD_NOT_SUPPORT);
			}

			List<RechargeChannel> channels = rechargeChannelRepository.findByPaymentMethod(paymentMethod);
			LOG.error("channels.size():{}", channels.size());
			List<RechargeChannelVO> vos = new ArrayList<>();
			channels.forEach(channel -> {
				RechargeChannelVO vo = RechargeChannelVO.builder().build();
				try {
					BeanUtils.copyProperties(channel, vo, "info");
					RechargeChannelInfoDTO dto = objectMapper.readValue(channel.getInfo(), RechargeChannelInfoDTO.class);
//					String qrCodeUrl = StringUtil.isNotBlank(channel.getQrCodeImage())? httpFilePath.concat(channel.getQrCodeImage()) :null;
//					dto.setQrCodeImage(qrCodeUrl);
					dto.setQrCodeImage(channel.getQrCodeImage());
					vo.setInfo(dto);
				} catch (JsonProcessingException e) {
					LOG.error("error msg:{}", e.getMessage(), e);
				}
				vos.add(vo);
			});
			return RestResult.ok(vos);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("rechargeChannel exception", e);
			return RestResult.error(ERROR_SERVER_ERROR);
		}
	}

	@Override
	public RestResult paymentMethods() {
		try {
			Subject subject = SecurityUtils.getSubject();
			Long memberId = (Long) subject.getSession().getAttribute("memberId");
			LOG.info("paymentMethods memberId: {}", memberId);

			List<WithdrawPaymentMethod> list = withdrawPaymentMethodRepository.findByUserId(memberId);
//			list.forEach(method -> {
//				if (method.getImage() != null) {
//					method.setImage(httpFilePath.concat("/").concat(method.getImage()));
//				}
//			});
			return RestResult.ok(list);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("paymentMethods exception", e);
			return RestResult.error(ERROR_SERVER_ERROR);
		}
	}

	@Override
	public RestResult addPaymentMethod(WithdrawPaymentMethodAddRequest request, MultipartFile file) {
		try {
			Subject subject = SecurityUtils.getSubject();
			Long memberId = (Long) subject.getSession().getAttribute("memberId");
			LOG.info("addPaymentMethod memberId: {}, request: {}", memberId, request);

			WithdrawOrderChannelEnum channelEnum = WithdrawOrderChannelEnum.parse(request.getChannel());
			if (channelEnum == null) {
				return RestResult.error(RestResult.RestCode.ERROR_WITHDRAW_CHANNEL_NOT_EXIST);
			}

			if (org.h2.util.StringUtils.isNullOrEmpty(request.getInfo())) {
				return RestResult.error(RestResult.RestCode.ERROR_WITHDRAW_INFO_MUST_NOT_BE_EMPTY);
			}

			WithdrawPaymentMethod method = new WithdrawPaymentMethod(memberId, request.getName(),
					channelEnum.getValue(), request.getInfo());

			if (file != null) {
				String fileName = memberId + "_" + file.getOriginalFilename();
				file.transferTo(new File(uploadPath, fileName));
				method.setImage(fileName);
			}

			withdrawPaymentMethodRepository.save(method);
			return RestResult.ok(null);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("addPaymentMethod exception", e);
			return RestResult.error(ERROR_SERVER_ERROR);
		}
	}

	@Override
	public RestResult removePaymentMethod(Long methodId) {
		try {
			List<WithdrawPaymentMethod> list = withdrawPaymentMethodRepository.findById(methodId);
			if (list.size() <= 0) {
				return RestResult.error(RestResult.RestCode.ERROR_WITHDRAW_PAYMENT_METHOD_NOT_EXIST);
			}

			LOG.info("removePaymentMethod id: {}", list.get(0).getId().toString());
			withdrawPaymentMethodRepository.deleteById(list.get(0).getId());
			return RestResult.ok(null);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("removePaymentMethod exception", e);
			return RestResult.error(ERROR_SERVER_ERROR);
		}
	}

	@Override
	public RestResult getAsserts() {
		try {
			Subject subject = SecurityUtils.getSubject();
			Long memberId = (Long) subject.getSession().getAttribute("memberId");

			List<UserAssertVO> vos = new ArrayList<>();
			Arrays.stream(CurrencyEnum.values()).forEach(curr->{
				MemberBalance balanceBo = memberBalanceRepository.findByUserIdAndCurrency(memberId, curr.name());
				String balance = ObjectUtils.isEmpty(balanceBo)? "0" : balanceBo.getBalance().setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
				String freeze = ObjectUtils.isEmpty(balanceBo)? "0" : balanceBo.getFreeze().setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
				vos.add(UserAssertVO.builder()
						.currency(curr.name())
						.balance(balance)
						.freeze(freeze)
						.canRecharge(Boolean.TRUE)
						.canWithdraw(Boolean.TRUE)
						.build());
			});
			return RestResult.ok(vos);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("getAsserts exception", e);
			return RestResult.error(ERROR_SERVER_ERROR);
		}
	}

	@Override
	public RestResult orderList() {
		try {
			Subject subject = SecurityUtils.getSubject();
			Long memberId = (Long) subject.getSession().getAttribute("memberId");

			List<GeneralOrderVO> vos = new ArrayList<>();
			List<RechargeOrder> ros = rechargeOrderRepository.findByUserId(memberId);
			List<WithdrawOrder> wos = withdrawOrderRepository.findByUserId(memberId);

			//充值订单
			ros.forEach(ro -> {
				//充值渠道
				RechargeChannelVO vo = RechargeChannelVO.builder().build();
				try {
					RechargeChannel channel = rechargeChannelRepository.findById(ro.getChannelId()).orElse(null);
					BeanUtils.copyProperties(channel, vo, "info");
					RechargeChannelInfoDTO dto = objectMapper.readValue(channel.getInfo(), RechargeChannelInfoDTO.class);
//					String qrCodeUrl = StringUtil.isNotBlank(channel.getQrCodeImage())? httpFilePath.concat(channel.getQrCodeImage()) :null;
//					dto.setQrCodeImage(qrCodeUrl);
					dto.setQrCodeImage(channel.getQrCodeImage());
					vo.setInfo(dto);
				} catch (JsonProcessingException e) {
					LOG.error("error msg:{}", e.getMessage(), e);
				}

				vos.add(GeneralOrderVO.builder()
						.id(ro.id)
						.type(OrderTypeEnum.RECHARGE.getValue())
						.rechargeChannel(vo)
						.amount(ro.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString())
						.status(ro.getStatus())
						.createTime(ro.getCreateTime())
						.orderCode(ro.getOrderCode())
						.build());
			});

			//提现订单
			wos.forEach(wo -> {
				vos.add(GeneralOrderVO.builder()
						.id(wo.id)
						.type(OrderTypeEnum.WITHDRAW.getValue())
						.amount(wo.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString())
						.status(wo.getStatus())
						.createTime(wo.getCreateTime())
						.orderCode(wo.getOrderCode())
						.build());
			});

			//依據CreateTime排序
			List<GeneralOrderVO> sortedVos = vos.stream().sorted(Comparator.comparing(GeneralOrderVO::getCreateTime).reversed()).collect(Collectors.toList());
			return RestResult.ok(sortedVos);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("orderList exception", e);
			return RestResult.error(ERROR_SERVER_ERROR);
		}
	}


	@Override
	public RestResult chatRoomList() {
		try {
			Iterable<ChatRoom> allChatRoom = chatRoomRepository.findRoomByStatus(StatusBasicEnum.OPEN.getValue());
			List<ChatRoomVO> voList = StreamSupport.stream(allChatRoom.spliterator(), false)
				.map(room -> {
//					if (StringUtil.isNotBlank(room.getImage())) {
//						room.setImage(httpFilePath.concat(room.getImage()));
//					}
					ChatRoomVO vo = ChatRoomVO.builder().build();
					ReflectionUtils.copyFields(vo, room, ReflectionUtils.STRING_TRIM_TO_NULL);
					return vo;
				})
				.sorted(Comparator.comparing(ChatRoomVO::getSort)).collect(Collectors.toList());

			return RestResult.ok(voList);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("chatRoomList exception", e);
			return RestResult.error(ERROR_SERVER_ERROR);
		}
	}

}
