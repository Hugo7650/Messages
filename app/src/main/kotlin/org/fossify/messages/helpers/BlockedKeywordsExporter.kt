package org.fossify.messages.helpers

import org.fossify.commons.helpers.ExportResult
import org.fossify.messages.dialogs.BlockedKeyword
import java.io.OutputStream

object BlockedKeywordsExporter {

    fun exportBlockedKeywords(
        blockedKeywords: List<BlockedKeyword>,
        outputStream: OutputStream?,
        callback: (result: ExportResult) -> Unit,
    ) {
        if (outputStream == null) {
            callback.invoke(ExportResult.EXPORT_FAIL)
            return
        }

        try {
            outputStream.bufferedWriter().use { out ->
                out.write(blockedKeywords.joinToString(BLOCKED_KEYWORDS_EXPORT_DELIMITER) {
                    if (it.isRegex) "regex:${it.keyword}" else "simple:${it.keyword}"
                })
            }
            callback.invoke(ExportResult.EXPORT_OK)
        } catch (e: Exception) {
            callback.invoke(ExportResult.EXPORT_FAIL)
        }
    }
}
