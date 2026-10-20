package com.roavai.parent

import com.roavai.parent.core.util.AppResult
import com.roavai.parent.core.util.UiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun uiState_success_holdsData() {
        val state: UiState<String> = UiState.Success("test_data")
        assertTrue(state is UiState.Success)
        assertEquals("test_data", (state as UiState.Success).data)
    }

    @Test
    fun appResult_success_holdsData() {
        val result: AppResult<Int> = AppResult.Success(42)
        assertTrue(result is AppResult.Success)
        assertEquals(42, (result as AppResult.Success).data)
    }
}
