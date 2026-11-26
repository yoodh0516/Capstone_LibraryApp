package com.example.libraryapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.libraryapp.R

class PrivacySettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.minseong_ifragment_privacy_settings, container, false)

        // 약관 버튼들 찾기
        val layoutTerms = view.findViewById<LinearLayout>(R.id.layoutTerms)
        val layoutPrivacy = view.findViewById<LinearLayout>(R.id.layoutPrivacy)

        // 클릭했을 때 반응 설정
        layoutTerms.setOnClickListener {
            Toast.makeText(context, "이용약관 페이지 준비 중", Toast.LENGTH_SHORT).show()
        }

        layoutPrivacy.setOnClickListener {
            Toast.makeText(context, "개인정보 처리방침 페이지 준비 중", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}