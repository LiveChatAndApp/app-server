package cn.wildfirechat.app.pojo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OperateLogMemoDto {
	private List<LogPairDto> before;
	private List<LogPairDto> after;

	public List<LogPairDto> getAfter() {
		if (this.after == null) {
			return new ArrayList<LogPairDto>();
		}
		return this.after;
	}
}

