package cn.wildfirechat.app.jpa;

import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface GroupRepository extends BaseRepository<Group, Long> {

	@Query("SELECT GM FROM GroupMember GM WHERE GM.member.id = :memberId")
	List<GroupMember> findGroupByMemberId(Long memberId);
}
