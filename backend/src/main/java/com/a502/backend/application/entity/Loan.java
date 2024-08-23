package com.a502.backend.application.entity;

import com.a502.backend.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "loans")
public class Loan extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "loan_id")
	private int id;

	@Column(name = "loan_uuid")
	private UUID loanUuid;

	@Column(name = "amount")
	private int amount;

	@Column(name = "reason")
	private String reason;

	@Column(name = "payment_date")
	private int paymentDate;

	@Column(name = "penalty")
	private String penalty;

	@Column(name = "payment_total_cnt")
	private int paymentTotalCnt;

	@Column(name = "payment_now_cnt")
	private int paymentNowCnt;

	@Column(name = "overdue_cnt")
	private int overdueCnt;

	@Column(name = "start_date")
	private LocalDate startDate;

	@ManyToOne
	@JoinColumn(name = "child_id")
	private User child;

	@ManyToOne
	@JoinColumn(name = "parent_id")
	private User parent;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "loan_conversation_id")
	private LoanConversation loanConversation;

	// L001심사중, L002진행, L003거절 ,L004상환완료
	@ManyToOne
	@JoinColumn(name = "code_id")
	private Code code;

	@Builder
	public Loan(int amount, String reason, int paymentDate, String penalty, int paymentTotalCnt, LocalDate startDate, User child, User parent, LoanConversation loanConversation, Code code) {
		this.amount = amount;
		this.reason = reason;
		this.paymentDate = paymentDate;
		this.penalty = penalty;
		this.paymentTotalCnt = paymentTotalCnt;
		this.startDate = startDate;
		this.child = child;
		this.parent = parent;
		this.code = code;
		this.loanConversation = loanConversation;
	}

	@PrePersist
	public void initUUID() {
		if (loanUuid == null)
			loanUuid = UUID.randomUUID();
	}

	// 대출 상환시
	public boolean repayLoan(int cnt) {
		this.paymentNowCnt += cnt;
		if (this.overdueCnt > 0) {
			this.overdueCnt -= cnt;
			if (this.overdueCnt < 0)
				this.overdueCnt = 0;
		}
		if (this.paymentNowCnt == this.paymentTotalCnt && overdueCnt == 0) {
			return true;
		}
		return false;
	}

	public void completeLoan(Code code) {
		this.code = code;
	}

	public void startLoan(LocalDate startDate, Code code) {
		this.startDate = startDate;
		this.code = code;
	}

	public void refuseLoan(Code code) {
		this.code = code;
	}

	public void updateOverdueCnt(int cnt){
		this.overdueCnt = cnt;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Loan loan = (Loan) o;
		return getId() == loan.getId();
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId());
	}
}
