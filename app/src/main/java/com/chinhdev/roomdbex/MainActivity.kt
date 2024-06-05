package com.chinhdev.roomdbex

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.toColorInt
import androidx.room.Room
import com.chinhdev.roomdbex.ui.theme.RoomDBexTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RoomDBexTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding()
                        .padding(16.dp)
                ) { innerPadding ->
                    HomeScreen()
                }
            }
        }
    }
}

@Composable
fun HomeScreen() {
    val context = LocalContext.current

    val db = Room.databaseBuilder(
        context,
        StudentDB::class.java, "student-db"
    ).allowMainThreadQueries().build()

    var listStudents by remember {
        mutableStateOf(db.studentDAO().getAll())
    }

    var showDialogAddSV by remember { mutableStateOf(false) }

    var showDialogThongtinSV by remember { mutableStateOf(false) }

    var dialogMessage by remember {
        mutableStateOf("")
    }
    var showDialogXoa by remember { mutableStateOf(false) }
    var studentToDelete by remember { mutableStateOf<StudentModel?>(null) }
    var showUpdateDialog by remember { mutableStateOf(false) }
    var studentToUpdate by remember { mutableStateOf<StudentModel?>(null) }

    if (showDialogThongtinSV) {
        val tatDialog = {
            showDialogThongtinSV = false
        }
        ShowDialogStudentInfor(
            onConfirmation = tatDialog,
            dialogMessage = dialogMessage
        )
    }
    if (showDialogXoa) {
        studentToDelete?.let { student ->
            AlertDialog(
                onDismissRequest = { showDialogXoa = false },
                title = { Text(text = "Xác nhận xóa") },
                text = { Text("Bạn có chắc chắn muốn xóa sinh viên này không?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            db.studentDAO().delete(student)
                            listStudents = db.studentDAO().getAll()
                            showDialogXoa = false
                            Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Text("Xóa")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDialogXoa = false }
                    ) {
                        Text("Hủy")
                    }
                }
            )
        }
    }

    if (showUpdateDialog) {
        studentToUpdate?.let {
            UpdateStudentDialog(
                context = LocalContext.current,
                student = it,
                onUpdateConfirmation = { updatedStudent ->
                    db.studentDAO().updateStudent(updatedStudent)
                    listStudents = db.studentDAO().getAll()
                    showUpdateDialog = false
                    Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
                },
                onDismissRequest = { showUpdateDialog = false }
            )
        }
    }


    Box(Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Quản lý Sinh viên",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {

                items(listStudents) { student ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clickable {
                                dialogMessage = student.getThongtin()
                                showDialogThongtinSV = true
                            }
                    ) {
                        Text(modifier = Modifier.weight(1f), text = student.uid.toString())
                        Text(modifier = Modifier.weight(1f), text = student.hoten.toString())
                        Text(modifier = Modifier.weight(1f), text = student.mssv.toString())
                        Text(modifier = Modifier.weight(1f), text = student.diemTB.toString())
                        Image(painter = painterResource(id = R.drawable.pen),
                            contentDescription = "",
                            modifier = Modifier
                                .size(30.dp)
                                .clickable {
                                    showUpdateDialog = true
                                    studentToUpdate = student
                                })
                        Spacer(modifier = Modifier.width(10.dp))
                        Image(painter = painterResource(id = R.drawable.delete),
                            contentDescription = "",
                            modifier = Modifier
                                .size(30.dp)
                                .clickable {
                                    studentToDelete = student
                                    showDialogXoa = true
                                })
                    }
                    Divider()
                }
            }
        }
        Row(modifier = Modifier
            .clip(shape = RoundedCornerShape(15.dp))
            .align(Alignment.BottomEnd)
            .clickable { showDialogAddSV = true }
            .background(color = Color("#A8D5BA".toColorInt()))
            .size(60.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "+", color = Color.White, fontSize = 30.sp)
        }
        if (showDialogAddSV) {
            AddStudentDialog(context = LocalContext.current,
                onConfirmation = { hoten, mssv, diemTB ->
                    val score = diemTB
                    db.studentDAO().insert(
                        StudentModel(
                            hoten = hoten,
                            mssv = mssv,
                            diemTB = score
                        )
                    )
                    listStudents = db.studentDAO().getAll()
                    showDialogAddSV = false
                    Toast.makeText(context, "Them thanh cong", Toast.LENGTH_SHORT).show()

                },
                onDismissRequest = { showDialogAddSV = false })
        }
    }


}

