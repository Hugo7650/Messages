package org.fossify.messages.helpers

import android.content.Context
import org.fossify.messages.extensions.config

object ReceiverUtils {

    fun isMessageFilteredOut(context: Context, body: String): Boolean {
        // 检查普通关键词
        for (blockedKeyword in context.config.blockedKeywords) {
            if (body.contains(blockedKeyword, ignoreCase = true)) {
                return true
            }
        }

        // 检查正则表达式关键词
        for (regexKeyword in context.config.blockedRegexKeywords) {
            try {
                val regex = Regex(regexKeyword)
                if (regex.containsMatchIn(body)) {
                    return true
                }
            } catch (e: Exception) {
                // 如果正则表达式格式错误，忽略此关键词
                continue
            }
        }

        return false
    }
}
