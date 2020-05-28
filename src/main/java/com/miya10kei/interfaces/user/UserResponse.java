package com.miya10kei.interfaces.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class UserResponse {
    private long id;
    private String name;
}
