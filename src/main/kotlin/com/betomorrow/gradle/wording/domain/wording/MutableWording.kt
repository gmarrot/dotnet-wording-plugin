package com.betomorrow.gradle.wording.domain.wording

class MutableWording(
    override val language: Language
) : Wording {
    private val items = linkedMapOf<String, WordingItem>()

    override val keys: Set<String>
        get() = items.keys

    override fun containsKey(key: String): Boolean {
        return items.containsKey(key)
    }

    override fun getValue(key: String): String? {
        return items.getOrDefault(key, null)?.value
    }

    override fun getComment(key: String): String? {
        return items.getOrDefault(key, null)?.comment
    }

    fun addOrUpdate(key: String, value: String, comment: String?) {
        items[key] = WordingItem(value, if (!comment.isNullOrEmpty()) comment else null)
    }

    private data class WordingItem(
        val value: String,
        val comment: String?
    )
}
