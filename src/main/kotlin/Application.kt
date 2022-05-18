import io.ktor.util.date.getTimeMillis
import java.time.Duration
import java.util.Properties
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import mu.KotlinLogging
import kotlinx.coroutines.*
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import org.apache.kafka.common.serialization.StringDeserializer

const val DELAY_MS = 10_000L
val log = KotlinLogging.logger {}
val pollDuration: Duration = Duration.ofMillis(30_000L)
val topics = listOf("test-topic")

@DelicateCoroutinesApi
@ExperimentalSerializationApi
fun main() {
    val consumer = getConsumer()
    consumer.subscribe(topics)

    while (true) {
        process(consumer)
    }
}

fun getConsumer(): KafkaConsumer<String, ByteArray> {
    val properties = Properties()
    properties[ConsumerConfig.GROUP_ID_CONFIG] = "test-001"
    properties[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = "0.0.0.0:9092"
    properties[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer().javaClass
    properties[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = ByteArrayDeserializer().javaClass
    properties[ConsumerConfig.MAX_POLL_RECORDS_CONFIG] = 1
    properties[ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG] = false
    properties[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"

    return KafkaConsumer<String, ByteArray>(properties)
}

@DelicateCoroutinesApi
@ExperimentalSerializationApi
fun process(consumer: KafkaConsumer<String, ByteArray>) {
    runBlocking {
        val records = consumer.poll(pollDuration)

        records.forEach { record ->
            try {
                val message = Serde.decode<Message>(record.value())
                val useDelay = message.id % 2 == 0
                val work = getWorkAsync(message, useDelay)

                GlobalScope.launch {
                    work.await()
                    val delta = getTimeMillis() - message.start!!
                    log.info("Completed work for message with ID ${message.id}, in ${delta}ms")
                }
            } catch (ex: Exception) {
                log.warn(ex.stackTraceToString())
            }
        }

        if (records.any()) {
            consumer.commitSync()
            log.info("Committed ${records.count()} records")
        }
    }
}

@DelicateCoroutinesApi
suspend fun getWorkAsync(message: Message, useDelay: Boolean): Deferred<Message> {
    message.start = getTimeMillis()

    return if (useDelay) {
        GlobalScope.async {
            delay(DELAY_MS)
            message
        }
    } else {
        GlobalScope.async {
            message
        }
    }
}

@kotlinx.serialization.Serializable
data class Message(val id: Int, var start: Long?)
