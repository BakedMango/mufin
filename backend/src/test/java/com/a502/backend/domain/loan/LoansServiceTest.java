package com.a502.backend.domain.loan;

import com.a502.backend.application.entity.Loan;
import com.a502.backend.application.entity.User;
import com.a502.backend.global.error.BusinessException;
import com.a502.backend.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoansServiceTest {

    @Mock
    private LoansRepository loansRepository;

    @InjectMocks
    private LoansService loansService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindById_Exists() {
        User parent = User.builder().name("Parent").email("parent@example.com").build();
        Loan loan = Loan.builder()
                .amount(1000)
                .reason("Test Reason")
                .paymentDate(30)
                .penalty("None")
                .paymentTotalCnt(10)
                .startDate(LocalDate.now())
                .child(parent)
                .build();
        when(loansRepository.findById(1)).thenReturn(Optional.of(loan));

        Loan foundLoan = loansService.findById(1);

        assertNotNull(foundLoan);
        assertEquals(loan, foundLoan);
    }

    @Test
    void testFindById_NotExists() {
        when(loansRepository.findById(1)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> loansService.findById(1));
        assertEquals(ErrorCode.API_ERROR_LOAN_NOT_EXIST, exception.getErrorCode());
    }

    @Test
    void testSaveLoan() {
        User parent = User.builder().name("Parent").email("parent@example.com").build();
        Loan loan = Loan.builder()
                .amount(1000)
                .reason("Test Reason")
                .paymentDate(30)
                .penalty("None")
                .paymentTotalCnt(10)
                .startDate(LocalDate.now())
                .child(parent)
                .build();
        loansService.saveLoan(loan);

        verify(loansRepository, times(1)).save(loan);
    }

    @Test
    void testFindByUuid_Exists() {
        User parent = User.builder().name("Parent").email("parent@example.com").build();
        Loan loan = Loan.builder()
                .amount(1000)
                .reason("Test Reason")
                .paymentDate(30)
                .penalty("None")
                .paymentTotalCnt(10)
                .startDate(LocalDate.now())
                .child(parent)
                .build();
        UUID uuid = UUID.randomUUID();
        when(loansRepository.findByUuid(uuid)).thenReturn(Optional.of(loan));

        Loan foundLoan = loansService.findByUuid(uuid.toString());

        assertNotNull(foundLoan);
        assertEquals(loan, foundLoan);
    }

    @Test
    void testFindByUuid_NotExists() {
        UUID uuid = UUID.randomUUID();
        when(loansRepository.findByUuid(uuid)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> loansService.findByUuid(uuid.toString()));
        assertEquals(ErrorCode.API_ERROR_LOAN_NOT_EXIST, exception.getErrorCode());
    }

    @Test
    void testGetAllLoansForChild_WithLoans() {
        User child = User.builder().name("Child").email("child@example.com").build();
        Loan loan = Loan.builder()
                .amount(1000)
                .reason("Test Reason")
                .paymentDate(30)
                .penalty("None")
                .paymentTotalCnt(10)
                .startDate(LocalDate.now())
                .child(child)
                .build();
        when(loansRepository.findAllLoansInProgressByUser(child)).thenReturn(List.of(loan));

        List<Loan> loans = loansService.getAllLoansForChild(child);

        assertFalse(loans.isEmpty());
        assertEquals(1, loans.size());
    }

    @Test
    void testGetAllLoansForChild_NoLoans() {
        User child = User.builder().name("Child").email("child@example.com").build();
        when(loansRepository.findAllLoansInProgressByUser(child)).thenReturn(List.of());

        BusinessException exception = assertThrows(BusinessException.class, () -> loansService.getAllLoansForChild(child));
        assertEquals(ErrorCode.API_ERROR_LOAN_NOT_EXIST, exception.getErrorCode());
    }

}
