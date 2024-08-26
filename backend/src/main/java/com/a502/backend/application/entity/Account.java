package com.a502.backend.application.entity;

import com.a502.backend.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Profile;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "accounts")
public class Account extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "account_id")
	private int id;

	@Column(name = "account_uuid")
	private UUID accountUuid;
	@Column(name = "account_number")
	private String accountNumber;
	@Column(name = "balance")
	private int balance;
	@Column(name = "interestAmount")
	private int interestAmount; //이자수령액
	@Column(name = "payment_amount")
	private int paymentAmount;
	@Column(name = "payment_date")
	private int paymentDate;
	@Column(name = "payment_cycle")
	private int paymentCycle;
	@Column(name = "password")
	private String password;
	@Column(name = "incorrect_cnt")
	private int incorrectCount;
	@ManyToOne
	@JoinColumn(name = "saving_id")
	private Savings savings;
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	@ManyToOne
	@JoinColumn(name = "status_code_id")
	private Code statusCode;
	@ManyToOne
	@JoinColumn(name = "type_code_id")
	private Code typeCode;

	@Builder
	public Account(String accountNumber, int balance, int interestAmount, int paymentAmount, int paymentDate, int paymentCycle, String password, int incorrectCount, User user, Code statusCode, Code typeCode, Savings savings) {
		this.accountNumber = accountNumber;
		this.balance = balance;
		this.interestAmount = interestAmount;
		this.paymentAmount = paymentAmount;
		this.paymentDate = paymentDate;
		this.paymentCycle = paymentCycle;
		this.password = password;
		this.incorrectCount = incorrectCount;
		this.user = user;
		this.statusCode = statusCode;
		this.typeCode = typeCode;
		this.savings = savings;
	}

	@PrePersist
	public void initUUID() {
		if (accountUuid == null)
			accountUuid = UUID.randomUUID();
	}

	public void updateAccount(int balance) {
		this.balance = balance;
	}

	public void updateCode(Code code) {
		this.statusCode = code;
	}

	public int updateIncorectCnt(int cnt) {
		return this.incorrectCount = cnt;
	}

	public void depositSavings(int cnt, int amount) {
		this.paymentCycle += cnt;
		this.balance += amount;
	}

	public void cancelSavings(Code statusCode) {
		this.statusCode = statusCode;
		this.balance = 0;
		this.setDeleted(true);
	}

	public void terminateSavings(int interestAmount, Code code) {
		this.balance = 0;
		this.interestAmount = interestAmount;
		this.setDeleted(true);
		this.statusCode = code;
	}

	@Profile("test")
	public void setAccountUuid() {
		this.accountUuid = UUID.randomUUID();
	}
}
