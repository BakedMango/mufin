package com.a502.backend.application.entity;

import com.a502.backend.application.entity.Code;
import com.a502.backend.application.entity.Loan;
import com.a502.backend.application.entity.User;
import com.a502.backend.domain.loan.LoanFactory;
import com.a502.backend.domain.user.UserFactory;
import com.a502.backend.domain.code.CodeFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class LoanTest {

    @Autowired
    private LoanFactory loanFactory;

    @Autowired
    private UserFactory userFactory;

    @Autowired
    private CodeFactory codeFactory;

    private User child;
    private User parent;
    private Code loanInProgressCode;

    @BeforeEach
    public void setUp() {
        // Factory를 이용해 필요한 객체 생성
        child = userFactory.createAndSaveUser("ChildUser");
        parent = userFactory.createAndSaveUser("ParentUser");
        loanInProgressCode = codeFactory.createAndSaveCode("L002", "진행");
    }

    @Test
    public void testCreateLoan() {
        // LoanFactory를 사용해 Loan 객체 생성 및 저장
        Loan loan = loanFactory.createAndSaveLoanWithChild(1000, 30, 12, child, loanInProgressCode);

        assertNotNull(loan);
        assertEquals(1000, loan.getAmount());
        assertEquals("Test Reason", loan.getReason());
        assertEquals(child, loan.getChild());
        assertEquals(loanInProgressCode, loan.getCode());
        assertEquals(0, loan.getPaymentNowCnt());
        assertEquals(0, loan.getOverdueCnt());
    }

    @Test
    public void testRepayLoan() {
        // LoanFactory를 사용해 Loan 객체 생성 및 저장
        Loan loan = loanFactory.createAndSaveLoanWithChild(1000, 30, 12, child, loanInProgressCode);

        boolean isComplete = loan.repayLoan(5);
        assertFalse(isComplete);
        assertEquals(5, loan.getPaymentNowCnt());
        assertEquals(0, loan.getOverdueCnt());

        isComplete = loan.repayLoan(7);
        assertTrue(isComplete);
        assertEquals(12, loan.getPaymentNowCnt());
        assertEquals(0, loan.getOverdueCnt());
    }

    @Test
    public void testLoanStatusChange() {
        // LoanFactory를 사용해 Loan 객체 생성 및 저장
        Loan loan = loanFactory.createAndSaveLoanWithChild(1000, 30, 12, child, loanInProgressCode);

        // Code 변경 테스트
        Code completedCode = codeFactory.createAndSaveCode("L004", "상환완료");
        loan.completeLoan(completedCode);
        assertEquals(completedCode, loan.getCode());

        Code refusedCode = codeFactory.createAndSaveCode("L003", "거절");
        loan.refuseLoan(refusedCode);
        assertEquals(refusedCode, loan.getCode());
    }

    @Test
    public void testUpdateOverdueCnt() {
        // LoanFactory를 사용해 Loan 객체 생성 및 저장
        Loan loan = loanFactory.createAndSaveLoanWithChild(1000, 30, 12, child, loanInProgressCode);

        loan.updateOverdueCnt(3);
        assertEquals(3, loan.getOverdueCnt());

        loan.updateOverdueCnt(0);
        assertEquals(0, loan.getOverdueCnt());
    }
}