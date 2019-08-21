package comc.mohammedsalah.test;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;


public class NotificationFragment extends Fragment {
    public NotificationFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        ConnectionInternet connectionInternet = new ConnectionInternet(getActivity());
        Boolean c = connectionInternet.isConnectToInternt();
        if (!c){
            Toast.makeText(getActivity(),"No Internt Connection\nلا يوجد اتصال بالانترنت",Toast.LENGTH_SHORT).show();
        }
         View view = inflater.inflate(R.layout.fragment_notification, container, false);
         WebView webView = view.findViewById(R.id.webview);
         webView.getSettings().setJavaScriptEnabled(true);
         webView.setWebViewClient(new WebViewClient());
         webView.loadUrl("https://www.youtube.com/watch?v=LENrkd3SkEs&t=2s");
        return view ;

    }

}
