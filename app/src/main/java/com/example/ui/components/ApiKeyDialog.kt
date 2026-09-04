package com.example.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BuildConfig

@Composable
fun ApiKeyDialog(
    currentCustomKey: String,
    onKeySaved: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var keyInput by remember { mutableStateOf(currentCustomKey) }
    val hasBuildConfigKey = try {
        val k = BuildConfig.GEMINI_API_KEY
        k.isNotBlank() && k != "MY_GEMINI_API_KEY"
    } catch (e: Throwable) {
        false
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Gemini API Engine Settings",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = if (hasBuildConfigKey) {
                        "An active Gemini API key is injected via BuildConfig from AI Studio Secrets."
                    } else {
                        "AI Studio injects the key automatically at runtime. You can also paste an explicit Gemini API key below to override."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = keyInput,
                    onValueChange = { keyInput = it },
                    label = { Text("Gemini API Key (Optional Override)") },
                    placeholder = { Text("AIzaSy...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Model: gemini-3.5-flash with temperature 0.1 and strict JSON output mode.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onKeySaved(keyInput.trim())
                    onDismiss()
                }
            ) {
                Text("Save Key")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
