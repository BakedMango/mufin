package com.a502.backend.domain.loan.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

class LoanListTest {

    @Test
    @DisplayName("LoanList 객체 생성 및 업데이트 테스트")
    void testLoanListCreationAndUpdate() {
        // Given
        String reason = "Home Renovation";
        String loanUuid = "123e4567-e89b-12d3-a456-426614174000";
        int amount = 50000;
        int paymentTotalCnt = 24;
        int paymentNowCnt = 12;
        int remainderAmount = 25000;
        String status = "ACTIVE";
        int overDueCnt = 3;
        String loanRefusalReason = "Low Credit Score";

        // When
        LoanList loanList = LoanList.builder()
                .reason(reason)
                .loanUuid(loanUuid)
                .amount(amount)
                .paymentTotalCnt(paymentTotalCnt)
                .paymentNowCnt(paymentNowCnt)
                .remainderAmount(remainderAmount)
                .status(status)
                .overDueCnt(overDueCnt)
                .loanRefusalReason(loanRefusalReason)
                .build();

        // Then
        assertNotNull(loanList);
        assertEquals(reason, loanList.getReason());
        assertEquals(loanUuid, loanList.getLoanUuid());
        assertEquals(amount, loanList.getAmount());
        assertEquals(paymentTotalCnt, loanList.getPaymentTotalCnt());
        assertEquals(paymentNowCnt, loanList.getPaymentNowCnt());
        assertEquals(remainderAmount, loanList.getRemainderAmount());
        assertEquals(status, loanList.getStatus());
        assertEquals(overDueCnt, loanList.getOverDueCnt());
        assertEquals(loanRefusalReason, loanList.getLoanRefusalReason());

        // Update Test
        String newLoanRefusalReason = "Policy Change";
        loanList.updateLoanDetail(newLoanRefusalReason);
        assertEquals(newLoanRefusalReason, loanList.getLoanRefusalReason());
    }
}