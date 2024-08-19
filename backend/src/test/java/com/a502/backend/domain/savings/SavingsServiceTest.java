package com.a502.backend.domain.savings;

import com.a502.backend.application.entity.Savings;
import com.a502.backend.application.entity.User;
import com.a502.backend.global.error.BusinessException;
import com.a502.backend.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static com.a502.backend.global.exception.ErrorCode.API_ERROR_SAVINGS_NOT_EXIST;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
public class SavingsServiceTest {
    @Mock
    private SavingsRepository savingsRepository;
    @InjectMocks
    private SavingsService savingsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("[적금] 삭제된 적금상품 조회할 경우 -> 예외 발생")
    void 삭제된_UUID로_조회할경우(){
        Savings savings = Savings.builder()
                .build();
        savings.setSavingUuid();
        savings.setDeleted(true);

        when(savingsRepository.findSavingsListByUuid(savings.getSavingUuid()))
                .thenReturn(Optional.of(savings));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            savingsService.findByUuid(savings.getSavingUuid().toString());
        });
        assertEquals(API_ERROR_SAVINGS_NOT_EXIST.getStatus(), exception.getErrorCode().getStatus());
    }

    @Test
    @DisplayName("[적금] 유효한 적금상품 조회할 경우 -> 성공")
    void 활성화된_적금상품_조회할경우() {
        Savings savings = Savings.builder().build();
        savings.setSavingUuid();
        when(savingsRepository.findSavingsListByUuid(savings.getSavingUuid())).thenReturn(Optional.of(savings));

        Savings resultSavings = savingsService.findByUuid(savings.getSavingUuid().toString());
        assertNotNull(resultSavings);
        assertEquals(savings, resultSavings);
    }

    @Test
    @DisplayName("[적금] 적금 상품이 존재하지 않을 경우 -> 예외 발생")
    void 적금상품이_존재하지_않을_경우(){
        User parents = User.builder().build();

        when(savingsRepository.findSavingsListByUuid(parents.getUserUuid())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            savingsService.findAllByParents(parents);
        });

        assertEquals(ErrorCode.API_ERROR_SAVINGS_NOT_EXIST, exception.getErrorCode());
    }

    @Test
    @DisplayName("[적금] 적금 상품이 1개 이상일 경우 -> 성공")
    void 적금상품이_1개이상_존재_할_경우(){
        User parents = User.builder().build();
        Savings savings1 = Savings.builder().build();
        Savings savings2 = Savings.builder().build();
        List<Savings> expectedSavingsList = Arrays.asList(savings1, savings2);

        when(savingsRepository.findAllByParents(parents)).thenReturn(expectedSavingsList);

        List<Savings> actualSavingsList = savingsService.findAllByParents(parents);

        assertEquals(expectedSavingsList, actualSavingsList);
    }

    @Test
    @DisplayName("[적금] 저장이 잘 되는 경우 -> 성공")
    void testSave() {
        Savings savings = Savings.builder().build();

        savingsService.save(savings);

        verify(savingsRepository, times(1)).save(savings);
    }
}