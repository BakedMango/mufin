package com.a502.backend.domain.savings;

import com.a502.backend.application.entity.Savings;
import com.a502.backend.application.entity.User;
import com.a502.backend.global.error.BusinessException;
import com.a502.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SavingsService {
	private final SavingsRepository savingsRepository;

	public Savings findByUuid(String savingsUuid) {
		UUID uuid = UUID.fromString(savingsUuid);
		Savings savings = savingsRepository.findSavingsListByUuid(uuid).orElseThrow(() -> BusinessException.of(ErrorCode.API_ERROR_SAVINGS_NOT_EXIST));
		if (savings.isDeleted())
			throw BusinessException.of(ErrorCode.API_ERROR_SAVINGS_NOT_EXIST);
		return savings;
	}

	public void save(Savings savings) {
		savingsRepository.save(savings);
	}

	public List<Savings> findAllByParents(User parents) {
		List<Savings> savingsList = savingsRepository.findAllByParents(parents);
		if (savingsList.isEmpty())
			throw BusinessException.of(ErrorCode.API_ERROR_SAVINGS_NOT_EXIST);
		return savingsList;
	}
}
