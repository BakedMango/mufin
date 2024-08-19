package com.a502.backend.application.entity;

import com.a502.backend.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.context.annotation.Profile;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "savings")
public class Savings extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "saving_id")
	private int id;

	@Column(name = "saving_uuid")
	private UUID savingUuid;
	@PrePersist
	public void initUUID() {
		if (savingUuid == null)
			savingUuid = UUID.randomUUID();
	}

	@Column(name = "interest")
	private double interest;

	@Column(name = "period")
	private int period;

	@Column(name = "name")
	private String name;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User parent;

	@Builder
	public Savings(double interest, int period, String name, User parent) {
		this.interest = interest;
		this.period = period;
		this.name = name;
		this.parent = parent;
	}

	public void delete(){
		this.setDeleted(true);
	}

	@Profile("test")
	public void setSavingUuid() {
		this.savingUuid = UUID.randomUUID();
	}
}
