package com.vale.valechat.constant;

import org.springframework.beans.factory.annotation.Value;

public interface PathConstant {

    @Value(("${web.root-path}"))
    String ROOT_PATH = "";
}
