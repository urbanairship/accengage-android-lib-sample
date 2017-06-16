package com.accengage.samples.inbox.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.accengage.samples.R;
import com.accengage.samples.base.AccengageFragment;
import com.accengage.samples.firebase.models.InboxMessage;
import com.accengage.samples.inbox.InboxMessagesManager;
import com.accengage.samples.inbox.InboxNavActivity;
import com.ad4screen.sdk.Acc;
import com.ad4screen.sdk.Message;


public class InboxMessageDetailFragment extends AccengageFragment {

    public static final String TAG = "InboxMsgDetailFrag";

    private InboxMessage mMessage;

    private TextView mSender;
    private TextView mTitle;
    private TextView mBody;
    private WebView mWebView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mMessage = ((InboxNavActivity) getActivity()).getClickedMessage();
    }

    @Override
    public void onCreatingView(View fragmentView) {
        super.onCreatingView(fragmentView);

        mSender = fragmentView.findViewById(R.id.inbox_msg_sender);
        mTitle = fragmentView.findViewById(R.id.inbox_msg_title);
        mBody = fragmentView.findViewById(R.id.inbox_msg_body);
        mWebView = fragmentView.findViewById(R.id.inbox_msg_webview);

        mSender.setText(mMessage.sender);
        mTitle.setText(mMessage.title);

        Message accMessage = mMessage.getAccMessage();
        accMessage.display(getContext(), new Acc.Callback<Message>() {
            @Override
            public void onResult(Message result) {
                Log.d(TAG, "onResult display OK");

                if (mMessage.contentType.equals(Message.MessageContentType.Web.name())) {
                    Log.d(TAG, "message with a web content");
                    if (mMessage.body != null) {
                        Log.d(TAG, "message with a web body is not null " + mMessage.body);
                        mWebView.setVisibility(View.VISIBLE);
                        mWebView.setWebViewClient(new WebViewClient());
                        WebSettings webSettings = mWebView.getSettings();
                        webSettings.setJavaScriptEnabled(true);
                        mWebView.loadUrl(mMessage.body);
                        mBody.setVisibility(View.GONE);
                    }
                } else {
                    Log.d(TAG, "message with an other content: " + mMessage.contentType);
                    mWebView.setVisibility(View.GONE);
                    mBody.setText(mMessage.body);
                    mBody.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(int error, String errorMessage) {
                Log.d(TAG, "onError display KO");
            }
        });
    }

    @Override
    public String getViewName(Context context) {
        return "Message Details";
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_inbox_message_details;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.inbox_msg_actions, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_inbox_archive) {
            mMessage.archived = true;
            InboxMessagesManager.get(getContext()).updateMessage(mMessage);
            getActivity().getSupportFragmentManager().popBackStack();
            return true;
        } else if (i == R.id.action_inbox_delete) {
            return true;
        } else if (i == R.id.action_inbox_more) {
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}