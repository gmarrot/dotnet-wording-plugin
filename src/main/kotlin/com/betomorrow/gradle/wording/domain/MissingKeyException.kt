package com.betomorrow.gradle.wording.domain

import java.io.File

class MissingKeyException(keys: Set<String>, file: File) :
    Exception("Missing keys [${keys.joinToString(", ")}] in file $file")