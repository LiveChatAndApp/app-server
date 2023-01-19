package cn.wildfirechat.app.jpa;

import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "t_member_password")
public class UserPassword {
	@Id
	@Column(length = 128)
	private String userId;

	private String password;

	private String salt;

	private String resetCode;

	private long resetCodeTime;

	private int tryCount;

	private long lastTryTime;

	@Column(name = "is_register")
	private boolean isRegister;

	public UserPassword() {
	}

	public UserPassword(String userId) {
		this.userId = userId;
	}

	public UserPassword(String userId, String password, String salt) {
		this.userId = userId;
		this.password = password;
		this.salt = salt;
		this.resetCodeTime = 0;
		this.tryCount = 0;
		this.lastTryTime = 0;
	}

	public UserPassword(String userId, String password, String salt, String resetCode, long resetCodeTime) {
		this.userId = userId;
		this.password = password;
		this.salt = salt;
		this.resetCode = resetCode;
		this.resetCodeTime = resetCodeTime;
		this.tryCount = 0;
		this.lastTryTime = 0;
	}
}
