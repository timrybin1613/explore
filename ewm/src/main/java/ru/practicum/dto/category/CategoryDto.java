package ru.practicum.dto.category;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {

    private Long id;
    @NotBlank
    @Length(max = 50, min = 1)
    private String name;

}
