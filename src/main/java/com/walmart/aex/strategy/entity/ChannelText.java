package com.walmart.aex.strategy.entity;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Entity
@Table(name = "channel_text", schema = "dbo")
public class ChannelText {
    @Id
    @Column(name = "channel_id", nullable = false)
    private Integer channelId;

    @Column(name = "channel_desc", nullable = false)
    private String channelDesc;
}
