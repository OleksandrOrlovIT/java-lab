package ua.orlov.springcoregym.model.token;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Table(name = "invalid_token")
@NoArgsConstructor
@SuperBuilder
@Entity
public class InvalidToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token", nullable = false, unique = true, length = 512)
    private String token;

    @CreationTimestamp
    @Column(name = "blacklisted_at")
    private LocalDateTime blacklistedAt;
}
