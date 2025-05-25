package com.luppol.life_balance.models;

import jakarta.persistence.*;
import lombok.*;


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

    @Column(name = "first_name", nullable = false, length = 128)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 128)
    private String lastName;

    @Column(name = "middle_name", length = 256)
    private String middleName;

    @Column(name = "phone_number", length = 20, unique = true)
    private String phoneNumber;

    @Column(length = 256)
    private String address;
    
    @OneToOne(fetch=FetchType.LAZY, optional=true)
    @JoinColumn(name="mission_id", unique=true, nullable=true)
    private Mission mission;
}
