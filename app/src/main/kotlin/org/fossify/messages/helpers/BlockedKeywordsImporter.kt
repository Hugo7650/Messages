package org.fossify.messages.helpers

import android.app.Activity
import org.fossify.commons.extensions.showErrorToast
import org.fossify.messages.extensions.config

import java.io.File

class BlockedKeywordsImporter(
    private val activity: Activity,
) {
    enum class ImportResult {
        IMPORT_FAIL, IMPORT_OK
    }

    fun importBlockedKeywords(path: String): ImportResult {
        return try {
            val inputStream = File(path).inputStream()
            val keywords = inputStream.bufferedReader().use {
                val content = it.readText().trimEnd().split(BLOCKED_KEYWORDS_EXPORT_DELIMITER)
                content
            }
            if (keywords.isNotEmpty()) {
                keywords.forEach { keywordEntry: String ->
                    when {
                        keywordEntry.startsWith("regex:") -> {
                            val keyword = keywordEntry.substringAfter("regex:")
                            if (keyword.isNotEmpty()) {
                                activity.config.addBlockedRegexKeyword(keyword)
                            }
                        }
                        keywordEntry.startsWith("simple:") -> {
                            val keyword = keywordEntry.substringAfter("simple:")
                            if (keyword.isNotEmpty()) {
                                activity.config.addBlockedKeyword(keyword)
                            }
                        }
                        else -> {
                            // 向后兼容，处理旧格式没有类型标记的关键词
                            if (keywordEntry.isNotEmpty()) {
                                activity.config.addBlockedKeyword(keywordEntry)
                            }
                        }
                    }
                }
                ImportResult.IMPORT_OK
            } else {
                ImportResult.IMPORT_FAIL
            }

        } catch (e: Exception) {
            activity.showErrorToast(e)
            ImportResult.IMPORT_FAIL
        }
    }
}
