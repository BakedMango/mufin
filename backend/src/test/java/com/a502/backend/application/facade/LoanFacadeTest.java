package com.a502.backend.application.facade;

import com.a502.backend.global.error.BusinessException;
import com.a502.backend.global.exception.ErrorCode;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.a502.backend.application.entity.*;
import com.a502.backend.domain.account.*;
import com.a502.backend.domain.loan.*;
import com.a502.backend.domain.user.UserService;
import com.a502.backend.global.code.CodeService;
import com.a502.backend.domain.user.UserFactory;
import com.a502.backend.domain.code.CodeFactory;
import com.a502.backend.domain.loan.Request.*;
import com.a502.backend.domain.loan.Response.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.UUID;

public class LoanFacadeTest {

    @InjectMocks
    private LoanFacade loanFacade;

    @Mock
    private LoansService loansService;
    @Mock
    private LoanRefusalService loanRefusalService;
    @Mock
    private UserService userService;
    @Mock
    private LoanConversationService loanConversationService;
    @Mock
    private CodeService codeService;
    @Mock
    private AccountService accountService;
    @Mock
    private AccountDetailService accountDetailService;
    @Mock
    private LoanDetailService loanDetailService;
    @Mock
    private UserFactory userFactory;
    @Mock
    private CodeFactory codeFactory;
    @Mock
    private LoanFactory loanFactory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testApplyLoan() {
        // given
        ApplyLoanRequest request = ApplyLoanRequest.builder()
                .reason("Test reason")
                .amount(1000)
                .paymentTotalCnt(12)
                .paymentDate(15)
                .conversation("Test conversation")
                .penalty("Test penalty")
                .build();

        User child = User.builder().name("Child").build();
        User parent = User.builder().name("Parent").build();
        child.addParent(parent);

        Code code = Code.builder().id("L001").name("심사중").build();

        LoanConversation conversation = LoanConversation.builder()
                .content("Test conversation")
                .user(child)
                .build();

        when(userService.userFindByEmail()).thenReturn(child);
        when(codeService.findStatusCode("심사중")).thenReturn(code);
        when(loanConversationService.findByUserLast(child)).thenReturn(conversation);

        // when
        loanFacade.applyLoan(request);

        // then
        verify(loanConversationService).saveLoanConversation(any(LoanConversation.class));
        verify(loansService).saveLoan(any(Loan.class));
    }

    @Test
    void testGetAllLoansForChild() {
        // given
        User child = User.builder().name("Child").build();
        Code code = Code.builder().id("L002").name("진행중").build();
        Loan loan = Loan.builder().amount(1000).paymentDate(15).paymentTotalCnt(12).child(child).code(code).build();

        when(userService.userFindByEmail()).thenReturn(child);
        when(loansService.getAllLoansForChild(child)).thenReturn(Arrays.asList(loan));
        when(codeService.findById(anyString())).thenReturn(code);

        // when
        LoanListForChildResponse response = loanFacade.getAllLoansForChild();

        // then
        assertNotNull(response);
        assertEquals(1, response.getLoansList().size());
    }

    @Test
    void testGetLoanDetailForChild() {
        // given
        LoanUuidRequest request = LoanUuidRequest.builder()
                .loanUuid(UUID.randomUUID().toString())
                .build();

        User child = User.builder().name("Child").build();
        Code code = Code.builder().id("L002").name("진행중").build();
        Loan loan = Loan.builder().amount(1000).paymentDate(15).paymentTotalCnt(12).child(child).code(code).build();

        when(loansService.findByUuid(anyString())).thenReturn(loan);

        // when
        LoanDetailResponse response = loanFacade.getLoanDetailForChild(request);

        // then
        assertNotNull(response);
        assertEquals(1000, response.getTotalAmount());
    }

    @Test
    void testAcceptLoan() {
        // given
        LoanUuidRequest request = LoanUuidRequest.builder()
                .loanUuid(UUID.randomUUID().toString())
                .build();

        User child = User.builder().name("Child").build();
        User parent = User.builder().name("Parent").build();
        Code code = Code.builder().id("L002").name("진행중").build();
        Loan loan = Loan.builder().amount(1000).paymentDate(15).paymentTotalCnt(12).child(child).code(code).build();

        Account parentAccount = Account.builder()
                .balance(2000)
                .user(parent)
                .build();

        Account childAccount = Account.builder()
                .balance(500)
                .user(child)
                .build();

        when(userService.userFindByEmail()).thenReturn(parent);
        when(loansService.findByUuid(anyString())).thenReturn(loan);
        when(codeService.findByName("진행중")).thenReturn(code);
        doReturn(parentAccount).when(accountService).findByUser(same(parent));
        doReturn(childAccount).when(accountService).findByUser(same(child));

        // when
        loanFacade.acceptLoan(request);

        // then
        assertEquals(LocalDate.now(), loan.getStartDate());
        assertEquals(code, loan.getCode());
        assertEquals(1000, parentAccount.getBalance());
        assertEquals(1500, childAccount.getBalance());
        verify(accountDetailService, times(2)).save(any(AccountDetail.class));
    }

    @Test
    void testAcceptLoan_LessAmount() {
        // given
        LoanUuidRequest request = LoanUuidRequest.builder()
                .loanUuid(UUID.randomUUID().toString())
                .build();

        User child = User.builder().name("Child").build();
        User parent = User.builder().name("Parent").build();
        Code code = Code.builder().id("L002").name("진행중").build();
        Loan loan = Loan.builder().amount(1000).paymentDate(15).paymentTotalCnt(12).child(child).code(code).build();

        Account parentAccount = Account.builder()
                .balance(100)
                .user(parent)
                .build();

        Account childAccount = Account.builder()
                .balance(500)
                .user(child)
                .build();

        when(userService.userFindByEmail()).thenReturn(parent);
        when(loansService.findByUuid(anyString())).thenReturn(loan);
        when(codeService.findByName("진행중")).thenReturn(code);
        doReturn(parentAccount).when(accountService).findByUser(same(parent));
        doReturn(childAccount).when(accountService).findByUser(same(child));

        // when&then
        BusinessException exception = assertThrows(
                BusinessException.class, () -> loanFacade.acceptLoan(request));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.API_ERROR_ACCOUNT_INSUFFICIENT_BALANCE);
    }
}