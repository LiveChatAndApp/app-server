package cn.wildfirechat.app.jpa;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource()
public interface RechargeOrderRepository extends PagingAndSortingRepository<RechargeOrder, Long> {

    List<RechargeOrder> findByUserId(Long userId);
}
