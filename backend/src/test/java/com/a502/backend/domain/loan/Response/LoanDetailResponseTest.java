package com.a502.backend.domain.loan.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class LoanDetailResponseTest {

    @Test
    @DisplayName("LoanDetailResponse 객체 생성 테스트")
    void testLoanDetailResponseCreation() {
        // Given
        String reason = "Car Loan";
        int totalAmount = 20000;
        int remainderAmount = 15000;
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        String remainderDay = "30 days";
        int paymentDate = 10;
        int paymentAmount = 500;
        int overDueCnt = 2;

        // When
        LoanDetailResponse response = LoanDetailResponse.builder()
                .reason(reason)
                .totalAmount(totalAmount)
                .remainderAmount(remainderAmount)
                .startDate(startDate)
                .remainderDay(remainderDay)
                .paymentDate(paymentDate)
                .paymentAmount(paymentAmount)
                .overDueCnt(overDueCnt)
                .build();

        // Then
        assertNotNull(response);
        assertEquals(reason, response.getReason());
        assertEquals(totalAmount, response.getTotalAmount());
        assertEquals(remainderAmount, response.getRemainderAmount());
        assertEquals(startDate, response.getStartDate());
        assertEquals(remainderDay, response.getRemainderDay());
        assertEquals(paymentDate, response.getPaymentDate());
        assertEquals(paymentAmount, response.getPaymentAmount());
        assertEquals(overDueCnt, response.getOverDueCnt());
    }
}