package com.jbp.admin.service;

import com.jbp.common.dto.LztReviewDto;

import java.util.List;

public interface LztReviewService {

    List<LztReviewDto> list(Integer merId, Boolean ifDraw, Boolean ifPay, String status);
}
