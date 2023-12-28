package com.jjeong.kiwi.repository;

import com.jjeong.kiwi.domain.ChatRoom;
import com.jjeong.kiwi.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findByUsersIn(List<User> users);

    List<ChatRoom> findChatRoomByRoomName(String roomName);

    ChatRoom findChatRoomById(Long roomId);

//    @Query("SELECT c FROM ChatRoom c WHERE :users MEMBER OF c.users")
//    List<ChatRoom> findByUsersContainingAll(@Param("users") List<User> users);

}
