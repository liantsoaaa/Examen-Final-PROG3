package com.hei.openapi_federation.service;

import com.hei.openapi_federation.entity.FinancialAccount;
import com.hei.openapi_federation.exception.BadRequestException;
import com.hei.openapi_federation.repository.CollectivityRepository;
import com.hei.openapi_federation.repository.FinancialAccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class FinancialAccountService {

    private final FinancialAccountRepository financialAccountRepository;
    private final CollectivityRepository collectivityRepository;

    public FinancialAccountService(FinancialAccountRepository financialAccountRepository,
                                   CollectivityRepository collectivityRepository) {
        this.financialAccountRepository = financialAccountRepository;
        this.collectivityRepository = collectivityRepository;
    }

    public List<FinancialAccount> getByCollectivity(String collectivityId, LocalDate at) {
        Long id = parseId(collectivityId);

        collectivityRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(
                        HttpStatus.NOT_FOUND, "Collectivity not found: " + collectivityId));

        return financialAccountRepository.findByCollectivityId(id, at);
    }

    private Long parseId(String id) {
        try {
            return Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new BadRequestException(HttpStatus.NOT_FOUND, "Invalid collectivity id: " + id);
        }
    }
}