package cn.wildfirechat.app.jpa;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ChatRoomRepository extends CrudRepository<ChatRoom, Long> {

    @Query("SELECT CR FROM ChatRoom CR WHERE CR.status = :status")
    List<ChatRoom> findRoomByStatus(Integer status);
}
