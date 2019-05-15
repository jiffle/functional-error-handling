import io.kotlintest.matchers.beEmpty
import io.kotlintest.matchers.instanceOf
import io.kotlintest.matchers.types.shouldNotBeNull
import io.kotlintest.shouldBe
import io.kotlintest.shouldNot
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.StringSpec

class TryResourceTest : StringSpec() {

    init {
        "processRequest should return processing failure" {
            val response = TryResource().processRequest( 1)
            response shouldBe instanceOf(SadResponse::class)
            with(response as SadResponse) {
                reason shouldBe "processing failure"
                stackTrace.shouldNotBeNull()
                stackTrace.asList() shouldNot beEmpty<StackTraceElement>()
            }
        }

        "processRequest should return client ineligible" {
            val response = TryResource().processRequest( 101)
            response shouldBe instanceOf(SadResponse::class)
            with(response as SadResponse) {
                reason shouldBe "client ineligible"
                stackTrace.shouldNotBeNull()
                stackTrace.asList() shouldBe beEmpty<StackTraceElement>()
            }
        }
        "processRequest should return OK for successful call" {
            val response = TryResource().processRequest( 201)
            response shouldBe HappyResponse( "hello")
        }
        "processRequest should return infrastructure failure" {
            val response = TryResource().processRequest( -101)
            response shouldBe instanceOf(SadResponse::class)
            with(response as SadResponse) {
                reason shouldBe "infrastructure call failure"
                stackTrace.shouldNotBeNull()
                stackTrace.asList() shouldNot beEmpty<StackTraceElement>()
            }
        }
        "processRequest2" { }
    }

}

