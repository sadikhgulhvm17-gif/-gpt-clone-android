package com.example.gptclone

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { ChatScreen() } }
    }
}

data class ChatLine(val fromUser: Boolean, val text: String)

class ChatViewModel : ViewModel() {
    var lines by mutableStateOf(listOf(ChatLine(false, "مرحباً! اختر ملف نموذج LiteRT-LM بصيغة .litertlm لبدء المحادثة المحلية.")))
        private set
    var status by mutableStateOf("لم يتم تحميل نموذج")
        private set
    var modelPath by mutableStateOf<String?>(null)
        private set

    fun setModel(path: String) {
        modelPath = path
        status = "تم اختيار النموذج. تكامل محرك LiteRT-LM سيُفعّل بعد اختبار توافق إصدار المكتبة على جهازك."
    }

    fun send(text: String) {
        if (text.isBlank()) return
        lines = lines + ChatLine(true, text.trim())
        val answer = if (modelPath == null) {
            "اختر ملف النموذج أولاً. هذا الإصدار التجريبي يجهز واجهة التطبيق والبناء؛ لم يتم بعد تشغيل محرك الاستدلال المحلي."
        } else {
            "تم استلام رسالتك. واجهة النموذج محددة، لكن تشغيل الاستدلال يحتاج استكمال ربط API المكتبة واختباره على جهازك."
        }
        lines = lines + ChatLine(false, answer)
    }
}

@Composable
fun ChatScreen(vm: ChatViewModel = viewModel()) {
    var input by remember { mutableStateOf("") }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let { vm.setModel(it.toString()) }
    }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("GPT Clone — نموذج محلي", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        Text(vm.status, style = MaterialTheme.typography.bodySmall)
        Button(onClick = { picker.launch(arrayOf("*/*")) }, modifier = Modifier.fillMaxWidth()) {
            Text("اختيار ملف النموذج .litertlm")
        }
        Spacer(Modifier.height(8.dp))
        LazyColumn(Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(vm.lines) { line ->
                Surface(color = if (line.fromUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.medium) {
                    Text((if (line.fromUser) "أنت: " else "المساعد: ") + line.text, Modifier.padding(12.dp))
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = input, onValueChange = { input = it }, modifier = Modifier.weight(1f), placeholder = { Text("اكتب رسالتك") })
            Button(onClick = { vm.send(input); input = "" }) { Text("إرسال") }
        }
    }
}
