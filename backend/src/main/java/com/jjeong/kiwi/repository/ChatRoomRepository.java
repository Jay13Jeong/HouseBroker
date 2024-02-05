package com.jjeong.kiwi.repository;

import com.jjeong.kiwi.model.ChatRoom;
import com.jjeong.kiwi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findByUsersIn(List<User> users);

    List<ChatRoom> findChatRoomByRoomName(String roomName);

    ChatRoom findChatRoomById(Long roomId);

}
