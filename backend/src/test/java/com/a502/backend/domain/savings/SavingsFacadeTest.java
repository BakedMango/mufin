package com.a502.backend.domain.savings;

import com.a502.backend.application.entity.*;
import com.a502.backend.application.facade.SavingFacade;
import com.a502.backend.domain.account.AccountDetailService;
import com.a502.backend.domain.account.AccountService;
import com.a502.backend.domain.savings.Request.*;
import com.a502.backend.domain.savings.Response.AllSavingsProductResponse;
import com.a502.backend.domain.savings.Response.MyChildSavingsListResponse;
import com.a502.backend.domain.savings.Response.MySavings;
import com.a502.backend.domain.savings.Response.SavingsDetail;
import com.a502.backend.domain.user.UserService;
import com.a502.backend.global.code.CodeService;
import com.a502.backend.global.error.BusinessException;
import com.a502.backend.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
public class SavingsFacadeTest {
    @Mock
    private SavingsService savingsService;
    @Mock
    private SavingsRepository savingsRepository;
    @Mock
    private UserService userService;
    @Mock
    private AccountService accountService;
    @Mock
    private CodeService codeService;
    @Mock
    private AccountDetailService accountDetailService;
    @InjectMocks
    private SavingFacade savingFacade;

    private User user;
    private User parent;
    private Account savingsAccount;
    private Account checkingAccount;
    private Account parentAccount;
    private DepositSavingsRequest depositRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        parent = User.builder()
                .email("parent@test.com")
                .build();
        parent.setCreatedAt();
        parent.setUserUuid();

        parentAccount = Account.builder()
                .balance(100000000)
                .build();
        parentAccount.setAccountUuid();
        parentAccount.setCreatedAt();


        user = User.builder().email("test@example.com").build();
        user.setUserUuid();
        user.setCreatedAt();
        user.addParent(parent);

        savingsAccount = Account.builder()
                .accountNumber("savings account number")
                .balance(5000)
                .paymentAmount(2000)
                .paymentCycle(1)
                .savings(Savings.builder().period(12).interest(2.5).build())
                .typeCode(new Code("AS002", "해지"))
                .build();

        savingsAccount.setCreatedAt();
        savingsAccount.setAccountUuid();

        checkingAccount = Account.builder()
                .accountNumber("account number")
                .balance(2000)
                .build();

        checkingAccount.setAccountUuid();
        checkingAccount.setCreatedAt();

