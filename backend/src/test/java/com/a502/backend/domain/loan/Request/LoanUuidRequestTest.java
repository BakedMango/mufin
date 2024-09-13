package com.a502.backend.domain.loan.Request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoanUuidRequestTest {

    @Test
    @DisplayName("LoanUuidRequest 객체 생성 테스트")
    void testLoanUuidRequestCreation() {
        // Given
        String loanUuid = "123e4567-e89b-12d3-a456-426614174000";

        // When
        LoanUuidRequest request = LoanUuidRequest.builder()
                .loanUuid(loanUuid)
                .build();

        // Then
        assertNotNull(request);
        assertEquals(loanUuid, request.getLoanUuid());
    }
}