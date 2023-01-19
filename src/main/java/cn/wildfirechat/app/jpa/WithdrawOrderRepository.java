package cn.wildfirechat.app.jpa;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource()
public interface WithdrawOrderRepository extends PagingAndSortingRepository<WithdrawOrder, String> {

    List<WithdrawOrder> findByUserId(Long userId);
}
