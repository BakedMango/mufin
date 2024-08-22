package com.a502.backend.domain.loan;

import com.a502.backend.application.entity.Loan;
import com.a502.backend.application.entity.User;
import com.a502.backend.domain.user.UserFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class LoanFactory {

    @Autowired
    LoansRepository loansRepository;

    @Autowired
    UserFactory userFactory;

    public Loan createAndSaveLoan(int amount, int period, int count, String childName) {

        User user = userFactory.createAndSaveUser(childName);
        Loan loan = Loan.builder()
                .amount(amount)
                .reason("Test Reason")
                .paymentDate(period)
                .penalty("None")
                .paymentTotalCnt(count)
                .startDate(LocalDate.now())
                .child(user)
                .build();

        return loansRepository.save(loan);
    }

    public Loan createLoan(int amount, int period, int count, String childName) {

        User user = userFactory.createAndSaveUser(childName);
        return Loan.builder()
                .amount(amount)
                .reason("Test Reason")
                .paymentDate(period)
                .penalty("None")
                .paymentTotalCnt(count)
                .startDate(LocalDate.now())
                .child(user)
                .build();
    }
}
