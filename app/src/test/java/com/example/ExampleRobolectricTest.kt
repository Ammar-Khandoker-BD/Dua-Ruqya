package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.repository.DuaDataset
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Dua & Ruqyah", appName)

    val developerCredit = context.getString(R.string.developer_credit)
    assertEquals("Developed By Ammar Khandoker", developerCredit)
  }

  @Test
  fun `verify dua dataset is loaded and contains ruqyah`() {
    val duas = DuaDataset.duas
    assertTrue(duas.isNotEmpty())

    val ruqyahDuas = duas.filter { it.isRuqyah }
    assertTrue(ruqyahDuas.isNotEmpty())

    val steps = DuaDataset.ruqyahSteps
    assertTrue(steps.size >= 5)
  }
}
