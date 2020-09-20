package com.betomorrow.gradle.wording.domain.wording.state

class InvalidWordingException(invalidWordingStates: List<WordingState>) :
    Exception("Found ${invalidWordingStates.size} invalid ${if (invalidWordingStates.size > 1) "wordings" else "wording"}")
