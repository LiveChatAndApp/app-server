package cn.wildfirechat.app.jpa;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RepositoryRestResource()
public interface RechargeChannelRepository extends PagingAndSortingRepository<RechargeChannel, Long> {

    List<RechargeChannel> findByPaymentMethod(Integer findByPaymentMethod);

}
