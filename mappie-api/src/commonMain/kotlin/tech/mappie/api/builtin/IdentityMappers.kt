package tech.mappie.api.builtin

import tech.mappie.api.ObjectMappie

public class IdentityMapper<T> : ObjectMappie<T, T>() {
    public override fun map(from: T): T = from
}