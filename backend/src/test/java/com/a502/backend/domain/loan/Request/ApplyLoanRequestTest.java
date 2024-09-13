package com.a502.backend.domain.loan.Request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ApplyLoanRequestTest {

    @Test
    @DisplayName("ApplyLoanRequest 객체 생성 테스트")
    void testApplyLoanRequestCreation() {
        // Given
        String reason = "Personal Reason";
        int amount = 10000;
        int paymentTotalCnt = 12;
        int paymentDate = 15;
        String conversation = "Loan Agreement Conversation";
        String penalty = "5% Late Fee";

        // When
        ApplyLoanRequest request = ApplyLoanRequest.builder()
                .reason(reason)
                .amount(amount)
                .paymentTotalCnt(paymentTotalCnt)
                .paymentDate(paymentDate)
                .conversation(conversation)
                .penalty(penalty)
                .build();

        // Then
        assertNotNull(request);
        assertEquals(reason, request.getReason());
        assertEquals(amount, request.getAmount());
        assertEquals(paymentTotalCnt, request.getPaymentTotalCnt());
        assertEquals(paymentDate, request.getPaymentDate());
        assertEquals(conversation, request.getConversation());
        assertEquals(penalty, request.getPenalty());
    }
}