package tech.mappie.testing.scenarios

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class AccountDocumentTest : MappieTestCase() {

    @JvmInline
    value class AccountId(val value: String)

    class Account(val id: AccountId) {
        override fun equals(other: Any?): Boolean {
            if (other is Account) {
                return id == other.id
            }
            return false
        }

        override fun hashCode() = id.hashCode()
    }

    class AccountDocument(val id: String) {
        override fun equals(other: Any?): Boolean {
            if (other is AccountDocument) {
                return id == other.id
            }
            return false
        }

        override fun hashCode() = id.hashCode()
    }

    @Test
    fun `map Account to AccountDocument`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.scenarios.AccountDocumentTest.*

                class AccountToAccountDocumentMapper : ObjectMappie<Account, AccountDocument>() {
            
                    override fun map(from: Account) = mapping {
                        to::id fromProperty from::id transform AccountId::value
                    }
                }
                """)
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            objectMappie<Account, AccountDocument>("AccountToAccountDocumentMapper").let { mapper ->
                assertThat(mapper.map(Account(AccountId("1"))))
                    .isEqualTo(AccountDocument("1"))
            }
        }
    }

    @Test
    fun `map AccountDocument to Account`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.scenarios.AccountDocumentTest.*

                class AccountDocumentToAccountMapper : ObjectMappie<AccountDocument, Account>() {
            
                    override fun map(from: AccountDocument) = mapping {
                        to::id fromProperty from::id transform ::AccountId
                    }
                }
                """)
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            objectMappie<AccountDocument, Account>("AccountDocumentToAccountMapper").let { mapper ->
                assertThat(mapper.map(AccountDocument("1")))
                    .isEqualTo(Account(AccountId("1")))
            }
        }
    }
}