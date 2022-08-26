package com.gm.gmall.model.to;

import lombok.Data;

import java.util.List;

/**
 * @author gym
 * @create 2022/8/26 0026 19:19
 */
@Data
public class IndexTreeTo {
    private Long categoryId;
    private String categoryName;
    private List<IndexTreeTo> categoryChild;
}
