SET SERVEROUTPUT ON;

DECLARE
   v_dummy               NUMBER;
   p_contract_id         NUMBER;
   p_proposal_no         VARCHAR2 (30);
   v_activity            NUMBER;
   v_rcp_no              VARCHAR2 (30);
   v_scheme_type         azbj_master_policy_contract.scheme_type%TYPE;
   v_policy_group_type   azbj_master_policy_contract.policy_group_type%TYPE;
   v_mph_no              VARCHAR2 (50);
   v_issuance_user       VARCHAR2 (50);
   v_cnt                 NUMBER:=0;
   p_appln_no            VARCHAR2 (50);   
    BEGIN
       FOR i IN (SELECT NVL (TO_CHAR (a.verification_no),
                             a.sign_card_no) application_no,
                        TO_CHAR (b.contract_id) contract_id,
                        c.policy_ref policy_ref
                 FROM   wip_azbj_policy_bases_ext a, wip_policy_versions b,
                        wip_policy_bases c
                 WHERE  a.contract_id = b.contract_id
                        AND b.contract_id = c.contract_id
                        and sign_card_no in(select appln_no from it_250282.iifl_wip_failed_reissue_280322)
                        --AND b.change_description = 'PROP_MONTH_END'
                        AND NOT EXISTS (SELECT 1
                                        FROM   ocp_policy_bases d
                                        WHERE  c.policy_ref = d.policy_ref)) 
       LOOP
          BEGIN
             p_contract_id := i.contract_id;
             p_proposal_no := i.policy_ref;
             p_appln_no := i.application_no;
             DBMS_OUTPUT.put_line ('Contract_Id -' || p_contract_id
                                   || '~~p_proposal_no-' || p_proposal_no
                                   || '~~p_appln_no-'||p_appln_no);

             BEGIN
                SELECT master_policy_no
                INTO   v_mph_no
                FROM   azbj_group_policy_ext
                WHERE  contract_id = to_char(p_contract_id);
             EXCEPTION
                WHEN OTHERS THEN
                   v_mph_no := NULL;
             END;

             BEGIN
                SELECT scheme_type, policy_group_type
                INTO   v_scheme_type, v_policy_group_type
                FROM   azbj_master_policy_contract
                WHERE  master_policy_no = v_mph_no;
             EXCEPTION
                WHEN OTHERS THEN
                   v_scheme_type := NULL;
                   v_policy_group_type := NULL;
             END;

             IF v_scheme_type = 'GTLN' AND v_policy_group_type = 'NE' THEN
                BEGIN
                   SELECT char_value
                   INTO   v_issuance_user
                   FROM   azbj_system_constants
                   WHERE  sys_type = 'GROUP' AND sys_code = 'BAFL_ISSUANCE_USER'
                          AND pme_api.opus_date BETWEEN start_date
                                                    AND NVL (end_date,
                                                             '01-JAN-3000');
                EXCEPTION
                   WHEN OTHERS THEN
                      v_issuance_user := NULL;
                END;

                SELECT COUNT (1)
                INTO   v_cnt
                FROM   azbj_auto_issuance_grp
                WHERE  application_no = p_appln_no;

                IF v_cnt = 0 THEN
                   INSERT INTO azbj_auto_issuance_grp
                               (contract_id, policy_ref, user_id,
                                create_date, schedule_flag, application_no,
                                bbu_type, manual_bbu_reason,
                                schedule_execute_date
                               )
                   VALUES      (p_contract_id, p_proposal_no, v_issuance_user,
                                SYSDATE, 'Y', p_appln_no,
                                'AUTO', NULL,
                                SYSDATE
                               );
                END IF;
             END IF;

             UPDATE wip_policy_versions
             SET change_description = 'ISSUED',
                 username = 'P00OU999'
             WHERE  contract_id = p_contract_id;

             --41599496
             UPDATE wip_azbj_policy_contract_ext
             SET appln_sign_date = pme_api.opus_date
             WHERE  contract_id = p_contract_id;

             DBMS_OUTPUT.put_line ('Contract_Id -' || p_contract_id
                                   || 'Before azbj_hub_status_tracker');
             azbj_pk0_hub_metapara.azbj_hub_status_tracker
                                             (p_proposal_no, p_appln_no,
                                              NULL, 'ISSUED', 'G-Auto BBU',
                                              TO_DATE (SYSDATE,
                                                       'dd/mon/rrrr hh12:mi:ss am'));
             DBMS_OUTPUT.put_line ('Contract_Id -' || p_contract_id
                                   || 'After azbj_hub_status_tracker');
             DBMS_OUTPUT.put_line ('Contract_Id -' || p_contract_id
                                   || 'Before pme_public.wip_restore_sync');
             v_dummy := pme_public.wip_restore_sync (p_contract_id);
             DBMS_OUTPUT.put_line ('Contract_Id -' || p_contract_id || '~~'
                                   || 'v_dummy-' || v_dummy
                                   || '~~After pme_public.wip_restore_sync');

             IF v_dummy = 0 THEN
                UPDATE azbj_auto_issuance_grp
                SET manual_bbu_reason = 'PROP_MONTH_END_RESTORE_SYNC_SUCCESS'
                WHERE  contract_id = p_contract_id;

                ---changed sobin
                UPDATE azbj_grp_auto_issuance_log
                SET log_text = 'ISSUED'
                WHERE  appln_no = TO_NUMBER (p_appln_no) AND log_type = 'S';

                UPDATE azbj_phub_tracker
                SET proposal_status = 'ISSUED'
                WHERE  application_no = p_appln_no;

                ---changed sobin ends here
                COMMIT;

                BEGIN
                   DBMS_OUTPUT.put_line
                               ('Contract_Id -' || p_contract_id
                                || '~~Before azbj_pk_pol_mul_batches.tr_run_mul_bat');

                   IF (v_scheme_type = 'GTLN' AND v_policy_group_type = 'NE')
                      OR v_scheme_type = 'GCPL' THEN
                      azbj_pk_pol_mul_batches.tr_run_mul_bat (p_contract_id);
                   END IF;

                   DBMS_OUTPUT.put_line
                                 ('Contract_Id -' || p_contract_id
                                  || '~~After azbj_pk_pol_mul_batches.tr_run_mul_bat');
                EXCEPTION
                   WHEN OTHERS THEN
                      NULL;
                      DBMS_OUTPUT.put_line
                            ('Contract_Id -' || p_contract_id
                             || '~~Error azbj_pk_pol_mul_batches.tr_run_mul_bat-'
                             || SQLERRM);
                END;

                BEGIN
                   SELECT MAX (NVL (pol_activity_no, 0)) + 1
                   INTO   v_activity
                   FROM   azbj_pol_activity_log
                   WHERE  policy_ref = p_proposal_no;
                EXCEPTION
                   WHEN OTHERS THEN
                      v_activity := 1;
                END;

                DBMS_OUTPUT.put_line ('Contract_Id -' || p_contract_id
                                      || '~~Before azbj_pol_activity_log');

                BEGIN
                   INSERT INTO azbj_pol_activity_log
                               (activity_seq, effective_date,
                                activity_date, username, contract_id,
                                perm_rcpt_no, pol_activity_no, policy_ref,
                                event_code, event_desc, comments,
                                doc_link, request_date
                               )
                   VALUES      (azbj_policy_log_seq.NEXTVAL, pme_api.opus_date,
                                pme_api.opus_date, 'G-Auto BBU', p_contract_id,
                                NULL, v_activity, p_proposal_no,
                                'ISSUED', 'ISSUED', 'ISSUED_WIP_RESTORE_SYNC_FAILED',
                                NULL, pme_api.opus_date
                               );
                END;
             ELSE
                DBMS_OUTPUT.put_line ('Contract_Id -' || p_contract_id
                                      || '~~v_dummy <> 0');

                UPDATE azbj_auto_issuance_grp
                SET manual_bbu_reason = 'WIP_RESTORE_SYNC_RETRY_FAILED'
                WHERE  contract_id = p_contract_id;

                /*---sobin
                UPDATE wip_policy_versions
                SET change_description = 'PROP_MONTH_END'
                WHERE  contract_id = p_contract_id;

                ---sobin  ends here*/
                COMMIT;
             END IF;

             DBMS_OUTPUT.put_line ('Contract_Id -' || p_contract_id
                                   || '~~After azbj_pol_activity_log');
          EXCEPTION
             WHEN OTHERS THEN
                ROLLBACK;
                DBMS_OUTPUT.put_line ('Inside Loop Error-' || SQLERRM
                                      || DBMS_UTILITY.format_error_backtrace);

                UPDATE azbj_auto_issuance_grp
                SET manual_bbu_reason = 'WIP_RESTORE_SYNC_RETRY_SQLERRM'
                WHERE  contract_id = p_contract_id;

                COMMIT;
          END;
       END LOOP;
    EXCEPTION
    WHEN OTHERS THEN
      DBMS_OUTPUT.put_line ('Main Error-' || SQLERRM
                            || DBMS_UTILITY.format_error_backtrace);
    END;