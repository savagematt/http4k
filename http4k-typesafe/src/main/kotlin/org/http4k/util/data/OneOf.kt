package org.http4k.util.data

sealed class OneOf

interface IOneOf<T> {
    val value: T
}

object OneOf0 : OneOf()

sealed class OneOf1<A> : OneOf() {
    data class A<A>(override val value: A) : OneOf1<A>(), IOneOf<A>
}

sealed class OneOf2<A, B> : OneOf() {
    data class A<A, B>(override val value: A) : OneOf2<A, B>(), IOneOf<A>
    data class B<A, B>(override val value: B) : OneOf2<A, B>(), IOneOf<B>
}

sealed class OneOf3<A, B, C> : OneOf() {
    data class A<A, B, C>(override val value: A) : OneOf3<A, B, C>(), IOneOf<A>

    data class B<A, B, C>(override val value: B) : OneOf3<A, B, C>(), IOneOf<B>

    data class C<A, B, C>(override val value: C) : OneOf3<A, B, C>(), IOneOf<C>
}

sealed class OneOf4<A, B, C, D> : OneOf() {
    data class A<A, B, C, D>(override val value: A) : OneOf4<A, B, C, D>(), IOneOf<A>

    data class B<A, B, C, D>(override val value: B) : OneOf4<A, B, C, D>(), IOneOf<B>

    data class C<A, B, C, D>(override val value: C) : OneOf4<A, B, C, D>(), IOneOf<C>

    data class D<A, B, C, D>(override val value: D) : OneOf4<A, B, C, D>(), IOneOf<D>
}

sealed class OneOf5<A, B, C, D, E> : OneOf() {
    data class A<A, B, C, D, E>(override val value: A) : OneOf5<A, B, C, D, E>(), IOneOf<A>

    data class B<A, B, C, D, E>(override val value: B) : OneOf5<A, B, C, D, E>(), IOneOf<B>

    data class C<A, B, C, D, E>(override val value: C) : OneOf5<A, B, C, D, E>(), IOneOf<C>

    data class D<A, B, C, D, E>(override val value: D) : OneOf5<A, B, C, D, E>(), IOneOf<D>

    data class E<A, B, C, D, E>(override val value: E) : OneOf5<A, B, C, D, E>(), IOneOf<E>
}

sealed class OneOf6<A, B, C, D, E, F> : OneOf() {
    data class A<A, B, C, D, E, F>(override val value: A) : OneOf6<A, B, C, D, E, F>(), IOneOf<A>

    data class B<A, B, C, D, E, F>(override val value: B) : OneOf6<A, B, C, D, E, F>(), IOneOf<B>

    data class C<A, B, C, D, E, F>(override val value: C) : OneOf6<A, B, C, D, E, F>(), IOneOf<C>

    data class D<A, B, C, D, E, F>(override val value: D) : OneOf6<A, B, C, D, E, F>(), IOneOf<D>

    data class E<A, B, C, D, E, F>(override val value: E) : OneOf6<A, B, C, D, E, F>(), IOneOf<E>

    data class F<A, B, C, D, E, F>(override val value: F) : OneOf6<A, B, C, D, E, F>(), IOneOf<F>
}

sealed class OneOf7<A, B, C, D, E, F, G> : OneOf() {
    data class A<A, B, C, D, E, F, G>(override val value: A) : OneOf7<A, B, C, D, E, F, G>(), IOneOf<A>

    data class B<A, B, C, D, E, F, G>(override val value: B) : OneOf7<A, B, C, D, E, F, G>(), IOneOf<B>

    data class C<A, B, C, D, E, F, G>(override val value: C) : OneOf7<A, B, C, D, E, F, G>(), IOneOf<C>

    data class D<A, B, C, D, E, F, G>(override val value: D) : OneOf7<A, B, C, D, E, F, G>(), IOneOf<D>

    data class E<A, B, C, D, E, F, G>(override val value: E) : OneOf7<A, B, C, D, E, F, G>(), IOneOf<E>

    data class F<A, B, C, D, E, F, G>(override val value: F) : OneOf7<A, B, C, D, E, F, G>(), IOneOf<F>

    data class G<A, B, C, D, E, F, G>(override val value: G) : OneOf7<A, B, C, D, E, F, G>(), IOneOf<G>
}

sealed class OneOf8<A, B, C, D, E, F, G, H> : OneOf() {
    data class A<A, B, C, D, E, F, G, H>(override val value: A) : OneOf8<A, B, C, D, E, F, G, H>(), IOneOf<A>

