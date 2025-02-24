package com.tuskerlanka.tourismmangement.util;

import com.tuskerlanka.tourismmangement.constants.ApplicationConstants;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component(value = "auditorAwareUtil")
public class AuditorAwareUtil implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(ApplicationConstants.USER_SYSTEM);
    }
}
