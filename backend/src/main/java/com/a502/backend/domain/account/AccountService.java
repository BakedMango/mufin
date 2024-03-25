package com.a502.backend.domain.account;

import com.a502.backend.application.entity.Account;
import com.a502.backend.application.entity.Code;
import com.a502.backend.application.entity.Savings;
import com.a502.backend.application.entity.User;
import com.a502.backend.domain.account.dto.DepositWithdrawalAccountDto;
import com.a502.backend.domain.parking.ParkingService;
import com.a502.backend.domain.user.UserService;
import com.a502.backend.global.code.CodeRepository;
import com.a502.backend.global.code.CodeService;
import com.a502.backend.global.error.BusinessException;
import com.a502.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class AccountService {


    private final AccountRepository accountRepository;
    private final CodeService codeService;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final ParkingService parkingService;


    // 입출금 계좌 생성 메소드
    public DepositWithdrawalAccountDto createDepositWithdrawalAccount(String password) {

        User user = userService.userFindByEmail();

        String encodedPassword = passwordEncoder.encode(password);
        String accountNumber = generateUniqueAccountNumber(false);
        Code typeCode = codeService.findTypeCode("입출금");
        Code statusCode = codeService.findTypeCode("정상");

        Account account = Account.builder()
                .password(encodedPassword)
                .accountNumber(accountNumber)
                .balance(new AtomicInteger(0))
                .user(user)
                .typeCode(typeCode)
                .statusCode(statusCode)
                .build();

        Account createdAccount = saveAccount(account);

        DepositWithdrawalAccountDto accountDto = DepositWithdrawalAccountDto.builder()
                .accountNumber(createdAccount.getAccountNumber())
                .balance(createdAccount.getBalance())
                .interestAmount(createdAccount.getInterestAmount())
                .build();

        return accountDto;
    }

    // 적금 계좌 생성 메소드
    public Account createSavingsAccount(Savings savings,int cycle,int date,int ammount, String password ) {

        User user = userService.userFindByEmail();

        String encodedPassword = passwordEncoder.encode(password);

        String accountNumber = generateUniqueAccountNumber(true);

        Code typeCode = codeService.findTypeCode("적금");
        Code statusCode = codeService.findTypeCode("정상");


        Account account = Account.builder()
                .password(encodedPassword)
                .accountNumber(accountNumber)
                .balance(new AtomicInteger(0))
                .user(user)
                .typeCode(typeCode)
                .statusCode(statusCode)
                .interestAmount(0)
                .paymentCycle(cycle)
                .paymentAmount(ammount)
                .paymentDate(date)
                .build();

        Account createdAccount =  accountRepository.save(account);

        return createdAccount;
    }


    private Account saveAccount(Account account) {

        return accountRepository.save(account);
    }

    // 중복되지 않는 계좌 번호 생성
    private String generateUniqueAccountNumber(boolean isSavingsAccount) {
        String accountNumber;
        do {
            accountNumber = generateAccountNumber(isSavingsAccount);

        } while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }
    // 계좌 번호 생성 함수
    public static String generateAccountNumber(boolean isSavingsAccount) {

        Random random = new Random();

        // 적금 계좌인 경우 "5022", 입출금 계좌인 경우 "5021"로 시작
        String prefix = isSavingsAccount ? "5022" : "5021";
        // 랜덤 4자리 숫자 생성
        String middle1 = String.format("%04d", random.nextInt(10000));
        String middle2 = String.format("%02d", random.nextInt(100));
        String end = String.format("%04d", random.nextInt(10000));

        // 생성된 부분들을 하이픈(-)으로 연결
        return prefix + "-" + middle1 + "-" + middle2 + "-" + end;
    }
}
