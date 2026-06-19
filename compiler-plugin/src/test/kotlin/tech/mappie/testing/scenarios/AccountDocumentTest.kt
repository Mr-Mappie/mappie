package tech.mappie.testing.scenarios

import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class AccountDocumentTest : MappieTestCase() {

    @JvmInline
    value class AccountId(val value: String)

    class Account(
        val id: AccountId
    )

    class AccountDocument(
        val id: String
    )

    @Test
    fun `map Account to AccountDocument`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.scenarios.AccountDocumentTest.*
                import kotlin.enums.EnumEntries

                class AccountToPartialAccountMapper : ObjectMappie<Account, AccountDocument>() {
            
                    override fun map(from: Account) = mapping {
                        to::id fromProperty from::id transform AccountId::value
                    }
                }
            
//                class PartialAccountToAccountMapper : ObjectMappie<AccountDocument, Account>() {
//            
//                    override fun map(from: AccountDocument) = mapping {
//                        to::id fromProperty from::id transform ::AccountId
//                    }
//                }   
                """)
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()
        }
    }
}