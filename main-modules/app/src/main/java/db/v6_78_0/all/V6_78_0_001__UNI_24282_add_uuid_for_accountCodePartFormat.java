package db.v6_78_0.all;

import org.jetbrains.annotations.NotNull;

import com.unimarket.flyway.AbstractMigration;
import com.unimarket.flyway.MigrationTemplate;

public class V6_78_0_001__UNI_24282_add_uuid_for_accountCodePartFormat extends AbstractMigration {

    @Override
    protected void doMigrate(@NotNull MigrationTemplate db) throws Exception {
        LOGGER.info("UNI-24282 add UUID in ACCOUNT_CODE_PART_FORMAT - start");

        db.execute("alter table ACCOUNT_CODE_PART_FORMAT add column UUID varchar(36) not null after SEQ;");
        db.execute("update ACCOUNT_CODE_PART_FORMAT set UUID = UUID();");
        db.execute("alter table ACCOUNT_CODE_PART_FORMAT add constraint UK_hmccjkr8u7dsd06bqdkh34fxx unique (UUID)");

        LOGGER.info("UNI-24282 add UUID in ACCOUNT_CODE_PART_FORMAT - end");
    }
}
