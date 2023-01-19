package cn.wildfirechat.app.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;

@Slf4j
@Component
public class FileUtils {

    public final static String AVATAR_PATH ="/avatar";

    public final static String GROUP_AVATAR_PATH ="/group/avatar";
    public final static String RECHARGE_PAY_IMAGE_PATH ="/recharge/payImage";

    /**
     * 上傳圖片<br>
     * 回传路径:
     * ex. /picture/paychannel/PCXXXXXXXX.jpg
     * ex. /picture/paymentproof/PFXXXXXXXX.jpg
     * ex. /picture/avatar/AVXXXXXXXX.jpg
     * ex. /picture/realcheck/RCXXXXXXXX.jpg
     * @param file 文件
     * @param uploadRootPath   文件存放根路径
     * @param subDirPath   文件存放资料夹路径
     * @param fileOriginName 原文件名
     * @return
     */
    public static String upload(MultipartFile file,String uploadRootPath,String subDirPath,
                                String fileOriginName,String fileNamePrefix){

        String newFileName = FileNameUtils.getFileName(fileOriginName,fileNamePrefix);// 生成新的文件名 ex. PCXXXXXXXX.jpg
        String uriPath = subDirPath.concat("/").concat(newFileName); //uri访问路徑(存入DB) ex. /paychannel/PCXXXXXXXX.jpg
        String realPath = uploadRootPath.concat(uriPath); //上传路径 ex. /root/picture/paychannel/PCXXXXXXXX.jpg

        File dest = new File(realPath);

        //判断文件父目录是否存在
        if(!dest.getParentFile().exists()){
            dest.getParentFile().mkdir();
        }
        log.info("檔案名:{}, 上传路径:{}, 挡案uri路徑:{}",newFileName,realPath,uriPath);
        try {
            //保存文件
            file.transferTo(dest);
            log.info("完成上传:{}",newFileName);
            return uriPath;
        } catch (IOException e) {
            log.error("檔案名:{} 上傳圖片至路徑:{} 失敗",fileOriginName,realPath);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 删除照片
     * */
    public static Boolean delete(String fileName,String rootPath,String appPath){
        String realPath = rootPath.concat(appPath) + "/" + fileName;
        log.info("Delete Real Path :{}",realPath);
        File dest = new File(realPath);
        //不存在代表是default的大头贴 不删除
        if(!dest.exists()){
            return true;
        }
        return dest.delete();
    }

}
