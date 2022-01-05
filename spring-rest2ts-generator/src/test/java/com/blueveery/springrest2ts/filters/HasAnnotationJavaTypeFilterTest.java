package com.blueveery.springrest2ts.filters;

import org.junit.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class HasAnnotationJavaTypeFilterTest {

    @Retention(RetentionPolicy.SOURCE)
    @Target(value={TYPE})
    public @interface SourceTimeAnnotation {
    }

    @Deprecated
    static class Person {}

    @SourceTimeAnnotation
    static class Address {}

    @Test
    public void classWithRequiredRuntimeAnnotationIsAccepted() {
        HasAnnotationJavaTypeFilter filter = new HasAnnotationJavaTypeFilter(Deprecated.class);
        assertThat(filter.accept(Person.class)).isTrue();
    }

    @Test
    public void classWithoutRequiredRuntimeAnnotationIsRejected() {
        HasAnnotationJavaTypeFilter filter = new HasAnnotationJavaTypeFilter(Deprecated.class);
        assertThat(filter.accept(Address.class)).isFalse();
    }

    @Test
    public void sourceTimeAnnotationAreNotAcceptedAsFilters() {
        assertThatThrownBy(() -> {
            new HasAnnotationJavaTypeFilter(SourceTimeAnnotation.class);
        }).isInstanceOf(IllegalStateException.class).hasMessage("SourceTimeAnnotation is not a runtime annotation");
    }

    @Test
    public void onlyTypeLevelAnnotationAreAcceptedAsFilters() {
        assertThatThrownBy(() -> {
            new HasAnnotationJavaTypeFilter(Override.class);
        }).isInstanceOf(IllegalStateException.class).hasMessage("Override is not a type annotation");
    }
}