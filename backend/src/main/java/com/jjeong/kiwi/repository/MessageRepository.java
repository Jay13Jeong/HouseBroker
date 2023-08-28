package com.jjeong.kiwi.repository;

import com.jjeong.kiwi.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByUserId(Long userId);
    List<Message> findByUserIdAndTimestampBetween(Long userId, LocalDateTime startTime, LocalDateTime endTime);
}
