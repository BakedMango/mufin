package com.a502.backend.domain.loan.Request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RepayLoanRequestTest {

    @Test
    @DisplayName("RepayLoanRequest 객체 생성 테스트")
    void testRepayLoanRequestCreation() {
        // Given
        String loanUuid = "123e4567-e89b-12d3-a456-426614174000";
        int paymentCnt = 5;

        // When
        RepayLoanRequest request = RepayLoanRequest.builder()
                .loanUuid(loanUuid)
                .payment_cnt(paymentCnt)
                .build();

        // Then
        assertNotNull(request);
        assertEquals(loanUuid, request.getLoanUuid());
        assertEquals(paymentCnt, request.getPayment_cnt());
    }
}