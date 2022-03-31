SET SERVEROUTPUT ON;

DECLARE
   v_cd_bal                  NUMBER;
   v_cd_insert               NUMBER;
   v_date                    DATE   := azbj_weo_cashier.get_cashier_date;
   v_bg_issuance_amt_group   NUMBER;
   v_bg_issuance_amt_opus    NUMBER;
   v_master_recpt_amount     NUMBER;
   v_rec_end_date            DATE;
   v_adj_acc_balance         NUMBER := 0;
   v_rec_start_date          DATE := '18-jun-2019';
BEGIN
   
   FOR i IN (SELECT mph_no
             FROM   azbj_cd_balance) LOOP
      /*BEGIN
         SELECT start_date, NVL (end_date, pme_api.opus_date)
         INTO   v_rec_start_date, v_rec_end_date
         FROM   azbj_system_constants
         WHERE  char_value = i.mph_no AND sys_type = 'GROUP'
                AND sys_code = 'GTL_MASTER_BALANCE';
      EXCEPTION
         WHEN NO_DATA_FOUND THEN
            SELECT start_date, NVL (end_date, pme_api.opus_date)
            INTO   v_rec_start_date, v_rec_end_date
            FROM   azbj_system_constants
            WHERE  char_value IS NULL AND sys_type = 'GROUP'
                   AND sys_code = 'GTL_MASTER_BALANCE';
      END;*/

      SELECT NVL (SUM (amount), 0)
      INTO   v_master_recpt_amount
      FROM   azbj_batch_items a, azbj_cashier_coll b
      WHERE  policy_no = i.mph_no AND NVL (PRINT, 'X') <> 'C'
             AND a.cashier_batch_id = b.cashier_batch_id
             AND a.collection_no = b.collection_no
             AND a.transaction_type = 'FRP' AND b.pay_mode <> 'GTR'
             AND a.frp_receipt_no IS NULL
             AND perm_receipt_date BETWEEN v_rec_start_date AND v_date - 1;

      /*v_adj_acc_balance :=
              -1 * NVL (azbj_grp_accounting_pkg.get_mph_balance (i.mph_no), 0);*/
      v_cd_bal := 11398359.64;

      SELECT COUNT (*)
      INTO   v_cd_insert
      FROM   azbj_cd_balance
      WHERE  mph_no = i.mph_no;

      IF v_cd_insert = 0 THEN
         INSERT INTO azbj_cd_balance
                     (cd_balance, master_receipt_amt, mph_no, updated_date
                     )
         VALUES      (v_cd_bal, v_master_recpt_amount, i.mph_no, v_date
                     );
      ELSE
         UPDATE azbj_cd_balance
         SET cd_balance = v_cd_bal,
             updated_date = v_date,
             master_receipt_amt = v_master_recpt_amount
         WHERE  mph_no = i.mph_no;
      END IF;
   END LOOP;

   COMMIT;
EXCEPTION
   WHEN OTHERS THEN
      DBMS_OUTPUT.put_line ('Error' || SQLERRM
                            || DBMS_UTILITY.format_error_backtrace);
END;