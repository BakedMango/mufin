package com.a502.backend.domain.loan.Request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.a502.backend.domain.loan.Request.RefuseLoanRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RefuseLoanRequestTest {

    @Test
    @DisplayName("RefuseLoanRequest 객체 생성 테스트")
    void testRefuseLoanRequestCreation() {
        // Given
        String loanUuid = "123e4567-e89b-12d3-a456-426614174000";
        String reason = "Insufficient credit score";

        // When
        RefuseLoanRequest request = RefuseLoanRequest.builder()
                .loanUuid(loanUuid)
                .reason(reason)
                .build();

        // Then
        assertNotNull(request);
        assertEquals(loanUuid, request.getLoanUuid());
        assertEquals(reason, request.getReason());
    }
}