package ru.practicum.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestStatusUpdateResultDto {

    private List<RequestDto> confirmedRequests;
    private List<RequestDto> rejectedRequests;

}
