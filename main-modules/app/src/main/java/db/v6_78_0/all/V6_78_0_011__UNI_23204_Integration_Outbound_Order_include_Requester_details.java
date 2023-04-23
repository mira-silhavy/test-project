package db.v6_78_0.all;

import org.jetbrains.annotations.NotNull;

import com.unimarket.flyway.AbstractMigration;
import com.unimarket.flyway.MigrationTemplate;

/**
 * @author Alpesh Vyas
 */
public class V6_78_0_011__UNI_23204_Integration_Outbound_Order_include_Requester_details extends AbstractMigration {
    @Override
    protected void doMigrate(@NotNull MigrationTemplate migrationTemplate) throws Exception {
        LOGGER.info("UNI-23204 Integration - Outbound Order integration include Requester details - start");
        migrationTemplate.execute("alter table ORDER_INTEGRATION_POINT add column INCLUDE_REQUESTER_DETAILS char(1) not null after ON_ACCOUNT_ONLY;");
        migrationTemplate.execute("update ORDER_INTEGRATION_POINT set INCLUDE_REQUESTER_DETAILS = 'N';");
        migrationTemplate.execute("alter table ORDER_INTEGRATION_POINT_AUD add column INCLUDE_REQUESTER_DETAILS char(1) after ON_ACCOUNT_ONLY;");
        LOGGER.info("UNI-23204 Integration - Outbound Order integration include Requester details - end");
    }
}
