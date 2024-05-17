package com.remiges.rigel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RigelConfigDTO {
    String environment;
    String key;

}
