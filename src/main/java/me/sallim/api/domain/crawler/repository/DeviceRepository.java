package me.sallim.api.domain.crawler.repository;

import me.sallim.api.domain.crawler.domain.Device;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<Device, Long> {
}

