package com.a502.backend.application.entity;

import com.a502.backend.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private int id;

	@Column(name = "user_uuid")
	private UUID userUuid;
	@PrePersist
	public void initUUID() {
		if (userUuid == null)
			userUuid = UUID.randomUUID();
	}

	@Column(name = "name")
	private String name;

	@Column(name = "email")
	private String email;

	@Column(name = "password")
	private String password;

	@Column(name = "gender")
	private String gender;

	@Column(name = "address")
	private String address;

	@Column(name = "address2")
	private String address2;

	@Column(name = "telephone")
	private String telephone;

	@Column(name = "birth")
	private LocalDate birth;

	@Column(name = "failed")
	private int failed;

	@ManyToOne
	@JoinColumn(name = "parent_id")
	private User parent;

	@OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE)
	private List<User> childrens = new ArrayList<>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
	private List<Account> accounts = new ArrayList<>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Alert> alerts = new ArrayList<>();

	@OneToMany(mappedBy = "user")
	private List<StockHolding> stockHoldings = new ArrayList<>();

	@Builder
	public User(String name, String email, String password, String gender, String address, String address2, String telephone, LocalDate birth, User parent) {
		this.name = name;
		this.email = email;
		this.password = password;
		this.gender = gender;
		this.address = address;
		this.address2 = address2;
		this.telephone = telephone;
		this.birth = birth;
		this.parent = parent;
	}

	public boolean checkPassword(String password) {

		return this.password.equals(password);
	}

    public void addParent(User parent) {
		this.parent = parent;
    }

	@Profile("test")
	public void setUserUuid() {
		this.userUuid = UUID.randomUUID();

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		User user = (User) o;
		return getId() == user.getId();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getId());
	}
}
