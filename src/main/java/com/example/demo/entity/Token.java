package com.example.demo.entity;


import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//@Getter
//@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Token {
	@Id
	@GeneratedValue
	public Integer id;
	@Column(unique = true)
	public String token;
	@Enumerated(EnumType.STRING)
	public TokenType tokenType = TokenType.BEARER;
	public boolean revoked;
	public boolean expired;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	@JsonBackReference
	public User user;

}
