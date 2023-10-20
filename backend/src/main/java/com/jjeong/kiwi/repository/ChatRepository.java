package com.jjeong.kiwi.repository;

import com.jjeong.kiwi.domain.Chat;
import com.jjeong.kiwi.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findByChatRoom(ChatRoom chatRoom);
}
