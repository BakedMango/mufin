package com.a502.backend.domain.loan.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class LoanDetailForParentsTest {

    @Test
    @DisplayName("LoanDetailForParents 객체 생성 테스트")
    void testLoanDetailForParentsCreation() {
        // Given
        String childName = "John Doe";
        String reason = "Education Loan";
        int amount = 10000;
        int paymentDate = 15;
        String penalty = "5% Late Fee";
        int paymentTotalCnt = 12;
        int paymentNowCnt = 3;
        String statusCode = "ACTIVE";
        int overdueCnt = 1;
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 1);

        // When
        LoanDetailForParents loanDetail = LoanDetailForParents.builder()
                .childName(childName)
                .reason(reason)
                .amount(amount)
                .paymentDate(paymentDate)
                .penalty(penalty)
                .paymentTotalCnt(paymentTotalCnt)
                .paymentNowCnt(paymentNowCnt)
                .statusCode(statusCode)
                .overdueCnt(overdueCnt)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        // Then
        assertNotNull(loanDetail);
        assertEquals(childName, loanDetail.getChildName());
        assertEquals(reason, loanDetail.getReason());
        assertEquals(amount, loanDetail.getAmount());
        assertEquals(paymentDate, loanDetail.getPaymentDate());
        assertEquals(penalty, loanDetail.getPenalty());
        assertEquals(paymentTotalCnt, loanDetail.getPaymentTotalCnt());
        assertEquals(paymentNowCnt, loanDetail.getPaymentNowCnt());
        assertEquals(statusCode, loanDetail.getStatusCode());
        assertEquals(overdueCnt, loanDetail.getOverdueCnt());
        assertEquals(startDate, loanDetail.getStartDate());
        assertEquals(endDate, loanDetail.getEndDate());
    }
}