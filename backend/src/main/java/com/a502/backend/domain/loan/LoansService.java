package com.a502.backend.domain.loan;

import com.a502.backend.application.entity.Code;
import com.a502.backend.application.entity.Loan;
import com.a502.backend.application.entity.User;
import com.a502.backend.global.error.BusinessException;
import com.a502.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
//@Transactional(readOnly = true)
@Service
public class LoansService {
	private final LoansRepository loansRepository;

	public Loan findById(int id) {
		return loansRepository.findById(id).orElseThrow(() -> BusinessException.of(ErrorCode.API_ERROR_LOAN_NOT_EXIST));
	}

	public Loan saveLoan(Loan loan) {
		return loansRepository.save(loan);
	}

	public List<Loan> getAllLoansForChild(User child) {
		List<Loan> loans = loansRepository.findAllLoansInProgressByUser(child);
		if (loans.isEmpty()) {
			throw BusinessException.of(ErrorCode.API_ERROR_LOAN_NOT_EXIST);
		}
		return loans;
	}

	public Loan findByUuid(String loanUuid) {
		UUID Uuid = UUID.fromString(loanUuid);
		return loansRepository.findByUuid(Uuid).orElseThrow(() -> BusinessException.of(ErrorCode.API_ERROR_LOAN_NOT_EXIST));
	}

	public List<Loan> getAllLoansForParents(User parents) {
		List<Loan> result = loansRepository.findAllLoansInProgressByParents(parents);
		if (result.isEmpty())
			throw BusinessException.of(ErrorCode.API_ERROR_LOAN_NOT_EXIST_FOR_PARENTS);
		return result;
	}

	public List<Loan> getRequestedLoansForParents(User parents) {
		List<Loan> result = loansRepository.findRequestedLoansByParents(parents);
		if (result.isEmpty())
			throw BusinessException.of(ErrorCode.API_ERROR_LOAN_REQUESTED_NOT_EXIST_FOR_PARENTS);
		return result;
	}

	public List<Loan> findAllLoansInProgress() {
		return loansRepository.findAllLoansInProgress();
	}

	public void updateOverdueCnt(Loan loan) {
		loan.updateOverdueCnt(loan.getOverdueCnt() + 1);
		loansRepository.saveAndFlush(loan);
	}

	public void refuseLoan(Loan loan, Code code) {
		loan.refuseLoan(code);
		loansRepository.saveAndFlush(loan);
	}


	public List<Loan> findLoansByUserAndCode(User user, Code code) {
		return loansRepository.findByChildAndCode(user, code);
	}

	public boolean terminateLoan(Loan loan, int cnt) {
		return loan.repayLoan(cnt);
	}
}
