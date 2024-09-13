package com.a502.backend.domain.loan.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

class AllLoansListForParentResponseTest {

    @Test
    @DisplayName("AllLoansListForParentResponse 객체 생성 테스트")
    void testAllLoansListForParentResponseCreation() {
        // Given
        LoanDetailForParents loan1 = LoanDetailForParents.builder()
                .childName("John Doe")
                .reason("Education Loan")
                .amount(10000)
                .paymentDate(15)
                .penalty("5% Late Fee")
                .paymentTotalCnt(12)
                .paymentNowCnt(3)
                .statusCode("ACTIVE")
                .overdueCnt(1)
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2025, 1, 1))
                .build();

        LoanDetailForParents loan2 = LoanDetailForParents.builder()
                .childName("Jane Doe")
                .reason("Medical Loan")
                .amount(15000)
                .paymentDate(20)
                .penalty("3% Late Fee")
                .paymentTotalCnt(10)
                .paymentNowCnt(5)
                .statusCode("COMPLETED")
                .overdueCnt(0)
                .startDate(LocalDate.of(2023, 6, 1))
                .endDate(LocalDate.of(2024, 6, 1))
                .build();

        List<LoanDetailForParents> loansList = List.of(loan1, loan2);

        // When
        AllLoansListForParentResponse response = AllLoansListForParentResponse.builder()
                .loansList(loansList)
                .build();

        // Then
        assertNotNull(response);
        assertNotNull(response.getLoansList());
        assertEquals(2, response.getLoansList().size());

        LoanDetailForParents firstLoan = response.getLoansList().get(0);
        assertEquals("John Doe", firstLoan.getChildName());
        assertEquals("Education Loan", firstLoan.getReason());
        assertEquals(10000, firstLoan.getAmount());
        assertEquals(15, firstLoan.getPaymentDate());
        assertEquals("5% Late Fee", firstLoan.getPenalty());
        assertEquals(12, firstLoan.getPaymentTotalCnt());
        assertEquals(3, firstLoan.getPaymentNowCnt());
        assertEquals("ACTIVE", firstLoan.getStatusCode());
        assertEquals(1, firstLoan.getOverdueCnt());
        assertEquals(LocalDate.of(2024, 1, 1), firstLoan.getStartDate());
        assertEquals(LocalDate.of(2025, 1, 1), firstLoan.getEndDate());
    }
}