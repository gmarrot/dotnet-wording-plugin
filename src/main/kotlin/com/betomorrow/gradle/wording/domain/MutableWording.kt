package com.betomorrow.gradle.wording.domain

class MutableWording(override val language: Language) : Wording {

    private val values = sortedMapOf<String, String>()
    private val comments = sortedMapOf<String, String>()

    override val keys: Set<String>
        get() = values.keys

    override fun containsKey(key: String): Boolean {
        return values.containsKey(key)
    }

    override fun getValue(key: String): String? {
        return values.getOrDefault(key, null)
    }

    override fun getComment(key: String): String? {
        return comments.getOrDefault(key, null)
    }

    fun addOrUpdate(key: String, value: String, comment: String?) {
        values[key] = value
        if (!comment.isNullOrEmpty()) {
            comments[key] = comment
        } else {
            comments.remove(key)
        }
    }

}