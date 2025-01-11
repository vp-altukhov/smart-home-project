package ru.newvasuki.smarthome.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.newvasuki.smarthome.data.entity.DeviceValue;
import ru.newvasuki.smarthome.data.entity.Expression;
import ru.newvasuki.smarthome.data.entity.Profile;

import java.util.List;

public interface ExpressionRepository extends JpaRepository<Expression, Integer> {
    Expression findByDeviceValueAndProfileAndOperation(DeviceValue deviceValue, Profile profile, Integer operation);

    List<Expression> findAllByProfile(Profile profile);
}
