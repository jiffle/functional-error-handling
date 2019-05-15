import io.vavr.control.Try
import io.vavr.kotlin.`try`
import io.vavr.kotlin.failure
import io.vavr.kotlin.success
import java.lang.RuntimeException

data class HappyData( val name: String)
data class HappyResult( val foo: String) {
    constructor( data: HappyData) : this( data.name)
}
data class FirstIntermediate( val compo: String)
data class SecondIntermediate( val compo: String)
data class FinalResult( val compo: String)

class InfrastructureException( val techReason: String) : RuntimeException()
class ProcessingException( val issueReason: String) : RuntimeException()
class BusinessException( val businessReason: String) : Exception() {
    override fun fillInStackTrace(): Throwable {
        return this
    }
}

sealed class TryResponse
data class HappyResponse( val data: String) : TryResponse()
data class SadResponse(val reason: String, val stackTrace: Array<StackTraceElement>) : TryResponse()


class TryAdaptor {

    fun loadMyData( id: Int) : Try<HappyData> {
        return `try` {
            val result = performLoad( id)
            // postprocess result
            result
        }.onFailure {
            // log any failures
        }
    }

    private fun performLoad( id: Int) : HappyData {
        return if( id > -100) {
            HappyData("hello")
        }
        else {
            throw InfrastructureException("infrastructure call failure")
        }
    }
}

class TryService(private val adaptor: TryAdaptor) {
    fun processMyData(id: Int): Try<HappyResult> {
        return adaptor.loadMyData(id).flatMap {
            when {
                id > 200 -> success(HappyResult(it))
                id > 100 -> failure(BusinessException("client ineligible"))
                else -> failure(ProcessingException("processing failure"))
            }
        }
    }
}


class TryResource(private val service: TryService) {
    fun processRequest( id: Int) : TryResponse {
        return service.processMyData( id).map<TryResponse> {
            HappyResponse( it.foo)
        }.recover {
            when( it) {
                is BusinessException -> SadResponse( it.businessReason, it.stackTrace)
                is ProcessingException -> SadResponse( it.issueReason, it.stackTrace)
                is InfrastructureException -> SadResponse( it.techReason, it.stackTrace)
                else -> SadResponse( it.toString(), it.stackTrace)
            }
        }.get()
    }

    fun processRequest2( id: Int) : TryResponse {
        return service.processMyData( id).map<TryResponse> {
            HappyResponse( it.foo)
        }.recover(BusinessException::class.java) { ex ->
            SadResponse( ex.businessReason, ex.stackTrace)
        }.recover(ProcessingException::class.java) { ex ->
            SadResponse( ex.issueReason, ex.stackTrace)
        }.recover(InfrastructureException::class.java) { ex ->
            SadResponse( ex.techReason, ex.stackTrace)
        }.recover {
            SadResponse( it.toString(), it.stackTrace)
        }.get()
    }

    companion object {
        operator fun invoke() = TryResource(TryService(TryAdaptor()))
    }
}