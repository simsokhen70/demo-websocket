package org.example.demows.service;


import org.example.demows.dto.PromotionDto;

import java.util.List;

public interface PromotionService {
    List<PromotionDto> getUserPromotions(String username);
    List<PromotionDto> getAllActivePromotions();
    PromotionDto createPromotion(String username, PromotionDto promotionDto);
    PromotionDto updatePromotion(Long promotionId, String username, PromotionDto promotionDto);
    void deletePromotion(Long promotionId, String username);

}
