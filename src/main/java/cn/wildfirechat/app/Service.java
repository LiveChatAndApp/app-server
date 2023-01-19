package cn.wildfirechat.app;


import cn.wildfirechat.app.jpa.FavoriteItem;
import cn.wildfirechat.app.pojo.*;
import cn.wildfirechat.pojos.InputCreateDevice;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Service {
    RestResult sendLoginCode(String mobile);
    RestResult sendResetCode(String mobile);
    RestResult loginWithMobileCode(HttpServletRequest httpRequest, HttpServletResponse response, String mobile, String code, String inviteCode, String clientId, int platform);
    RestResult loginWithPassword(HttpServletRequest httpRequest, HttpServletResponse response, String mobile, String password, String clientId, int platform);
    RestResult changePassword(String oldPwd, String newPwd);
    RestResult resetPassword(String mobile, String resetCode, String newPwd);
    RestResult sendDestroyCode();
    RestResult destroy(HttpServletResponse response, String code);

    RestResult createPcSession(CreateSessionRequest request);
    RestResult loginWithSession(String token);

    RestResult scanPc(String token);
    RestResult confirmPc(ConfirmSessionRequest request);
    RestResult cancelPc(CancelSessionRequest request);

    RestResult changeName(String newName);
    RestResult complain(String text);

    RestResult putGroupAnnouncement(GroupAnnouncementPojo request);
    RestResult getGroupAnnouncement(String groupId);

    RestResult saveUserLogs(String userId, MultipartFile file);

    RestResult addDevice(InputCreateDevice createDevice);
    RestResult getDeviceList();
    RestResult delDevice(InputCreateDevice createDevice);

    RestResult sendMessage(SendMessageRequest request);
    RestResult uploadMedia(int mediaType, MultipartFile file);

    RestResult putFavoriteItem(FavoriteItem request);
    RestResult removeFavoriteItems(long id);
    RestResult getFavoriteItems(long id, int count);
    RestResult getGroupMembersForPortrait(String groupId);

    RestResult withdrawApply(WithdrawApplyRequest request);
    RestResult rechargeApply(RechargeApplyRequest request);
    RestResult rechargeConfirm(RechargeConfirmRequest request);

    RestResult info(String uid);
    RestResult updateInfo(UserInfoRequest userInfoRequest, Integer flag);
    RestResult generateMyInfoQrcode();
    RestResult changeTradePassword(String newPwd, String doubleCheckPwd, String oldPwd);

    RestResult rechargeChannel(Integer paymentMethod);
    RestResult paymentMethods();
    RestResult addPaymentMethod(WithdrawPaymentMethodAddRequest request, MultipartFile file);
    RestResult removePaymentMethod(Long id);

    RestResult getAsserts();
    RestResult orderList();

    RestResult chatRoomList();
}
