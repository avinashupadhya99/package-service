package com.production.packager.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.production.packager.repository.ProduceRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PackageService {
    @Autowired
    ProduceRepository produceRepository;

    private static final Logger logger = LoggerFactory.getLogger(PackageService.class);

    @Scheduled(cron = "0 0 * * * *")
    public void cronJobSch() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date now = new Date();
        String strDate = sdf.format(now);
        logger.info("Cron job completed at {}", strDate);
    }
}
