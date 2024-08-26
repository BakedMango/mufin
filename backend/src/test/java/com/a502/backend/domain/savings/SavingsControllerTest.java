package com.a502.backend.domain.savings;

import com.a502.backend.application.controller.SavingsController;
import lombok.extern.slf4j.Slf4j;

import com.a502.backend.application.facade.SavingFacade;
import com.a502.backend.domain.savings.Request.*;
import com.a502.backend.domain.savings.Response.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

@Slf4j
class SavingsControllerTest {

    @Mock
    private SavingFacade savingFacade;
    @InjectMocks
    private SavingsController savingsController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("[적금] 등록 -> 성공")
    void registerSavings() throws Exception {
        RegisterSavingsRequest request = RegisterSavingsRequest.builder()
                .name("Test Savings")
                .period(12)
                .interest(2.5)
                .build();

        doNothing().when(savingFacade).registerSavings(any(RegisterSavingsRequest.class));
        savingsController.registerSavings(request);
    }

    @Test
    @DisplayName("적금 삭제 -> 성공")
    void deleteSavings() throws Exception {
        SavingsUuidRequest request = SavingsUuidRequest.builder()
                .savingsUuid("123e4567-e89b-12d3-a456-426614174000")
                .build();

        doNothing().when(savingFacade).deleteSavings(any(SavingsUuidRequest.class));
        savingsController.deleteSavings(request);
    }

    @Test
    @DisplayName("모든 적금 상품 조회 -> 성공")
    void getAllSavingsProduct() throws Exception {
        AllSavingsProductResponse response = AllSavingsProductResponse.builder().build();
        when(savingFacade.getAllSavingProduct()).thenReturn(response);
        savingsController.getAllSavingsProduct();
    }

    @Test
    @DisplayName("적금 상품 상세 조회 -> 성공")
    void getSavingsProduct() throws Exception {
        SavingsUuidRequest request = SavingsUuidRequest.builder()
                .savingsUuid("123e4567-e89b-12d3-a456-426614174000")
                .build();

        SavingsDetail response = SavingsDetail.builder().build();
        when(savingFacade.getSavingsProduct(any(SavingsUuidRequest.class))).thenReturn(response);

        savingsController.deleteSavings(request);
    }

    @Test
    @DisplayName("적금 가입 -> 성공")
    void joinSavings() throws Exception {
        JoinSavingsRequest request = JoinSavingsRequest.builder()
                .savingsUuid("123e4567-e89b-12d3-a456-426614174000")
                .paymentAmount(1000)
                .paymentDate(15)
                .build();

        doNothing().when(savingFacade).joinSavings(any(JoinSavingsRequest.class));

        savingsController.joinSavings(request);
    }

    @Test
    @DisplayName("자녀 적금 계좌 조회 -> 성공")
    void getMyChildSavings() throws Exception {
        MyChildSavingsListResponse response = MyChildSavingsListResponse.builder().build();

        when(savingFacade.getMyChildSavings()).thenReturn(response);
        savingsController.getMyChildSavings();
    }

    @Test
    @DisplayName("적금 계좌 입금 -> 성공")
    void depositToSavings() throws Exception {
        DepositSavingsRequest request = DepositSavingsRequest.builder()
                .accountUuid("123e4567-e89b-12d3-a456-426614174000")
                .cnt(2)
                .password("correct_password")
                .build();

        doNothing().when(savingFacade).depositToSavings(any(DepositSavingsRequest.class));
        savingsController.depositToSavings(request);
    }

    @Test
    @DisplayName("적금 해지 -> 성공")
    void cancelSavings() throws Exception {
        CancelSavingsRequest request = CancelSavingsRequest.builder()
                .accountUuid("123e4567-e89b-12d3-a456-426614174000")
                .build();

        doNothing().when(savingFacade).cancelSavings(any(CancelSavingsRequest.class));
        savingsController.cancelSavings(request);
    }

    @Test
    @DisplayName("적금 만기 해지 -> 성공")
    void terminateSavings() throws Exception {
        CancelSavingsRequest request = CancelSavingsRequest.builder()
                .accountUuid("123e4567-e89b-12d3-a456-426614174000")
                .build();

        doNothing().when(savingFacade).terminateSavings(any(CancelSavingsRequest.class));
        savingsController.terminateSavings(request);
    }

    @Test
    @DisplayName("내 모든 적금 상품 조회 -> 성공")
    void getMyAllSavings() throws Exception {
        MyAllSavingsResponse response = MyAllSavingsResponse.builder().build();
        when(savingFacade.getMyAllSavings()).thenReturn(response);
        savingsController.getMyAllSavings();
    }

    @Test
    @DisplayName("내 적금 상품 상세 조회 -> 성공")
    void getMySavingsDetail() throws Exception {
        CancelSavingsRequest request = CancelSavingsRequest.builder()
                .accountUuid("123e4567-e89b-12d3-a456-426614174000")
                .build();

        MySavings response = MySavings.builder().build();
        when(savingFacade.getMySavingsDetail(any(CancelSavingsRequest.class))).thenReturn(response);
        savingsController.getMySavingsDetail(request);
    }
}
