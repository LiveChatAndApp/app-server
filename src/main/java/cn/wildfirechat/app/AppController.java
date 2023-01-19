package cn.wildfirechat.app;

import cn.wildfirechat.app.jpa.FavoriteItem;
import cn.wildfirechat.app.pojo.*;
import cn.wildfirechat.pojos.InputCreateDevice;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.h2.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Api(tags = "App操作接口")
@RestController
public class AppController {
	private static final Logger LOG = LoggerFactory.getLogger(AppController.class);
	@Autowired
	private Service mService;

	@ApiOperation(value = "服务状态", httpMethod = "GET")
	@GetMapping()
	public Object health() {
		return "Ok";
	}

	/*
	 * 移动端登录
	 */
	@ApiOperation(value = "发送登入码", httpMethod = "POST")
	@PostMapping(value = "/send_code", produces = "application/json;charset=UTF-8")
	public Object sendLoginCode(@RequestBody SendCodeRequest request) {
		return mService.sendLoginCode(request.getMobile());
	}

	@ApiOperation(value = "移动端登录-登入码验证", httpMethod = "POST")
	@PostMapping(value = "/login", produces = "application/json;charset=UTF-8")
	public Object loginWithMobileCode(@RequestBody PhoneCodeLoginRequest request, HttpServletRequest httpRequest, HttpServletResponse response) {
		return mService.loginWithMobileCode(httpRequest, response, request.getMobile(), request.getCode(), request.getInviteCode(),
				request.getClientId(), request.getPlatform() == null ? 0 : request.getPlatform());
	}

	@ApiOperation(value = "移动端登录-密码验证", httpMethod = "POST")
	@PostMapping(value = "/login_pwd", produces = "application/json;charset=UTF-8")
	public Object loginWithPassword(@RequestBody UserPasswordLoginRequest request, HttpServletRequest httpRequest, HttpServletResponse response) {
		return mService.loginWithPassword(httpRequest, response, request.getMobile(), request.getPassword(), request.getClientId(),
				request.getPlatform() == null ? 0 : request.getPlatform());
	}

	@ApiOperation(value = "修改密码", httpMethod = "POST")
	@PostMapping(value = "/change_pwd", produces = "application/json;charset=UTF-8")
	public Object changePassword(@RequestBody ChangePasswordRequest request) {
		return mService.changePassword(request.getOldPassword(), request.getNewPassword());
	}

	@ApiOperation(value = "发送重置码", httpMethod = "POST")
	@PostMapping(value = "/send_reset_code", produces = "application/json;charset=UTF-8")
	public Object sendResetCode(@RequestBody SendCodeRequest request) {
		return mService.sendResetCode(request.getMobile());
	}

	@ApiOperation(value = "重置密码", httpMethod = "POST")
	@PostMapping(value = "/reset_pwd", produces = "application/json;charset=UTF-8")
	public Object resetPassword(@RequestBody ResetPasswordRequest request) {
		return mService.resetPassword(request.getMobile(), request.getResetCode(), request.getNewPassword());
	}

	@ApiOperation(value = "发送注销码", httpMethod = "POST")
	@PostMapping(value = "/send_destroy_code", produces = "application/json;charset=UTF-8")
	public Object sendDestroyCode() {
		return mService.sendDestroyCode();
	}

	@ApiOperation(value = "注销", httpMethod = "POST")
	@PostMapping(value = "/destroy", produces = "application/json;charset=UTF-8")
	public Object destroy(@RequestBody DestroyRequest code, HttpServletResponse response) {
		return mService.destroy(response, code.getCode());
	}

	/*
	 * PC扫码操作 1, PC -> App 创建会话 2, PC -> App
	 * 轮询调用session_login进行登陆，如果已经扫码确认返回token，否则返回错误码9（已经扫码还没确认)或者10(还没有被扫码)
	 */
	@CrossOrigin
	@PostMapping(value = "/pc_session", produces = "application/json;charset=UTF-8")
	public Object createPcSession(@RequestBody CreateSessionRequest request) {
		return mService.createPcSession(request);
	}

