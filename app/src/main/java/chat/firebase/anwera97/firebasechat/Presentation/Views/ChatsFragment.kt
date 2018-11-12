package chat.firebase.anwera97.firebasechat.Presentation.Views

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import chat.firebase.anwera97.firebasechat.Presentation.Presenters.ChatsDelegate
import chat.firebase.anwera97.firebasechat.Presentation.Presenters.ChatsPresenter
import chat.firebase.anwera97.firebasechat.R

class ChatsFragment: Fragment(), ChatsDelegate {

    companion object {
        fun newInstance(): ChatsFragment {
            return ChatsFragment()
        }
    }

    private val mPresenter = ChatsPresenter(this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_chats, container, false)
        setHasOptionsMenu(true)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPresenter.getChats()

    }

    override fun onChatsChanged() {

    }
}