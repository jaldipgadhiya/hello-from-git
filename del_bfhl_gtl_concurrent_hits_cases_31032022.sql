---8 rows to be deleted-----
DELETE FROM azbj_tdmc_upload_detail
WHERE       memership_no IN
               ('RF01MGLI03248506', 'RF01MGLI03256210', 'RF01MGLI03258263', 'RF01MGLI03250796')
            AND mph_no = '0435141614' AND policy_ref IS NULL;


---4 rows to be deleted-----
DELETE FROM azbj_grp_upload_detail
WHERE       member_code IN
               ('RF01MGLI03248506', 'RF01MGLI03256210', 'RF01MGLI03258263', 'RF01MGLI03250796')
            AND master_policy_no = '0435141614'
            AND record_status NOT IN
                                ('PROCESSED', 'ACC PASSED', 'ACC NOT PASSED');