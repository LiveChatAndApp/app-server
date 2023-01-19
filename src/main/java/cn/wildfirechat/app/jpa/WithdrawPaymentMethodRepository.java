package cn.wildfirechat.app.jpa;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RepositoryRestResource()
public interface WithdrawPaymentMethodRepository extends PagingAndSortingRepository<WithdrawPaymentMethod, String> {
    List<WithdrawPaymentMethod> findById(Long id);
    List<WithdrawPaymentMethod> findByUserId(Long memberId);

    @Transactional
    @Modifying
    @Query(value = "delete from t_withdraw_payment_method where id = ?1", nativeQuery = true)
    void deleteById(Long id);
}
