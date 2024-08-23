package com.a502.backend.domain.code;

import com.a502.backend.application.entity.Code;
import com.a502.backend.global.code.CodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CodeFactory {

    @Autowired
    CodeRepository codeRepository;

    public Code createAndSaveCode(String id, String name) {
        Code code = Code.builder()
                .id(id)
                .name(name)
                .build();
        return codeRepository.save(code);
    }

    public Code createCode(String id, String name) {
        return Code.builder()
                .id(id)
                .name(name)
                .build();
    }
}
