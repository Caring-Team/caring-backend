package com.caring.caringbackend.domain.test.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTestData is a Querydsl query type for TestData
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTestData extends EntityPathBase<TestData> {

    private static final long serialVersionUID = 559230586L;

    public static final QTestData testData = new QTestData("testData");

    public final NumberPath<Integer> age = createNumber("age", Integer.class);

    public final StringPath description = createString("description");

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public QTestData(String variable) {
        super(TestData.class, forVariable(variable));
    }

    public QTestData(Path<? extends TestData> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTestData(PathMetadata metadata) {
        super(TestData.class, metadata);
    }

}

