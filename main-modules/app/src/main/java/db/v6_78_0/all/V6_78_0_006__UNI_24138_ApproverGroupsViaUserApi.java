package db.v6_78_0.all;

import org.jetbrains.annotations.NotNull;

import com.unimarket.flyway.AbstractMigration;
import com.unimarket.flyway.MigrationTemplate;
import com.unimarket.flyway.support.AuditHelper;

public class V6_78_0_006__UNI_24138_ApproverGroupsViaUserApi extends AbstractMigration {

    @Override
    protected void doMigrate(@NotNull MigrationTemplate migrationTemplate) throws Exception {
        LOGGER.info("UNI-24138 - Approver groups via user api - migration start");
        LOGGER.info("UNI-24138 - Approver groups via user api - creating new tables and relations");
        //Create the FK column in COMMUNITY_CORE_MODULE table
        migrationTemplate.execute("""
                                          alter table COMMUNITY_CORE_MODULE
                                          add column LEGACY_API_CONFIG_ID bigint;
                                          """);

        //Create the new table COMMUNITY_LEGACY_API_CONFIG with no FK constraints
        migrationTemplate.execute("""
                                          create table COMMUNITY_LEGACY_API_CONFIG (
                                            ID bigint not null auto_increment,
                                            CREATED datetime(6) not null,
                                            APPROVER_GROUPS_IN_USER_API char(1) not null,
                                            CREATOR_OPERATOR_ID bigint,
                                            SITE_ID bigint not null, primary key (ID)
                                          ) engine=InnoDB;
                                          """);

        //Now create the Audit table.
        migrationTemplate.execute("""
                                          create table COMMUNITY_LEGACY_API_CONFIG_AUD (
                                              ID bigint not null,
                                              REV bigint not null,
                                              REVTYPE tinyint,
                                              REVEND bigint,
                                              APPROVER_GROUPS_IN_USER_API char(1),
                                              SITE_ID bigint,
                                              primary key (ID, REV)
                                          ) engine=InnoDB;
                                          """);

        //Set the appropriate foreign keys
        migrationTemplate.execute("""
                                          alter table COMMUNITY_CORE_MODULE
                                          add constraint FK_cpfwydyom52bdgjhy2us870sg foreign key (LEGACY_API_CONFIG_ID)
                                          references COMMUNITY_LEGACY_API_CONFIG (ID);
                                          """);
        migrationTemplate.execute("""
                                          alter table COMMUNITY_LEGACY_API_CONFIG
                                          add constraint FK_9og3f6igm85fejis6437161hd foreign key (CREATOR_OPERATOR_ID)
                                          references OPERATOR (ID);
                                          """);
        migrationTemplate.execute("""
                                          alter table COMMUNITY_LEGACY_API_CONFIG
                                          add constraint FK_qreyers4ueo9t5b711pj2iifx foreign key (SITE_ID)
                                          references SITE (ID);
                                           """);
        migrationTemplate.execute("""
                                          alter table COMMUNITY_LEGACY_API_CONFIG_AUD
                                          add constraint FK_a3ah58pe9eqyitw7xp3xu2om9 foreign key (REV) references AUDIT_REVISION (ID);
                                          """);

        migrationTemplate.execute("""
                                          alter table COMMUNITY_LEGACY_API_CONFIG_AUD
                                          add constraint FK_otrh7qxpff7bts1ykp0iltq4m foreign key (REVEND) references AUDIT_REVISION (ID);
                                          """);






        LOGGER.info("UNI-24138 - Approver groups via user api - populating values in the new tables and column");
        //Insert values into the newly created table
        migrationTemplate.execute("""
                                          insert into COMMUNITY_LEGACY_API_CONFIG
                                          (CREATED, SITE_ID, APPROVER_GROUPS_IN_USER_API)
                                          select now(), ID, 'N' from SITE where TYPE = 'COMMUNITY';
                                          """);
        //Populate the new column in COMMUNITY_CORE_MODULE table
        migrationTemplate.execute("""
                                          update COMMUNITY_CORE_MODULE ccm
                                              inner join MODULE m ON ccm.ID = m.ID
                                              inner join COMMUNITY_LEGACY_API_CONFIG clac ON m.SITE_ID = clac.SITE_ID
                                          SET ccm.LEGACY_API_CONFIG_ID = clac.ID;
                                          """);

        //Now set the not null constraint on the new LEGACY_API_CONFIG_ID column in COMMUNITY_CORE_MODULE table
        migrationTemplate.execute("""
                                          alter table COMMUNITY_CORE_MODULE
                                          modify column LEGACY_API_CONFIG_ID bigint not null;
                                          """);
        //Populate the audit table
        LOGGER.info("UNI-24138 - Approver groups via user api - populate audit table");
        long revisionId = migrationTemplate.insertAuditRevision("UNI-24138 - Approver groups via user api");
        AuditHelper auditHelper = migrationTemplate.createAuditHelperFor("COMMUNITY_LEGACY_API_CONFIG");
        migrationTemplate.forEachRow("select ID from COMMUNITY_LEGACY_API_CONFIG", row ->
            auditHelper.insertAuditRecordForCreate((Long) row.get("ID"), revisionId)
        );

        LOGGER.info("UNI-24138 - Approver groups via user api - end");
    }

}
