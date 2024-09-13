package com.a502.backend.domain.loan.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

class RequestedLoanDetailForParentsResponseTest {

    @Test
    @DisplayName("RequestedLoanDetailForParentsResponse 객체 생성 테스트")
    void testRequestedLoanDetailForParentsResponseCreation() {
        // Given
        RequestedLoanDetail loan1 = RequestedLoanDetail.builder()
                .childName("Jane Doe")
                .reason("Student Loan")
                .loanUuid("123e4567-e89b-12d3-a456-426614174000")
                .amount(20000)
                .paymentDate(15)
                .penalty("5% Late Fee")
                .paymentTotalCnt(10)
                .chatBotConversation(new String[]{"Hello", "Loan request"})
                .build();

        RequestedLoanDetail loan2 = RequestedLoanDetail.builder()
                .childName("John Doe")
                .reason("Car Loan")
                .loanUuid("123e4567-e89b-12d3-a456-426614174001")
                .amount(15000)
                .paymentDate(10)
                .penalty("3% Late Fee")
                .paymentTotalCnt(8)
                .chatBotConversation(new String[]{"Hi", "Request approved"})
                .build();

        List<RequestedLoanDetail> loansList = List.of(loan1, loan2);

        // When
        RequestedLoanDetailForParentsResponse response = RequestedLoanDetailForParentsResponse.builder()
                .loansList(loansList)
                .build();

        // Then
        assertNotNull(response);
        assertNotNull(response.getLoansList());
        assertEquals(2, response.getLoansList().size());
    }
}