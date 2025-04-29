package org.fossify.messages.dialogs

import androidx.appcompat.app.AlertDialog
import org.fossify.commons.activities.BaseSimpleActivity
import org.fossify.commons.extensions.getAlertDialogBuilder
import org.fossify.commons.extensions.setupDialogStuff
import org.fossify.commons.extensions.showKeyboard
import org.fossify.commons.extensions.value
import org.fossify.messages.databinding.DialogAddBlockedKeywordBinding
import org.fossify.messages.extensions.config

data class BlockedKeyword(val keyword: String, val isRegex: Boolean = false)

class AddBlockedKeywordDialog(val activity: BaseSimpleActivity, private val originalKeyword: BlockedKeyword? = null, val callback: () -> Unit) {
    init {
        val binding = DialogAddBlockedKeywordBinding.inflate(activity.layoutInflater).apply {
            if (originalKeyword != null) {
                addBlockedKeywordEdittext.setText(originalKeyword.keyword)
                if (originalKeyword.isRegex) {
                    addBlockedKeywordTypeRegex.isChecked = true
                } else {
                    addBlockedKeywordTypeSimple.isChecked = true
                }
            }
        }

        activity.getAlertDialogBuilder()
            .setPositiveButton(org.fossify.commons.R.string.ok, null)
            .setNegativeButton(org.fossify.commons.R.string.cancel, null)
            .apply {
                activity.setupDialogStuff(binding.root, this) { alertDialog ->
                    alertDialog.showKeyboard(binding.addBlockedKeywordEdittext)
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        val newBlockedKeyword = binding.addBlockedKeywordEdittext.value
                        val isRegex = binding.addBlockedKeywordTypeRegex.isChecked

                        // 如果是编辑模式，先移除原关键词
                        if (originalKeyword != null) {
                            if (originalKeyword.isRegex) {
                                activity.config.removeBlockedRegexKeyword(originalKeyword.keyword)
                            } else {
                                activity.config.removeBlockedKeyword(originalKeyword.keyword)
                            }
                        }

                        // 添加新关键词
                        if (newBlockedKeyword.isNotEmpty()) {
                            if (isRegex) {
                                // 验证正则表达式有效性
                                try {
                                    Regex(newBlockedKeyword)
                                    activity.config.addBlockedRegexKeyword(newBlockedKeyword)
                                } catch (e: Exception) {
                                    binding.addBlockedKeywordInputlayout.error = "无效的正则表达式"
                                    return@setOnClickListener
                                }
                            } else {
                                activity.config.addBlockedKeyword(newBlockedKeyword)
                            }
                        }

                        callback()
                        alertDialog.dismiss()
                    }
                }
            }
    }
}
