package com.a502.backend.domain.loan;

import com.a502.backend.application.entity.Loan;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LoansRepositoryTest {

    @Autowired
    private LoansRepository loansRepository;

    @Autowired
    private LoanFactory loanFactory;

    @Test
    void save() {
        // given
        Loan loan = loanFactory.createLoan(1000, 30, 10, "kim");

        // when
        Loan savedLoan = loansRepository.save(loan);

        // then
        assertThat(loan).isEqualTo(savedLoan);
    }

    @Test
    void findByUUID_Exists() {
        // given
        Loan loan = loanFactory.createAndSaveLoan(1000, 30, 10, "kim");

        // when
        Loan savedLoan = loansRepository.findByUuid(loan.getLoanUuid()).get();

        // then
        assertThat(loan)
                .isNotNull()
                .isEqualTo(savedLoan);
    }

    @Test
    void findByUUID_NotExists() {
        // given
        Optional<Loan> optionalLoan = loansRepository.findByUuid(UUID.randomUUID());

        // when&then
        assertTrue(optionalLoan.isEmpty());
        assertThrows(NoSuchElementException.class, optionalLoan::get);
    }
}