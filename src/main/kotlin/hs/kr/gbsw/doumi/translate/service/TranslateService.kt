package hs.kr.gbsw.doumi.translate.service

import com.google.cloud.speech.v1.RecognitionAudio
import com.google.cloud.speech.v1.RecognitionConfig
import com.google.cloud.speech.v1.SpeechClient
import com.google.cloud.speech.v1.SpeechRecognitionResult
import com.google.cloud.translate.v3.TranslateTextRequest
import com.google.cloud.translate.v3.TranslationServiceClient
import com.google.protobuf.ByteString
import hs.kr.gbsw.doumi.translate.dto.TextRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException

@Service
class TranslateService(
    private val logger: Logger = LoggerFactory.getLogger(TranslateService::class.java)
) {
    fun toText(audioFile: MultipartFile, lang: String): String? {
        if (audioFile.isEmpty) {
            throw IOException("audioFile' is empty")
        }

        val audioBytes: ByteArray = audioFile.bytes

        try {
            SpeechClient.create().use { client ->
                val audioData = ByteString.copyFrom(audioBytes)
                val recognitionAudio = RecognitionAudio.newBuilder()
                    .setContent(audioData)
                    .build()

                val recognitionConfig = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.ENCODING_UNSPECIFIED)
                    .setSampleRateHertz(16000)
                    .setLanguageCode(lang)
                    .build()

                val response = client.recognize(recognitionConfig, recognitionAudio)
                val results: List<SpeechRecognitionResult> = response.resultsList

                return results.firstOrNull()?.alternativesList?.firstOrNull()?.transcript ?: run {
                    logger.error("no result found")
                    return null
                }
            }
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    @Value("\${gcp.project_id}")
    lateinit var project_id: String

    fun translate(
        text: String,
        beforeLang: String,
        afterLang: String
    ): String? {
        val sourceLang = beforeLang.substringBefore("-")
        val targetLang = afterLang.substringBefore("-")
        try {
            TranslationServiceClient.create().use { client ->
                val request = TranslateTextRequest.newBuilder()
                    .setParent("projects/$project_id/locations/global")
                    .setMimeType("text/plain")
                    .setTargetLanguageCode(targetLang)
                    .setSourceLanguageCode(sourceLang)
                    .addContents(text)
                    .build()

                val response = client.translateText(request)

                return response.translationsList.firstOrNull()?.translatedText ?: run {
                    logger.error("no result found")
                    return null
                }
            }
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    fun translate(dto: TextRequest): String? {
        val text = dto.text
        val beforeLang = dto.beforeLang!!.substringBefore("-")
        val afterLang = dto.afterLang!!.substringBefore("-")

        try {
            TranslationServiceClient.create().use { client ->
                val request = TranslateTextRequest.newBuilder()
                    .setParent("projects/$project_id/locations/global")
                    .setMimeType("text/plain")
                    .setTargetLanguageCode(afterLang)
                    .setSourceLanguageCode(beforeLang)
                    .addContents(text)
                    .build()

                val response = client.translateText(request)

                return response.translationsList.firstOrNull()?.translatedText ?: run {
                    logger.error("no result found")
                    return null
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException(e)
        }
    }
}