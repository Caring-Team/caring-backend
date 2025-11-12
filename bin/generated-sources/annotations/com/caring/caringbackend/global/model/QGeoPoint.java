package com.caring.caringbackend.global.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QGeoPoint is a Querydsl query type for GeoPoint
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QGeoPoint extends BeanPath<GeoPoint> {

    private static final long serialVersionUID = 820813620L;

    public static final QGeoPoint geoPoint = new QGeoPoint("geoPoint");

    public final NumberPath<Double> latitude = createNumber("latitude", Double.class);

    public final NumberPath<Double> longitude = createNumber("longitude", Double.class);

    public QGeoPoint(String variable) {
        super(GeoPoint.class, forVariable(variable));
    }

    public QGeoPoint(Path<? extends GeoPoint> path) {
        super(path.getType(), path.getMetadata());
    }

    public QGeoPoint(PathMetadata metadata) {
        super(GeoPoint.class, metadata);
    }

}

