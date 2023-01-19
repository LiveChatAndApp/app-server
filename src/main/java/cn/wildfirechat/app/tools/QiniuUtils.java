package cn.wildfirechat.app.tools;

import com.qiniu.http.Response;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@Slf4j
public class QiniuUtils {
	@Value(value = "${qiniu.accessKey}")
	private String accessKey;

	@Value(value = "${qiniu.secretKey}")
	private String secretKey;

	@Value(value = "${qiniu.bucket}")
	private String bucket;

	@Value(value = "${qiniu.bucket_url}")
	private String bucketUrl;

	public String uploadFile(MultipartFile file, String fileName) {
		Auth auth = Auth.create(accessKey, secretKey);
		String upToken = auth.uploadToken(bucket, fileName);
		try {
			Response response = getUploadManager().put(file.getBytes(), fileName, upToken);
			if (response.isOK()) {
				return fileName;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	private UploadManager getUploadManager() {
		com.qiniu.storage.Configuration cfg = new com.qiniu.storage.Configuration(Region.regionAs0());
		cfg.resumableUploadAPIVersion = com.qiniu.storage.Configuration.ResumableUploadAPIVersion.V2;
		return new UploadManager(cfg);
	}
}
