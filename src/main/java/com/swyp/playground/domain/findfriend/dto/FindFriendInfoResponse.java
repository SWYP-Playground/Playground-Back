package com.swyp.playground.domain.findfriend.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class FindFriendInfoResponse {

    @Builder
    public FindFriendInfoResponse(String playgroundName, String recruitmentStatus, String title, String description, String scheduleTime, FindFriendOwnerInfoResponse owner, List<FindFriendParticipantsListResponse> participants) {
        this.playgroundName = playgroundName;
        this.recruitmentStatus = recruitmentStatus;
        this.title = title;
        this.description = description;
        this.scheduleTime = scheduleTime;
        this.owner = owner;
        this.participants = participants;
    }

    private String playgroundName;
    private String recruitmentStatus;
    private String title;
    private String description;
    private String scheduleTime;
    private FindFriendOwnerInfoResponse owner;
    private List<FindFriendParticipantsListResponse> participants;

}