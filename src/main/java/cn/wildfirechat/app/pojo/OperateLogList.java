package cn.wildfirechat.app.pojo;

import cn.wildfirechat.app.tools.ConverterUtils;
import cn.wildfirechat.app.tools.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 *
 */
public class OperateLogList extends ArrayList<LogPairDto>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3710938888203514618L;
	
	private List<LogPairDto> after = new ArrayList<LogPairDto>();

	/**
	 * 新增log(调用方无需判断null)
	 * 
	 * @param key		后台显示中文字(建议对应到后台UI的栏位名称)
	 * @param val		原始值
	 * @param needMask	是否需要遮蔽敏感讯息
	 * @return
	 */
	public boolean addLog(String key , Object val , boolean needMask) {
		try {
			String value = ConverterUtils.getAsString(val);
			if(StringUtil.isBlank(value)) {
				return false;
			}
			if(needMask) {
				value = StringUtil.logMask(value);
			}
			return add(LogPairDto.builder().key(key).val(value).build());
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 新增差异log(调用方无需判断null)
	 * 
	 * @param key		后台显示中文字(建议对应到后台UI的栏位名称)
	 * @param oldValue	原始值
	 * @param newValue	修改后的值
	 * @param needMask	是否需要遮蔽敏感讯息
	 * @return
	 */
	public boolean addDiffLog(String key , Object oldValue, Object newValue , boolean needMask) {
		try {
			String oldVal = ConverterUtils.getAsString(oldValue);
			String newVal = ConverterUtils.getAsString(newValue);
			if(needMask) {
				// 敏感讯息遮罩
				oldVal = StringUtil.logMask(oldVal);
				newVal = StringUtil.logMask(newVal);
			}
			if(oldVal == null) {
				oldVal = "";
			}
			if(newVal == null) {
				newVal = "";
			}
			if(!oldVal.equals(newVal)) {
				after.add(LogPairDto.builder().key(key).val(newVal).build());
				return add(LogPairDto.builder().key(key).val(oldVal).build());
			} 
			return false;
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public List<LogPairDto> getAfter(){
		return this.after;
	}

}
