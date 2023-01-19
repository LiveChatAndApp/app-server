package cn.wildfirechat.app.jpa;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource()
public interface MemberBalanceRepository extends PagingAndSortingRepository<MemberBalance, String> {
    MemberBalance findByUserIdAndCurrency(Long memberId, String currency);
}
