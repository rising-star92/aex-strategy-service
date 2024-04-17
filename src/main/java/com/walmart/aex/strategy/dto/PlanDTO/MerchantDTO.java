package com.walmart.aex.strategy.dto.PlanDTO;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MerchantDTO {
    private String merchantAlias;
    private String firstName;
    private String lastName;
    private String merchantEmail;
    private String domain;
    @Override
    public String toString() {
        return "MerchantDTO{" +
                "merchantAlias='" + merchantAlias + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", merchantEmail='" + merchantEmail + '\'' +
                ", domain='" + domain + '\'' +
                '}';
    }
}
