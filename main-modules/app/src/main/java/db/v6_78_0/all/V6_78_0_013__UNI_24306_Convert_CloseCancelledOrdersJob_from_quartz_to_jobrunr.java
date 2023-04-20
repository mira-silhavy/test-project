package db.v6_78_0.all;

import org.jetbrains.annotations.NotNull;

import com.unimarket.flyway.AbstractMigration;
import com.unimarket.flyway.MigrationTemplate;

public class V6_78_0_013__UNI_24306_Convert_CloseCancelledOrdersJob_from_quartz_to_jobrunr extends AbstractMigration {

    @Override
    protected void doMigrate(@NotNull MigrationTemplate template) throws Exception {
        LOGGER.info("UNI-24306 Convert CloseCancelledOrdersJob from quartz to jobrunr - start");

        template.execute("delete from QRTZ_TRIGGERS where TRIGGER_NAME = 'CloseCancelledOrdersJob';");
        template.execute("delete from QRTZ_JOB_DETAILS where JOB_NAME = 'CloseCancelledOrdersJob';");

        LOGGER.info("UNI-24306 Convert CloseCancelledOrdersJob from quartz to jobrunr - finish");
    }

}
