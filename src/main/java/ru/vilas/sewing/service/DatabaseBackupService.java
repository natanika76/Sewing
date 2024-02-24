package ru.vilas.sewing.service;

import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Comparator;

@Service
public class DatabaseBackupService {

    public void createDatabaseDump(String containerName, String dbName, String backupDir) {
        try {
            String backupFileName = backupDir + "/" + dbName + "_backup_" + System.currentTimeMillis() + ".sql";
            String dumpCommand = "docker exec " + containerName + " pg_dump -U postgres " + dbName + " -f " + backupFileName;

            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("bash", "-c", dumpCommand);
            Process process = processBuilder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            int exitVal = process.waitFor();
            if (exitVal == 0) {
                System.out.println("Backup created successfully.");
            } else {
                System.out.println("Error occurred in backup creation.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File getLastBackupFile(String backupDir) {
        File dir = new File(backupDir);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".sql"));
        if (files == null || files.length == 0) {
            return null;
        }
        Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
        return files[0];
    }
}
