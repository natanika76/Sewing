package ru.vilas.sewing.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;

@Service
public class ScheduledTaskService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledTaskService.class);

    private final DatabaseDumpAndSend databaseDumpAndSend;

    public ScheduledTaskService(DatabaseDumpAndSend databaseDumpAndSend) {
        this.databaseDumpAndSend = databaseDumpAndSend;
    }

    @Scheduled(cron = "${task.cron.expression}", zone = "Europe/Moscow")
    public void performScheduledTask() throws Exception {
        LOGGER.info("Scheduled task started.");
        DatabaseDumpAndSend.main(null);
        LOGGER.info("Scheduled task completed.");
    }
}
