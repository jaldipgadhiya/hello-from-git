----23 Rows to be updated-----
UPDATE azbj_system_constants
SET start_date = '31-mar-2022',
    end_date = '01-apr-2022'
WHERE  sys_type = 'PARTNER' AND sys_code = 'DUAL_DATE_FLAG'
       AND char_value <> '0435141614' AND sys_desc = 'DUAL_DATE_ACTIVE_FLAG';