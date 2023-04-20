package db.v6_78_0.all;

import org.jetbrains.annotations.NotNull;

import com.unimarket.flyway.AbstractMigration;
import com.unimarket.flyway.MigrationTemplate;

public class V6_78_0_003__UNI_24359_drop_jobrunr_jobmap extends AbstractMigration {

    @Override
    protected void doMigrate(@NotNull MigrationTemplate db) throws Exception {
        LOGGER.info("UNI-24359 Remove jobrunr job map - start");

        db.renameAsLegacyTable("WORK_ORDER_JOBRUNR_JOB");

        Long jobId = db.insert("INSERT INTO MIGRATION_JOB (CREATED, BATCH_SIZE, COMPLETION_REQUIRED, ENTITY_CLASS, IGNORE_MISSING_RECORDS, MIGRATE_ORDER, NAME, STATE) " +
                                       "VALUES (now(),500,'N','com.unimarket.workorder.domain.WorkOrderImpl', 'N', 10, 'UNI-24359 remove jobrunrjobmap', 'WAITING');");
        int numMigrates = db.update("INSERT INTO MIGRATION_ENTITY (CREATED, TARGET_ID, JOB_ID) " +
                                            "SELECT now(), ID, ? from WORK_ORDER where STATE in ('PENDING', 'ACTIVE') order by ID desc", jobId);

        LOGGER.info("UNI-24359 Created an in-app migrate for {} jobs", numMigrates);

        LOGGER.info("UNI-24359 Remove jobrunr job map - end");
    }
}
