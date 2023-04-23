package db.v6_79_0.all;

import org.jetbrains.annotations.NotNull;

import com.unimarket.flyway.AbstractMigration;
import com.unimarket.flyway.MigrationTemplate;

public class V6_79_0_001__UNI_24279_Convert_SendTaskDigests_job_from_quartz_to_jobrunr extends AbstractMigration {

    @Override
    protected void doMigrate(@NotNull MigrationTemplate template) throws Exception {
        LOGGER.info("UNI-24279 Convert SendTaskDigest job from Quartz to JobRunr - start");
        template.execute("delete from QRTZ_CRON_TRIGGERS where TRIGGER_GROUP = 'SendTaskDigestJob';");
        template.execute("delete from QRTZ_TRIGGERS where JOB_NAME = 'SendTaskDigestJob';");
        template.execute("delete from QRTZ_JOB_DETAILS where JOB_NAME = 'SendTaskDigestJob';");

        Long jobId = template.insert("INSERT INTO MIGRATION_JOB (CREATED, BATCH_SIZE, COMPLETION_REQUIRED, ENTITY_CLASS, IGNORE_MISSING_RECORDS, MIGRATE_ORDER, NAME, STATE) " +
                                             "VALUES (now(),1,'N','com.unimarket.core.domain.task.TaskManagerImpl', 'N', 10, 'UNI-24279 Convert SendTaskDigest job from Quartz to JobRunr', 'WAITING');");
        int numMigrates = template.update("INSERT INTO MIGRATION_ENTITY (CREATED, TARGET_ID, JOB_ID) " +
                                                  "SELECT now(), ID, ? from TASK_MANAGER where DIGEST_ENABLED = 'Y' order by ID", jobId);

        LOGGER.info("UNI-24279 Created an in-app migrate for {} jobs", numMigrates);
        LOGGER.info("UNI-24279 Convert SendTaskDigest job from Quartz to JobRunr - finish");
    }

}
