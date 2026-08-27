package fukuro.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {
    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun startupAndPrimaryNavigation() = baselineProfileRule.collect(
        packageName = "nl.codefin.fukuro",
        includeInStartupProfile = true,
    ) {
        pressHome()
        startActivityAndWait()

        // The library can load from its local cache while the profile is recorded.
        device.wait(Until.hasObject(By.text("Library")), 5_000)
        device.findObject(By.text("Library"))?.click()
        device.waitForIdle()
        device.findObject(By.text("Home"))?.click()
        device.waitForIdle()
        device.findObject(By.text("Settings"))?.click()
        device.waitForIdle()
    }
}
