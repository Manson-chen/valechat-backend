package com.vale.valechat.common;

import com.vale.valechat.constant.CommonConstant;
import lombok.Data;

/**
 * Paging request
 *
 * @author yupi
 */
@Data
public class PageRequest {

    /**
     * Current page number
     */
    private long current = 1;

    /**
     * Page Size
     */
    private long pageSize = 10;

}
