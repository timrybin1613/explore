package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id;

    private String annotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    private String description;

    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id")
    private User initiator;

    private Boolean paid;

    @Column(name = "participant_limit")
    private Long participantLimit;

    @Column(name = "request_moderation")
    private Boolean requestModeration;

    @Column(name = "lat")
    private Double latitude;

    @Column(name = "lon")
    private Double longitude;

    private String title;

    @Column(name = "created_at")
    private LocalDateTime createdOn;

    @Column(name = "published_at")
    private LocalDateTime publishedOn;

    @Enumerated(EnumType.STRING)
    private EventState state;
}

