package com.a502.backend.application.controller;

import com.a502.backend.application.facade.LoanFacade;
import com.a502.backend.domain.loan.Request.*;
import com.a502.backend.domain.loan.Response.*;
import com.a502.backend.global.response.ApiResponse;
import com.a502.backend.global.response.ResponseCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class LoanControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LoanFacade loanFacade;

    @InjectMocks
    private LoanController loanController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(loanController).build();
    }

    @Test
    void testApplyLoan() throws Exception {
        doNothing().when(loanFacade).applyLoan(any(ApplyLoanRequest.class));

        mockMvc.perform(post("/api/loan/apply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 1000, \"reason\": \"Test Reason\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ResponseCode.API_SUCCESS_LOAN_APPLY.getMessage()));
    }

    @Test
    void testGetAllLoansForChild() throws Exception {
        LoanListForChildResponse response = LoanListForChildResponse.builder()
                .totalRemainderAmount(5000)
                .loansList(Collections.emptyList())  // Assuming empty list for this example
                .build();
        when(loanFacade.getAllLoansForChild()).thenReturn(response);

        mockMvc.perform(post("/api/loan/total/child")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ResponseCode.API_SUCCESS_LOAN_GET_ALL_FOR_CHILD.getMessage()))
                .andExpect(jsonPath("$.data.totalRemainderAmount").value(5000));
    }

    @Test
    void testGetLoanDetailForChild() throws Exception {
        LoanDetailResponse response = LoanDetailResponse.builder()
                .reason("Test Reason")
                .totalAmount(10000)
                .remainderAmount(5000)
                .startDate(LocalDate.now())
                .remainderDay("10 days")
                .paymentDate(30)
                .overDueCnt(0)
                .paymentAmount(1000)
                .build();
        when(loanFacade.getLoanDetailForChild(any(LoanUuidRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/loan/detail/child")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"loanUuid\": \"some-uuid\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ResponseCode.API_SUCCESS_LOAN_GET_DETAIL_FOR_CHILD.getMessage()))
                .andExpect(jsonPath("$.data.reason").value("Test Reason"));
    }

    @Test
    void testRepayLoan() throws Exception {
        doNothing().when(loanFacade).repayLoan(any(RepayLoanRequest.class));

        mockMvc.perform(post("/api/loan/repay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"loanUuid\": \"some-uuid\", \"amount\": 100}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ResponseCode.API_SUCCESS_LOAN_REPAY.getMessage()));
    }

}
