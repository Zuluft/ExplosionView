package com.zuluft

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment


class MatchConfirmationFragment :
        Fragment() {

    private lateinit var containerWithTitle: ContainerWithTitle

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_match_confirmation, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        containerWithTitle = context as ContainerWithTitle
    }

    override fun onResume() {
        super.onResume()
        containerWithTitle.setTitle(getString(R.string.match_confirmation_screen_title_text))
    }

}