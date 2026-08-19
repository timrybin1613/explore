package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateEventDto {
    @NotBlank
    @Length(min = 3, max = 120)
    private String title;

    @NotBlank
    @Length(min = 20, max = 2000)
    private String annotation;

    @NotNull
    private Long category;

    private Boolean paid;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotBlank
    @Length(min = 20, max = 7000)
    private String description;

    @PositiveOrZero
    @Nullable
    private Long participantLimit;

    @NotNull
    private LocationDto location;

    private Boolean requestModeration;

}
