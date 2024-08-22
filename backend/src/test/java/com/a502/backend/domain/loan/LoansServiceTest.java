package com.a502.backend.domain.loan;

import com.a502.backend.application.entity.Loan;
import com.a502.backend.application.entity.User;
import com.a502.backend.domain.user.UserFactory;
import com.a502.backend.global.error.BusinessException;
import com.a502.backend.global.exception.ErrorCode;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class LoansServiceTest {

    @Autowired
    private LoansService loansService;

    @Autowired
    LoanFactory loanFactory;

    @Autowired
    UserFactory userFactory;
    @Autowired
    private LoansRepository loansRepository;

    @Test
    void testFindById_Exists() {
        // given
        Loan savedLoan = loanFactory.createAndSaveLoan(1000, 30, 10, "kim");

        // when
        Loan foundLoan = loansService.findById(savedLoan.getId());

        // then
        assertNotNull(foundLoan);
        assertThat(foundLoan).isEqualTo(savedLoan);
    }

    @Test
    void testFindById_NotExists() {
        // when&then
        BusinessException exception = assertThrows(BusinessException.class, () -> loansService.findById(0));
        assertEquals(ErrorCode.API_ERROR_LOAN_NOT_EXIST, exception.getErrorCode());
    }

    @Test
    void testSaveLoan() {
        // given
        Loan loan = loanFactory.createLoan(1000, 30, 10, "kim");

        // when
        Loan savedLoan = loansService.saveLoan(loan);

        // then
        assertThat(savedLoan.getId()).isNotZero();
        assertThat(savedLoan)
                .extracting("amount", "paymentDate", "paymentTotalCnt")
                .contains(1000, 30, 10);
        assertThat(savedLoan.getChild().getName()).isEqualTo("kim");
    }

    @Test
    void testFindByUuid_Exists() {
        // given
        Loan savedLoan = loanFactory.createAndSaveLoan(1000, 30, 10, "kim");

        // when
        Loan foundLoan = loansService.findByUuid(savedLoan.getLoanUuid().toString());

        // then
        assertNotNull(foundLoan);
        assertThat(foundLoan).isEqualTo(savedLoan);
    }

    @Test
    void testFindByUuid_NotExists() {
        // given
        String uuid = UUID.randomUUID().toString();

        // when&then
        BusinessException exception = assertThrows(BusinessException.class, () -> loansService.findByUuid(uuid));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.API_ERROR_LOAN_NOT_EXIST);
    }

    @Disabled("Code 엔티티에 대해 모름")
    @Test
    void testGetAllLoansForChild_WithLoans() {
        // given
        Loan savedLoan = loanFactory.createAndSaveLoan(1000, 30, 10, "kim");
        User child = savedLoan.getChild();

        // when
        List<Loan> loans = loansService.getAllLoansForChild(child);

        // then
        assertThat(loans)
                .isNotEmpty()
                .hasSize(1);
    }

    @Disabled("Code 엔티티에 대해 모름")
    @Test
    void testGetAllLoansForChild_NoLoans() {
        // given
        User child = userFactory.createAndSaveUser("kim");

        // when&then
        BusinessException exception = assertThrows(BusinessException.class, () -> loansService.getAllLoansForChild(child));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.API_ERROR_LOAN_NOT_EXIST);
    }

    @Test
    void testUpdateOverdueCount() {
        // given
        Loan loan = loanFactory.createAndSaveLoan(1000, 30, 10, "kim");
        int overdueCount = loan.getOverdueCnt();

        // when
        loansService.updateOverdueCnt(loan);

        // then
        assertThat(loan.getOverdueCnt()).isEqualTo(overdueCount + 1);
    }
}
