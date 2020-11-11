package org.openhdp.hdt.ui.base


@Target(
    AnnotationTarget.FIELD,
    AnnotationTarget.LOCAL_VARIABLE,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.EXPRESSION
)
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
// refers to iterables, arrays, sequences(e.g. String) which could have empty size/count/length
annotation class MightBeEmpty