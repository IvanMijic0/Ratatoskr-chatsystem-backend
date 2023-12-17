package ba.nosite.chatsystem.core.scheduled;

import ba.nosite.chatsystem.core.services.NotificationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DataMigrationTask {
    private final NotificationService notificationService;

    public DataMigrationTask(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
    public void migrateExpiredData() {
        notificationService.migrateNotificationsToMongoDb();
    }
}