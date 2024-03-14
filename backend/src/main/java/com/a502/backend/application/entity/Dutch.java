package com.a502.backend.application.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "dutchs")
public class Dutch {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "dutch_id")
	private int id;

	@Column(name = "dutch_uuid")
	private byte[] dutchUuid;

	@Column(name = "total_person")
	private int totalPerson;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "received_persons_id")
	private User receivedUser;

	@Column(name = "received_persons")
	private int receivedPersonsCnt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "account_detail_id")
	private AccountDetail accountDetail;

	@Column(name = "is_deleted")
	private boolean isDeleted;

	@OneToMany(mappedBy = "dutchDetails", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DutchDetail> dutchDetails = new ArrayList<>();

	@Builder
	public Dutch(int id, byte[] dutchUuid, int totalPerson, User receivedUser, int receivedPersonsCnt, AccountDetail accountDetail, boolean isDeleted) {
		this.id = id;
		this.dutchUuid = dutchUuid;
		this.totalPerson = totalPerson;
		this.receivedUser = receivedUser;
		this.receivedPersonsCnt = receivedPersonsCnt;
		this.accountDetail = accountDetail;
		this.isDeleted = isDeleted;
	}
}
