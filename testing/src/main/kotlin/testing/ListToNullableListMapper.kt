package testing

import tech.mappie.api.ObjectMappie

data class SourceList(val value: List<String>)

data class TargetList(val value: List<String>?)

object NullableListFromListMapper : ObjectMappie<SourceList, TargetList>() {
    override fun map(from: SourceList): TargetList = mapping {
        TargetList::value fromProperty SourceList::value via StringIdentityMapper.forList
    }
}

object StringIdentityMapper : ObjectMappie<String, String>() {
    override fun map(from: String): String = from
}