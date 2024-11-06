package ua.kryha.transactionsservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class BlockTrackerEntity {

    @Id
    private String id = "lastProcessedBlock";

    private Long blockNumber = 0L;
}