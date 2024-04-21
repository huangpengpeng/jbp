package com.jbp.service.event;

import com.jbp.common.model.agent.UserCapa;
import lombok.*;

@Getter
@Setter
public class UserCapaUpdateEvent extends AbstractEvent {


    private EventDto eventDto;

    @NoArgsConstructor
    @Data
    @Builder
    public static class EventDto {

        public EventDto(Long orgCapaId, Long tagCapaId, UserCapa userCapa) {
            this.orgCapaId = orgCapaId;
            this.tagCapaId = tagCapaId;
            this.userCapa = userCapa;
        }

        private Long orgCapaId;

        private Long tagCapaId;

        private UserCapa userCapa;
    }

    public UserCapaUpdateEvent(EventDto dto) {
        super(dto);
        this.eventDto = dto;
    }
}
