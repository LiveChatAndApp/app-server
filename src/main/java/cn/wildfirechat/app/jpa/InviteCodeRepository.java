package cn.wildfirechat.app.jpa;

import org.springframework.data.repository.CrudRepository;

public interface InviteCodeRepository extends CrudRepository<InviteCode, String> {

    InviteCode findByInviteCodeAndStatus(String inviteCode, Integer status);

    InviteCode findInviteCodeByInviteCode(String inviteCode);

}
