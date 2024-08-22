package com.a502.backend.domain.loan;

import com.a502.backend.application.entity.Code;
import com.a502.backend.application.entity.Loan;
import com.a502.backend.application.entity.User;
import com.a502.backend.domain.code.CodeFactory;
import com.a502.backend.domain.user.UserFactory;
import com.a502.backend.domain.user.UserRepository;
import com.a502.backend.global.code.CodeRepository;
import com.a502.backend.global.error.BusinessException;
import com.a502.backend.global.exception.ErrorCode;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LoansServiceTest {

    @Autowired
    private LoansService loansService;

    @Autowired
    LoanFactory loanFactory;

    @Autowired
    UserFactory userFactory;

    @Autowired
    CodeFactory codeFactory;

    @Autowired
    private LoansRepository loansRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CodeRepository codeRepository;

    @AfterEach
    void tearDown() {
        loansRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        codeRepository.deleteAllInBatch();
    }

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

    @Test
    void testGetAllLoansForChild_WithLoans() {
        // given
        Code judgedCode = codeFactory.createAndSaveCode("L001", "심사중");
        Code inProgressCode = codeFactory.createAndSaveCode("L002", "진행");
        Code refusedCode = codeFactory.createAndSaveCode("L003", "거절");
        Code repaidCode = codeFactory.createAndSaveCode("L004", "상환완료");
        User child = userFactory.createAndSaveUser("kim");
        int n = 3;
        for (int i = 0; i < n; i++) {
            loanFactory.createAndSaveLoanWithChild(1000, 30, 10, child, refusedCode);
            loanFactory.createAndSaveLoanWithChild(1000, 30, 10, child, judgedCode);
            loanFactory.createAndSaveLoanWithChild(1000, 30, 10, child, inProgressCode);
        }
        loanFactory.createAndSaveLoanWithChild(1000, 30, 10, child, repaidCode);

        // when
        List<Loan> loans = loansService.getAllLoansForChild(child);

        // then
        assertThat(loans)
                .isNotEmpty()
                .hasSize(n * 3);
    }

    @Test
    void testGetAllLoansForChild_NoLoans() {
        // given
        Code judgedCode = codeFactory.createAndSaveCode("L001", "심사중");
        Code inProgressCode = codeFactory.createAndSaveCode("L002", "진행");
        Code refusedCode = codeFactory.createAndSaveCode("L003", "거절");
        Code repaidCode = codeFactory.createAndSaveCode("L004", "상환완료");
        User child = userFactory.createAndSaveUser("kim");

        loanFactory.createAndSaveLoan(1000, 30, 10, "kim", judgedCode);
        loanFactory.createAndSaveLoan(1000, 30, 10, "kim", refusedCode);
        loanFactory.createAndSaveLoan(1000, 30, 10, "kim", repaidCode);

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

    @Test
    void testGetAllLoansForParents_WithLoans() {
        // given
        Code judgedCode = codeFactory.createAndSaveCode("L001", "심사중");
        Code inProgressCode = codeFactory.createAndSaveCode("L002", "진행");
        Code refusedCode = codeFactory.createAndSaveCode("L003", "거절");
        Code repaidCode = codeFactory.createAndSaveCode("L004", "상환완료");
        User parents = userFactory.createAndSaveUser("kim");
        int n = 3;
        for (int i = 0; i < n; i++) {
            loanFactory.createAndSaveLoanWithParent(1000, 30, 10, parents, inProgressCode);
            loanFactory.createAndSaveLoanWithParent(1000, 30, 10, parents, repaidCode);
        }
        loanFactory.createAndSaveLoanWithParent(1000, 30, 10, parents, refusedCode);
        loanFactory.createAndSaveLoanWithParent(1000, 30, 10, parents, judgedCode);

        // when
        List<Loan> loans = loansService.getAllLoansForParents(parents);

        // then
        assertThat(loans)
                .isNotEmpty()
                .hasSize(n * 2);
    }

    @Test
    void testGetAllLoansForParents_NoLoans() {
        // given
        Code judgedCode = codeFactory.createAndSaveCode("L001", "심사중");
        Code inProgressCode = codeFactory.createAndSaveCode("L002", "진행");
        Code refusedCode = codeFactory.createAndSaveCode("L003", "거절");
        Code repaidCode = codeFactory.createAndSaveCode("L004", "상환완료");
        User parents = userFactory.createAndSaveUser("kim");
        User otherParents = userFactory.createAndSaveUser("lee");

        loanFactory.createAndSaveLoanWithParent(1000, 30, 10, otherParents, judgedCode);
        loanFactory.createAndSaveLoanWithParent(1000, 30, 10, otherParents, refusedCode);
        loanFactory.createAndSaveLoanWithParent(1000, 30, 10, otherParents, repaidCode);

        // when&then
        BusinessException exception = assertThrows(BusinessException.class, () -> loansService.getAllLoansForParents(parents));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.API_ERROR_LOAN_NOT_EXIST_FOR_PARENTS);
    }

    @Test
    void findAllLoansInProgress() {
        // given
        Code judgedCode = codeFactory.createAndSaveCode("L001", "심사중");
        Code inProgressCode = codeFactory.createAndSaveCode("L002", "진행");
        Code refusedCode = codeFactory.createAndSaveCode("L003", "거절");
        Code repaidCode = codeFactory.createAndSaveCode("L004", "상환완료");
        int n = 3;
        for (int i = 0; i < n; i++) {
            loanFactory.createAndSaveLoan(1000, 30, 10, "kim", inProgressCode);
        }
        loanFactory.createAndSaveLoan(1000, 30, 10, "kim", repaidCode);
        loanFactory.createAndSaveLoan(1000, 30, 10, "kim", refusedCode);
        loanFactory.createAndSaveLoan(1000, 30, 10, "kim", judgedCode);

        // when
        List<Loan> loans = loansService.findAllLoansInProgress();

        // then
        assertThat(loans)
                .isNotEmpty()
                .hasSize(n);
    }
}