@Composable
fun ShowDialogStudentInfor(
    onConfirmation: () -> Unit,
    dialogTitle: String = "Thông tin chi tiết",
    dialogMessage: String,
) {
    Dialog(onDismissRequest = {}) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.White,
            ),
            modifier = Modifier.padding(20.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 10.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    dialogTitle, style =
                    MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    dialogMessage, style =
                    MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onConfirmation,

                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.DarkGray,
                        contentColor = Color.White
                    )
                ) {
                    Text("Okay")
                }
            }
        }
    }
}

@Composable
fun AddStudentDialog(
    context: Context,
    onConfirmation: (String, String, Float) -> Unit,
    onDismissRequest: () -> Unit,
    dialogTitle: String = "Thêm sinh viên",
) {
    var name by remember { mutableStateOf("") }
    var MSSV by remember { mutableStateOf("") }
    var DTB by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.White,
            ),
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 10.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    dialogTitle,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Tên sinh viên") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = MSSV,
                    onValueChange = { MSSV = it },
                    label = { Text("MSSV") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = DTB,
                    onValueChange = { DTB = it },
                    label = { Text("Điểm trung bình") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = { onDismissRequest() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Gray,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Hủy")
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            if (name.isNotBlank() && MSSV.isNotBlank() && DTB.isNotBlank()) {
                                // Convert DTB to Float
                                val score = DTB.toFloatOrNull() ?: 0f
                                onConfirmation(name, MSSV, score)
                            } else {
                                // Hiển thị thông báo khi có trường rỗng
                                Toast.makeText(
                                    context,
                                    "Vui lòng điền đủ thông tin!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color("#0D99FF".toColorInt()),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Thêm")
                    }
                }
            }
        }
    }
}

@Composable
fun UpdateStudentDialog(
    context: Context,
    student: StudentModel,
    onUpdateConfirmation: (StudentModel) -> Unit,
    onDismissRequest: () -> Unit,
    dialogTitle: String = "Update Sinh Viên",
) {
    var name by remember { mutableStateOf(student.hoten) }
    var mssv by remember { mutableStateOf(student.mssv) }
    var diemTB by remember { mutableStateOf(student.diemTB.toString()) }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.White,
            ),
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 10.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    dialogTitle,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))

                name?.let {
                    OutlinedTextField(
                        value = it,
                        onValueChange = { name = it },
                        label = { Text("Tên sinh viên") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                mssv?.let {
                    OutlinedTextField(
                        value = it,
                        onValueChange = { mssv = it },
                        label = { Text("MSSV") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                diemTB.let {
                    OutlinedTextField(
                        value = it,
                        onValueChange = { diemTB = it },
                        label = { Text("Điểm trung bình") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = { onDismissRequest() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Gray,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Hủy")
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            if (name!!.isNotBlank() && mssv!!.isNotBlank() && diemTB.isNotBlank()) {
                                // Convert diemTB to Float
                                val score = diemTB.toFloatOrNull() ?: 0f
                                val updatedStudent = student.copy(
                                    hoten = name,
                                    mssv = mssv,
                                    diemTB = score
                                )
                                onUpdateConfirmation(updatedStudent)
                            } else {
                                // Hiển thị thông báo khi có trường rỗng
                                Toast.makeText(
                                    context,
                                    "Vui lòng điền đủ thông tin!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color("#0D99FF".toColorInt()),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Cập nhật")
                    }
                }
            }
        }
    }
}

