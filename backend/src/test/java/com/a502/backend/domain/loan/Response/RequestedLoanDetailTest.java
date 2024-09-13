package com.a502.backend.domain.loan.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestedLoanDetailTest {

    @Test
    @DisplayName("RequestedLoanDetail 객체 생성 테스트")
    void testRequestedLoanDetailCreation() {
        // Given
        String childName = "Jane Doe";
        String reason = "Student Loan";
        String loanUuid = "123e4567-e89b-12d3-a456-426614174000";
        int amount = 20000;
        int paymentDate = 15;
        String penalty = "5% Late Fee";
        int paymentTotalCnt = 10;
        String[] chatBotConversation = {"Hello", "Loan request"};

        // When
        RequestedLoanDetail loanDetail = RequestedLoanDetail.builder()
                .childName(childName)
                .reason(reason)
                .loanUuid(loanUuid)
                .amount(amount)
                .paymentDate(paymentDate)
                .penalty(penalty)
                .paymentTotalCnt(paymentTotalCnt)
                .chatBotConversation(chatBotConversation)
                .build();

        // Then
        assertNotNull(loanDetail);
        assertEquals(childName, loanDetail.getChildName());
        assertEquals(reason, loanDetail.getReason());
        assertEquals(loanUuid, loanDetail.getLoanUuid());
        assertEquals(amount, loanDetail.getAmount());
        assertEquals(paymentDate, loanDetail.getPaymentDate());
        assertEquals(penalty, loanDetail.getPenalty());
        assertEquals(paymentTotalCnt, loanDetail.getPaymentTotalCnt());
        assertEquals(chatBotConversation, loanDetail.getChatBotConversation());
    }
}