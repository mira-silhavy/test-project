package db.v6_79_0.all;

import org.jetbrains.annotations.NotNull;

import com.unimarket.flyway.AbstractMigration;
import com.unimarket.flyway.MigrationTemplate;

public class V6_79_0_002__UNI_24309_Convert_ScheduledReports_job_from_quartz_to_jobrunr extends AbstractMigration {

    @Override
    protected void doMigrate(@NotNull MigrationTemplate template) throws Exception {
        LOGGER.info("UNI-24309 Convert ScheduledReportJob to Jobrunr - start");
        template.execute("delete from QRTZ_CRON_TRIGGERS where TRIGGER_GROUP = 'ScheduledReportJob';");
        template.execute("delete from QRTZ_TRIGGERS where JOB_NAME = 'ScheduledReportJob';");
        template.execute("delete from QRTZ_JOB_DETAILS where JOB_NAME = 'ScheduledReportJob';");

        long jobId = template.insert("INSERT INTO MIGRATION_JOB (CREATED, BATCH_SIZE, COMPLETION_REQUIRED, ENTITY_CLASS, IGNORE_MISSING_RECORDS, MIGRATE_ORDER, NAME, STATE) " +
                                             "VALUES (now(),1,'N','com.unimarket.reporting.domain.ScheduledExportImpl', 'N', 10, 'UNI-24309 Convert ScheduledReportJob to Jobrunr', 'WAITING');");
        int numExportMigrates = template.update("INSERT INTO MIGRATION_ENTITY (CREATED, TARGET_ID, JOB_ID) " +
                                                        "SELECT now(), ID, ? from SCHEDULED_REPORT where TYPE = 'EXPORT' and DELETED_DATE is null order by ID", jobId);

        jobId = template.insert("INSERT INTO MIGRATION_JOB (CREATED, BATCH_SIZE, COMPLETION_REQUIRED, ENTITY_CLASS, IGNORE_MISSING_RECORDS, MIGRATE_ORDER, NAME, STATE) " +
                                        "VALUES (now(),1,'N','com.unimarket.reporting.domain.ScheduledReportImpl', 'N', 10, 'UNI-24309 Convert ScheduledReportJob to Jobrunr', 'WAITING');");
        int numReportMigrates = template.update("INSERT INTO MIGRATION_ENTITY (CREATED, TARGET_ID, JOB_ID) " +
                                                        "SELECT now(), ID, ? from SCHEDULED_REPORT where TYPE = 'REPORT' and DELETED_DATE is null order by ID", jobId);

        LOGGER.info("UNI-24309 Created an in-app migrate for {} jobs", numExportMigrates + numReportMigrates);
        LOGGER.info("UNI-24309 Convert ScheduledReportJob to Jobrunr - finish");
    }

}
