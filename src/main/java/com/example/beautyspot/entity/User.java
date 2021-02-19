package com.example.beautyspot.entity;

import com.example.beautyspot.entity.enums.ERole;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Data
@Entity
public class User implements UserDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String firstName;

	@Column(nullable = false)
	private String lastName;

	@Column(unique = true, updatable = false)
	private String userName;

	@Column(length = 3000)
	private String password;

	@Column(unique = true)
	private String email;

	@Column(length = 15)
	private String phone;

	@Column(columnDefinition = "text")
	private String biography;

	@ElementCollection(targetClass = ERole.class)
	@CollectionTable(name = "user_role",
			joinColumns = @JoinColumn(name = "user_id"))
	private Set<ERole> roles = new HashSet<>();

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "user", orphanRemoval = true)
	private List<Post> posts = new ArrayList<>();

	@JsonFormat(pattern = "yyyy-mm-dd HH-mm-ss")
	@Column(updatable = false)
	private LocalDateTime createdDate;

	@Transient
	private Collection<? extends GrantedAuthority> authorities;

	public User() {
	}

	public User(Long id,
				String userName,
				String password,
				String email,
				Collection<? extends GrantedAuthority> authorities) {
		this.id = id;
		this.userName = userName;
		this.password = password;
		this.email = email;
		this.authorities = authorities;
	}

	@PrePersist
	protected void onCreate() {
		this.createdDate = LocalDateTime.now();
	}


	/**
	 *SECURITY
	 */

	@Override
	public String getUsername() {
		return userName;
	}

	@Override
	public String getPassword(){
		return password;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
