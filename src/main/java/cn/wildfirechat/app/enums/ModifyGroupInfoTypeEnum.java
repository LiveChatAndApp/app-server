package cn.wildfirechat.app.enums;

public enum ModifyGroupInfoTypeEnum {
	GROUP_NAME(0, "群组名称"),
	GROUP_PORTRAIT(1, "群组头像"),
	GROUP_EXTRA(2, "附加讯息"),
	GROUP_MUTE(3, "禁音"),
	GROUP_JOIN_TYPE(4, "加入方式"),
	GROUP_PRIVATE_CHAT(5, "禁止私聊"),
	GROUP_SEARCHABLE(6, "是否允许搜索群云组"),
	HISTORY_WITH_NEWER(7, "是否允许新成员查看讯息"),// 专业版支持
	MAX_GROUP_MEMBER(8, "群组最大成员数"); // 专业版支持
	private final int value;
	private final String message;

	ModifyGroupInfoTypeEnum(int value, String message) {
		this.value = value;
		this.message = message;
	}

	public static ModifyGroupInfoTypeEnum parse(Integer value) {
		if (value != null) {
			for (ModifyGroupInfoTypeEnum info : values()) {
				if (info.value == value) {
					return info;
				}
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return value + "|" + message;
	}
}
