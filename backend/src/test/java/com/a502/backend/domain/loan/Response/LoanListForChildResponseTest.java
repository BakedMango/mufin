package com.a502.backend.domain.loan.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

class LoanListForChildResponseTest {

    @Test
    @DisplayName("LoanListForChildResponse 객체 생성 테스트")
    void testLoanListForChildResponseCreation() {
        // Given
        LoanList loan1 = LoanList.builder()
                .reason("Home Renovation")
                .loanUuid("123e4567-e89b-12d3-a456-426614174000")
                .amount(50000)
                .paymentTotalCnt(24)
                .paymentNowCnt(12)
                .remainderAmount(25000)
                .status("ACTIVE")
                .overDueCnt(3)
                .loanRefusalReason("Low Credit Score")
                .build();

        LoanList loan2 = LoanList.builder()
                .reason("Car Loan")
                .loanUuid("123e4567-e89b-12d3-a456-426614174001")
                .amount(30000)
                .paymentTotalCnt(12)
                .paymentNowCnt(6)
                .remainderAmount(15000)
                .status("COMPLETED")
                .overDueCnt(0)
                .loanRefusalReason(null)
                .build();

        List<LoanList> loansList = List.of(loan1, loan2);

        // When
        LoanListForChildResponse response = LoanListForChildResponse.builder()
                .totalRemainderAmount(40000)
                .loansList(loansList)
                .build();

        // Then
        assertNotNull(response);
        assertEquals(40000, response.getTotalRemainderAmount());
        assertNotNull(response.getLoansList());
        assertEquals(2, response.getLoansList().size());
    }
}