        depositRequest = DepositSavingsRequest.builder()
                .password("correct_password")
                .accountUuid(savingsAccount.getAccountUuid().toString())
                .cnt(2)
                .build();
    }

    @Test
    @DisplayName("[적금/생성] 부모 사용자가 적금 상품을 등록할 경우 -> 경우")
    void 적금상품_생성_유저가_부모인경우() {
        User parentUser = User.builder().build(); // 부모 사용자
        when(userService.userFindByEmail()).thenReturn(parentUser);

        RegisterSavingsRequest request = RegisterSavingsRequest.builder()
                .name("TestParentName")
                .period(12)
                .interest(0.3)
                .build();

        savingFacade.registerSavings(request);
    }

    @Test
    @DisplayName("[적금/생성] 부모가 아닌 사용자가 적금 상품을 등록할 경우 ->  예외 발생")
    void 적금상품_생성_부모가_아닌경우() {
        User parent = User.builder().build();
        User nonParent = User.builder().build();
        nonParent.addParent(parent);

        when(userService.userFindByEmail()).thenReturn(nonParent);

        RegisterSavingsRequest request = RegisterSavingsRequest.builder()
                .name("TestNonParentName")
                .period(12)
                .interest(0.3)
                .build();

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            savingFacade.registerSavings(request);
        });

        assertEquals(ErrorCode.API_ERROR_NO_AUTHORIZATION, exception.getErrorCode());
        verify(savingsRepository, never()).save(any(Savings.class));
    }

    @Test
    @DisplayName("[적금/삭제] 정상적으로 적금 상품을 정상적으로 삭제하는 경우 -> 성공")
    void 적금상품_삭제_정상() {
        User parent = User.builder().build();
        parent.setUserUuid();
        Savings savings = Savings.builder()
                .parent(parent)
                .build();
        savings.setSavingUuid();

        when(userService.userFindByEmail()).thenReturn(parent);
        when(savingsService.findByUuid(savings.getSavingUuid().toString())).thenReturn(savings);
        when(accountService.findAllSavingsBySaving(savings)).thenReturn(Collections.emptyList());

        SavingsUuidRequest request = SavingsUuidRequest.builder()
                .savingsUuid(savings.getSavingUuid().toString())
                .build();

        savingFacade.deleteSavings(request);

        assertTrue(savings.isDeleted());
    }

    @Test
    @DisplayName("[적금/삭제] 부모가 아닌 사용자가 적금 상품을 삭제하려고 할 때 -> 예외 발생")
    void 적금상품_삭제_소유주_아닌경우() {
        User owner = User.builder().build();
        owner.setUserUuid();
        Savings savings = Savings.builder().parent(User.builder().build()).build();
        savings.setSavingUuid();

        User nonOwner = User.builder().build();
        nonOwner.setUserUuid();

        when(userService.userFindByEmail()).thenReturn(nonOwner);
        when(savingsService.findByUuid(savings.getSavingUuid().toString())).thenReturn(savings);

        SavingsUuidRequest request = SavingsUuidRequest.builder()
                .savingsUuid(savings.getSavingUuid().toString())
                .build();

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            savingFacade.deleteSavings(request);
        });

        assertEquals(ErrorCode.API_ERROR_NO_AUTHORIZATION, exception.getErrorCode());
        assertFalse(savings.isDeleted());
    }

    @Test
    @DisplayName("[적금/삭제] 적금 상품에 자식들이 가입되어 있어 삭제할 수 없는 경우 -> 예외 발생")
    void 적금상품_삭제_상품에_가입된_경우() {
        User parentUser = User.builder().build();
        parentUser.setUserUuid();
        Savings savings = Savings.builder().parent(parentUser).build();
        savings.setSavingUuid();

        List<Account> childAccounts = List.of(Account.builder().build());

        when(userService.userFindByEmail()).thenReturn(parentUser);
        when(savingsService.findByUuid(savings.getSavingUuid().toString())).thenReturn(savings);
        when(accountService.findAllSavingsBySaving(savings)).thenReturn(childAccounts);

        SavingsUuidRequest request = SavingsUuidRequest.builder()
                .savingsUuid(savings.getSavingUuid().toString()).build();

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            savingFacade.deleteSavings(request);
        });

        assertEquals(ErrorCode.API_ERROR_SAVINGS_DELETE, exception.getErrorCode());
        assertFalse(savings.isDeleted());
    }

    @Test
    @DisplayName("[적금/조회] 부모가 있는 경우 적금 상품 조회 -> 성공")
    void 적금상품_조회_부모가_있는경우() {
        User parent = User.builder().build();
        User child = User.builder().build();
        child.addParent(parent);

        Savings savings = Savings.builder()
                .interest(2.5)
                .period(12)
                .name("SavingsSearchTest")
                .parent(parent)
                .build();

        savings.setSavingUuid();
        when(userService.userFindByEmail()).thenReturn(child);
        when(savingsService.findAllByParents(parent)).thenReturn(Arrays.asList(savings));

        AllSavingsProductResponse response = savingFacade.getAllSavingProduct();

        assertNotNull(response);
        assertEquals(1, response.getSavingsList().size());

        SavingsDetail makedSavings = response.getSavingsList().get(0);
        assertEquals(savings.getSavingUuid().toString(), makedSavings.getSavingsUuid());
        assertEquals(savings.getInterest(), makedSavings.getInterest());
        assertEquals(savings.getPeriod(), makedSavings.getPeriod());
        assertEquals(savings.getName(), makedSavings.getName());
        assertEquals(savings.getCreatedAt(), makedSavings.getCreatedAt());
    }

    @Test
    @DisplayName("[적금/조회] 부모가 적금 상품을 조회하려고 할 때 -> 예외 발생")
    void 적금상품_조회_부모가_조회하는경우() {
        User parent = User.builder().build();

        when(userService.userFindByEmail()).thenReturn(parent);

        doThrow(BusinessException.of(ErrorCode.API_ERROR_NO_AUTHORIZATION))
                .when(savingsService).findAllByParents(parent);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            savingFacade.getAllSavingProduct();
        });

        assertEquals(ErrorCode.API_ERROR_NO_AUTHORIZATION, exception.getErrorCode());
    }

    @Test
    @DisplayName("[적금/상세조회] 적금 상품을 정상적으로 조회할 경우 -> 성공")
    void 적금상품_조회_정상() {
        Savings savings = Savings.builder()
                .interest(2.5)
                .period(12)
                .name("Savings Test")
                .build();
        savings.setSavingUuid();

        when(savingsService.findByUuid(savings.getSavingUuid().toString())).thenReturn(savings);

        SavingsUuidRequest request = SavingsUuidRequest.builder()
                .savingsUuid(savings.getSavingUuid().toString())
                .build();

        SavingsDetail savingsDetail = savingFacade.getSavingsProduct(request);

        assertNotNull(savingsDetail);
        assertEquals(savings.getSavingUuid().toString(), savingsDetail.getSavingsUuid());
        assertEquals(savings.getInterest(), savingsDetail.getInterest());
        assertEquals(savings.getPeriod(), savingsDetail.getPeriod());
        assertEquals(savings.getName(), savingsDetail.getName());
        assertEquals(savings.getCreatedAt(), savingsDetail.getCreatedAt());
    }

    @Test
    @DisplayName("[적금/상세조회]존재하지 않는 UUID로 조회할 경우 -> 예외 발생")
    void 적금상품_조회_존재하지않는_UUID() {
        Savings savings = Savings.builder().build();
        savings.setSavingUuid();

        when(savingsService.findByUuid(savings.getSavingUuid().toString()))
                .thenThrow(BusinessException.of(ErrorCode.API_ERROR_SAVINGS_NOT_EXIST));

        SavingsUuidRequest request = SavingsUuidRequest.builder()
                .savingsUuid(savings.getSavingUuid().toString())
                .build();

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            savingFacade.getSavingsProduct(request);
        });

        assertEquals(ErrorCode.API_ERROR_SAVINGS_NOT_EXIST, exception.getErrorCode());
    }

    @Test
    @DisplayName("[적금/가입] 정상적으로 적금 가입이 이루어질 때 -> 성공")
    void 적금상품_가입_정상_성공() {
        User parent = User.builder().build();
        parent.setUserUuid();
        User child = User.builder().build();
        child.setUserUuid();
        child.addParent(parent);

        Savings savings = Savings.builder()
                .parent(parent)
                .build();
        savings.setSavingUuid();

        JoinSavingsRequest joinSavingsRequest = JoinSavingsRequest.builder()
                .savingsUuid(savings.getSavingUuid().toString())
                .paymentAmount(10000)
                .paymentDate(15)
                .build();

        Code typeCode = Code.builder().name("적금계좌").id("AT002").build();
        Code statusCode = Code.builder().name("정상").id("AS002").build();

        Account expectedAccount = Account.builder()
                .accountNumber("4949-4949-4949")
                .balance(0)
                .user(child)
                .typeCode(typeCode)
                .statusCode(statusCode)
                .interestAmount(0)
                .paymentCycle(0)
                .paymentAmount(10000)
                .paymentDate(15)
                .savings(savings)
                .build();

        when(savingsService.findByUuid(savings.getSavingUuid().toString())).thenReturn(savings);
        when(accountService.createSavingsAccount(savings, 0, 15, 10000)).thenReturn(expectedAccount);

        savingFacade.joinSavings(joinSavingsRequest);
    }

    @Test
    @DisplayName("[적금] 자녀가 있는 경우 적금 계좌 리스트 반환 -> 성공")
    void 적금상품_가입상품조회_자녀가_가입한경우() {
        User parent = User.builder().build();
        User child1 = User.builder().name("Child1").build();
        User child2 = User.builder().name("Child2").build();

        Account account1 = Account.builder()
                .savings(Savings.builder().period(12).interest(2.5).build())
                .paymentAmount(1000)
                .interestAmount(50)
                .statusCode(Code.builder().name("정상").build())
                .balance(1050)
                .build();
        account1.setAccountUuid();
        account1.setCreatedAt();

        Account account2 = Account.builder()
                .savings(Savings.builder().period(24).interest(3.0).build())
                .paymentAmount(2000)
                .interestAmount(150)
                .statusCode(Code.builder().name("정상").build())
                .balance(2150)
                .build();
        account2.setAccountUuid();
        account2.setCreatedAt();

        when(userService.userFindByEmail()).thenReturn(parent);
        when(userService.findMyKidsByParents(parent)).thenReturn(Arrays.asList(child1, child2));
        when(accountService.findAllSavingsByChild(child1)).thenReturn(List.of(account1));
        when(accountService.findAllSavingsByChild(child2)).thenReturn(List.of(account2));

        MyChildSavingsListResponse response = savingFacade.getMyChildSavings();

        assertNotNull(response);
        assertEquals(2, response.getSavingsDetailListByChild().size());
        assertEquals("Child1", response.getSavingsDetailListByChild().get(0).getUserName());
        assertEquals("Child2", response.getSavingsDetailListByChild().get(1).getUserName());
    }

    @Test
    @DisplayName("[적금] 자녀가 없는 경우 -> 예외 발생")
    void 자녀가_없는_경우_예외() {
        User parent = User.builder().build();

        when(userService.userFindByEmail()).thenReturn(parent);
        when(userService.findMyKidsByParents(parent))
                .thenThrow(BusinessException.of(ErrorCode.API_ERROR_USER_NOT_EXIST_MY_KIDS));

        assertThrows(BusinessException.class, () -> {
            savingFacade.getMyChildSavings();
        });
    }


    @Test
    @DisplayName("[적금] 적금 계좌에 정상적으로 입금이 수행되는 경우")
    void 적금계좌_입금_성공() {
        int initialPaymentCycle = 1;
        int initialBalance = 5000;
        int depositCount = 2;
        int depositAmount = 2000;

        savingsAccount.depositSavings(depositCount, depositAmount);

        assertEquals(initialPaymentCycle + depositCount, savingsAccount.getPaymentCycle());
        assertEquals(initialBalance + depositAmount, savingsAccount.getBalance());
    }


    @Test
    @DisplayName("[적금] 사용자의 계좌 잔액이 부족하여 입금이 실패하는 경우")
    void 적금입금_잔액부족_예외() {
        when(userService.userFindByEmail()).thenReturn(user);
        when(accountService.findByAccountUuid(savingsAccount.getAccountUuid().toString())).thenReturn(savingsAccount);
        when(accountService.findByUser(user)).thenReturn(checkingAccount);
        checkingAccount.updateAccount(2000);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            savingFacade.depositToSavings(depositRequest);
        });

        assertEquals(ErrorCode.API_ERROR_ACCOUNT_INSUFFICIENT_BALANCE, exception.getErrorCode());
    }

    @Test
    @DisplayName("[적금] 적금 해지 시 입출금 계좌로 송금 및 거래 내역이 정상적으로 기록되는 경우")
    void 적금해지_성공() {
        // Arrange
        when(userService.userFindByEmail()).thenReturn(user);
        when(accountService.findByAccountUuid(savingsAccount.getAccountUuid().toString())).thenReturn(savingsAccount);
        when(accountService.findByUser(user)).thenReturn(checkingAccount);
        when(codeService.findStatusCode("해지")).thenReturn(new Code("AS003", "해지"));
        when(codeService.findTypeCode("적금이체")).thenReturn(new Code("ADT002", "적금이체"));
        when(codeService.findStatusCode("거래완료")).thenReturn(new Code("ADS001", "거래완료"));

        CancelSavingsRequest cancelSavingsRequest = CancelSavingsRequest.builder()
                .accountUuid(savingsAccount.getAccountUuid().toString())
                .build();

        savingFacade.cancelSavings(cancelSavingsRequest);

        assertEquals(7000, checkingAccount.getBalance());
        verify(accountDetailService, times(1)).save(any(AccountDetail.class));
        assertEquals("해지", savingsAccount.getStatusCode().getName());
    }

    @Test
    @DisplayName("[적금] 정상적으로 적금 만기 해지가 수행되는 경우 -> 성공")
    void 적금만기해지_성공() {
        CancelSavingsRequest cancelSavingsRequest = CancelSavingsRequest.builder()
                .accountUuid(savingsAccount.getAccountUuid().toString())
                .build();

        when(userService.userFindByEmail()).thenReturn(user);
        when(accountService.findByUser(parent)).thenReturn(parentAccount);
        when(accountService.findByUser(user)).thenReturn(checkingAccount);
        when(accountService.findExpiredSavingsAccountByUuid(savingsAccount.getAccountUuid().toString())).thenReturn(savingsAccount);

        AccountDetail accountDetail = AccountDetail.builder()
                .balance(50000)
                .amount(1000)
                .build();
        when(accountDetailService.findSavingsAccountDetail(savingsAccount)).thenReturn(Collections.singletonList(
                accountDetail
        ));
        accountDetail.setCreatedAt();

        parentAccount.updateAccount(800);
        savingFacade.terminateSavings(cancelSavingsRequest);

        assertEquals(0, savingsAccount.getBalance());  // 적금 계좌 잔액 0으로 해지 처리
        assertEquals(savingsAccount.getTypeCode().getName(), "해지");  // 적금 계좌 상태가 해지 상태인지 확인

        verify(accountDetailService, times(3)).save(any(AccountDetail.class));
    }

    @Test
    @DisplayName("[적금] 적금 계좌 상세 조회 -> 성공")
    void getMySavingsDetail_성공() {
        user = User.builder()
                .email("test@example.com")
                .build();
        user.setCreatedAt();
        user.setUserUuid();

        Savings savings = Savings.builder()
                .name("Test Savings")
                .interest(2.5)
                .period(12)
                .build();
        savings.setSavingUuid();

        savingsAccount = Account.builder()
                .accountNumber("123-456-789")
                .balance(10000)
                .paymentAmount(1000)
                .paymentDate(15)
                .paymentCycle(5)
                .statusCode(new Code("AS002", "정상"))
                .savings(savings)
                .build();
        savingsAccount.setCreatedAt();
        savingsAccount.setAccountUuid();
        CancelSavingsRequest request = CancelSavingsRequest.builder()
                .accountUuid(savingsAccount.getAccountUuid().toString())
                .build();

        when(accountService.findByAccountUuid(savingsAccount.getAccountUuid().toString())).thenReturn(savingsAccount);
        when(userService.userFindByEmail()).thenReturn(user);

        MySavings mySavings = savingFacade.getMySavingsDetail(request);

        assertNotNull(mySavings);
        assertEquals(savingsAccount.getAccountNumber(), mySavings.getAccountNumber());
        assertEquals(savingsAccount.getAccountUuid().toString(), mySavings.getAccountUuid());
        assertEquals(savingsAccount.getBalance(), mySavings.getBalance());
        assertEquals(savingsAccount.getStatusCode().getName(), mySavings.getState());
        assertEquals(savingsAccount.getPaymentAmount(), mySavings.getPaymentAmount());
        assertEquals(savingsAccount.getPaymentDate(), mySavings.getPaymentDate());
        assertEquals(savingsAccount.getPaymentCycle(), mySavings.getPaymentCycle());
        assertEquals(savingsAccount.getCreatedAt(), mySavings.getCreatedAt());
        assertEquals(savings.getInterest(), mySavings.getSavingsInterest());
        assertEquals(savings.getPeriod(), mySavings.getSavingsPeriod());
        assertEquals(savings.getName(), mySavings.getSavingsName());
        assertEquals(savingsAccount.getCreatedAt().plusMonths(savings.getPeriod()), mySavings.getExpiredAt());

        LocalDate today = LocalDate.now();
        LocalDate startedAt = savingsAccount.getCreatedAt().toLocalDate().withDayOfMonth(savingsAccount.getPaymentDate());
        Period period = Period.between(startedAt, today);
        int expectedOverdueCount = period.getMonths() + 1 > savingsAccount.getPaymentCycle() ? period.getMonths() + 1 - savingsAccount.getPaymentCycle() : 0;

        assertEquals(expectedOverdueCount, mySavings.getOverdueCnt());
    }
}
