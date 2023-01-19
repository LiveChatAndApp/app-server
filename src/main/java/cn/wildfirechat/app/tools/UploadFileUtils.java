package cn.wildfirechat.app.tools;

import cn.wildfirechat.app.enums.MediaUploadEnum;
import com.google.common.net.MediaType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class UploadFileUtils {

	@Value(value = "${media.upload_target}")
	private Integer uploadTarget;

	@Value(value = "${http.file.path}")
	private String httpPath;

	@Value(value = "${upload.real.path}")
	private String uploadPath;

	@Value(value = "${http.file.path.domain.variable}")
	private String domainReplaceString;

	@Value(value = "${qiniu.bucket_url}")
	private String qiniuBucketUrl;

	@Autowired
	private QiniuUtils qiniuUtils;

	public String uploadFile(MultipartFile file, String subDirPath, String fileNamePrefix) {
		return uploadFile(file, subDirPath, fileNamePrefix, null);
	}

	public String uploadFile(MultipartFile file, String subDirPath, String fileNamePrefix,
			List<MediaType> allowExtension) {
		String urlPath = "";
		MediaUploadEnum mediaUploadEnum = MediaUploadEnum.parse(uploadTarget);
		if (file != null && !file.isEmpty()) {
			if (allowExtension == null) {
				allowExtension = new ArrayList<>();
			}
			boolean allowUpload = allowExtension.isEmpty();

			String fileMediaType = Arrays.stream(file.getContentType().split("/")).findFirst().orElse("");

			for (MediaType mediaType : allowExtension) {
				if (Objects.equals(fileMediaType, mediaType.type())) {
					allowUpload = true;
					break;
				}
			}

			if (allowUpload) {
				try {
					if (mediaUploadEnum == MediaUploadEnum.LOCAL) {
						urlPath = FileUtils.upload(file, uploadPath, subDirPath, file.getOriginalFilename(),
								fileNamePrefix);
						log.info("[Upload Local] path: {}", urlPath);
					} else if (mediaUploadEnum == MediaUploadEnum.QINIU) {
						String fileName = FileNameUtils.getFileName(file.getOriginalFilename(), fileNamePrefix);
						urlPath = qiniuUtils.uploadFile(file, subDirPath.substring(1).concat("/" + fileName));
						log.info("[Upload Qiniu] path: {}", urlPath);
						urlPath = "/" + urlPath;
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
		return domainReplaceString + urlPath;
	}

	/**
	 * 将档案路径转换为Url {{@param source}} -> {{domain}}/{{@param source}}
	 *
	 */
	public String parseFilePathToUrl(String source) {
		return source.replace(domainReplaceString, getDomain());
	}

	/**
	 * 将档案Url转换为Path {{domain}}/{{@param source}} ->
	 * {@value httpPath}/{{@param source}}
	 *
	 */
	public String parseFileUrlToPath(String source) {
		return source.replace(getDomain(), domainReplaceString);
	}

	public String getDomain() {
		MediaUploadEnum mediaUploadEnum = MediaUploadEnum.parse(uploadTarget);
		String host = "";
		if (mediaUploadEnum == MediaUploadEnum.LOCAL) {
			host = httpPath;
		} else if (mediaUploadEnum == MediaUploadEnum.QINIU) {
			host = qiniuBucketUrl;
		}
		return host;
	}
}
