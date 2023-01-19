package cn.wildfirechat.app.model.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Data
@Builder
@AllArgsConstructor
@ApiModel("分页查询表")
public class PageForm {
	@ApiModelProperty("单页数量")
	private int pageOfSize;
	@ApiModelProperty("页数")
	private int page;

	public PageForm() {
		pageOfSize = 10;
		page = 0;
	}

	public Pageable startPage() {
		return PageRequest.of(page > 0 ? page - 1 : page, pageOfSize);
	}
}
