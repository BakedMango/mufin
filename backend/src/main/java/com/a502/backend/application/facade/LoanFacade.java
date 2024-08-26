package com.a502.backend.application.facade;

import com.a502.backend.application.entity.*;
import com.a502.backend.domain.account.AccountDetailService;
import com.a502.backend.domain.account.AccountService;
import com.a502.backend.domain.loan.LoanConversationService;
import com.a502.backend.domain.loan.LoanDetailService;
import com.a502.backend.domain.loan.LoanRefusalService;
import com.a502.backend.domain.loan.LoansService;
import com.a502.backend.domain.loan.Request.ApplyLoanRequest;
import com.a502.backend.domain.loan.Request.LoanUuidRequest;
import com.a502.backend.domain.loan.Request.RefuseLoanRequest;
import com.a502.backend.domain.loan.Request.RepayLoanRequest;
import com.a502.backend.domain.loan.Response.*;
import com.a502.backend.domain.user.UserService;
import com.a502.backend.global.code.CodeService;
import com.a502.backend.global.error.BusinessException;
import com.a502.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoanFacade {
	private final LoansService loansService;
	private final LoanRefusalService loanRefusalService;
	private final UserService userService;
	private final LoanConversationService loanConversationService;
	private final CodeService codeService;
	private final AccountService accountService;
	private final AccountDetailService accountDetailService;
	private final LoanDetailService loanDetailService;

	public void applyLoan(ApplyLoanRequest applyLoanRequest) {
		User child = userService.userFindByEmail();
		User parent = child.getParent();
		String reason = applyLoanRequest.getReason();
		int amount = applyLoanRequest.getAmount();
		int paymentTotalCnt = applyLoanRequest.getPaymentTotalCnt();
		int paymentDate = applyLoanRequest.getPaymentDate();
		String conversation = applyLoanRequest.getConversation();
		String penalty = applyLoanRequest.getPenalty();
		Code code = codeService.findStatusCode("심사중");
		// gpt 대화
		LoanConversation loanConversation = LoanConversation.builder()
				.content(conversation)
				.user(child)
				.build();

		loanConversationService.saveLoanConversation(loanConversation);
		LoanConversation loanConversationForLoan = loanConversationService.findByUserLast(child);
		// 대출
		Loan loan = Loan.builder()
				.reason(reason)
				.paymentTotalCnt(paymentTotalCnt)
				.paymentDate(paymentDate)
				.amount(amount)
				.penalty(penalty)
				.loanConversation(loanConversationForLoan)
				.child(child)
				.parent(parent)
				.code(code)
				.build();

		loansService.saveLoan(loan);
	}

	public LoanListForChildResponse getAllLoansForChild() {
		// 아이
		User child = userService.userFindByEmail();
		// 가입한 대출 전체 조회(상환된 것 제외)
		List<Loan> loans = loansService.getAllLoansForChild(child);
		// 결과값 담을 리스트
		List<LoanList> loansList = new ArrayList<>();
		int totalRemainderAmount = 0;
		for (Loan l : loans) {
			int remainderAmount = l.getAmount() - l.getPaymentNowCnt() * (l.getAmount() / l.getPaymentTotalCnt());
			if (l.getCode().getId().equals("L002")) {
				totalRemainderAmount += remainderAmount;
			}
			LoanRefusal loanRefusal = loanRefusalService.findByLoan(l);
			LoanList loanDetail = LoanList.builder()
					.reason(l.getReason())
					.loanUuid(String.valueOf(l.getLoanUuid()))
					.amount(l.getAmount())
					.paymentTotalCnt(l.getPaymentTotalCnt())
					.paymentNowCnt(l.getPaymentNowCnt())
					.remainderAmount(remainderAmount)
					.status(codeService.findById(l.getCode().getId()).getName())
					.overDueCnt(l.getOverdueCnt())
					.build();
			if (loanRefusal != null) {
				loanDetail.updateLoanDetail(loanRefusal.getReason());
			}
			loansList.add(loanDetail);
		}
		return LoanListForChildResponse.builder().totalRemainderAmount(totalRemainderAmount).loansList(loansList).build();
	}

	public LoanDetailResponse getLoanDetailForChild(LoanUuidRequest loanUuidRequest) {

		String loanUuid = loanUuidRequest.getLoanUuid();

		Loan loan = loansService.findByUuid(loanUuid);
		String remainderDay = null;
		int totalCnt = loan.getPaymentTotalCnt();
		if (loan.getStartDate() != null) {
			LocalDate endDate = loan.getStartDate().plusMonths(totalCnt);
			Period period = Period.between(LocalDate.now(), endDate);
			remainderDay = period.getMonths() + "개월 " + period.getDays() + "일";
		}

		int amountByMonth = loan.getAmount() / loan.getPaymentTotalCnt();

		return LoanDetailResponse.builder()
				.reason(loan.getReason())
				.totalAmount(loan.getAmount())
				.remainderAmount(loan.getAmount() - loan.getPaymentNowCnt() * amountByMonth)
				.startDate(loan.getStartDate())
				.remainderDay(remainderDay)
				.paymentDate(loan.getPaymentDate())
				.paymentAmount(amountByMonth)
				.overDueCnt(loan.getOverdueCnt())
				.build();
	}

	@Transactional
	public void repayLoan(RepayLoanRequest repayLoanRequest) {
		String loanUuid = repayLoanRequest.getLoanUuid();
		int paymentCnt = repayLoanRequest.getPayment_cnt();
		Loan loan = loansService.findByUuid(loanUuid);
		User parents = loan.getParent();
		int payment = loan.getAmount() / loan.getPaymentTotalCnt();
		User user = userService.userFindByEmail();
		// 아이 계좌
		Account account = accountService.findByUser(user);
		// 부모 계좌
		Account parentsAccount = accountService.findByUser(parents);
		// 계좌 잔액이 적은 경우
		AtomicInteger atomicInteger = new AtomicInteger(account.getBalance());
		if (atomicInteger.addAndGet(-payment * paymentCnt) < 0)
			throw BusinessException.of(ErrorCode.API_ERROR_ACCOUNT_INSUFFICIENT_BALANCE);
		// 있다면 상환
		account.updateAccount(account.getBalance() - payment * paymentCnt);
		account.updateAccount(parentsAccount.getBalance() + payment * paymentCnt);
		// 대출 cnt 업데이트 및 전액 상환여부 체크
		boolean isFinal = loansService.terminateLoan(loan, paymentCnt);
		// 상환된 경우 업데이트
		if (isFinal)
			loan.completeLoan(codeService.findStatusCode("상환완료"));
		// 계좌 거래 내역 등록 //
		// 타입코드
		Code typeCode = codeService.findTypeCode("대출");
		// 상태코드
		Code statusCode = codeService.findStatusCode("거래완료");
		// 아이계좌 거래 내역 업데이트
		AccountDetail accountDetail = AccountDetail.builder()
				.amount(-payment * paymentCnt)
				.balance(account.getBalance())
				.counterpartyName("대출 상환  (" + loan.getPaymentNowCnt() + "/" + loan.getPaymentTotalCnt() + ")")
				.counterpartyAccount(parentsAccount.getAccountNumber())
				.account(account)
				.accountDetailTypeCode(typeCode)
				.accountDetailStatusCode(statusCode)
				.build();
		accountDetailService.save(accountDetail);
		// 부모 계좌 거래 내역 업데이트
		AccountDetail parentAccountDetail = AccountDetail.builder()
				.amount(payment * paymentCnt)
				.balance(parentsAccount.getBalance())
				.counterpartyName("대출금 입금  (" + loan.getPaymentNowCnt() + "/" + loan.getPaymentTotalCnt() + ")")
				.counterpartyAccount(account.getAccountNumber())
				.account(parentsAccount)
				.accountDetailTypeCode(typeCode)
				.accountDetailStatusCode(statusCode)
				.build();
		accountDetailService.save(parentAccountDetail);
		// 대출 납부 내역 등록(대출 , 거래내역 연결)
		LoanDetail loanDetail = LoanDetail.builder()
				.accountDetail(accountDetail)
				.loan(loan)
				.build();
		loanDetailService.save(loanDetail);
	}

	public AllLoansListForParentResponse getMyKidsLoans() {
		User user = userService.userFindByEmail();
		List<LoanDetailForParents> loanDetailList = new ArrayList<>();
		List<Loan> loanList = loansService.getAllLoansForParents(user);

		for (Loan l : loanList) {
			LocalDate endDate = null;
			//
			if (l.getStartDate().getDayOfMonth() > l.getPaymentDate()) {
				endDate = l.getStartDate().plusMonths(l.getPaymentTotalCnt()).withDayOfMonth(l.getPaymentDate());
			} else if (l.getStartDate().getDayOfMonth() < l.getPaymentDate()) {
				endDate = l.getStartDate().plusMonths(l.getPaymentTotalCnt() - 1).withDayOfMonth(l.getPaymentDate());
			} else {
				endDate = l.getStartDate().plusMonths(l.getPaymentTotalCnt());
			}
			LoanDetailForParents loanDetailForParents = LoanDetailForParents.builder()
					.childName(l.getChild().getName())
					.reason(l.getReason())
					.amount(l.getAmount())
					.paymentDate(l.getPaymentDate())
					.penalty(l.getPenalty())
					.paymentTotalCnt(l.getPaymentTotalCnt())
					.paymentNowCnt(l.getPaymentNowCnt())
					.statusCode(l.getCode().getName())
					.overdueCnt(l.getOverdueCnt())
					.startDate(l.getStartDate())
					.endDate(endDate)
					.build();
			loanDetailList.add(loanDetailForParents);
		}
		return AllLoansListForParentResponse.builder()
				.loansList(loanDetailList)
				.build();
	}

	public RequestedLoanDetailForParentsResponse getRequestedLoans() {
		User parent = userService.userFindByEmail();
		List<RequestedLoanDetail> requestedLoanDetail = new ArrayList<>();
		List<Loan> loans = loansService.getRequestedLoansForParents(parent);
		for (Loan l : loans) {
			String[] loanConversation = l.getLoanConversation().getContent().split("!#@#!");
			RequestedLoanDetail loanDetail = RequestedLoanDetail.builder()
					.childName(l.getChild().getName())
					.reason(l.getReason())
					.loanUuid(l.getLoanUuid().toString())
					.amount(l.getAmount())
					.paymentDate(l.getPaymentDate())
					.penalty(l.getPenalty())
					.paymentTotalCnt(l.getPaymentTotalCnt())
					.chatBotConversation(loanConversation)
					.build();
			requestedLoanDetail.add(loanDetail);
		}
		RequestedLoanDetailForParentsResponse result = RequestedLoanDetailForParentsResponse.builder()
				.loansList(requestedLoanDetail).build();
		return result;
	}

	@Transactional
	public void acceptLoan(LoanUuidRequest loanUuidRequest) {
		User parent = userService.userFindByEmail();
		String loanUuid = loanUuidRequest.getLoanUuid();
		// 대출 테이블 업데이트(시작일, 코드)
		Loan loan = loansService.findByUuid(loanUuid);
		Code code = codeService.findByName("진행중");
		loan.startLoan(LocalDate.now(), code);
		// 부모 계좌에서 송금(+돈 여부 체크)
		Account parentAccount = accountService.findByUser(parent);
		int parentBalance = parentAccount.getBalance();
		if (parentBalance < loan.getAmount()) {
			log.info("parent balance: " + parentBalance);
			log.info("loan balance: " + loan.getAmount());
			throw BusinessException.of(ErrorCode.API_ERROR_ACCOUNT_INSUFFICIENT_BALANCE);
		}
		parentAccount.updateAccount(parentBalance - loan.getAmount());
		// 아이 계좌 업데이트
		Account childAccount = accountService.findByUser(loan.getChild());
		int childBalance = childAccount.getBalance();
		log.info("child balance: " + childBalance);
		childAccount.updateAccount(childBalance + loan.getAmount());
		// 거래내역(부모, 아이) 테이블 업데이트
		// 부모
		accountDetailService.save(AccountDetail.builder()
				.amount(-loan.getAmount())
				.balance(parentAccount.getBalance())
				.counterpartyName(childAccount.getUser().getName())
				.counterpartyAccount(childAccount.getAccountNumber())
				.account(parentAccount)
				.accountDetailStatusCode(codeService.findByName("거래완료"))
				.accountDetailTypeCode(codeService.findByName("대출"))
				.build());
		// 아이
		accountDetailService.save(AccountDetail.builder()
				.amount(loan.getAmount())
				.balance(childAccount.getBalance())
				.counterpartyName(parentAccount.getUser().getName())
				.counterpartyAccount(parentAccount.getAccountNumber())
				.account(childAccount)
				.accountDetailStatusCode(codeService.findByName("거래완료"))
				.accountDetailTypeCode(codeService.findByName("대출"))
				.build());
	}

	public void refuseLoan(RefuseLoanRequest refuseLoanRequest) {
		String loanUuid = refuseLoanRequest.getLoanUuid();
		String reason = refuseLoanRequest.getReason();

		Loan loan = loansService.findByUuid(loanUuid);
		User parent = userService.userFindByEmail();

		// 대출 거절 사유 테이블 추가
		LoanRefusal loanRefusal = LoanRefusal.builder()
				.reason(reason)
				.loan(loan)
				.build();
		loanRefusalService.save(loanRefusal);

		// 대출 상태 = 거절 업데이트
		Code code = codeService.findStatusCode("거절");
		loansService.refuseLoan(loan, code);
	}
}
