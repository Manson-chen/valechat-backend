package com.vale.valechat.oauth;

import com.vale.valechat.model.vo.UserVO;
import lombok.Data;

import java.io.Serializable;

@Data
public class OAuth2UserVO implements Serializable {
    /**
     * User data after OAuth2 login
     */
    private UserVO user;

    /**
     * token
     */
    private String token;

    private static final long serialVersionUID = 8273263964559438662L;
}
