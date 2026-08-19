package ru.practicum.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@Entity
@Table(name = "compilations")
@NoArgsConstructor
@AllArgsConstructor
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "compilation_id")
    private Long id;

    @JoinTable(
            name = "compilations_events",
            joinColumns = @JoinColumn(
                    name = "compilation_id",
                    referencedColumnName = "compilation_id"),
            inverseJoinColumns = @JoinColumn(
                    name = "event_id",
                    referencedColumnName = "event_id")
    )
    @ManyToMany
    private List<Event> events;

    @Column
    private Boolean pinned;

    private String title;
}
