package ru.newvasuki.smarthome.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.newvasuki.smarthome.data.entity.Value;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface ValueRepository extends JpaRepository<Value, Integer> {
    List<Value> findAllByUid(String uid);

    List<Value> findAllByDateTimeAfterAndUid(LocalDateTime dateTime, String uid);

    List<Value> findAllByDateTimeBetweenAndUid(LocalDateTime startTime, LocalDateTime endTime, String uid);

    List<Value> findAllByUidAndDateTimeBetween(String uid, Date start, Date end);

    List<Value> findAllByUidIn(List<String> uids);

    @Query(value = "SELECT * FROM public.value WHERE uid = ?1 Order By date_time Desc limit 1", nativeQuery = true)
    Value getLastByUid(String uid);
}