    data class B<A, B, C, D, E, F, G, H>(override val value: B) : OneOf8<A, B, C, D, E, F, G, H>(), IOneOf<B>

    data class C<A, B, C, D, E, F, G, H>(override val value: C) : OneOf8<A, B, C, D, E, F, G, H>(), IOneOf<C>

    data class D<A, B, C, D, E, F, G, H>(override val value: D) : OneOf8<A, B, C, D, E, F, G, H>(), IOneOf<D>

    data class E<A, B, C, D, E, F, G, H>(override val value: E) : OneOf8<A, B, C, D, E, F, G, H>(), IOneOf<E>

    data class F<A, B, C, D, E, F, G, H>(override val value: F) : OneOf8<A, B, C, D, E, F, G, H>(), IOneOf<F>

    data class G<A, B, C, D, E, F, G, H>(override val value: G) : OneOf8<A, B, C, D, E, F, G, H>(), IOneOf<G>

    data class H<A, B, C, D, E, F, G, H>(override val value: H) : OneOf8<A, B, C, D, E, F, G, H>(), IOneOf<H>
}

sealed class OneOf9<A, B, C, D, E, F, G, H, I> : OneOf() {
    data class A<A, B, C, D, E, F, G, H, I>(override val value: A) : OneOf9<A, B, C, D, E, F, G, H, I>(), IOneOf<A>

    data class B<A, B, C, D, E, F, G, H, I>(override val value: B) : OneOf9<A, B, C, D, E, F, G, H, I>(), IOneOf<B>

    data class C<A, B, C, D, E, F, G, H, I>(override val value: C) : OneOf9<A, B, C, D, E, F, G, H, I>(), IOneOf<C>

    data class D<A, B, C, D, E, F, G, H, I>(override val value: D) : OneOf9<A, B, C, D, E, F, G, H, I>(), IOneOf<D>

    data class E<A, B, C, D, E, F, G, H, I>(override val value: E) : OneOf9<A, B, C, D, E, F, G, H, I>(), IOneOf<E>

    data class F<A, B, C, D, E, F, G, H, I>(override val value: F) : OneOf9<A, B, C, D, E, F, G, H, I>(), IOneOf<F>

    data class G<A, B, C, D, E, F, G, H, I>(override val value: G) : OneOf9<A, B, C, D, E, F, G, H, I>(), IOneOf<G>

    data class H<A, B, C, D, E, F, G, H, I>(override val value: H) : OneOf9<A, B, C, D, E, F, G, H, I>(), IOneOf<H>

    data class I<A, B, C, D, E, F, G, H, I>(override val value: I) : OneOf9<A, B, C, D, E, F, G, H, I>(), IOneOf<I>
}

sealed class OneOf10<A, B, C, D, E, F, G, H, I, J> : OneOf() {
    data class A<A, B, C, D, E, F, G, H, I, J>(override val value: A) : OneOf10<A, B, C, D, E, F, G, H, I, J>(), IOneOf<A>

    data class B<A, B, C, D, E, F, G, H, I, J>(override val value: B) : OneOf10<A, B, C, D, E, F, G, H, I, J>(), IOneOf<B>

    data class C<A, B, C, D, E, F, G, H, I, J>(override val value: C) : OneOf10<A, B, C, D, E, F, G, H, I, J>(), IOneOf<C>

    data class D<A, B, C, D, E, F, G, H, I, J>(override val value: D) : OneOf10<A, B, C, D, E, F, G, H, I, J>(), IOneOf<D>

    data class E<A, B, C, D, E, F, G, H, I, J>(override val value: E) : OneOf10<A, B, C, D, E, F, G, H, I, J>(), IOneOf<E>

    data class F<A, B, C, D, E, F, G, H, I, J>(override val value: F) : OneOf10<A, B, C, D, E, F, G, H, I, J>(), IOneOf<F>

    data class G<A, B, C, D, E, F, G, H, I, J>(override val value: G) : OneOf10<A, B, C, D, E, F, G, H, I, J>(), IOneOf<G>

    data class H<A, B, C, D, E, F, G, H, I, J>(override val value: H) : OneOf10<A, B, C, D, E, F, G, H, I, J>(), IOneOf<H>

    data class I<A, B, C, D, E, F, G, H, I, J>(override val value: I) : OneOf10<A, B, C, D, E, F, G, H, I, J>(), IOneOf<I>

    data class J<A, B, C, D, E, F, G, H, I, J>(override val value: J) : OneOf10<A, B, C, D, E, F, G, H, I, J>(), IOneOf<J>
}