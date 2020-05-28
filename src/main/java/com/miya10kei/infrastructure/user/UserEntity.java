package com.miya10kei.infrastructure.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserEntity {

    private Long id;
    private String name;
}
