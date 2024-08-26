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
        Loan loan = loanFactory.createAndSaveLoanWithChild(1000, 30, 12, child, loanInProgressCode);
        loan.updateOverdueCnt(2);
        boolean isComplete = loan.repayLoan(1);

        assertFalse(isComplete);
        assertEquals(1, loan.getPaymentNowCnt());
        assertEquals(1, loan.getOverdueCnt());

        loan.repayLoan(1);
        assertEquals(2, loan.getPaymentNowCnt());
        assertEquals(0, loan.getOverdueCnt());

        isComplete = loan.repayLoan(10);
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

    @Test
    public void testLombokGetters() {
        // Loan 객체 생성 및 저장
        Loan loan = loanFactory.createAndSaveLoanWithChild(1000, 30, 12, child, loanInProgressCode);

        // Lombok의 getter 메서드 테스트
        assertEquals(1000, loan.getAmount());
        assertEquals("Test Reason", loan.getReason());
        assertEquals(30, loan.getPaymentDate());
        assertEquals("None", loan.getPenalty());
        assertEquals(12, loan.getPaymentTotalCnt());
        assertEquals(0, loan.getPaymentNowCnt());
        assertEquals(0, loan.getOverdueCnt());
        assertEquals(LocalDate.now(), loan.getStartDate());
        assertEquals(child, loan.getChild());
        assertNull(loan.getParent());  // 부모는 null로 설정됨
        assertEquals(loanInProgressCode, loan.getCode());
    }

    @Test
    public void testLombokBuilder() {
        // Lombok의 @Builder를 사용해 Loan 객체 생성
        Loan loan = Loan.builder()
                .amount(1500)
                .reason("Another Test Reason")
                .paymentDate(45)
                .penalty("Minor")
                .paymentTotalCnt(10)
                .startDate(LocalDate.now())
                .child(child)
                .parent(parent)
                .code(loanInProgressCode)
                .build();

        // 빌더 패턴으로 생성된 객체 검증
        assertNotNull(loan);
        assertEquals(1500, loan.getAmount());
        assertEquals("Another Test Reason", loan.getReason());
        assertEquals(45, loan.getPaymentDate());
        assertEquals("Minor", loan.getPenalty());
        assertEquals(10, loan.getPaymentTotalCnt());
        assertEquals(LocalDate.now(), loan.getStartDate());
        assertEquals(child, loan.getChild());
        assertEquals(parent, loan.getParent());
        assertEquals(loanInProgressCode, loan.getCode());
    }

    @Test
    public void testStartLoan() {
        Loan loan = loanFactory.createAndSaveLoanWithChild(1000, 30, 12, child, loanInProgressCode);
        LocalDate newStartDate = LocalDate.now().plusDays(1);
        Code newCode = codeFactory.createAndSaveCode("L001", "심사중");

        loan.startLoan(newStartDate, newCode);

        assertEquals(newStartDate, loan.getStartDate());
        assertEquals(newCode, loan.getCode());
    }

    @Test
    public void testEqualsAndHashCode() {
        Loan loan1 = loanFactory.createAndSaveLoanWithChild(1000, 30, 12, child, loanInProgressCode);
        Loan loan2 = loanFactory.createAndSaveLoanWithChild(1000, 30, 12, child, loanInProgressCode);
        Loan loan3 = loanFactory.createAndSaveLoanWithChild(2000, 40, 24, child, loanInProgressCode);

        assertNotEquals(loan1.hashCode(), loan2.hashCode()); // 다른 객체는 다른 hashCode여야 합니다.
        assertNotEquals(loan2.hashCode(), loan3.hashCode()); // 다른 객체는 다른 hashCode여야 합니다.
        assertNotEquals(loan1.hashCode(), loan3.hashCode()); // 다른 객체는 다른 hashCode여야 합니다.
    }
}