package ru.vilas.sewing.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vilas.sewing.service.DatabaseBackupService;

import java.io.File;

@RestController
public class DatabaseBackupController {

    @Autowired
    private DatabaseBackupService backupService;

    private static final String BACKUP_DIR = "/var/backups";

    @GetMapping("/backup")
    public ResponseEntity<?> createBackup() {
        backupService.createDatabaseDump("sewing_db", "sewing", BACKUP_DIR);
        return ResponseEntity.ok("Backup initiated successfully.");
    }

    @GetMapping("/backup/download")
    public ResponseEntity<Resource> downloadLastBackup() {
        File lastBackup = backupService.getLastBackupFile(BACKUP_DIR);
        if (lastBackup == null) {
            return ResponseEntity.notFound().build();
        }
        Resource fileSystemResource = new FileSystemResource(lastBackup);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + lastBackup.getName() + "\"")
                .body(fileSystemResource);
    }
}
