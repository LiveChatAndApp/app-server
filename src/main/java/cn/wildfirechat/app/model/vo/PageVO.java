package cn.wildfirechat.app.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageVO implements Serializable {

	private Object data;
	private long page;
	private long pageSize;
	private long totalPage;
	private long totalElement;

	public static <T> PageVO convert(Page<T> originalPage) {
		return new PageVO(originalPage.getContent(), originalPage.getPageable().getPageNumber(),
				originalPage.getPageable().getPageSize(), originalPage.getTotalPages(),
				originalPage.getTotalElements());
	}
}
