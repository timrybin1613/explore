package ru.practicum.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserPrivacySettingsDto {
    private Boolean subscriptionsAllowed;
    private Boolean subscriptionsPublic;
}
