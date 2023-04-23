package db.v6_78_0.all;

import org.jetbrains.annotations.NotNull;

import com.unimarket.flyway.AbstractMigration;
import com.unimarket.flyway.MigrationTemplate;

public class V6_78_0_002__UNI_24283_add_uuid_for_accountCodePartPickListOption extends AbstractMigration {

    @Override
    protected void doMigrate(@NotNull MigrationTemplate db) throws Exception {
        LOGGER.info("UNI-24283 add UUID in ACCOUNT_CODE_PICK_LIST_OPTION - start");

        db.execute("alter table ACCOUNT_CODE_PICK_LIST_OPTION add column UUID varchar(36) not null after OPTION_KEY;");
        db.execute("update ACCOUNT_CODE_PICK_LIST_OPTION set UUID = UUID();");
        db.execute("alter table ACCOUNT_CODE_PICK_LIST_OPTION add constraint UK_ol947g0roefek6xrnq6b4107e unique (UUID)");

        LOGGER.info("UNI-24283 add UUID in ACCOUNT_CODE_PICK_LIST_OPTION - end");
    }
}
