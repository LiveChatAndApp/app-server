package cn.wildfirechat.app.controller;

import cn.wildfirechat.app.RestResult;
import cn.wildfirechat.app.enums.GroupStatusEnum;
import cn.wildfirechat.app.enums.GroupTypeEnum;
import cn.wildfirechat.app.jpa.GroupMember;
import cn.wildfirechat.app.jpa.GroupMemberRepository;
import cn.wildfirechat.app.model.form.PageForm;
import cn.wildfirechat.app.model.vo.GroupListVO;
import cn.wildfirechat.app.model.vo.PageVO;
import cn.wildfirechat.app.tools.*;
import com.google.common.collect.Iterables;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import static cn.wildfirechat.app.RestResult.RestCode.ERROR_SERVER_ERROR;

@Api(tags = "群组接口")
@RestController()
@RequestMapping("/group")
public class GroupController {
	private static final Logger LOG = LoggerFactory.getLogger(GroupController.class);

	@Value("${upload.real.path}")
	private String uploadPath;

	@Value("${http.file.path.domain.variable}")
	private String domainReplaceString;

	@Autowired
	private UploadFileUtils uploadFileUtils;

	@Autowired
	private GroupMemberRepository groupMemberRepository;

	@ApiOperation(value = "查询群组列表")
	@GetMapping("/list")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "groupType", value = "群组类型 1: 一般, 2: 广播", dataTypeClass = Integer.class)})
	public RestResult groupList(int groupType, PageForm page) {
		try {
			Subject subject = SecurityUtils.getSubject();
			Long memberId = (Long) subject.getSession().getAttribute("memberId");
			LOG.info("查询群组列表 groupType: {}, memberId:{}", groupType, memberId);

			Specification<GroupMember> groupMemberSpecification = (root, query, criteriaBuilder) -> {
				List<Predicate> conditions = new ArrayList<>();
				conditions.add(criteriaBuilder.equal(root.get("member").get("id"), memberId));
				conditions.add(criteriaBuilder.equal(root.get("group").get("status"), GroupStatusEnum.NORMAL.getValue()));
				conditions.add(criteriaBuilder.equal(root.get("group").get("groupType"), groupType));
				return criteriaBuilder.and(Iterables.toArray(conditions, Predicate.class));
			};
			Page<GroupMember> groupList = groupMemberRepository.findAll(groupMemberSpecification, page.startPage());

			List<GroupListVO> groupListVOList = new ArrayList<>();
			groupList.get().map(GroupMember::getGroup).forEach(group -> {
				GroupListVO groupListVO = new GroupListVO();
				ReflectionUtils.copyFields(groupListVO, group);
				if (StringUtil.isNotEmpty(group.getGroupImage())) {
					groupListVO.setPortrait(uploadFileUtils.parseFilePathToUrl(group.getGroupImage()));
				}
				groupListVO.setGroupName(group.getName());
				groupListVOList.add(groupListVO);
			});

			Page<GroupListVO> groupListVOPage = new PageImpl<>(groupListVOList, groupList.getPageable(),
					groupList.getTotalElements());

			groupListVOPage.forEach(pp -> {
				LOG.info("查询群组列表 gid: {}, groupName:{}, memberId: {}", pp.getGid(), pp.getGroupName(), memberId);
			});

			return RestResult.ok(PageVO.convert(groupListVOPage));
		} catch (Exception e) {
			LOG.error("查询群组列表 exception", e);
			return RestResult.error(ERROR_SERVER_ERROR);
		}
	}

	@ApiOperation(value = "上传群组头像")
	@PostMapping("/updatePortrait")
	public RestResult updatePortrait(MultipartFile file) {
		try {
			LOG.info("上传群组头像");

			// 更新头像
			String urlPath = null;
			if (file != null && !file.isEmpty()) {
				urlPath = uploadFileUtils.uploadFile(file, FileUtils.GROUP_AVATAR_PATH, FileNameUtils.GROUP_AVATAR_PREFIX);
			}
			if (urlPath != null) {
				return RestResult.ok(urlPath);
			} else {
				return RestResult.error(ERROR_SERVER_ERROR);
			}
		} catch (Exception e) {
			LOG.error("上传群组头像 exception", e);
			return RestResult.error(ERROR_SERVER_ERROR);
		}
	}
}