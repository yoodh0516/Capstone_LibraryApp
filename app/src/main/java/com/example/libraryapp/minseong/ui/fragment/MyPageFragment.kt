package com.example.libraryapp.minseong.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.libraryapp.MainActivity
import com.example.libraryapp.R
import com.example.libraryapp.community.LoginActivity
import com.example.libraryapp.minseong.ui.adapter.MyPageAdapter
import com.example.libraryapp.ui.fragment.PrivacySettingsFragment

// 🔥 파이어베이스 기능 사용을 위한 도구 추가
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.Firebase
import kotlin.jvm.java

//class MyPageFragment : Fragment() {
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        val view = inflater.inflate(R.layout.minseong_ifragment_mypage, container, false)
//
//        val rvMyPageMenu = view.findViewById<RecyclerView>(R.id.rvMyPageMenu)
//        rvMyPageMenu.layoutManager = LinearLayoutManager(requireContext())
//
//        // 보여줄 메뉴 리스트 (이메일 변경 삭제됨)
//        val menuList = mutableListOf<MyPageAdapter.MenuItem>()
//        menuList.add(MyPageAdapter.MenuItem("비밀번호 변경", false))
//        menuList.add(MyPageAdapter.MenuItem("정보 동의 설정", false))
//        menuList.add(MyPageAdapter.MenuItem("로그아웃", false))
//        menuList.add(MyPageAdapter.MenuItem("회원탈퇴", true))
//
//        val adapter = MyPageAdapter(menuList, MyPageAdapter.OnItemClickListener { title ->
//            when (title) {
//                "정보 동의 설정" -> {
//                    parentFragmentManager.beginTransaction()
//                        .replace(R.id.fragment_container, PrivacySettingsFragment())
//                        .addToBackStack(null)
//                        .commit()
//                }
//                "비밀번호 변경" -> {
//                    // 🔥 [수정됨] 비밀번호 변경 화면으로 이동!
//                    parentFragmentManager.beginTransaction()
//                        .replace(R.id.fragment_container, ChangePasswordFragment()) // 여기 연결됨
//                        .addToBackStack(null)
//                        .commit()
//                }
//                "로그아웃" -> {
//                    showLogoutDialog()
//                }
//                "회원탈퇴" -> {
//                    showDeleteAccountDialog()
//                }
//            }
//        })
//
//        rvMyPageMenu.adapter = adapter
//
//        return view
//    }
//
//    // ==========================================
//    // 🚪 로그아웃 확인 팝업
//    // ==========================================
//    private fun showLogoutDialog() {
//        val builder = AlertDialog.Builder(requireContext())
//        builder.setTitle("로그아웃")
//        builder.setMessage("정말 로그아웃 하시겠습니까?")
//
//        builder.setPositiveButton("로그아웃") { dialog, _ ->
//            executeLogout()
//        }
//
//        builder.setNegativeButton("취소") { dialog, _ ->
//            dialog.dismiss()
//        }
//
//        builder.show()
//    }
//
//    // 로그아웃 실제 처리
//    private fun executeLogout() {
//        Toast.makeText(requireContext(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
//        // 나중에 로그인 화면이 생기면 이동 코드로 변경하세요. 지금은 앱 종료.
//        requireActivity().finish()
//    }
//
//    // ==========================================
//    // 🚨 회원 탈퇴 확인 팝업
//    // ==========================================
//    private fun showDeleteAccountDialog() {
//        val builder = AlertDialog.Builder(requireContext())
//        builder.setTitle("회원 탈퇴")
//        builder.setMessage("정말 탈퇴하시겠습니까?\n탈퇴 시 모든 정보가 삭제되며 복구할 수 없습니다.")
//
//        builder.setPositiveButton("탈퇴") { dialog, _ ->
//            executeAccountDeletion()
//        }
//
//        builder.setNegativeButton("취소") { dialog, _ ->
//            dialog.dismiss()
//        }
//
//        builder.show()
//    }
//
//    // 회원 탈퇴 실제 처리
//    private fun executeAccountDeletion() {
//        Toast.makeText(requireContext(), "회원 탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show()
//        requireActivity().finish()
//    }
//}


class MyPageFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.minseong_ifragment_mypage, container, false)

        val rvMyPageMenu = view.findViewById<RecyclerView>(R.id.rvMyPageMenu)
        rvMyPageMenu.layoutManager = LinearLayoutManager(requireContext())

        // 보여줄 메뉴 리스트
        val menuList = mutableListOf<MyPageAdapter.MenuItem>()
        menuList.add(MyPageAdapter.MenuItem("비밀번호 변경", false))
        menuList.add(MyPageAdapter.MenuItem("정보 동의 설정", false))
        menuList.add(MyPageAdapter.MenuItem("로그아웃", false))
        menuList.add(MyPageAdapter.MenuItem("회원탈퇴", true))

        val adapter = MyPageAdapter(menuList, MyPageAdapter.OnItemClickListener { title ->
            when (title) {
                "정보 동의 설정" -> {
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, PrivacySettingsFragment())
                        .addToBackStack(null)
                        .commit()
                }
                "비밀번호 변경" -> {
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, ChangePasswordFragment())
                        .addToBackStack(null)
                        .commit()
                }
                "로그아웃" -> {
                    showLogoutDialog()
                }
                "회원탈퇴" -> {
                    showDeleteAccountDialog()
                }
            }
        })

        rvMyPageMenu.adapter = adapter
        return view
    }

    // ==========================================
    // 🚪 로그아웃 확인 팝업
    // ==========================================
    private fun showLogoutDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("로그아웃")
        builder.setMessage("정말 로그아웃 하시겠습니까?")

        builder.setPositiveButton("로그아웃") { dialog, _ ->
            executeLogout()
        }

        builder.setNegativeButton("취소") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    // 로그아웃 실제 처리
    private fun executeLogout() {
        // 🔥 진짜 파이어베이스 로그아웃 처리
        Firebase.auth.signOut()
        Toast.makeText(requireContext(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()

        // 🚀 [수정된 부분] 도착지를 MainActivity(홈 화면)로 변경!
        val intent = android.content.Intent(requireContext(), MainActivity::class.java)
        intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    // ==========================================
    // 🚨 회원 탈퇴 확인 팝업 (비밀번호 입력창 추가)
    // ==========================================
    private fun showDeleteAccountDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("회원 탈퇴")
        builder.setMessage("정말 탈퇴하시겠습니까?\n본인 확인을 위해 현재 비밀번호를 입력해 주세요.")

        // 팝업창 안에 비밀번호 입력칸(EditText) 만들기
        val input = EditText(requireContext())
        // 입력할 때 글자가 별표(*)로 보이도록 설정
        input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        builder.setView(input)

        builder.setPositiveButton("탈퇴") { _, _ ->
            val password = input.text.toString()
            // 비밀번호를 입력했을 때만 탈퇴 진행
            if (password.isNotEmpty()) {
                executeAccountDeletion(password)
            } else {
                Toast.makeText(requireContext(), "비밀번호를 입력해야 합니다.", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("취소") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    // 회원 탈퇴 실제 처리 (파이어베이스 연동)
    private fun executeAccountDeletion(password: String) {
        val user = Firebase.auth.currentUser

        if (user != null && user.email != null) {
            // 1. 현재 비밀번호로 묶은 '열쇠(credential)' 만들기
            val credential = EmailAuthProvider.getCredential(user.email!!, password)

            // 2. 파이어베이스 서버에 본인 확인(재인증) 요청
            user.reauthenticate(credential).addOnCompleteListener { reAuthTask ->
                if (reAuthTask.isSuccessful) {

                    // 3. 본인 확인 성공! -> 계정 진짜로 삭제하기
                    user.delete().addOnCompleteListener { deleteTask ->
                        if (deleteTask.isSuccessful) {
                            Toast.makeText(requireContext(), "회원 탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show()
                            // 탈퇴 성공 시 앱 종료 (나중에는 로그인 화면으로 이동하도록 수정 가능)
                            val intent = android.content.Intent(requireContext(), MainActivity::class.java)
                            intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        } else {
                            Toast.makeText(requireContext(), "탈퇴 실패: ${deleteTask.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }

                } else {
                    // 비밀번호를 틀리게 입력한 경우
                    Toast.makeText(requireContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(requireContext(), "로그인 정보가 없습니다. 다시 로그인 해주세요.", Toast.LENGTH_SHORT).show()
        }
    }
}