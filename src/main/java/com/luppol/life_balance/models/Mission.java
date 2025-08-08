package com.luppol.life_balance.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.Builder;

@Entity
@Table(name = "\"Mission\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mission {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Column(nullable = false, length = 2048)
  private String text;
  
  @OneToOne(mappedBy="mission", fetch=FetchType.LAZY)
  private Person person;
}