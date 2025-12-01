package com.caring.caringbackend.domain.tag.repository.dto;

import com.caring.caringbackend.domain.tag.entity.InstitutionTag;
import lombok.AllArgsConstructor;
import lombok.Data;


@AllArgsConstructor
@Data
public class InstitutionTagWithTagIdDto {
    private InstitutionTag institutionTag;
    private Long tagId;
}
