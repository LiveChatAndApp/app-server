package cn.wildfirechat.app.tools;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Component
public class FileNameUtils {

    private static int GAMEFI_LENGTH = 8;
    /** 大头贴照片 命名前缀 */
    public static final String AVATAR_PREFIX = "AVATAR_";
    /** 群组头贴照片 命名前缀 */
    public static final String GROUP_AVATAR_PREFIX = "GROUP_AVATAR_";
    /** 群组聊天媒体 命名前缀 */
    public static final String GROUP_MESSAGE_PREFIX = "GROUP_MESSAGE_";
    /** 聊天室头贴照片 命名前缀 */
    public static final String CHATROOM_IMAGE_PREFIX = "CHATROOM_";
    /** 充值渠道QRCode 命名前缀 */
    public static final String RECHARGE_CHANNEL_QR_PREFIX = "RECHARGE_CHANNEL_QR_";
    /** 充值截图 命名前缀 */
    public static final String PAY_IMAGE_PREFIX = "PAY_IMAGE";

    /**
     * 获取文件后缀
     * @param fileName
     * @return
     */
    public static String getSuffix(String fileName){
        return StringUtils.substring(fileName,fileName.lastIndexOf("."));
    }

    /**
     * 生成新的文件名
     * (日期+隨機5個文字+檔名後綴)
     * @param fileOriginName 源文件名
     * @return
     */
    public static String getFileName(String fileOriginName,String fileNamePrefix){
        return getRandomName(fileNamePrefix,GAMEFI_LENGTH) + getSuffix(fileOriginName);
    }

    public static String getRandomName(String prefix, int randomLength) {
        return String.format("%s%s%s",
                prefix,
                formatDateTimeCompact(),
                RandomStringUtils.randomNumeric(randomLength));
    }

    public static String formatDateTimeCompact() {
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }


}
