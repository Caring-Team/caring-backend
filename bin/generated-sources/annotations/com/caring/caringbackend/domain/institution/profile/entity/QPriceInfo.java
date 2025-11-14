package com.caring.caringbackend.domain.institution.profile.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPriceInfo is a Querydsl query type for PriceInfo
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QPriceInfo extends BeanPath<PriceInfo> {

    private static final long serialVersionUID = -1580849418L;

    public static final QPriceInfo priceInfo = new QPriceInfo("priceInfo");

    public final NumberPath<Integer> admissionFee = createNumber("admissionFee", Integer.class);

    public final NumberPath<Integer> monthlyBaseFee = createNumber("monthlyBaseFee", Integer.class);

    public final NumberPath<Integer> monthlyMealCost = createNumber("monthlyMealCost", Integer.class);

    public final StringPath priceNotes = createString("priceNotes");

    public QPriceInfo(String variable) {
        super(PriceInfo.class, forVariable(variable));
    }

    public QPriceInfo(Path<? extends PriceInfo> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPriceInfo(PathMetadata metadata) {
        super(PriceInfo.class, metadata);
    }

}

