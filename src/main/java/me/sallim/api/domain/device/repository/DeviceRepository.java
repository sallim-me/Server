package me.sallim.api.domain.device.repository;

import me.sallim.api.domain.device.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, Long> {
    Optional<Device> findFirstByModelNameOrderByIdDesc(String modelName);
}
