package ru.practicum.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserDto {

    @Length(min = 6, max = 254)
    @Email
    @NotBlank
    @NotNull
    private String email;

    @Length(min = 2, max = 250)
    @NotNull
    @NotBlank
    private String name;
}
