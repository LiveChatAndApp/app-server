package cn.wildfirechat.app.jpa;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import java.util.List;
import java.util.Optional;

public interface FriendRepository extends CrudRepository<Friend, Long> {

	@Query("SELECT F FROM Friend AS F WHERE F.memberSource.id IN (:memberId, :targetId) AND F.memberTargetId IN (:memberId, :targetId)")
	Optional<Friend> relateExist(Long memberId, Long targetId);

	@Query("SELECT F FROM Friend AS F WHERE F.requestReceiver = ?1 AND F.verify < 2")
	List<Friend> findFriendRequest(Long memberId);
}
