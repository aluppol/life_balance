package com.luppol.life_balance.auth.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "\"User\"", schema = "auth")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", length = 256,  nullable = false, unique = true)
    private String username;

    @Column(name = "password", length = 256, nullable = false, updatable = false)
    private String password;

    @Column(name = "email", length = 256, nullable = false, unique = true, updatable = false)
    private String email;
}