	@CrossOrigin
	@PostMapping(value = "/session_login/{token}", produces = "application/json;charset=UTF-8")
	public Object loginWithSession(@PathVariable("token") String token) {
		LOG.info("receive login with session key {}", token);
		RestResult timeoutResult = RestResult.error(RestResult.RestCode.ERROR_SESSION_EXPIRED);
		ResponseEntity<RestResult> timeoutResponseEntity = new ResponseEntity<>(timeoutResult, HttpStatus.OK);
		int timeoutSecond = 60;
		DeferredResult<ResponseEntity> deferredResult = new DeferredResult<>(timeoutSecond * 1000L,
				timeoutResponseEntity);
		CompletableFuture.runAsync(() -> {
			try {
				int i = 0;
				while (i < timeoutSecond) {
					RestResult restResult = mService.loginWithSession(token);
					if (restResult.getCode() == RestResult.RestCode.ERROR_SESSION_NOT_VERIFIED.code
							&& restResult.getResult() != null) {
						deferredResult.setResult(new ResponseEntity(restResult, HttpStatus.OK));
						break;
					} else if (restResult.getCode() == RestResult.RestCode.SUCCESS.code
							|| restResult.getCode() == RestResult.RestCode.ERROR_SESSION_EXPIRED.code
							|| restResult.getCode() == RestResult.RestCode.ERROR_SERVER_ERROR.code
							|| restResult.getCode() == RestResult.RestCode.ERROR_SESSION_CANCELED.code
							|| restResult.getCode() == RestResult.RestCode.ERROR_CODE_INCORRECT.code) {
						ResponseEntity.BodyBuilder builder = ResponseEntity.ok();
						if (restResult.getCode() == RestResult.RestCode.SUCCESS.code) {
							Subject subject = SecurityUtils.getSubject();
							Object sessionId = subject.getSession().getId();
							builder.header("authToken", sessionId.toString());
						}
						deferredResult.setResult(builder.body(restResult));
						break;
					} else {
						TimeUnit.SECONDS.sleep(1);
					}
					i++;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				deferredResult.setResult(
						new ResponseEntity(RestResult.error(RestResult.RestCode.ERROR_SERVER_ERROR), HttpStatus.OK));
			}
		}, Executors.newCachedThreadPool());
		return deferredResult;
	}

	/*
	 * 手机扫码操作 1，扫码，调用/scan_pc接口。 2，调用/confirm_pc 接口进行确认
	 */
	@PostMapping(value = "/scan_pc/{token}", produces = "application/json;charset=UTF-8")
	public Object scanPc(@PathVariable("token") String token) {
		return mService.scanPc(token);
	}

	@PostMapping(value = "/confirm_pc", produces = "application/json;charset=UTF-8")
	public Object confirmPc(@RequestBody ConfirmSessionRequest request) {
		return mService.confirmPc(request);
	}
	@PostMapping(value = "/cancel_pc", produces = "application/json;charset=UTF-8")
	public Object cancelPc(@RequestBody CancelSessionRequest request) {
		return mService.cancelPc(request);
	}

	/*
	 * 修改野火账户
	 */
	@CrossOrigin
	@PostMapping(value = "/change_name", produces = "application/json;charset=UTF-8")
	public Object changeName(@RequestBody ChangeNameRequest request) {
		if (StringUtils.isNullOrEmpty(request.getNewName())) {
			return RestResult.error(RestResult.RestCode.ERROR_INVALID_PARAMETER);
		}
		return mService.changeName(request.getNewName());
	}

	/*
	 * 群公告相关接口
	 */
	@CrossOrigin
	@PostMapping(value = "/put_group_announcement", produces = "application/json;charset=UTF-8")
	public Object putGroupAnnouncement(@RequestBody GroupAnnouncementPojo request) {
		return mService.putGroupAnnouncement(request);
	}

	@CrossOrigin
	@PostMapping(value = "/get_group_announcement", produces = "application/json;charset=UTF-8")
	public Object getGroupAnnouncement(@RequestBody GroupIdPojo request) {
		return mService.getGroupAnnouncement(request.groupId);
	}

	/*
	 * 客户端上传协议栈日志
	 */
	@PostMapping(value = "/logs/{userId}/upload")
	public Object uploadFiles(@RequestParam("file") MultipartFile file, @PathVariable("userId") String userId)
			throws IOException {
		return mService.saveUserLogs(userId, file);
	}

	/*
	 * 投诉和建议
	 */
	@CrossOrigin
	@PostMapping(value = "/complain", produces = "application/json;charset=UTF-8")
	public Object complain(@RequestBody ComplainRequest request) {
		return mService.complain(request.text);
	}

	/*
	 * 物联网相关接口
	 */
	@PostMapping(value = "/things/add_device")
	public Object addDevice(@RequestBody InputCreateDevice createDevice) {
		return mService.addDevice(createDevice);
	}

	@PostMapping(value = "/things/list_device")
	public Object getDeviceList() {
		return mService.getDeviceList();
	}

	@PostMapping(value = "/things/del_device")
	public Object delDevice(@RequestBody InputCreateDevice createDevice) {
		return mService.delDevice(createDevice);
	}

	/*
	 * 发送消息
	 */
	@PostMapping(value = "/messages/send")
	public Object sendMessage(@RequestBody SendMessageRequest sendMessageRequest) {
		return mService.sendMessage(sendMessageRequest);
	}

	/*
	 * iOS设备Share extension分享图片文件等使用
	 */
	@PostMapping(value = "/media/upload/{media_type}")
	public Object uploadMedia(@RequestParam("file") MultipartFile file, @PathVariable("media_type") int mediaType)
			throws IOException {
		return mService.uploadMedia(mediaType, file);
	}

	@CrossOrigin
	@PostMapping(value = "/fav/add", produces = "application/json;charset=UTF-8")
	public Object putFavoriteItem(@RequestBody FavoriteItem request) {
		return mService.putFavoriteItem(request);
	}

	@CrossOrigin
	@PostMapping(value = "/fav/del/{fav_id}", produces = "application/json;charset=UTF-8")
	public Object removeFavoriteItem(@PathVariable("fav_id") int favId) {
		return mService.removeFavoriteItems(favId);
	}

	@CrossOrigin
	@PostMapping(value = "/fav/list", produces = "application/json;charset=UTF-8")
	public Object getFavoriteItems(@RequestBody LoadFavoriteRequest request) {
		return mService.getFavoriteItems(request.id, request.count);
	}

	@CrossOrigin
	@PostMapping(value = "/group/members_for_portrait", produces = "application/json;charset=UTF-8")
	public Object getGroupMembersForPortrait(@RequestBody GroupIdPojo groupIdPojo) {
		return mService.getGroupMembersForPortrait(groupIdPojo.groupId);
	}

	@ApiOperation(value = "提币申请", httpMethod = "POST")
	@PostMapping(value = "/withdraw/apply", produces = "application/json;charset=UTF-8")
	public Object withdrawApply(@RequestBody WithdrawApplyRequest request) {
		return mService.withdrawApply(request);
	}

	@ApiOperation(value = "充值申请", httpMethod = "POST")
	@PostMapping(value = "/recharge/apply", produces = "application/json;charset=UTF-8")
	public Object rechargeApply(@RequestBody RechargeApplyRequest request) {
		return mService.rechargeApply(request);
	}

	@ApiOperation(value = "充值截图上传", httpMethod = "POST")
	@PostMapping(value = "/recharge/confirm", produces = "application/json;charset=UTF-8")
	public Object rechargeConfirm(RechargeConfirmRequest request) {
		return mService.rechargeConfirm(request);
	}

	@CrossOrigin
	@ApiOperation(value = "我的信息", httpMethod = "GET")
	@GetMapping(value = "/info", produces = "application/json;charset=UTF-8")
	public Object info(String userId) {
		return mService.info(userId);
	}

	@CrossOrigin
	@ApiOperation(value = "更新我的信息", httpMethod = "POST")
	@PostMapping(value = "/info/update/{flag}", produces = "application/json;charset=UTF-8")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "flag", value = "更新目标资料(bit制) 1: 呢称, 2: 头像, 3: 性别, 4: 电话, 5: email, 6: 地址, 7: 公司, 8: 社交信息, 9: extra信息, 10: name信息</br> 范例：修改头像和呢称, flag为 十六进制(二进制): 0x03(11)", required = true)})
	public Object updateInfo(@PathVariable("flag") Integer flag, UserInfoRequest userInfoRequest) {
		return mService.updateInfo(userInfoRequest, flag);
	}

	@CrossOrigin
	@ApiOperation(value = "充值渠道列表", httpMethod = "GET")
	@GetMapping(value = "/recharge/channel", produces = "application/json;charset=UTF-8")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "paymentMethod", value = "收款方式 1:银行卡,2:微信,3:支付宝", required = true)})
	public Object rechargeChannel(@RequestParam(value = "paymentMethod") Integer paymentMethod) {
		return mService.rechargeChannel(paymentMethod);
	}

	@CrossOrigin
	@ApiOperation(value = "收款方式列表", httpMethod = "GET")
	@GetMapping(value = "/withdraw/payment_method", produces = "application/json;charset=UTF-8")
	public Object paymentMethod() {
		return mService.paymentMethods();
	}

	@CrossOrigin
	@ApiOperation(value = "添加提币收款方式", httpMethod = "POST")
	@PostMapping(value = "/withdraw/payment_method/add")
	public Object addPaymentMethod(WithdrawPaymentMethodAddRequest request, MultipartFile file) {
		return mService.addPaymentMethod(request, file);
	}

	@CrossOrigin
	@ApiOperation(value = "移除提币收款方式", httpMethod = "POST")
	@PostMapping(value = "/withdraw/payment_method/remove/{methodId}", produces = "application/json;charset=UTF-8")
	public Object removePaymentMethod(@PathVariable("methodId") Long methodId) {
		return mService.removePaymentMethod(methodId);
	}

	@CrossOrigin
	@ApiOperation(value = "设置/修改支付密码", httpMethod = "POST")
	@PostMapping(value = "/changeTradePassword", produces = "application/json;charset=UTF-8")
	public Object changeTradePassword(@RequestBody UserTradePasswordUpdateRequest request) {
		return mService.changeTradePassword(request.getNewPwd(), request.getDoubleCheckPwd(), request.getOldPwd());
	}

	@CrossOrigin
	@ApiOperation(value = "个人资讯QRCode Token", httpMethod = "GET")
	@GetMapping(value = "/info/qrcode", produces = "application/json;charset=UTF-8")
	public Object generateQrCode() {
		return mService.generateMyInfoQrcode();
	}

	@CrossOrigin
	@ApiOperation(value = "我的钱包", httpMethod = "GET")
	@GetMapping(value = "/info/asserts", produces = "application/json;charset=UTF-8")
	public Object infoAsserts() {
		return mService.getAsserts();
	}

	@CrossOrigin
	@ApiOperation(value = "我的钱包-订单明細", httpMethod = "GET")
	@GetMapping(value = "/info/orderList", produces = "application/json;charset=UTF-8")
	public Object infoOrderList() {
		return mService.orderList();
	}

	@CrossOrigin
	@ApiOperation(value = "发现-聊天室列表", httpMethod = "GET")
	@GetMapping(value = "/chatRoomList", produces = "application/json;charset=UTF-8")
	public Object chatRoomList() {
		return mService.chatRoomList();
	}
}
