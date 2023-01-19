package cn.wildfirechat.app.pojo;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class LogPairDto {
	/** 栏位名 */
	String key;
	/** 栏位值 */
	String val;
}
