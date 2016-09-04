package edu.rit.se.crashavoidance.infrastructure.di.annotation;

import java.lang.annotation.Retention;

import javax.inject.Scope;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by troncaglia on 24/06/2015.
 */
@Scope
@Retention (RUNTIME)
public @interface PerService {
}
