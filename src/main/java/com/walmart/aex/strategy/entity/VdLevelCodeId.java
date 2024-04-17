package com.walmart.aex.strategy.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@EqualsAndHashCode
@Builder
@ToString
public class VdLevelCodeId implements Serializable {

    @Column(name = "vd_level_code")
    private Integer vdLevelCode;

}
