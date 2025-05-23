package com.luppol.life_balance.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "Person")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 128)
    private String first_name;

    @Column(nullable = false, length = 128)
    private String last_name;

    @Column(length = 256)
    private String middle_name;

    @Column(length = 20)
    private String phone_number;

    @Column(length = 256)
    private String address;
    
    @OneToOne(fetch=FetchType.LAZY, optional=true)
    @JoinColumn(name="mission_id", unique=true, nullable=true)
    private Misssion mission;
}
