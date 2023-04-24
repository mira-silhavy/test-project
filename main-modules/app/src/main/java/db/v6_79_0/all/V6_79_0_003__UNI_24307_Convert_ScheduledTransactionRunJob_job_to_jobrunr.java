package main.java.db.v6_79_0.all;

import com.unimarket.flyway.AbstractMigration;
import com.unimarket.flyway.MigrationTemplate;
import org.jetbrains.annotations.NotNull;

public class V6_79_0_003__UNI_24307_Convert_ScheduledTransactionRunJob_job_to_jobrunr extends AbstractMigration {

    @Override
    protected void doMigrate(@NotNull MigrationTemplate template) throws Exception {
        LOGGER.info("UNI-24307 Convert ScheduledTransactionRunJob to Jobrunr - start");

        template.execute("delete from QRTZ_CRON_TRIGGERS where TRIGGER_GROUP = 'ScheduledTransactionRunJob';");
        template.execute("delete from QRTZ_TRIGGERS where JOB_NAME = 'ScheduledTransactionRunJob';");
        template.execute("delete from QRTZ_JOB_DETAILS where JOB_NAME = 'ScheduledTransactionRunJob';");

        long jobId = template.insert("INSERT INTO MIGRATION_JOB (CREATED, BATCH_SIZE, COMPLETION_REQUIRED, ENTITY_CLASS, IGNORE_MISSING_RECORDS, MIGRATE_ORDER, NAME, STATE) " +
                                             "VALUES (now(),1,'N','com.unimarket.transaction.domain.ScheduledTransactionRunImpl', 'N', 10, 'UNI-24307 Convert ScheduledTransactionRunJob to Jobrunr', 'WAITING');");
        int entityCount = template.update("INSERT INTO MIGRATION_ENTITY (CREATED, TARGET_ID, JOB_ID) " +
                                                  "SELECT now(), ID, ? from SCHEDULED_TRANSACTION_RUN where DELETED_DATE is null order by ID", jobId);

        LOGGER.info("UNI-24307 Created an in-app migrate for {} ScheduledTransactionRunImpl jobs", entityCount);
        LOGGER.info("UNI-24307 Convert ScheduledTransactionRunJob to Jobrunr - finish");
    }

}