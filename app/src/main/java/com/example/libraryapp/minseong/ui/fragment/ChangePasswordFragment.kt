package com.example.libraryapp.minseong.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.libraryapp.R

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.Firebase
//class ChangePasswordFragment : Fragment() {
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val view = inflater.inflate(R.layout.minseong_fragment_change_password, container, false)
//
//        val etCurrentPassword = view.findViewById<EditText>(R.id.etCurrentPassword)
//        val etNewPassword = view.findViewById<EditText>(R.id.etNewPassword)
//        val etConfirmPassword = view.findViewById<EditText>(R.id.etConfirmPassword)
//        val btnChangePassword = view.findViewById<Button>(R.id.btnChangePassword)
//
//        btnChangePassword.setOnClickListener {
//            val currentPw = etCurrentPassword.text.toString()
//            val newPw = etNewPassword.text.toString()
//            val confirmPw = etConfirmPassword.text.toString()
//
//            // 1. 빈칸이 있는지 검사
//            if (currentPw.isEmpty() || newPw.isEmpty() || confirmPw.isEmpty()) {
//                Toast.makeText(requireContext(), "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//
//            // 2. 새 비밀번호와 확인용 비밀번호가 일치하는지 검사
//            if (newPw != confirmPw) {
//                Toast.makeText(requireContext(), "새 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//
//            // TODO: (나중에 추가) 서버로 현재 비밀번호와 새 비밀번호를 보내서 변경 요청하는 코드 작성
//
//            // 3. 성공 처리 (지금은 서버가 없으니 바로 성공으로 간주)
//            Toast.makeText(requireContext(), "비밀번호가 성공적으로 변경되었습니다.", Toast.LENGTH_SHORT).show()
//
//            // 이전 화면(마이페이지)으로 돌아가기
//            parentFragmentManager.popBackStack()
//        }
//
//        return view
//    }
//}

class ChangePasswordFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.minseong_fragment_change_password, container, false)

        val etCurrentPassword = view.findViewById<EditText>(R.id.etCurrentPassword)
        val etNewPassword = view.findViewById<EditText>(R.id.etNewPassword)
        val etConfirmPassword = view.findViewById<EditText>(R.id.etConfirmPassword)
        val btnChangePassword = view.findViewById<Button>(R.id.btnChangePassword)

        btnChangePassword.setOnClickListener {
            val currentPw = etCurrentPassword.text.toString()
            val newPw = etNewPassword.text.toString()
            val confirmPw = etConfirmPassword.text.toString()

            // 1. 빈칸이 있는지 검사
            if (currentPw.isEmpty() || newPw.isEmpty() || confirmPw.isEmpty()) {
                Toast.makeText(requireContext(), "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 2. 새 비밀번호와 확인용 비밀번호가 일치하는지 검사
            if (newPw != confirmPw) {
                Toast.makeText(requireContext(), "새 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 파이어베이스 정책상 최소 6자리 이상이어야 합니다.
            if (newPw.length < 6) {
                Toast.makeText(requireContext(), "비밀번호는 6자리 이상이어야 합니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 3. 🚀 진짜 파이어베이스 서버로 변경 요청하기
            updatePasswordInFirebase(currentPw, newPw)
        }

        return view
    }

    // 파이어베이스 비밀번호 변경 로직
    private fun updatePasswordInFirebase(currentPw: String, newPw: String) {
        val user = Firebase.auth.currentUser

        if (user != null && user.email != null) {
            // 현재 비밀번호가 맞는지 재인증 (보안 필수 절차)
            val credential = EmailAuthProvider.getCredential(user.email!!, currentPw)

            user.reauthenticate(credential).addOnCompleteListener { reAuthTask ->
                if (reAuthTask.isSuccessful) {
                    // 재인증 성공 시, 새 비밀번호로 업데이트
                    user.updatePassword(newPw).addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            Toast.makeText(requireContext(), "비밀번호가 성공적으로 변경되었습니다.", Toast.LENGTH_SHORT).show()
                            parentFragmentManager.popBackStack() // 마이페이지로 돌아가기
                        } else {
                            Toast.makeText(requireContext(), "변경 실패: ${updateTask.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "현재 비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(requireContext(), "로그인 정보가 없습니다. 다시 로그인 해주세요.", Toast.LENGTH_SHORT).show()
        }
    }
}