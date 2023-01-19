package cn.wildfirechat.app.exception;

import cn.wildfirechat.common.ErrorCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IMServerException extends Exception {
	private ErrorCode errorCode;


	public IMServerException(ErrorCode errorCode) {
		super(errorCode.msg);
		this.errorCode = errorCode;
	}

	public IMServerException(ErrorCode errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}
}
