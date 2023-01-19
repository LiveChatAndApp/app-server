package cn.wildfirechat.app.jpa;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.List;

@RepositoryRestResource()
public interface DefaultMemberRepository extends PagingAndSortingRepository<DefaultMember, Long> {

    @Query("SELECT dm FROM DefaultMember AS dm WHERE dm.inviteCodeId = :inviteCodeId OR dm.inviteCodeId = 0")
    List<DefaultMember> findDefaultMembersByInviteCodeIdAndGlobal(Long inviteCodeId);

}
